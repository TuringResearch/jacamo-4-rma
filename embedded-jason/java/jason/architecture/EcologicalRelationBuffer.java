package jason.architecture;

import jason.AslTransferenceModel;

import java.util.List;

public class EcologicalRelationBuffer {

    private String token;

    private EcologicalRelationType relationType;

    private TransferenceActionType actionType;

    private List<AslTransferenceModel> agentsToGiveBirth;

    private List<AslTransferenceModel> agentsToKill;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public EcologicalRelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(EcologicalRelationType relationType) {
        this.relationType = relationType;
    }

    public TransferenceActionType getActionType() {
        return actionType;
    }

    public void setActionType(TransferenceActionType actionType) {
        this.actionType = actionType;
    }

    public List<AslTransferenceModel> getAgentsToGiveBirth() {
        return agentsToGiveBirth;
    }

    public void setAgentsToGiveBirth(List<AslTransferenceModel> agentsToGiveBirth) {
        this.agentsToGiveBirth = agentsToGiveBirth;
    }

    public List<AslTransferenceModel> getAgentsToKill() {
        return agentsToKill;
    }

    public void setAgentsToKill(List<AslTransferenceModel> agentsToKill) {
        this.agentsToKill = agentsToKill;
    }
}
