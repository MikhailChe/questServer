package quest.view;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import quest.model.common.classes.MicroUnit;
import quest.model.common.classes.fields.Property;
import quest.model.common.classes.fields.PropertyGroup;

public class SingleMicroUnit extends JPanel {
	private static final long serialVersionUID = -8630553082343961425L;

	final MicroUnit unit;

	final Component gui;

	public SingleMicroUnit(MicroUnit unit) {
		this.unit = unit;
		this.gui = getComponentList(unit);
		SwingUtilities.invokeLater(this::createAndShowGUI);
		validate();
	}

	public void createAndShowGUI() {
		setBorder(BorderFactory.createTitledBorder(this.unit.getName()));
		BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		setLayout(layout);
		add(this.gui);
		invalidate();
		revalidate();
		validate();
	}

	public static Component getComponentList(MicroUnit unit) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		for (PropertyGroup group : unit.group) {
			panel.add(getComponentForGroup(unit, group));
		}

		return panel;
	}

	public static Component getComponentForGroup(MicroUnit unit, PropertyGroup group) {
		JPanel panel = new JPanel();
		switch (group.align) {
		default:
		case HORIZONTAL:
			panel.setLayout(new GridLayout(1, 0));
			break;
		case VERTICAL:
			panel.setLayout(new GridLayout(0, 1));
			break;
		}
		if (group.name != null && !group.name.isEmpty()) {
			panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(group.name),
					BorderFactory.createEmptyBorder(8, 8, 8, 8)));
		}
		for (PropertyGroup grp : group.group) {
			panel.add(getComponentForGroup(unit, grp));
		}
		for (Property prop : group.property) {
			panel.add(getComponentForProperty(unit, prop,
					group.align.equals(PropertyGroup.Align.HORIZONTAL) ? true : false));
		}

		return panel;
	}

	public static Component getComponentForProperty(MicroUnit unit, Property prop, boolean horizontal) {
		if (prop.isEditable()) {
			return new EditablePropertyGUI(unit, prop, horizontal);
		} else {
			return new UneditablePropertyGUI(unit, prop, horizontal);
		}
	}
}
