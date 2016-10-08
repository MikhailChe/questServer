package quest.view;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import quest.model.common.classes.MicroUnit;

public class SettingsPanel extends JPanel {
	List<MicroUnit> units;

	public SettingsPanel(List<MicroUnit> units) {
		super();
		this.units = units;
		SwingUtilities.invokeLater(this::initAndShowGUI);
	}

	void initAndShowGUI() {
		// Сюда воткнуть несколько SingleUnitSettings в зависимости отколичества
		// MicroUnit
	}
}

class SingleUnitSettings extends JPanel {
	MicroUnit unit;
	JTextField field = new JTextField("0.0.0.0");

	SingleUnitSettings(MicroUnit unit) {
		super();
		this.unit = unit;
		SwingUtilities.invokeLater(this::initAndShowGUI);
	}

	void initAndShowGUI() {
		setBorder(new TitledBorder(unit.getName()));
		add(field);
	}

	String getIP() {
		return field.getText();
	}

}
