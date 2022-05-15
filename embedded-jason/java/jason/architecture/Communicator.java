package jason.architecture;

import br.pro.turing.rma.core.model.Action;
import br.pro.turing.rma.core.model.Device;
import br.pro.turing.rma.core.service.ServiceManager;
import jason.AslFileGenerator;
import jason.AslTransferenceModel;
import jason.asSyntax.Plan;
import jason.infra.centralised.CentralisedAgArch;
import jason.infra.centralised.RunCentralisedMAS;
import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;
import lac.cnclib.sddl.serialization.Serialization;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Communicator extends AgArch implements NodeConnectionListener {

    /** IoT by ContextNet connection instance. */
    private MrUdpNodeConnection connection;

    /** Model of IoT objects. */
    private Device device;

    /** State of connection. */
    private boolean connected = false;

    private final List<String> nameAgents = new ArrayList<>();

    private ArrayList<jason.asSemantics.Message> jMsg = new ArrayList<jason.asSemantics.Message>();

    public Communicator() {
    }

    private void onAction(Action action) {
        // TODO transformar em chamada de plano.
        for (Plan plan : getTS().getAg().getPL()) {
            final String tevent = plan.getTerm(1).toString();
            final String command = tevent.substring(2);
            if (action.getCommand().equals(command)) {
                jason.asSemantics.Message jasonMsgs = new jason.asSemantics.Message();
                jasonMsgs.setIlForce("achieve");
                jasonMsgs.setSender(this.getAgName());
                jasonMsgs.setPropCont(command);
                jasonMsgs.setReceiver(this.getAgName());
                this.getTS().getC().addMsg(jasonMsgs);
                break;
            }
        }
    }

    @Override
    public void init() throws Exception {
        super.init();
        File aslFile = new File(this.getTS().getAg().getASLSrc());
        String deviceConfigFilePath = aslFile.getParent() + File.separator + "deviceConfiguration.json";
        File deviceConfigFile = new File(deviceConfigFilePath);
        if (!deviceConfigFile.exists()) {
            this.getTS().getLogger().severe("deviceConfiguration.json not found in '" + aslFile.getParent() + "'.");
            return;
        }

        Reader jsonFile = new FileReader(deviceConfigFile.getPath());
        this.device = ServiceManager.getInstance().jsonService.fromJson(jsonFile, Device.class);
    }

    /**
     * Informs that this IoT Object is connected to RML server. Once connected, this Object will send a Device
     * instance to be registered (if not exists on RML) or logged in (if this device is already registered).
     *
     * @param nodeConnection Node connection.
     */
    @Override
    public void connected(NodeConnection nodeConnection) {
        new Thread(() -> {
            String msg = ServiceManager.getInstance().jsonService.toJson(this.device);
            Message message = new ApplicationMessage();
            message.setContentObject(msg);
            try {
                nodeConnection.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            // TODO Log here for the TIME CONNECTION SEND
            this.connected = true;
        }).start();
    }

    /**
     * Menage incoming messages from RML. If the message is about connection state, it means that the Device was
     * registered or logged in RML. If message is about Action, a new action will be performed in some resource of
     * this device.
     *
     * @param nodeConnection Node cnnection
     * @param message        Message.
     */
    @Override
    public final void newMessageReceived(NodeConnection nodeConnection, Message message) {
        String receivedMessage = (String) Serialization.fromJavaByteStream(message.getContent());
        if (ServiceManager.getInstance().jsonService.jasonIsObject(receivedMessage, Device.class.getName())) {
            proccessDeviceMessage(receivedMessage);
        } else if (ServiceManager.getInstance().jsonService.jasonIsObject(receivedMessage, Action.class.getName())) {
            proccessActionMessage(receivedMessage);
        } else if (ServiceManager.getInstance().jsonService.jasonIsObject(receivedMessage, EcologicalRelationBuffer.class.getName())) {
            proccessEcologicalRelationMessage(receivedMessage);
        } else {
            proccessDefaultCommunicatorMessage(receivedMessage);
        }
    }

    private void proccessDeviceMessage(String receivedMessage) {
        // TODO Log here for the TIME CONNECTION RECEIVE
        this.device = ServiceManager.getInstance().jsonService.fromJson(receivedMessage, Device.class);
        try {
            if (CommunicatorUtils.getUUIDFromFile().isEmpty()) {
                CommunicatorUtils.setUUIDToFile(this.device.getUUID());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void proccessActionMessage(String receivedMessage) {
        Action action = ServiceManager.getInstance().jsonService.fromJson(receivedMessage, Action.class);
        this.onAction(action);
        // todo parei aqui. Precisa tipar a mensagem contextNet para conciliar RML com Contextnet comum. O protocolo de predação deve ser um comunicador estendido.
    }

    private void proccessEcologicalRelationMessage(String receivedMessage) {
        EcologicalRelationBuffer ecologicalRelationBuffer = ServiceManager.getInstance().jsonService.fromJson(receivedMessage, EcologicalRelationBuffer.class);

        AslFileGenerator aslFileGenerator = new AslFileGenerator();

        if (TransferenceActionType.GIVE_BIRTH.equals(ecologicalRelationBuffer.getActionType())) {
            giveBirthAgents(ecologicalRelationBuffer, aslFileGenerator);
        } else if (TransferenceActionType.KILL.equals(ecologicalRelationBuffer.getActionType())) {
            killAgents(ecologicalRelationBuffer);
        } else if (TransferenceActionType.GIVE_BIRTH_AND_KILL.equals(ecologicalRelationBuffer.getActionType())) {
            giveBirthAgents(ecologicalRelationBuffer, aslFileGenerator);
        }
        giveRelationFeedback(ecologicalRelationBuffer);
    }

    private void giveRelationFeedback(EcologicalRelationBuffer ecologicalRelationBuffer) {
        EcologicalRelationBuffer feedbackEcologicalRelationBuffer = new EcologicalRelationBuffer();
        feedbackEcologicalRelationBuffer.setAgentsToKill(ecologicalRelationBuffer.getAgentsToGiveBirth());
        Message message = new ApplicationMessage();
        message.setContentObject(ServiceManager.getInstance().jsonService.toJson(feedbackEcologicalRelationBuffer));
        try {
            this.getConnection().sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void killAgents(EcologicalRelationBuffer ecologicalRelationBuffer) {
        List<String> agentsToKillNames = ecologicalRelationBuffer.getAgentsToKill().stream().map(AslTransferenceModel::getName).collect(Collectors.toList());
        Map<String, CentralisedAgArch> agentsOfTheSMA = RunCentralisedMAS.getRunner().getAgs();
        for (CentralisedAgArch centralisedAgArch : agentsOfTheSMA.values()) {
            if (agentsToKillNames.contains(centralisedAgArch.getAgName())) {
                String path = centralisedAgArch.getTS().getAg().getASLSrc();
                File file = new File(path);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    private void giveBirthAgents(EcologicalRelationBuffer ecologicalRelationBuffer, AslFileGenerator aslFileGenerator) {
        List<AslTransferenceModel> agentsToGiveBirth = ecologicalRelationBuffer.getAgentsToGiveBirth();
        for (AslTransferenceModel aslTransferenceModel : agentsToGiveBirth) {
            CentralisedAgArch centralisedAgArch = RunCentralisedMAS.getRunner().getAgs().values().stream().findFirst().orElse(null);
            String path = centralisedAgArch == null || centralisedAgArch.getAgName() == null ? "" :
                    centralisedAgArch.getTS().getAg().getASLSrc();
            path = path.isEmpty() ? "" : path.substring(0, path.length() - (centralisedAgArch.getAgName() + AslFileGenerator.ASL_EXTENSION).length());
            aslFileGenerator.createAslFile(path, aslTransferenceModel);
        }
    }

    private void proccessDefaultCommunicatorMessage(String receivedMessage) {
        final char[] message = receivedMessage.toCharArray();
        String preamble = String.valueOf(message, 0, 4);
        if (preamble.equals("fffe")) {
            String sender = "";
            for (int cont = 6; cont < (this.hex2int(char2int(message[5]), char2int(message[4])) + 6); cont++) {
                sender += message[cont];
            }
            String force = "";
            String msg = "";
            int nextValue = sender.length() + 6;
            int shift = this.hex2int(char2int(message[nextValue + 1]), char2int(message[nextValue]));
            for (int cont = nextValue + 2; cont <= (shift + nextValue + 1); cont++) {
                force += message[cont];
            }
            nextValue = shift + nextValue + 2;
            shift = this.hex2int(char2int(message[nextValue + 1]), char2int(message[nextValue]));
            for (int cont = nextValue + 2; cont <= (shift + nextValue + 1); cont++) {
                msg += message[cont];
            }

            jason.asSemantics.Message jasonMsgs = new jason.asSemantics.Message();
            jasonMsgs.setIlForce(force);
            jasonMsgs.setSender(sender);
            jasonMsgs.setPropCont(msg);
            jasonMsgs.setReceiver(this.getAgName());
            //System.out.println("[ARGO]: The message is: " + jasonMsgs.toString());
            this.jMsg.add(jasonMsgs);
        }
    }

    private int hex2int(int x, int y) {
        int converted = x + (y * 16);
        return converted;
    }

    private int char2int(char charValue) {
        int intValue = 0;
        switch (charValue) {
            case '1':
                intValue = 1;
                break;
            case '2':
                intValue = 2;
                break;
            case '3':
                intValue = 3;
                break;
            case '4':
                intValue = 4;
                break;
            case '5':
                intValue = 5;
                break;
            case '6':
                intValue = 6;
                break;
            case '7':
                intValue = 7;
                break;
            case '8':
                intValue = 8;
                break;
            case '9':
                intValue = 9;
                break;
            case 'a':
                intValue = 10;
                break;
            case 'b':
                intValue = 11;
                break;
            case 'c':
                intValue = 12;
                break;
            case 'd':
                intValue = 13;
                break;
            case 'e':
                intValue = 14;
                break;
            case 'f':
                intValue = 15;
                break;
        }
        return intValue;
    }

    /**
     * Informs that this IoT Object is reconnected to TML server.
     *
     * @param nodeConnection Node connection.
     * @param socketAddress  Socket address.
     * @param b              B
     * @param b1             B1
     */
    @Override
    public final void reconnected(NodeConnection nodeConnection, SocketAddress socketAddress, boolean b, boolean b1) {
        this.connected = true;
    }

    /**
     * Informs that this IoT Object is disconnected from RML server.
     *
     * @param nodeConnection Node connection.
     */
    @Override
    public final void disconnected(NodeConnection nodeConnection) {
        this.connected = false;
    }

    /**
     * Informs that some messages was not sent. If this Application layer is connected to RML server, the unsert
     * messages will be resent.
     *
     * @param nodeConnection Node connection.
     * @param list           Unsent messages list.
     */
    @Override
    public final void unsentMessages(NodeConnection nodeConnection, List<Message> list) {
        StringBuilder errorMessageLog = new StringBuilder();
        errorMessageLog.append("Unsent mesage(s):");
        for (Message message : list) {
            errorMessageLog.append("\n").append(Serialization.fromJavaByteStream(message.getContent()).toString());
        }
        for (Message message : list) {
            try {
                connection.sendMessage(message);
            } catch (IOException e) {
                return;
            }
        }
    }

    @Override
    public final void internalException(NodeConnection nodeConnection, Exception e) {

    }

    /**
     * @return {@link #device}
     */
    public Device getDevice() {
        return this.device;
    }

    /**
     * @param connection {@link #connection}
     */
    public void setConnection(MrUdpNodeConnection connection) {
        this.connection = connection;
    }

    /**
     * @return {@link #connection}
     */
    public MrUdpNodeConnection getConnection() {
        return this.connection;
    }

    /**
     * @return {@link #connected}
     */
    public boolean isConnected() {
        return this.connected;
    }

    public List<String> getNameAgents() {
        return this.nameAgents;
    }
}
