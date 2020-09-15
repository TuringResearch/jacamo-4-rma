// Agent argo in project approach1

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

//+!start : true <- 
//	.port(COM1);
//	.percepts(open).

+resource1(T) <-
	.send(communicator, tell, resource1(T)).

+resource2(T) <-
	.send(communicator, tell, resource2(T)).

+!command1 : true <-
	.print("Running the command 1.");
	.act(command1).
	
+!command2 : true <-
	.print("Running the command 2.");
	.act(command2).
		
+!command3 : true <-
	.print("Running the command 3.");
	.act(command3).
		
+!command4 : true <-
	.print("Running the command 4.");
	.act(command4).
	
{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moiseJar/asl/org-obedient.asl") }
