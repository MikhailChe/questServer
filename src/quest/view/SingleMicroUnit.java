package quest.view;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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

	final List<Component> guiFields = new ArrayList<>();

	public SingleMicroUnit(MicroUnit unit) {
		this.unit = unit;

		Map<Integer, List<Property>> propGroups = new Hashtable<>();

		for (Property prop : unit.property) {
			propGroups.compute(prop.group, (k, v) -> {
				if (v == null)
					v = new ArrayList<>();
				v.add(prop);
				return v;
			});
		}
		for (List<Property> group : propGroups.values()) {
			if (group.get(0).isEditable()) {
				this.guiFields.add(new JLabel("Редактируемый пункт. Добавить позже"));
			} else {
				System.out.println(group.get(0).isHorizontal());
				this.guiFields.add(new UneditablePropertyGroupGUI(group, group.get(0).isHorizontal(), unit));
			}
		}
		SwingUtilities.invokeLater(this::createAndShowGUI);
		validate();
	}

	public void createAndShowGUI() {
		setBorder(BorderFactory.createTitledBorder(this.unit.getName()));
		BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		setLayout(layout);

		for (Component sf : this.guiFields) {
			add(sf);
		}
		invalidate();
		revalidate();
		validate();
	}

}
