/* Initial beliefs and rules */

/* Initial goals */

!start.
!light.
!humidity.
!temperature.
!pH.

/* Plans */

+!start : true <- 
	percepts;
	.wait(1000);
	!start.

+!humidity : humidity == "wet" & jia.irrigation("Hey") | T == "Dry" & jia.isRaining("Hey") == false <-	
	!on;
	!humidity.

+!humidity : humidity == "dry" & jia.isRaining("Hey") <-
	.send(communicator, tell, humidity(humi));
	!off;
	!humidity.

+!on : true <-
	.print("Asking physical artifact to turn on the irrigator.");
	act(on).
	
+!off : true <-
	.print("Asking physical artifact to turn off the irrigator.");
	act(off).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moiseJar/asl/org-obedient.asl") }
