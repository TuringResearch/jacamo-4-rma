package jason;

import jason.architecture.AgArch;
import jason.architecture.CommMiddleware;
import jason.architecture.EcologicalRelationType;
import jason.infra.centralised.CentralisedAgArch;
import jason.infra.centralised.RunCentralisedMAS;
import jason.mas2j.ClassParameters;
import jason.runtime.RuntimeServices;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommunicatorUltron extends AgArch {

    private CommMiddleware commBridge = new CommMiddleware();

    private static final String AGENT_FILE_EXTENSION = ".asl";

    @Override
    public void addMessageToC() {
        this.getTS().getC().addMsg(this.commBridge.checkMailCN());
        //this.commBridge.cleanMailBox();
    }

    @Override
    public void instantiateAgents() {
        if (this.commBridge.getProtocol().equals(EcologicalRelationType.PREDATOR.getName())) {
            this.executePredatorProtocol();
        } else if (this.commBridge.getProtocol().equals(EcologicalRelationType.MUTUALISM.getName())) {
            this.executeMutualismProtocol();
        } else if (this.commBridge.getProtocol().equals(EcologicalRelationType.INQUILINISM.getName())) {
            this.executeInquilinismProtocol();
        }
    }

    private void executePredatorProtocol () {
        int qtdAgentsInstantiated = 0;
        for (AslTransferenceModel aslTransferenceModel : this.commBridge.getAgentsReceived()) {
            String name = aslTransferenceModel.getName();
            String path = getPath(name);

            try {
                String agClass = null;
                List<String> agArchClasses = new ArrayList<String>();
                ClassParameters bbPars = null;

                RuntimeServices rs = this.getTS().getUserAgArch().getRuntimeServices();
                name = rs.createAgent(name, path, agClass, agArchClasses, bbPars, this.getTS().getSettings(), this.getTS().getAg());
                rs.startAgent(name);
                qtdAgentsInstantiated++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (qtdAgentsInstantiated == this.commBridge.getAgentsReceived().size()) {
            // Todos os agentes instanciados, enviando mensagem para deletar da origem
            this.commBridge.sendMsgToDeleteAllAgents();
            this.killAllAgents();
            // Apagando Variaveis do transporte
            this.commBridge.cleanAtributesOfTransference();
            System.out.println("Terminou: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS")));
        }
    }

    private void executeMutualismProtocol() {
        int qtdAgentsInstantiated = 0;
        for (AslTransferenceModel aslTransferenceModel : this.commBridge.getAgentsReceived()) {
            String name = aslTransferenceModel.getName();
            String path = getPath(name);
            try {
                String agClass = null;
                List<String> agArchClasses = new ArrayList<String>();
                ClassParameters bbPars = null;

                RuntimeServices rs = this.getTS().getUserAgArch().getRuntimeServices();
                name = rs.createAgent(name, path, agClass, agArchClasses, bbPars, this.getTS().getSettings(), this.getTS().getAg());
                rs.startAgent(name);
                qtdAgentsInstantiated++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (qtdAgentsInstantiated == this.commBridge.getAgentsReceived().size()) {
            // Todos os agentes instanciados, enviando mensagem para deletar da origem
            this.commBridge.sendMsgToDeleteAllAgents();
            // Apagando Variáveis do transporte
            this.commBridge.cleanAtributesOfTransference();
            System.out.println("Terminou: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS")));
        }
    }

    private void executeInquilinismProtocol () {
        int qtdAgentsInstantiated = 0;
        for (AslTransferenceModel aslTransferenceModel : this.commBridge.getAgentsReceived()) {
            String name = aslTransferenceModel.getName();
            String path = getPath(name);
            try {
                String agClass = null;
                List<String> agArchClasses = new ArrayList<String>();
                ClassParameters bbPars = null;

                RuntimeServices rs = this.getTS().getUserAgArch().getRuntimeServices();
                name = rs.createAgent(name, path, agClass, agArchClasses, bbPars, this.getTS().getSettings(), this.getTS().getAg());
                rs.startAgent(name);
                qtdAgentsInstantiated++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (qtdAgentsInstantiated == this.commBridge.getAgentsReceived().size()) {
            // Todos os agentes instanciados, enviando mensagem para deletar da origem
            this.commBridge.sendMsgToDeleteAllAgents();
            // Apagando Variáveis do transporte
            this.commBridge.cleanAtributesOfTransference();
            System.out.println("Terminou: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS")));
        }
    }

    private String getPath(String agentName) {
        String path = "";
        for (CentralisedAgArch centralisedAgArch : RunCentralisedMAS.getRunner().getAgs().values()) {
            path = centralisedAgArch.getTS().getAg().getASLSrc();
            path = path.substring(0, path.length() - (centralisedAgArch.getAgName() + AGENT_FILE_EXTENSION).length());
            break;
        }
        path += agentName + AGENT_FILE_EXTENSION;
        return path;
    }

    @Override
    public void killAllAgents() {
        Map<String, CentralisedAgArch> agentsOfTheSMA = RunCentralisedMAS.getRunner().getAgs();
        if (this.commBridge.getAgentsReceived() != null && !this.commBridge.getAgentsReceived().isEmpty()
                && this.commBridge.getAgentsReceived().size() > 0) {
            for (CentralisedAgArch centralisedAgArch : agentsOfTheSMA.values()) {
                if (!this.commBridge.getNameAgents().contains(centralisedAgArch.getAgName())) {
                    this.getTS().getUserAgArch().getRuntimeServices().killAgent(centralisedAgArch.getAgName(),
                            this.getTS().getUserAgArch().getAgName());
                    File file = new File(centralisedAgArch.getTS().getAg().getASLSrc());
                    this.commBridge.deleteFileAsl(file);
                }
            }
        } else {
            for (CentralisedAgArch centralisedAgArch : agentsOfTheSMA.values()) {
                if (this.commBridge.getNameAgents().contains(centralisedAgArch.getAgName())) {
                    this.getTS().getUserAgArch().getRuntimeServices().killAgent(centralisedAgArch.getAgName(),
                            this.getTS().getUserAgArch().getAgName());
                }
            }
            this.commBridge.cleanAtributesOfTransference();
        }
    }


}
