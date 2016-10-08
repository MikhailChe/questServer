package quest.view;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Mainframe {

	private JFrame frame;

	public Mainframe(String str) {
		frame = new JFrame("Квест");
	}

	public void setContentPane(JPanel panel) {
		this.frame.setContentPane(panel);
	}

}
