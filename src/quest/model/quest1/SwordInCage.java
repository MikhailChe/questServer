package quest.model.quest1;

import quest.model.common.classes.MicroUnit;
import quest.model.common.ifaces.InputByteProcessor;

public class SwordInCage extends MicroUnit implements InputByteProcessor {
	public SwordInCage() {
		super(null, "Меч в клетке");
	}

	boolean photoDiode;
	boolean magneticLock;

	@Override
	public void processInput(byte[] data) {
		// TODO Auto-generated method stub

	}
}
