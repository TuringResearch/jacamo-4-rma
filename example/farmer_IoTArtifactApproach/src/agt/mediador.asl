/* Initial beliefs and rules */
irrigator(off).

/* Initial goals */

!irrigate.

/* Plans */

// irrigate
+!irrigate : (temperature >= 35 | humidity(dry) | action.isAfternon) & action.isRaining == false & irrigator(off) <-
	!on;
	!irrigate.
+!irrigate : (temperature <= 10 | humidity(wet) | action.isRaining) & irrigator(on) <-
	!off;
	!irrigate.
-!irrigate <-
	!irrigate.
	
	
// on 
+!on : true <-
	act(on);
	-irrigator(off);
	+irrigator(on).
	
// off 
+!off : true <-
	act(off);
	-irrigator(on);
	+irrigator(off).
	
{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
