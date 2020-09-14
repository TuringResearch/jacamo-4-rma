package jason.stdlib;

import jason.architecture.Communicator;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;

public class connectToRml extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        super.execute(ts, un, args);
        Communicator communicator = Communicator.getCommunicatorArch(ts.getUserAgArch());
        if (communicator != null) {
            final String gatewayIp = args[0].toString();
            final int gatewayPort = Integer.parseInt(args[1].toString());
            communicator.connect(gatewayIp, gatewayPort);
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
