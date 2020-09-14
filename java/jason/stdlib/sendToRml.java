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
            List<Literal> nonResourceDataList = new ArrayList<>();
            final BeliefBase bb = ts.getAg().getBB();
            bb.forEach(literal -> {
                String resourceName = literal.getFunctor();
                boolean isResourceData = false;
                for (Resource resource : communicator.getDevice().getResourceList()) {
                    if (resource.getResourceName().equals(resourceName)) {
                        String value = literal.getTerm(0).toString();
                        final Device device = communicator.getDevice();
                        communicator.sendToRml(
                                new Data(LocalDateTime.now(), device.getDeviceName(), resourceName, value));
                        ts.getAg().getBB().remove(literal);
                        isResourceData = true;
                        break;
                    }
                }
                if (!isResourceData) {
                    nonResourceDataList.add(literal);
                }
            });

            if (!nonResourceDataList.isEmpty()) {
                mountNonResourcesDataLog(ts, nonResourceDataList);
                return false;
            }
            return true;
        } else {
            ts.getLogger().warning(
                    "Was not possible to call .sendToRml internal action because this AgArch is not a Communicator.");
            return false;
        }
    }

    private void mountNonResourcesDataLog(TransitionSystem ts, List<Literal> nonResourceDataList) {
        StringBuilder nonResourcesData = new StringBuilder();
        nonResourceDataList.forEach(literal -> {
            if (nonResourceDataList.indexOf(literal) == nonResourceDataList.size() - 1) {
                nonResourcesData.append(literal.toString());
            } else {
                nonResourcesData.append(literal.toString() + ", ");
            }
        });
        ts.getLogger().warning("The beliefs are not Resources' Data: " + nonResourceDataList);
    }
}
