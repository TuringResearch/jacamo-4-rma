package jason.stdlib;

import br.pro.turing.rma.core.model.Data;
import br.pro.turing.rma.core.model.Device;
import br.pro.turing.rma.core.model.Resource;
import br.pro.turing.rma.core.service.ServiceManager;
import jason.architecture.AgArch;
import jason.architecture.Communicator;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import jason.bb.BeliefBase;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static jason.architecture.CommunicatorUtils.getCommunicatorArch;

public class sendToRml extends DefaultInternalAction {

    private void sendDataToRml(Communicator communicator, Data data) {
        if (communicator.isConnected() && data != null) {
            ArrayList<Data> dataList = new ArrayList<>();
            dataList.add(data);
            Message message = new ApplicationMessage();
            message.setContentObject(ServiceManager.getInstance().jsonService.toJson(dataList));
            try {
                communicator.getConnection().sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
        Communicator communicator = getCommunicatorArch(ts.getUserAgArch());
        if (communicator != null) {
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
                ts.getLogger().warning("[WARNING] [sentToRml] No beliefs is a Resource's Data.");
            } else if (!resourceLiteralMap.isEmpty()) {
                resourceLiteralMap.forEach((resource, data) -> {
                    this.sendDataToRml(communicator, data);
                });
            }
            return true;
        } else {
            ts.getLogger().warning(
                    "[WARNING] Was not possible to call .sendToRml internal action because this AgArch is not a Communicator.");
            return false;
        }
    }
}
