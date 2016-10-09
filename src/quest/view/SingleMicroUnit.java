package quest.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import quest.model.common.classes.MicroUnit;
import quest.model.common.classes.fields.Property;

public class SingleMicroUnit extends JPanel {
	private static final long serialVersionUID = -8630553082343961425L;

	final MicroUnit unit;

	final List<SingleField> guiFields = new ArrayList<>();

	public SingleMicroUnit(MicroUnit unit) {
		this.unit = unit;
		for (Property prop : unit.property) {
			guiFields.add(new SingleField(unit, prop));
		}

		SwingUtilities.invokeLater(this::createAndShowGUI);
		validate();
	}

	public void createAndShowGUI() {
		setBorder(BorderFactory.createTitledBorder(unit.getName()));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new JLabel(unit.getAddress().toString()));
		for (SingleField sf : guiFields) {
			add(sf);
		}
		invalidate();
		revalidate();
		validate();
	}

}
