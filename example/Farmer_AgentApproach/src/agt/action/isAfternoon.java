package action;

import java.time.LocalTime;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;

public class isAfternoon extends DefaultInternalAction {

	private static final long serialVersionUID = 1L;

	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        LocalTime firstDate = LocalTime.now();
        LocalTime secondDate = LocalTime.of(19, 0);
        return firstDate.isAfter(secondDate);
    }
}