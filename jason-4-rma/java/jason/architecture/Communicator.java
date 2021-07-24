package jason.architecture;

import br.pro.turing.rma.core.model.Action;
import br.pro.turing.rma.core.model.Data;
import br.pro.turing.rma.core.model.Device;
import br.pro.turing.rma.core.service.ServiceManager;
import jason.asSyntax.Plan;
import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;
import lac.cnclib.sddl.serialization.Serialization;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Communicator extends AgArch implements NodeConnectionListener {

    /** IoT by ContextNet connection instance. */
    private MrUdpNodeConnection connection;

    /** Model of IoT objects. */
    private Device device;

    /** IP socket address of the ContextNet gateway. */
    private InetSocketAddress gatewayAddress;

    /** State of connection. */
    private boolean connected = false;

    /**
     * Builds a device given a device configuration file.
     *
     * @param deviceConfigurationFilePath Device configuration file.
     * @return Device.
     */
    public static Device buildDeviceByConfigFile(String deviceConfigurationFilePath) throws FileNotFoundException {
        Reader jsonFile = new FileReader(deviceConfigurationFilePath);
        return ServiceManager.getInstance().jsonService.fromJson(jsonFile, Device.class);
    }

    public static Communicator getCommunicatorArch(AgArch currentArch) {
        if (currentArch == null) {
            return null;
        }
        if (currentArch instanceof Communicator) {
            return (Communicator) currentArch;
        }
        return getCommunicatorArch(currentArch.getNextAgArch());
    }

    /**
     * Search by an UUID if exists.
     *
     * @return UUID or empty text.
     */
    private String getUUIDFromFile() throws IOException {
        File file = new File(".device");
        if (!file.exists()) {
            return "";
        }

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final String uuid;
            uuid = bufferedReader.readLine();
            return uuid;
        } finally {
            inputStream.close();
        }
    }

    /**
     * Create a file if not exists and write a new UUID value.
     *
     * @param uuid UUID.
     * @throws IOException
     */
    private void setUUIDToFile(String uuid) throws IOException {
        File file = new File(".device");
        if (!file.exists()) {
            Files.createFile(file.toPath());
        }
        FileWriter writer = new FileWriter(".device");
        writer.write(uuid);
        writer.close();
    }

    /**
     * Connects to the RML.
     *
     * @param gatewayIP   Gateway IP.
     * @param gatewayPort Gateway port.
     */
    public void connect(String gatewayIP, int gatewayPort) throws IOException {
        this.gatewayAddress = new InetSocketAddress(gatewayIP, gatewayPort);
        final String uuid = this.getUUIDFromFile();
        connection = uuid.isEmpty() ? new MrUdpNodeConnection() : new MrUdpNodeConnection(UUID.fromString(uuid));
        connection.addNodeConnectionListener(this);

        connection.connect(gatewayAddress);
    }

    public void sendToRml(Data data) {
        if (this.connected && data != null) {
            ArrayList<Data> dataList = new ArrayList<>();
            dataList.add(data);
            Message message = new ApplicationMessage();
            message.setContentObject(ServiceManager.getInstance().jsonService.toJson(dataList));
            try {
                connection.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        }

        this.device = buildDeviceByConfigFile(deviceConfigFile.getPath());
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
        String messageReceived = (String) Serialization.fromJavaByteStream(message.getContent());
        if (ServiceManager.getInstance().jsonService.jasonIsObject(messageReceived, Device.class.getName())) {
            // TODO Log here for the TIME CONNECTION RECEIVE
            this.device = ServiceManager.getInstance().jsonService.fromJson(messageReceived, Device.class);
            try {
                if (this.getUUIDFromFile().isEmpty()) {
                    this.setUUIDToFile(this.device.getUUID());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (ServiceManager.getInstance().jsonService.jasonIsObject(messageReceived, Action.class.getName())) {
            Action action = ServiceManager.getInstance().jsonService.fromJson(messageReceived, Action.class);
            this.onAction(action);
        }
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
}
