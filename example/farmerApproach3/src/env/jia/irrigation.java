// Internal action code for project farmerSMA

package jia;

import java.time.LocalTime;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class irrigation extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
        ts.getAg().getLogger().info("executing internal action 'jia.irrigation'");
        
        LocalTime firstDate = LocalTime.now();
        LocalTime secondDate = LocalTime.of(8, 0);
        LocalTime thirdDate = LocalTime.of(20, 0);
        if(firstDate.isBefore(secondDate) && firstDate.isAfter(secondDate.minusMinutes(1))){
            System.out.println("Time to irrigate - morning.");
            return true;
        }
        else if(firstDate.isBefore(thirdDate) && firstDate.isAfter(thirdDate.minusMinutes(1))){
            System.out.println("Time to irrigate - afternoon.");
            return true;
        }
        return false;
    }
}
