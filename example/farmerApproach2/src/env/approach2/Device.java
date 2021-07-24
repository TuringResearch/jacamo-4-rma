// CArtAgO artifact code for project approach2

package approach2;

import cartago.*;

public class Device extends PhysicalArtifact {
	void init() {
		defineObsProperty("light", "");
	}
	
	@Override
	protected int defineAttemptsAfterFailure() {
		return 3;
	}

	@Override
	protected String definePort() {
		// TODO Auto-generated method stub
		return "COM3";
	}

	@Override
	protected int defineWaitTimeout() {
		return 1000;
	}
}
