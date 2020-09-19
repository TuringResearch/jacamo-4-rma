// CArtAgO artifact code for project approach2

package approach2;

import cartago.*;

public class Device extends PhysicalArtifact {
	void init() {
		defineObsProperty("resource1", 0);
		defineObsProperty("resource2", 0);
	}
	
	@Override
	protected int defineAttemptsAfterFailure() {
		return 3;
	}

	@Override
	protected String definePort() {
		// TODO Auto-generated method stub
		return "COM6";
	}

	@Override
	protected int defineWaitTimeout() {
		return 1000;
	}

	@Override
	public void onIoTAction(String arg0) {
		// TODO Auto-generated method stub
		
	}
}

