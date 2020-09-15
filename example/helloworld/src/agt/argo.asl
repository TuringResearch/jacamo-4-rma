// Agent argo in project helloworld

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- 
	.random(R1);
	.send(communicator, tell, resource1(R1));
	.random(R2);
	.send(communicator, tell, resource2(R2));
	.print("argo executando");
	.wait(1000);
	!start.

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moiseJar/asl/org-obedient.asl") }
