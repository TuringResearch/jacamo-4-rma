/* Initial beliefs and rules */
irrigator(off).

/* Initial goals */
!light.
!humidity.
!temperature.
!pH.
!irrigate.

/* Plans */

// light 
+!light : light(noLight) & action.isMorning <-
	.send(communicator, tell, light(without_ligh_during_the_day));
	!light;
	.
+!light : light(withLight) & action.isAfternoon <-
	.send(communicator, tell, light(light_during_the_night));
	!light;
	.
-!light <-
	!light;
	.
	
// humidity
+!humidity : humidity(dry) <-	
	.send(communicator, tell, humidity(dry_environment));
	!humidity;
	.
+!humidity : humidity(wet) <-
	.send(communicator, tell, humidity(wet_environment));
	!humidity;
	.
-!humidity <-
	!humidity;
	.
	
	
// temperature
+!temperature : temperature <= 10 <-
	.send(communicator, tell, temperature(too_cold_for_plants));
	!temperature;
	.
+!temperature : temperature >= 35 <-
	.send(communicator, tell, temperature(too_hot_for_plants));
	!temperature;
	.
-!temperature <-
	!temperature;
	.
	
	
// pH
+!pH : ph < 5 <-
	.send(communicator, tell, pH(ph_level_too_Low));
	!pH;
	.
+!pH : ph > 5.8 <-
	.send(communicator, tell, pH(ph_level_too_High))
	!pH.
-!pH <-
	!pH.
	

// irrigate
+!irrigate : (temperature >= 35 | humidity(dry) | action.isAfternon) & action.isRaining == false & irrigator(off) <-
	!on;
	!irrigate.
+!irrigate : (temperature <= 10 | humidity(wet) | action.isRaining) & irrigator(on) <-
	!off;
	!irrigate.
-!irrigate <-
	!irrigate.
	
	
// on 
+!on : true <-
	.print("Asking argo to turn on the irrigator.");
	.send(argo, achieve, on);
	.send(communicator, tell, irrigator(on));
	-irrigator(off);
	+irrigator(on).
	
// off 
+!off : true <-
	.print("Asking argo to turn off the irrigator.");
	.send(argo, achieve, off);
	.send(communicator, tell, irrigator(off));
	-irrigator(on);
	+irrigator(off).
	
{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }