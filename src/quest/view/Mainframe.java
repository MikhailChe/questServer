package quest.view;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Mainframe {

	private JFrame frame;

	public Mainframe(String str) {
		this.frame = new JFrame(str);
		this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.frame.setPreferredSize(new Dimension(800, 600));
		this.frame.setSize(this.frame.getPreferredSize());
		this.frame.setLocationRelativeTo(null);
	}

	public void setContentPane(JPanel panel) {

		JScrollPane pane = new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.frame.setContentPane(pane);
	}

	public void showMe() {
		SwingUtilities.invokeLater(() -> {
			this.frame.validate();
			this.frame.setVisible(true);
		});
	}

}
