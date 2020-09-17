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
	.send(argo, achieve, command1).
	
+!command2 : true <-
	.send(argo, achieve, command2).
		
+!command3 : true <-
	.send(argo, achieve, command3).
		
+!command4 : true <-
	.send(argo, achieve, command4).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moiseJar/asl/org-obedient.asl") }
