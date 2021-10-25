// Agent sample_agent in project farmerSMA

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- 
	.port(COM3);
	.percepts(open).

+light(T) <-
	.percepts(block);
	.send(mediator, tell, light(T));
	.percepts(open).
	
+humidity(T) <-
	.percepts(block);
	.send(mediator, tell, humidity(T));
	.percepts(open).

+temperature(T) <-
	.percepts(block);
	.send(mediator, tell, temperature(T));
	.percepts(open).

+pH(T) <-
	.percepts(block);
	.send(mediator, tell, pH(T));
	.percepts(open).

+!on : true <-
	.print("Turning irrigator ON.");
	.act(on);
	.wait(1000);
	!off.
	
+!off : true <-
	.print("Turning irrigator OFF.");
	.act(off).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
