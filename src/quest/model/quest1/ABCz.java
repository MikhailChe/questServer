package quest.model.quest1;

import quest.model.common.classes.MicroUnit;
import quest.model.common.ifaces.InputByteProcessor;

public class ABCz extends MicroUnit implements InputByteProcessor {
	public ABCz() {
		super(null, "Азбука");
	}

	boolean[] sensors;
	boolean mp3Player;

	boolean magneticLock;

	@Override
	public void processInput(byte[] data) {
		// TODO Auto-generated method stub

	}
}
