package jason.architecture;

import br.pro.turing.rma.core.model.Action;
import br.pro.turing.rma.core.model.Device;
import br.pro.turing.rma.core.service.ServiceManager;
import protocol.communication.SimpleCommunicationBuffer;
import protocol.ecologicalrelation.AslFileGenerator;
import protocol.ecologicalrelation.AslTransferenceModel;
import protocol.ecologicalrelation.EcologicalRelationBuffer;
import protocol.ecologicalrelation.TransferenceActionType;
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
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Communicator extends AgArch {
    private Device device;

    private CommMiddleware commMiddleware = new CommMiddleware();

    private final List<String> nameAgents = new ArrayList<>();

    private ArrayList<jason.asSemantics.Message> jMsg = new ArrayList<jason.asSemantics.Message>();

    public Communicator() {
    }

    @Override
    public void init() throws Exception {
        super.init();

        File aslFile = new File(this.getTS().getAg().getASLSrc());
        String deviceConfigFilePath = aslFile.getParent() + File.separator + "deviceConfiguration.json";
        File deviceConfigFile = new File(deviceConfigFilePath);
        if (!deviceConfigFile.exists()) {
            this.getTS().getLogger().warning("[WARNING] deviceConfiguration.json not found in '" + aslFile.getParent() + "'. You cannot to communicate using the RMA.");
            return;
        }

        Reader jsonFile = new FileReader(deviceConfigFile.getPath());
        this.device = ServiceManager.getInstance().jsonService.fromJson(jsonFile, Device.class);
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

    private void proccessActionMessage(String receivedMessage) {
        Action action = ServiceManager.getInstance().jsonService.fromJson(receivedMessage, Action.class);
        this.onAction(action);
    }

    private void proccessEcologicalRelationMessage(String receivedMessage) {
        EcologicalRelationBuffer ecologicalRelationBuffer = ServiceManager.getInstance().jsonService.fromJson(receivedMessage, EcologicalRelationBuffer.class);

        AslFileGenerator aslFileGenerator = new AslFileGenerator();

        if (TransferenceActionType.GIVE_BIRTH.equals(ecologicalRelationBuffer.getActionType())) {
            giveBirthAgents(ecologicalRelationBuffer, aslFileGenerator);
        } else if (TransferenceActionType.KILL.equals(ecologicalRelationBuffer.getActionType())) {
            killAgents(ecologicalRelationBuffer);
        } else if (TransferenceActionType.KILL_AND_GIVE_BIRTH.equals(ecologicalRelationBuffer.getActionType())) {
            killAllAgents();
            giveBirthAgents(ecologicalRelationBuffer, aslFileGenerator);
        }
        giveRelationFeedback(ecologicalRelationBuffer);
    }

    private void giveRelationFeedback(EcologicalRelationBuffer ecologicalRelationBuffer) {
        EcologicalRelationBuffer feedbackEcologicalRelationBuffer = new EcologicalRelationBuffer();
        feedbackEcologicalRelationBuffer.setRelationType(ecologicalRelationBuffer.getRelationType());
        feedbackEcologicalRelationBuffer.setAgentsToKill(ecologicalRelationBuffer.getAgentsToGiveBirth());
        feedbackEcologicalRelationBuffer.setActionType(TransferenceActionType.KILL);

        Message message = new ApplicationMessage();
        message.setContentObject(ServiceManager.getInstance().jsonService.toJson(feedbackEcologicalRelationBuffer));
//        try {
//            this.getConnection().sendMessage(message);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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

    private void killAllAgents() {
        Map<String, CentralisedAgArch> agentsOfTheSMA = RunCentralisedMAS.getRunner().getAgs();
        for (CentralisedAgArch centralisedAgArch : agentsOfTheSMA.values()) {
            String path = centralisedAgArch.getTS().getAg().getASLSrc();
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void proccessDefaultCommunicatorMessage(String receivedMessage) {
        SimpleCommunicationBuffer simpleCommunicationBuffer = ServiceManager.getInstance().jsonService.fromJson(receivedMessage, SimpleCommunicationBuffer.class);

        jason.asSemantics.Message jasonMsgs = new jason.asSemantics.Message();
        jasonMsgs.setIlForce(simpleCommunicationBuffer.getIlForce());
        jasonMsgs.setSender(simpleCommunicationBuffer.getSender());
        jasonMsgs.setPropCont(simpleCommunicationBuffer.getContent());
        jasonMsgs.setReceiver(simpleCommunicationBuffer.getReceiver());
        //System.out.println("[ARGO]: The message is: " + jasonMsgs.toString());
        this.jMsg.add(jasonMsgs);
    }

    @Override
    public void checkMail() {
        super.checkMail();
        if (this.commMiddleware.hasMsg()) {
            getTS().getC().addMsg(this.commMiddleware.checkMailCN());
            this.commMiddleware.cleanMailBox();
        }
//        if (!this.jMsg.isEmpty()) {
//            this.getTS().getC().addMsg(this.jMsg.get(0));
//            this.jMsg.remove(0);
//        }
    }

    /**
     * @return {@link #device}
     */
    public Device getDevice() {
        return this.device;
    }

    public List<String> getNameAgents() {
        return this.nameAgents;
    }

    public CommMiddleware getCommMiddleware() {
        this.commMiddleware.setAgName(this.getAgName());
        return this.commMiddleware;
    }

    public void addMessageToC() {
        this.getTS().getC().addMsg(this.commMiddleware.checkMailCN());
    }
}
