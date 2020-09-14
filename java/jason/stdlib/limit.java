// Internal action code for project javinoArchitectureWithInternalActions

//If you want only to use '.move' command type 'package jason.stdlib'. By Pantoja.
package jason.stdlib;

import jason.architecture.Argo;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;

public class limit extends DefaultInternalAction {

    private static final long serialVersionUID = -4841692752581197132L;

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        final Argo argoArch = Argo.getArgoArch(ts.getUserAgArch());
        if (argoArch != null) {
            if (args[0].isNumeric()) {
                argoArch.setLimit(Long.valueOf(args[0] + "000000"));
                return true;
            } else {
                return false;
            }
        } else {
            ts.getLogger().warning(
                    "Was not possible to call .limit internal action because this AgArch is not an Argo.");
            return false;
        }
    }
}
