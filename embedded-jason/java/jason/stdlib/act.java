// Internal action code for project javinoArchitectureWithInternalActions

//If you want only to use '.move' command type 'package jason.stdlib'. By Pantoja.
package jason.stdlib;

import jason.architecture.Argo;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;

public class act extends DefaultInternalAction {

    private static final long serialVersionUID = -4841692752581197132L;

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) {
        final Argo argoArch = Argo.getArgoArch(ts.getUserAgArch());
        if (argoArch != null) {
            Term action = args[0];
            if (argoArch.getJavino().sendCommand(argoArch.getPort(), action.toString())) {
                return true;
            } else {
                ts.getLogger().info("[INFO] act did not work.");
                return false;
            }
        } else {
            ts.getLogger().warning(
                    "[WARNING] Was not possible to call .act internal action because this AgArch is not an Argo.");
            return false;
        }
    }
}
