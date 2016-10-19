package quest.view;

import static quest.controller.log.QLog.MsgType.INFO;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import quest.controller.log.QLog;
import quest.model.common.classes.MicroUnit;
import quest.model.common.classes.fields.Property;

@Deprecated
public class SingleField extends JPanel {

	private static final long serialVersionUID = -1583881201458974568L;
	MicroUnit unit;
	Property prop;

	public SingleField(MicroUnit unit, Property prop) {
		this.prop = prop;
		this.unit = unit;
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		SwingUtilities.invokeLater(this::createAndShowGUI);
	}

	public void createAndShowGUI() {
		Class<?> type = this.prop.getType();
		if (type.equals(Boolean.class)) {
			if (this.prop.isEditable()) {
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

			} else {

				add(new JLabel(this.prop.getName()));

				JCheckBox cb = new JCheckBox();
				cb.addActionListener((e) -> {
					QLog.inst().print(
							"Запрос на принудительное изменение: " + this.unit.getName() + " " + this.prop.getName(),
							INFO);
					this.unit.requestRemoteUpdate(this.prop.address, cb.isSelected());
					cb.setSelected(!cb.isSelected());
				});
				add(cb);
				this.unit.addPropertyChangeListener((e) -> {
					if (this.prop.getValue() != null)
						if (this.prop.getValue() instanceof Boolean)
							cb.setSelected((boolean) this.prop.getValue());
				});
			}
		} else if (type.equals(Short.class)) {
			JFormattedTextField ftxt = new JFormattedTextField(NumberFormat.getIntegerInstance());
			ftxt.setColumns(10);
			add(ftxt);
			this.unit.addPropertyChangeListener((e) -> {
				if (this.prop.getValue() != null) {
					if (this.prop.getValue() instanceof Short)
						ftxt.setText("" + (short) this.prop.getValue());
				}
			});
		}

		validate();
	}

}
