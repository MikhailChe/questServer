package quest.model.quest1;

import quest.model.common.classes.MicroUnit;
import quest.model.common.ifaces.InputByteProcessor;

public class LockerKnocker extends MicroUnit implements InputByteProcessor {
	public LockerKnocker() {
		super(null, "Стук в шкафу");
	}

	int[] accelOmeter = new int[3];
	boolean magneticLock = false;

	@Override
	public void processInput(byte[] data) {
		// TODO Auto-generated method stub

	}
}
