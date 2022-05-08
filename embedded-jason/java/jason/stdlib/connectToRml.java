package jason.stdlib;

import jason.architecture.Communicator;
import jason.architecture.CommunicatorUtils;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

import static jason.architecture.CommunicatorUtils.getCommunicatorArch;

public class connectToRml extends DefaultInternalAction {

    /**
     * Connects to the RML.
     *
     * @param gatewayIP   Gateway IP.
     * @param gatewayPort Gateway port.
     */
    private void connect(Communicator communicator, String gatewayIP, int gatewayPort) throws IOException {
        InetSocketAddress gatewayAddress = new InetSocketAddress(gatewayIP, gatewayPort);
        final String uuid = CommunicatorUtils.getUUIDFromFile();
        communicator.setConnection(uuid.isEmpty() ? new MrUdpNodeConnection() : new MrUdpNodeConnection(UUID.fromString(uuid)));
        communicator.getConnection().addNodeConnectionListener(communicator);
        communicator.getConnection().connect(gatewayAddress);
    }

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        super.execute(ts, un, args);
        Communicator communicator = getCommunicatorArch(ts.getUserAgArch());
        if (communicator != null) {
            final String gatewayIp = args[0].toString();
            final int gatewayPort = Integer.parseInt(args[1].toString());
            this.connect(communicator, gatewayIp, gatewayPort);
            ts.getLogger().warning("Connected to RML.");
            return true;
        } else {
            ts.getLogger().warning(
                    "Was not possible to call .connectToRml internal action because this AgArch is not a Communicator"
                            + ".");
            return false;
        }
    }
}
