package jason.architecture;

import br.pro.turing.javino.Javino;
import jason.asSyntax.Literal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Argo extends AgArch {

    public static void main(String[] args) {
        String os = System.getProperty("os.name");
        System.out.println(os);
    }
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

        if (((perceiving - this.lastPerceived) < this.limit) || this.blocked) {
            return null;
        }
        this.lastPerceived = perceiving;

        int cont;
        List<Literal> jPercept = new ArrayList<Literal>();
        try {
            if (this.javino.requestData(this.port, "getPercepts")) {
                String rwPercepts = this.javino.getData();
                String[] perception = rwPercepts.split(";");
                for (cont = 0; cont < perception.length; cont++) {
                    jPercept.add(Literal.parseLiteral(perception[cont]));
                }
            }
            for (Literal literal : jPercept) {
                this.getTS().getLogger().info(literal.toString());
            }
            return jPercept;
        } catch (Exception e) {
            return null;
        }
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
