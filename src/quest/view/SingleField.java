package quest.view;

import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import quest.controller.log.QLog;
import quest.model.common.classes.MicroUnit;
import quest.model.common.classes.fields.Property;

public class SingleField extends JPanel {

	private static final long serialVersionUID = -1583881201458974568L;
	MicroUnit unit;
	Property prop;

	public SingleField(MicroUnit unit, Property prop) {
		this.prop = prop;
		this.unit = unit;
		SwingUtilities.invokeLater(this::createAndShowGUI);
	}

	public void createAndShowGUI() {
		setBorder(BorderFactory.createTitledBorder(((Byte) prop.address).toString()));
		Class<?> type = prop.getType();
		add(new JLabel(prop.getName()));
		JComponent comp = null;
		if (type.equals(Boolean.class)) {
			JCheckBox cb = new JCheckBox();
			cb.addActionListener((e) -> {
				QLog.inst().print("Запрос на принудительное изменение: " + unit.getName() + " " + prop.getName(),
						QLog.MsgType.INFO);
				unit.requestRemoteUpdate(prop.address, cb.isSelected());
				cb.setSelected(!cb.isSelected());
			});
			comp = cb;
			unit.addPropertyChangeListener((e) -> {
				if (prop.getValue() != null)
					if (prop.getValue() instanceof Boolean)
						cb.setSelected((boolean) prop.getValue());
			});
		} else if (type.equals(Short.class)) {
			JFormattedTextField ftxt = new JFormattedTextField(NumberFormat.getIntegerInstance());
			ftxt.setColumns(10);
			comp = ftxt;
			unit.addPropertyChangeListener((e) -> {
				if (prop.getValue() != null) {
					if (prop.getValue() instanceof Short)
						ftxt.setText("" + (short) prop.getValue());
				}
			});
		}
		if (comp != null) {
			comp.setEnabled(prop.isEditable());
			add(comp);
		}

		validate();
	}

}
