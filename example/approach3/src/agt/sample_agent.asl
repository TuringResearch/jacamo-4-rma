/* Initial beliefs and rules */

/* Initial goals */
!start.

/* Plans */
+!start : true <- 
	percepts;
	//.wait(1000);
	!start.
	
+!command1 : true <-
	act(command1).
	
+!command2 : true <-
	act(command2).
		
+!command3 : true <-
	act(command3).
		
+!command4 : true <-
	act(command4).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moiseJar/asl/org-obedient.asl") }
