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
	.print("Asking argo to turn on the irrigator.");
	.send(argo, achieve, on);
	.send(communicator, tell, irrigator(on));
	-irrigator(off);
	+irrigator(on).
	
// off 
+!off : true <-
	.print("Asking argo to turn off the irrigator.");
	.send(argo, achieve, off);
	.send(communicator, tell, irrigator(off));
	-irrigator(on);
	+irrigator(off).
	
{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
