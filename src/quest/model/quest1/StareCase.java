package quest.model.quest1;

import quest.model.common.classes.MicroUnit;
import quest.model.common.ifaces.InputByteProcessor;

public class StareCase extends MicroUnit implements InputByteProcessor {
	public StareCase() {
		super(null, "Лестница");
	}

	int[][] weights;
	boolean capcitiveSensor;
	boolean magneticLock;

	@Override
	public void processInput(byte[] data) {
		// TODO Auto-generated method stub

	}
}
