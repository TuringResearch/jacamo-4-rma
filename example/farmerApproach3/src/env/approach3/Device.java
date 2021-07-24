package approach3;

import cartago.*;

public class Device extends PhysicalArtifact {
	void init() {
		enableIoT(System.getProperty("user.dir") + "\\src\\env\\approach3", "127.0.0.1", 5500);
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

