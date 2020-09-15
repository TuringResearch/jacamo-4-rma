package jason.stdlib;

import br.pro.turing.rma.core.model.Data;
import br.pro.turing.rma.core.model.Device;
import br.pro.turing.rma.core.model.Resource;
import jason.architecture.Communicator;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import jason.bb.BeliefBase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class sendToRml extends DefaultInternalAction {

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        super.execute(ts, un, args);
        Communicator communicator = Communicator.getCommunicatorArch(ts.getUserAgArch());
        if (communicator != null) {
            final boolean[] hasResourceData = {false};
            final BeliefBase bb = ts.getAg().getBB();
            bb.forEach(literal -> {
                String resourceName = literal.getFunctor();
                for (Resource resource : communicator.getDevice().getResourceList()) {
                    if (resource.getResourceName().equals(resourceName)) {
                        String value = literal.getTerm(0).toString();
                        final Device device = communicator.getDevice();
                        System.err.println("Enviando...");
                        communicator.sendToRml(
                                new Data(LocalDateTime.now(), device.getDeviceName(), resourceName, value));
                        ts.getAg().getBB().remove(literal);
                        System.err.println("Enviado!");
                        hasResourceData[0] = true;
                        break;
                    }
                }
            });

            if (!hasResourceData[0]) {
                ts.getLogger().warning("No beliefs is a Resource's Data");
            }
            return true;
        } else {
            ts.getLogger().warning(
                    "Was not possible to call .sendToRml internal action because this AgArch is not a Communicator.");
            return false;
        }
    }
}
