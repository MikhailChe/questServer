package quest.view;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import quest.model.common.classes.MicroUnit;
import quest.model.common.classes.fields.Property;
import quest.model.common.classes.fields.PropertyGroup;
import quest.view.layout.TableLayout;

public class Table extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5545091979275735674L;

	private class Row {
		String name;
		List<Property> columns;

		public Row() {
			this.name = "";
			this.columns = new ArrayList<>();
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	Set<String> categoryRows = new LinkedHashSet<>();
	List<Row> valueColumns = new ArrayList<>();

	private MicroUnit unit;

	public Table(MicroUnit unit, PropertyGroup grp) {
		this.unit = unit;
		for (PropertyGroup innerGrp : grp.group) {
			Row row = new Row();
			row.name = innerGrp.name;
			for (Property prop : innerGrp.getProperties()) {
				row.columns.add(prop);
			}
			addRow(row);
		}
		SwingUtilities.invokeLater(this::createGui);
	}

	public void createGui() {
		setLayout(new TableLayout(this.categoryRows.size() + 1, this.valueColumns.size() + 1, 2, 2));
		Component[][] table = new Component[this.categoryRows.size() + 1][this.valueColumns.size() + 1];

		List<String> categories = new ArrayList<>(this.categoryRows);

		// insert category names;
		for (int i = 0; i < categories.size(); i++) {
			JLabel label = new JLabel(categories.get(i));
			label.setHorizontalAlignment(SwingConstants.RIGHT);
			table[i + 1][0] = label;
		}
		// fill in value names;
		for (int j = 0; j < this.valueColumns.size(); j++) {
			JLabel label = new JLabel(this.valueColumns.get(j).name);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			table[0][j + 1] = label;
		}
		// fill in checkboxes
		for (int j = 0; j < this.valueColumns.size(); j++) {
			Map<String, Property> columnCategoryNames = new HashMap<>();

			for (Property prop : this.valueColumns.get(j).columns) {
				columnCategoryNames.put(prop.getName(), prop);
			}

			for (int i = 0; i < categories.size(); i++) {
				if (columnCategoryNames.containsKey(categories.get(i)))
					table[i + 1][j + 1] = printForProps(this.unit, columnCategoryNames.get(categories.get(i)));
			}
		}

		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table.length; j++) {
				if (table[i][j] == null) {
					JPanel panel = new JPanel();
					panel.setOpaque(false);
					add(panel);
				} else {
					add(table[i][j]);
				}
			}
		}
	}

	static JComponent printForProps(MicroUnit unit, Property prop) {
		Box box = Box.createHorizontalBox();
		box.setOpaque(false);
		JCheckBox cb = new JCheckBox();
		cb.setOpaque(false);
		cb.setEnabled(false);
		UneditablePropertyGUI.setCheckboxListener(cb, unit, prop);
		box.add(Box.createHorizontalGlue());
		box.add(cb);
		box.add(Box.createHorizontalGlue());
		return box;
	}

	public Row addRow(Row row) {
		this.valueColumns.add(row);
		for (Property column : row.columns) {
			this.categoryRows.add(column.getName());
		}
		return row;
	}
}
