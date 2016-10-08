package quest.model.quest1;

import quest.model.common.classes.MicroUnit;
import quest.model.common.ifaces.InputByteProcessor;

public class AlarmClock extends MicroUnit implements InputByteProcessor {
	public AlarmClock() {
		super(null, "Будильник");
	}

	boolean clockArmed = false;
	boolean magneticLock = false;

	@Override
	public void processInput(byte[] data) {
		// TODO Auto-generated method stub

	}
}
