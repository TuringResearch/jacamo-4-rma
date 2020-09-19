package approach3;

import java.util.ArrayList;

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
		return "COM6";
	}

	@Override
	protected int defineWaitTimeout() {
		return 1000;
	}

	@Override
	public void onIoTAction(String arg0) {
		act(arg0);
	}
}

