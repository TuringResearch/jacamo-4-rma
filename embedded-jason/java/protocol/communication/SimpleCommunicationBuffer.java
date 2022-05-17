package protocol.communication;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class SimpleCommunicationBuffer implements Serializable {

    /** Serial version ID for serialization. */
    private static final long serialVersionUID = 1L;

    /** Class name to be used by JSON strings. */
    @JsonIgnore
    private final String className = getClass().getName();

    private String ilForce;
    private String sender;
    private String receiver;
    private String content;

    public SimpleCommunicationBuffer() {
    }

    public String getIlForce() {
        return ilForce;
    }

    public void setIlForce(String ilForce) {
        this.ilForce = ilForce;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
