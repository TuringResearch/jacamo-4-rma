package protocol.ecologicalrelation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Tipos de protocolo de transferência de agentes entre SMA distintos.
 */
public enum EcologicalRelationType implements Serializable {

    PREDATOR(1, "PREDATOR"),
    MUTUALISM(2, "MUTUALISM"),
    INQUILINISM(3, "INQUILINISM"),

    CAN_TRANSFER(4, "CAN TRANSFER THE AGENT(S)"),
    CAN_KILL(5, "CAN KILL THE AGENT(S)");

    /** Serial version ID for serialization. */
    private static final long serialVersionUID = 1L;

    /** Class name to be used by JSON strings. */
    @JsonIgnore
    private final String className = getClass().getName();

    int id;
    String name;

    EcologicalRelationType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static EcologicalRelationType getByName(String name) {
        for (EcologicalRelationType value : values()) {
            if (value.name.equals(name.toUpperCase())) {
                return value;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
