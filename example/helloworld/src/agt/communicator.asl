/* Initial beliefs and rules */

/* Initial goals */
!start.

/* Plans */
+!start : true <- 
	.print("Connecting to RML");
	.connectToRml('127.0.0.1', 5500);
	!rmlCycle.
	
+!rmlCycle : true <-
	.sendToRml;
	.wait(1000);
	!rmlCycle.

+!command1 : true <-
	.print("Running the command 1.").
	
+!command2 : true <-
	.print("Running the command 2.").
	
+!command3 : true <-
	.print("Running the command 3.").
	
+!command4 : true <-
	.print("Running the command 4.").

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moiseJar/asl/org-obedient.asl") }
