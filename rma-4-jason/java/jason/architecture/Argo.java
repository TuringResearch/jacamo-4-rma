package jason.architecture;

import br.pro.turing.javino.Javino;
import jason.asSyntax.Literal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Argo extends AgArch {

    public static final String DEFAULT_PORT = "COM1";

    public Javino javino = new Javino();

    private String port = "";

    long lastPerceived = 0;

    private long limit = 0;

    public Boolean blocked = true;

    public static Argo getArgoArch(AgArch currentArch) {
        if (currentArch == null) {
            return null;
        }
        if (currentArch instanceof Argo) {
            return (Argo) currentArch;
        }
        return getArgoArch(currentArch.getNextAgArch());
    }

    @Override
    public void init() throws Exception {
        super.init();
        this.setPort(DEFAULT_PORT);
    }

    @Override
    public Collection<Literal> perceive() {
        long perceiving = System.nanoTime();

        // System.out.println(perceiving + ";" + lastPerceived + "; " +
        // (perceiving - lastPerceived ));
        if (((perceiving - this.lastPerceived) < this.limit) || this.blocked) {
            // System.out.println("I'm in! " + this.blocked);
            return null;
        }
        this.lastPerceived = perceiving;

        int cont;
        List<Literal> jPercept = new ArrayList<Literal>();
        // Boolean hasMsg =
        // this.jBridge.requestData("","getPercepts");//this.jBridge.listenArduino("");
        // if (hasMsg) {
        if (this.javino.requestData(this.port, "getPercepts")) {
            String rwPercepts = this.javino.getData();
            String[] perception = rwPercepts.split(";");
            //if (rwPercepts != null) {
            // System.out.println("[javino] msg: ok");
            // ********* log middleware Pantoja
            // this.logStatus = "ok";
            //}
            // System.out.println("[javino] msg: " + rwPercepts);
            for (cont = 0; cont <= perception.length - 1; cont++) {
                jPercept.add(Literal.parseLiteral(perception[cont]));
                // System.out.println("[javino] array: " + perception[cont]);
            }
            // System.out.println("[javino]: " +jPercept.toString());
            // percepts.add(jPercept);
        } //else {
        // ********* log middleware Pantoja
        // this.logStatus = "error";
        //}
        return jPercept;
    }

    public Javino getJavino() {
        return this.javino;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public long getLastPerceived() {
        return this.lastPerceived;
    }

    public void setLastPerceived(long lastPerceived) {
        this.lastPerceived = lastPerceived;
    }

    public long getLimit() {
        return this.limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public Boolean getBlocked() {
        return this.blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }
}
