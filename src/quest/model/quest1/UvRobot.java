package quest.model.quest1;

import quest.model.common.ifaces.InputByteProcessor;

public class UvRobot implements InputByteProcessor {
	public boolean button;
	public boolean[] uvDiode = new boolean[2];
	@Override
	public void processInput(byte[] data) {
		// TODO Auto-generated method stub
		
	}
}
