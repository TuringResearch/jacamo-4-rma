package protocol.ecologicalrelation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.List;

public class EcologicalRelationBuffer implements Serializable {

    /** Serial version ID for serialization. */
    private static final long serialVersionUID = 1L;

    /** Class name to be used by JSON strings. */
    @JsonIgnore
    private final String className = getClass().getName();

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
