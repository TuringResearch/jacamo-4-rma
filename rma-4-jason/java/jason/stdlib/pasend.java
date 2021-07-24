package jason.stdlib;

import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class pasend extends send {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        ts.getLogger().info(";Send;" + System.nanoTime());
        return super.execute(ts, un, args);
    }
}
