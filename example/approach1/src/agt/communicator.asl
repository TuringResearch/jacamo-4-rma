/* Initial beliefs and rules */

/* Initial goals */
!start.

/* Plans */
+!start : true <- 
	.print("Connecting to RML");
	.connectToRml('127.0.0.1', 5500);
	!iotObjectCycle.
	
+!iotObjectCycle : true <-
	.sendToRml;
	.wait(1000);
	!iotObjectCycle.

+!command1 : true <-
	.print("Running the command 1.");
	.send(argoCOM1, achieve, command1).
	
+!command2 : true <-
	.print("Running the command 2.");
	.send(argoCOM1, achieve, command2).
		
+!command3 : true <-
	.print("Running the command 3.");
	.send(argoCOM1, achieve, command3).
		
+!command4 : true <-
	.print("Running the command 4.");
	.send(argoCOM1, achieve, command4).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moiseJar/asl/org-obedient.asl") }
