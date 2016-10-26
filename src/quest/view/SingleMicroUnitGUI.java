package quest.view;

import static javax.swing.BorderFactory.createCompoundBorder;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.BoxLayout.PAGE_AXIS;
import static javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION;
import static javax.swing.border.TitledBorder.DEFAULT_POSITION;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import quest.model.common.classes.MicroUnit;
import quest.model.common.classes.fields.Property;
import quest.model.common.classes.fields.PropertyGroup;

public class SingleMicroUnitGUI extends JPanel {
	private static final long serialVersionUID = -8630553082343961425L;

	final MicroUnit unit;

	final Component gui;

	public SingleMicroUnitGUI(MicroUnit unit) {
		this.unit = unit;
		this.gui = getComponentList(unit);
		SwingUtilities.invokeLater(this::createAndShowGUI);
		validate();
	}

	public void createAndShowGUI() {
		setBorder(createTitledBorder(null, this.unit.getName(), DEFAULT_JUSTIFICATION, DEFAULT_POSITION,
				UIManager.getFont("TitledBorder.font").deriveFont(Font.BOLD)));
		BoxLayout layout = new BoxLayout(this, PAGE_AXIS);
		setLayout(layout);
		add(this.gui);

		invalidate();
		revalidate();
		validate();
		doLayout();

	}

	public static JComponent getComponentList(MicroUnit unit) {
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, PAGE_AXIS));

		for (int i = 0; i < unit.group.size(); i++) {
			PropertyGroup group = unit.group.get(i);
			JComponent forGroup = getComponentForGroup(unit, group, (i == unit.group.size() - 1));
			forGroup.setAlignmentX(0f);
			forGroup.setAlignmentY(0f);
			panel.add(Box.createRigidArea(new Dimension(1, 1)));
			panel.add(forGroup);
			panel.add(Box.createRigidArea(new Dimension(1, 1)));
		}
		for (Property prop : unit.getProperties()) {
			if (prop.isKeyValue() && prop.getType().equals(Boolean.class)) {
				unit.addPropertyChangeListener((e) -> {
					Object o;
					if ((o = prop.getValue()) != null) {
						if (o instanceof Boolean) {
							Boolean b = (Boolean) o;
							if (b) {
								panel.setBackground(new Color(64, 192, 64));
							} else {
								panel.setBackground(UIManager.getColor("panel.background"));
							}
						}
					}
				});
			}
		}

		return panel;
	}

	public static JComponent getComponentForGroup(MicroUnit unit, PropertyGroup group, boolean last) {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		switch (group.align) {
		default:
		case HORIZONTAL:
			panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
			break;
		case VERTICAL:
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			break;
		}
		if (group.name != null && !group.name.isEmpty()) {
			panel.setBorder(createCompoundBorder(createTitledBorder(group.name), createEmptyBorder(8, 8, 8, 8)));
		}
		for (int i = 0; i < group.group.size(); i++) {
			PropertyGroup grp = group.group.get(i);
			JComponent forGroup = getComponentForGroup(unit, grp, (i == group.group.size() - 1) && last);
			forGroup.setAlignmentX(0f);
			forGroup.setAlignmentY(0f);
			panel.add(Box.createRigidArea(new Dimension(1, 1)));
			panel.add(forGroup);
		}

		for (int i = 0; i < group.property.size(); i++) {
			Property prop = group.property.get(i);

			if (!prop.isHidden()) {
				JComponent forProperty = getComponentForProperty(unit, prop,
						group.align.equals(PropertyGroup.Align.HORIZONTAL) ? true : false,
						group.group.size() == 0 && last);
				forProperty.setAlignmentX(0f);
				forProperty.setAlignmentY(0f);
				panel.add(Box.createRigidArea(new Dimension(1, 1)));
				panel.add(forProperty);
			}
		}

		return panel;

	}

	public static JComponent getComponentForProperty(MicroUnit unit, Property prop, boolean horizontal, boolean last) {
		JComponent propertyComponent = null;
		if (prop.isEditable()) {
			propertyComponent = new EditablePropertyGUI(unit, prop, horizontal);
		} else {
			propertyComponent = new UneditablePropertyGUI(unit, prop, horizontal);
		}
		propertyComponent.setOpaque(false);

		Box box = null;

		if (horizontal) {
			box = Box.createHorizontalBox();
			box.add(Box.createHorizontalGlue());
			box.add(propertyComponent);
			box.add(Box.createHorizontalGlue());
		} else {
			box = Box.createVerticalBox();
			propertyComponent.setAlignmentX(0);
			box.add(propertyComponent);
			box.add(Box.createHorizontalGlue());
		}
		return box;
	}
}
