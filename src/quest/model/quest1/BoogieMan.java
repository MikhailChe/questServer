package quest.model.quest1;

import quest.model.common.classes.MicroUnit;
import quest.model.common.ifaces.InputByteProcessor;

public class BoogieMan extends MicroUnit implements InputByteProcessor {
	public BoogieMan() {
		super(null, "Храп Бугимена");
	}

	boolean servo;
	boolean mp3;

	@Override
	public void processInput(byte[] data) {
		// TODO Auto-generated method stub

	}
}
