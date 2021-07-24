package jason.stdlib;

import br.pro.turing.rma.core.model.Data;
import br.pro.turing.rma.core.model.Device;
import br.pro.turing.rma.core.model.Resource;
import jason.architecture.Communicator;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import jason.bb.BeliefBase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
            ts.getLogger().info(";SendToRML;in;" + System.nanoTime());
            Map<Resource, Data> resourceLiteralMap = new HashMap<>();
            final BeliefBase bb = ts.getAg().getBB();

            // Converting beliefs into resources' Data if it is possible.
            bb.forEach(literal -> {
                String beliefFunctor = literal.getFunctor();
                for (Resource resource : communicator.getDevice().getResourceList()) {
                    if (resource.getResourceName().equals(beliefFunctor)) {
                        if (literal.getTerms().size() > 0 && literal.getTerm(0) != null) {
                            // As the bb is organized in a stack structure, I am ignoring beliefs that represents
                            // resources already added in the map.
                            if (!resourceLiteralMap.containsKey(resource)) {
                                String value = literal.getTerm(0).toString();
                                final Device device = communicator.getDevice();
                                final Data data = new Data(LocalDateTime.now(), device.getDeviceName(),
                                        resource.getResourceName(), value);
                                resourceLiteralMap.put(resource, data);
                            } else {
                                // Removing old belief from a resource.
                                ts.getAg().getBB().remove(literal);
                            }
                            break;
                        }
                    }
                }
            });

            if (bb.size() > 0 && resourceLiteralMap.isEmpty()) {
                ts.getLogger().warning("[sentToRml] No beliefs is a Resource's Data.");
            } else if (!resourceLiteralMap.isEmpty()) {
                resourceLiteralMap.forEach((resource, data) -> {
                    communicator.sendToRml(data);
                });
                ts.getLogger().info(";SendToRML;out;" + System.nanoTime());
            }
            return true;
        } else {
            ts.getLogger().warning(
                    "Was not possible to call .sendToRml internal action because this AgArch is not a Communicator.");
            return false;
        }
    }
}
