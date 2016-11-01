package quest.view;

import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.BoxLayout.PAGE_AXIS;
import static quest.controller.log.QLog.MsgType.INFO;

import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import quest.controller.log.QLog;
import quest.model.common.classes.MicroUnit;
import quest.model.common.classes.fields.Property;

public class EditablePropertyGUI extends JComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 350671636456970892L;

	MicroUnit unit;
	Property prop;

	public EditablePropertyGUI(MicroUnit unit, Property prop, boolean horizontal) {
		this.unit = unit;
		this.prop = prop;

		SwingUtilities.invokeLater(this::createAndShowGUI);
	}

	private void createAndShowGUI() {
		this.setOpaque(false);
		setLayout(new BoxLayout(this, PAGE_AXIS));
		if (this.prop.getOnValue() == null && this.prop.getOffValue() == null) {
			final JCheckBox checkbox = new JCheckBox();
			setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			checkbox.setOpaque(false);

			setCheckboxListener(checkbox, this.unit, this.prop);

			JLabel nameLabel = new JLabel(this.prop.getName());

			nameLabel.setAlignmentX(.5f);
			checkbox.setAlignmentX(.5f);

			Box innerBox = Box.createVerticalBox();
			innerBox.add(nameLabel);
			innerBox.add(checkbox);
			innerBox.setAlignmentY(0);

			Box verticalGlueBox = Box.createHorizontalBox();
			verticalGlueBox.add(innerBox);

			verticalGlueBox.add(Box.createVerticalGlue());

			add(verticalGlueBox);
		} else {
			final ButtonGroup buttonGroup = new ButtonGroup();
			final JRadioButton buttonOn = new JRadioButton(this.prop.getOnValue());
			final JRadioButton buttonOff = new JRadioButton(this.prop.getOffValue());

			setBorder(createTitledBorder(this.prop.getName()));

			buttonOn.setOpaque(false);
			buttonOff.setOpaque(false);
			ActionListener al = (e) -> {
				buttonGroup.clearSelection();
				if (e.getSource().equals(buttonOn)) {
					this.unit.requestRemoteUpdate(this.prop.address, true);
				} else {
					this.unit.requestRemoteUpdate(this.prop.address, false);
				}
			};
			buttonOn.addActionListener(al);
			buttonOff.addActionListener(al);

			this.unit.addPropertyChangeListener((e) -> {
				if (this.prop.getValue() != null) {
					if (this.prop.getValue() instanceof Boolean) {
						if (this.prop.getValue().equals(true)) {
							QLog.inst().print("Письмо булевая правда", INFO);
							buttonGroup.setSelected(buttonOn.getModel(), true);
						} else {
							QLog.inst().print("Письмо булевая ложь", INFO);
							buttonGroup.setSelected(buttonOff.getModel(), true);
						}
					}
				}
			});
			buttonGroup.add(buttonOn);
			buttonGroup.add(buttonOff);

			Box innerBox = Box.createVerticalBox();
			innerBox.add(Box.createHorizontalGlue());
			innerBox.add(buttonOn);
			innerBox.add(buttonOff);
			innerBox.add(Box.createHorizontalGlue());
			innerBox.setAlignmentY(0);
			Box verticalGlueBox = Box.createHorizontalBox();
			verticalGlueBox.add(innerBox);

			verticalGlueBox.add(Box.createVerticalGlue());

			add(verticalGlueBox);
		}
	}

	public static void setCheckboxListener(AbstractButton checkbox, MicroUnit unit, Property prop) {
		if (checkbox == null || unit == null || prop == null)
			return;
		final ButtonModel bm = checkbox.getModel();
		ActionListener al = (e) -> {
			SwingUtilities.invokeLater(() -> {
				unit.requestRemoteUpdate(prop.address, bm.isSelected());
				bm.setEnabled(false);
				bm.setRollover(true);
				bm.setPressed(true);
				bm.setArmed(true);
				bm.setSelected(!bm.isSelected());
			});
		};
		checkbox.addActionListener(al);
		unit.addPropertyChangeListener((e) -> {
			if (prop.getValue() != null) {
				if (prop.getValue() instanceof Boolean) {
					if (prop.getValue().equals(true)) {
						bm.setSelected(true);
					} else {
						bm.setSelected(false);
					}
					bm.setRollover(false);
					bm.setPressed(false);
					bm.setArmed(false);
					bm.setEnabled(true);
				}
			}
		});
	}
}
