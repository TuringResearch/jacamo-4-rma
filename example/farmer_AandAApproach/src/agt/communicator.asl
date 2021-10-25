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

+!on : true <-
	.print("Asking mediator to turn on irrigator.");
	.send(mediator, achieve, on).
	
+!off : true <-
	.print("Asking mediator to turn off irrigator.");
	.send(mediator, achieve, off).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
