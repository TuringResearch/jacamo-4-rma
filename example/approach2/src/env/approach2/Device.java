// CArtAgO artifact code for project approach2

package approach2;

import cartago.*;

public class Device extends PhysicalArtifact {
	void init() {
		defineObsProperty("resource1", "Off");
		defineObsProperty("resource2", "Off");
	}

	@OPERATION
	void perceptsFromArtifact() {
		String[] perceptions = super.percepts();
		for (String perception : perceptions) {
			String[] datas = perception.replace(")", "").split("\\(");
			ObsProperty prop = getObsProperty(datas[0]);
			if (prop != null) {
				int v = Integer.parseInt(datas[1]);
				prop.updateValue(v == 0 ? "Off" : v == 1 ? "On" : "null");
			}
		}
	}
	
	@OPERATION
	void command1() {
		send("command1");
	}
	
	@OPERATION
	void command2() {
		send("command2");
	}
	
	@OPERATION
	void command3() {
		send("command3");
	}
	
	@OPERATION
	void command4() {
		send("command4");
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
}

