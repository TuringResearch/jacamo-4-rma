package protocol.ecologicalrelation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Modelo de transferência de agente.
 */
public class AslTransferenceModel implements Serializable {

    /** Serial version ID for serialization. */
    private static final long serialVersionUID = 1L;

    /** Class name to be used by JSON strings. */
    @JsonIgnore
    private final String className = getClass().getName();

    /** Nome do agente. */
    private String name;

    /** Conteúdo do arquivo. */
    private byte[] fileContent;

    /**
     * Construtor.
     *
     * @param name {@link #name}
     * @param fileContent {@link #fileContent}
     */
    public AslTransferenceModel(String name, byte[] fileContent) {
        this.name = name;
        this.fileContent = fileContent;
    }

    public String getName() {
        return name;
    }

    public byte[] getFileContent() {
        return fileContent;
    }
}
