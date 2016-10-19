package quest.view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import quest.model.common.classes.MicroUnit;
import quest.model.common.classes.fields.Property;

public class UneditablePropertyGroupGUI extends JPanel {
	private static final long serialVersionUID = -9168576951960928929L;

	boolean horizontal;
	List<Property> props;

	List<AbstractButton> buttons;

	public UneditablePropertyGroupGUI(List<Property> props, boolean horizontal, MicroUnit unit) {
		this.horizontal = horizontal;
		this.props = props;

		this.buttons = new ArrayList<>();

		for (Property prop : props) {
			AbstractButton cb = new JCheckBox();
			this.buttons.add(cb);
			unit.addPropertyChangeListener((e) -> {
				if (prop.getValue() != null)
					if (prop.getValue() instanceof Boolean)
						cb.setSelected((boolean) prop.getValue());
			});
		}

		SwingUtilities.invokeLater(this::createAndShowGUI);
	}

	private void createAndShowGUI() {
		if (this.horizontal) {
			setLayout(new GridLayout(2, 0));
			for (Property prop : this.props) {
				JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
				panel.add(new JLabel(prop.getName()));
				this.add(panel);
			}
			for (AbstractButton button : this.buttons) {
				JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
				panel.add(button);
				this.add(panel);
			}
		} else {
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			setLayout(new GridLayout(0, 2));
			for (int i = 0; i < this.buttons.size(); i++) {
				{
					JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
					panel.add(new JLabel(this.props.get(i).getName()));
					this.add(panel);
				}
				{
					JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
					panel.add(this.buttons.get(i));
					this.add(panel);
				}
			}
		}
		this.validate();
		this.revalidate();
	}
}
