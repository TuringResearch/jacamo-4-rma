// CArtAgO artifact code for project approach2

package artifact;

import cartago.*;

public class Device extends PhysicalArtifact {
	void init() {
		defineObsProperty("light", "");
		defineObsProperty("humidity", "");
		defineObsProperty("temperature", 0);
		defineObsProperty("ph", 0);
	}
	
	@Override
	protected int defineAttemptsAfterFailure() {
		return 3;
	}

	@Override
	protected String definePort() {
		return "COM3";
	}

	@Override
	protected int defineWaitTimeout() {
		return 1000;
	}
}

