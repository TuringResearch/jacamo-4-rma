// Internal action code for project javinoArchitectureWithInternalActions

//If you want only to use '.move' command type 'package jason.stdlib'. By Pantoja.
package jason.stdlib;

import jason.architecture.Argo;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;

public class port extends DefaultInternalAction {

    private static final long serialVersionUID = -4841692752581197132L;

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) {
        final Argo argoArch = Argo.getArgoArch(ts.getUserAgArch());
        if (argoArch != null) {
            Term illoc = args[0];
            String os = System.getProperty("os.name");
            if (os.substring(0, 1).equals("W")) {
                argoArch.setPort(illoc.toString());
            } else {
                argoArch.setPort("/dev/" + illoc.toString());
            }
            return true;
        } else {
            ts.getLogger().warning(
                    "Was not possible to call .port internal action because this AgArch is not an Argo.");
            return false;
        }
    }
}
