package quest.view;

import java.awt.Dimension;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import quest.controller.log.QLog;
import quest.controller.log.QLog.MsgType;
import quest.model.common.classes.MicroUnit;
import quest.model.common.classes.fields.Property;

public class UneditablePropertyGUI extends JComponent {
	private static final long serialVersionUID = -9168576951960928929L;

	boolean horizontal;
	Property prop;

	JComponent component;

	public UneditablePropertyGUI(MicroUnit unit, Property prop, boolean horizontal) {
		this.horizontal = horizontal;
		this.prop = prop;

		if (prop == null) {
			QLog.inst().print("Null property in unit " + unit, MsgType.WARNING);
			return;
		}
		if (prop.getType() == null) {
			QLog.inst().print("Null type for property " + prop + " in unit " + unit, MsgType.WARNING);
			return;
		}
		if (prop.getType().equals(java.lang.Boolean.class)) {
			AbstractButton cb = new JCheckBox();
			this.component = (cb);
			this.component.setEnabled(false);
			unit.addPropertyChangeListener((e) -> {
				if (prop.getValue() != null)

					if (prop.getValue() instanceof Boolean)
						cb.setSelected((boolean) prop.getValue());
			});
		} else {
			JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, Short.MIN_VALUE, Short.MAX_VALUE, 1));
			this.component = (spinner);
			this.component.setEnabled(false);
			this.component.setMaximumSize(new Dimension(60, 20));
			this.component.setPreferredSize(this.component.getMaximumSize());
			unit.addPropertyChangeListener((e) -> {
				if (prop.getValue() != null)
					if (prop.getValue() instanceof Number)
						spinner.setValue((Number) prop.getValue());
			});
		}

		SwingUtilities.invokeLater(this::createAndShowGUI);
	}

	private void createAndShowGUI() {
		this.setOpaque(false);
		JComponent label = new JLabel(this.prop.getName());
		label.setOpaque(false);
		this.component.setOpaque(false);

		if (this.horizontal) {
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			{
				{
					label.setAlignmentX(.5f);
					label.setAlignmentY(.5f);
					add(label);
				}
				add(Box.createRigidArea(new Dimension(1, 1)));
				{
					this.component.setAlignmentX(.5f);
					this.component.setAlignmentY(.5f);
					add(this.component);
				}
			}
		} else {
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			{
				{
					label.setAlignmentX(0f);
					label.setAlignmentY(.5f);
					add(label);
				}
				add(Box.createRigidArea(new Dimension(1, 1)));
				{
					this.component.setAlignmentX(0f);
					this.component.setAlignmentY(.5f);
					add(this.component);
				}
			}
		}
		this.validate();
		this.revalidate();
	}
}
