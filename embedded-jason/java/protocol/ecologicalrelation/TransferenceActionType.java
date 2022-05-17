package protocol.ecologicalrelation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public enum TransferenceActionType implements Serializable {
    GIVE_BIRTH,
    KILL,
    KILL_AND_GIVE_BIRTH;

    /** Serial version ID for serialization. */
    private static final long serialVersionUID = 1L;

    /** Class name to be used by JSON strings. */
    @JsonIgnore
    private final String className = getClass().getName();
}
