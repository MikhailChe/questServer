package quest.model.quest1;

import quest.model.ifaces.InputByteProcessor;

public class AlarmClock implements InputByteProcessor {
	boolean clockArmed = false;
	boolean magneticLock = false;

	@Override
	public void processInput(byte[] data) {
		// TODO Auto-generated method stub

	}
}
