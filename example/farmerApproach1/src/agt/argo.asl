// Agent sample_agent in project farmerSMA

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- 
	.port(COM3);
	.percepts(open).

+light(T) <-
	.pasend(communicator, tell, light(T));
	.
	
+openperception <-
	.percepts(open);
	.
	
{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moiseJar/asl/org-obedient.asl") }
