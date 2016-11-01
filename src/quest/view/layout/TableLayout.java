package quest.view.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.function.Function;

public class TableLayout implements java.awt.LayoutManager2 {

	final int rows;
	final int cols;

	final int vgap;
	final int hgap;

	public TableLayout(int rows, int cols) {
		this(rows, cols, 0, 0);
	}

	public TableLayout(int rows, int cols, int vgap, int hgap) {
		if (rows < 0 || cols < 0) {
			throw new IllegalArgumentException("Количество рядов и колонок должно быть больше нуля");
		}
		if (rows == 0 && cols == 0) {
			throw new IllegalArgumentException("Только колонки или ряды могут быть равны нулю, но не все сразу");
		}

		if (vgap < 0 || hgap < 0) {
			throw new IllegalArgumentException("Расстояние должно быть больше нуля");
		}

		this.rows = rows;
		this.cols = cols;

		this.vgap = vgap;
		this.hgap = hgap;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

	private Dimension getLayoutSize(Container parent, Function<Component, Dimension> fun) {
		if (parent == null)
			return null;
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int numComponents = parent.getComponentCount();

			int numCols = this.cols;
			int numRows = this.rows;
			if (numCols == 0) {
				numCols = (numComponents + numRows - 1) / numRows;
			}
			if (numRows == 0) {
				numRows = (numComponents + numCols - 1) / numCols;
			}

			int[] componentWidths = new int[numCols];
			int[] componentHeights = new int[numRows];

			for (int i = 0; i < parent.getComponentCount() && i < numRows * numCols; i++) {

				Component component = parent.getComponent(i);
				Dimension size = fun.apply(component);

				int colNumber = i % numCols;
				int rowNumber = i / numCols;

				if (componentWidths[colNumber] < size.width) {
					componentWidths[colNumber] = size.width;
				}
				if (componentHeights[rowNumber] < size.height) {
					componentHeights[rowNumber] = size.height;
				}
			}

			int widthWithoutGaps = 0;
			int heightWithoutGaps = 0;

			for (int i = 0; i < componentWidths.length; i++) {
				widthWithoutGaps += componentWidths[i];
			}
			for (int i = 0; i < componentHeights.length; i++) {
				heightWithoutGaps += componentHeights[i];
			}

			int widthWithGaps = widthWithoutGaps + this.hgap * (numCols + 1);
			int heightWithGaps = heightWithoutGaps + this.vgap * (numCols + 1);

			return new Dimension(widthWithGaps + insets.left + insets.right,
					heightWithGaps + insets.top + insets.bottom);
		}
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		Dimension d = getLayoutSize(parent, Component::getPreferredSize);
		System.out.println("Preferred size: " + d);

		return d;
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		Dimension d = getLayoutSize(parent, Component::getMinimumSize);
		System.out.println("Minimum size: " + d);

		return d;
	}

	@Override
	public void layoutContainer(Container parent) {
		if (parent == null)
			return;
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int numComponents = parent.getComponentCount();

			int numCols = this.cols;
			int numRows = this.rows;
			if (numCols == 0) {
				numCols = (numComponents + numRows - 1) / numRows;
			}
			if (numRows == 0) {
				numRows = (numComponents + numCols - 1) / numCols;
			}

			int[] componentWidths = new int[numCols];
			int[] componentHeights = new int[numRows];

			for (int i = 0; i < parent.getComponentCount() && i < numRows * numCols; i++) {
				Component component = parent.getComponent(i);
				Dimension size = component.getMinimumSize();
				int colNumber = i % numCols;
				int rowNumber = i / numCols;

				if (componentWidths[colNumber] < size.width) {
					componentWidths[colNumber] = size.width;
				}
				if (componentHeights[rowNumber] < size.height) {
					componentHeights[rowNumber] = size.height;
				}
			}

			int widthWithoutGaps = 0;
			int heightWithoutGaps = 0;

			for (int i = 0; i < componentWidths.length; i++) {
				widthWithoutGaps += componentWidths[i];
			}
			for (int i = 0; i < componentHeights.length; i++) {
				heightWithoutGaps += componentHeights[i];
			}

			int totalGapsWidth = this.hgap * (numCols + 1);
			int totalGapsHeight = this.vgap * (numCols + 1);

			int[] realComponentWidths = new int[numCols];
			int[] realComponentHeights = new int[numRows];

			int totalParentWidth = parent.getWidth();
			int totalParentHeight = parent.getHeight();

			int realWidthWOinsets = totalParentWidth - insets.left - insets.right;
			int realHeightWOinsets = totalParentHeight - insets.top - insets.bottom;

			int realWidthWithoutGaps = totalParentWidth - insets.left - insets.right - totalGapsWidth;
			int realHeightWithoutGaps = totalParentHeight - insets.top - insets.bottom - totalGapsHeight;

			int realWidthOnComponents = 0;
			int realHeightOnComponents = 0;

			for (int i = 0; i < realComponentWidths.length; i++) {
				if (widthWithoutGaps > 0) {
					realComponentWidths[i] = ((realWidthWithoutGaps * componentWidths[i]) / widthWithoutGaps);
				} else {
					realComponentWidths[i] = 0;
				}
				realWidthOnComponents += realComponentWidths[i];
			}
			for (int i = 0; i < realComponentHeights.length; i++) {
				if (heightWithoutGaps > 0) {
					realComponentHeights[i] = ((realHeightWithoutGaps * componentHeights[i]) / heightWithoutGaps);
				} else {
					realComponentHeights[i] = 0;
				}
				realHeightOnComponents += realComponentHeights[i];
			}
			int extraWidthAvailable = (realWidthWOinsets - (realWidthOnComponents + totalGapsWidth)) / 2;
			int extraHeightAvailable = (realHeightWOinsets - (realHeightOnComponents + totalGapsHeight)) / 2;

			for (int col = 0, x = insets.left + extraWidthAvailable; col < numCols; x += realComponentWidths[col]
					+ this.hgap, col++) {
				for (int row = 0, y = insets.top + extraHeightAvailable; row < numRows; y += realComponentHeights[row]
						+ this.vgap, row++) {
					int i = row * numCols + col;
					if (i < parent.getComponentCount()) {
						parent.getComponent(i).setBounds(x, y, realComponentWidths[col], realComponentHeights[row]);
					}
				}
			}

		}
	}

	@Override
	public String toString() {
		return "cols=" + this.cols + ", rows=" + this.rows + ", hgap=" + this.hgap + ", vgap=" + this.vgap;
	}

	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		return getLayoutSize(target, Component::getPreferredSize);
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	@Override
	public void invalidateLayout(Container target) {
	}
}
