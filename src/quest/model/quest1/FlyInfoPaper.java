package quest.model.quest1;

import quest.model.common.classes.MicroUnit;
import quest.model.common.ifaces.InputByteProcessor;

public class FlyInfoPaper extends MicroUnit implements InputByteProcessor {
	public FlyInfoPaper() {
		super(null, "Мухи и свиток");
	}

	boolean photoresistor = false;
	boolean magneticLock = false;

	public void infoPaperProcess(byte[] data) {
		// ....
	}

	@Override
	public void processInput(byte[] data) {
		// TODO Auto-generated method stub

	}
}