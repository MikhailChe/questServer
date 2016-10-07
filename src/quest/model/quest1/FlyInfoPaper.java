package quest.model.quest1;

import java.net.SocketAddress;

import quest.model.common.classes.MicroUnit;
import quest.model.common.ifaces.InputByteProcessor;

public class FlyInfoPaper extends MicroUnit implements InputByteProcessor {
	public FlyInfoPaper(SocketAddress addr) {
		super(addr);
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