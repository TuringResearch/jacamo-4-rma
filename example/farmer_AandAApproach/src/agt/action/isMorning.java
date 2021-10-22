// Internal action code for project farmerSMA

package action;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class isMorning extends DefaultInternalAction {

	private static final long serialVersionUID = 1L;

	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		LocalTime firstDate = LocalTime.now();
        LocalTime secondDate = LocalTime.of(5, 0);
        return firstDate.isBefore(secondDate);
    }
}
