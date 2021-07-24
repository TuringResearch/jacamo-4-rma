// Internal action code for project farmerSMA

package jia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class dayTime extends DefaultInternalAction {

	private static final long serialVersionUID = 1L;

	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
        ts.getAg().getLogger().info("executing internal action 'jia.dayTime'");
        
        LocalTime firstDate = LocalTime.now();
        LocalTime secondDate = LocalTime.of(19, 0);
        LocalTime thirdDate = LocalTime.of(5, 0);
        if(firstDate.isAfter(secondDate)){
            System.out.println("Afternoon");
            return false;
        }
        else if(firstDate.isBefore(thirdDate)){
            System.out.println("Morning.");
            return true;
        }
        return false;
    }
}
