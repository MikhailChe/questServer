package quest.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Mainframe {

	private JFrame frame;

	public Mainframe(String str) {
		frame = new JFrame("Квест");
		this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public void setContentPane(JPanel panel) {

		JScrollPane pane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.frame.setContentPane(pane);
	}

	public void showMe() {
		SwingUtilities.invokeLater(() -> {
			this.frame.pack();
			this.frame.validate();
			this.frame.setVisible(true);
		});
	}

}
