package quest.view;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import quest.model.common.classes.MicroUnit;
import quest.model.common.classes.fields.Property;

public class UneditablePropertyGUI extends JPanel {
	private static final long serialVersionUID = -9168576951960928929L;

	boolean horizontal;
	Property prop;

	Component button;

	public UneditablePropertyGUI(MicroUnit unit, Property prop, boolean horizontal) {
		this.horizontal = horizontal;
		this.prop = prop;

		if (prop.getType().equals(java.lang.Boolean.class)) {
			AbstractButton cb = new JCheckBox();
			this.button = (cb);
			this.button.setEnabled(false);
			unit.addPropertyChangeListener((e) -> {
				if (prop.getValue() != null)
					if (prop.getValue() instanceof Boolean)
						cb.setSelected((boolean) prop.getValue());
			});
		} else {
			JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, Short.MIN_VALUE, Short.MAX_VALUE, 1));
			this.button = (spinner);
			this.button.setEnabled(false);
			unit.addPropertyChangeListener((e) -> {
				if (prop.getValue() != null)
					if (prop.getValue() instanceof Number)
						spinner.setValue((Number) prop.getValue());
			});
		}

		SwingUtilities.invokeLater(this::createAndShowGUI);
	}

	private void createAndShowGUI() {
		if (this.horizontal) {
			setLayout(new GridLayout(2, 0));
			{
				JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
				panel.add(new JLabel(this.prop.getName()));
				this.add(panel);
			}
			{
				JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
				panel.add(this.button);
				this.add(panel);
			}
		} else {
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			setLayout(new GridLayout(0, 2));
			{
				{
					JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
					panel.add(new JLabel(this.prop.getName()));
					this.add(panel);
				}
				{
					JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
					panel.add(this.button);
					this.add(panel);
				}
			}
		}
		this.validate();
		this.revalidate();
	}
}
