package quest.view;

import static quest.controller.log.QLog.MsgType.INFO;

import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import quest.controller.log.QLog;
import quest.model.common.classes.MicroUnit;
import quest.model.common.classes.fields.Property;

public class EditablePropertyGUI extends JPanel {
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
		setBorder(BorderFactory.createTitledBorder(this.prop.getName()));

		final ButtonGroup buttonGroup = new ButtonGroup();
		final JRadioButton buttonOn = new JRadioButton(this.prop.getOnValue());
		final JRadioButton buttonOff = new JRadioButton(this.prop.getOffValue());
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
			QLog.inst().print("Пришло письмо для кнопки", INFO);
			if (this.prop.getValue() != null) {
				QLog.inst().print("Письмо не нулевое", INFO);
				if (this.prop.getValue() instanceof Boolean) {
					QLog.inst().print("Письмо булевое", INFO);
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

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		add(buttonOn);
		add(buttonOff);

	}
}
