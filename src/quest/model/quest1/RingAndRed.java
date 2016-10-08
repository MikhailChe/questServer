package quest.model.quest1;

import quest.model.common.classes.MicroUnit;
import quest.model.common.ifaces.InputByteProcessor;

public class RingAndRed extends MicroUnit implements InputByteProcessor {
	public RingAndRed() {
		super(null, "Кольцо с Рэдом");
	}

	int proximity;
	boolean magneticLock;

	@Override
	public void processInput(byte[] data) {
		// TODO Auto-generated method stub

	}
}
