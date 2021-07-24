package jason.architecture;

import br.pro.turing.javino.Javino;
import jason.asSyntax.Literal;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Argo extends AgArch {
    public static final String DEFAULT_PORT = "COM1";

//    public Javino javino = new Javino();

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

    int c = 0;

    @Override
    public Collection<Literal> perceive() {
        long perceiving = System.nanoTime();

        if (((perceiving - this.lastPerceived) < this.limit) || this.blocked) {
            return null;
        }
        blocked = true;
        this.lastPerceived = perceiving;

        getTS().getLogger().info(";Percept;" + System.nanoTime());
        List<Literal> jPercept = new ArrayList<Literal>();


        String luzValor = new Random().nextBoolean() ? "withLight" : "noLight";
        final int i = new Random().nextInt(4);
        String umidadeValor = i == 0 ? "dry" : i == 1 ? "wet" : "ideal";
        String rwPercepts =
                "light(" + (c++) + ");";
        String[] perception = rwPercepts.split(";");
        int cont;
        for (cont = 0; cont < perception.length; cont++) {
            jPercept.add(Literal.parseLiteral(perception[cont]));
        }
        return jPercept;
    }

//    public Javino getJavino() {
//        return this.javino;
//    }

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
