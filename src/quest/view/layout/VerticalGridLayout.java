package quest.view.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.function.Function;

public class VerticalGridLayout implements LayoutManager {

	private int hgap;
	private int vgap;

	public VerticalGridLayout(int hgap, int vgap) {
		this.hgap = hgap;
		this.vgap = vgap;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		return getLayoutSize(parent, Component::getPreferredSize);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return getLayoutSize(parent, Component::getMinimumSize);
	}

	private Dimension getLayoutSize(Container parent, Function<Component, Dimension> fun) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int ncomponents = parent.getComponentCount();
			int width = 0;
			int height = 0;
			for (int i = 0; i < ncomponents; i++) {
				Component comp = parent.getComponent(i);
				Dimension size = fun.apply(comp);
				if (width < size.width) {
					width = size.width;
				}
				if (height < size.height) {
					height = size.height;
				}
			}
			int totalComponentsWidth = 0;
			int totalComponentsHeight = 0;
			if (width > 0) {
				int ncols = (parent.getWidth() + this.hgap) / (width + this.hgap);
				if (ncols <= 0)
					ncols = 1;
				int nrows = (ncomponents + ncols - 1) / ncols;
				totalComponentsWidth = ncols * width + (ncols - 1) * this.hgap;
				totalComponentsHeight = nrows * height + (nrows - 1) * this.vgap;
			}
			return new Dimension(insets.left + insets.right + totalComponentsWidth,
					insets.top + insets.bottom + totalComponentsHeight);
		}
	}

	@Override
	public void layoutContainer(Container parent) {
		synchronized (parent.getTreeLock()) {

			Insets insets = parent.getInsets();
			int ncomponents = parent.getComponentCount();
			if (ncomponents == 0)
				return;
			int maxComponentWidth = 0;
			int maxComponentHeight = 0;

			for (int i = 0; i < ncomponents; i++) {
				Component comp = parent.getComponent(i);
				Dimension compSize = comp.getMinimumSize();
				if (maxComponentWidth < compSize.width)
					maxComponentWidth = compSize.width;
				if (maxComponentHeight < compSize.height)
					maxComponentHeight = compSize.height;
			}
			if (maxComponentWidth == 0)
				return;
			int ncols = 0;
			int nrows = 0;
			if (maxComponentWidth > 0) {

				ncols = (parent.getWidth() + this.hgap) / (maxComponentWidth + this.hgap);

				if (ncols <= 0) {
					ncols = 1;
				}
				nrows = (ncomponents + ncols - 1) / ncols;
			}
			int totalGapsWidth = (ncols - 1) * this.hgap;
			int widthWOInsets = parent.getWidth() - (insets.left + insets.right);
			int widthOnComponent = (widthWOInsets - totalGapsWidth) / ncols;
			int extraWidthAvailable = (widthWOInsets - (widthOnComponent * ncols + totalGapsWidth)) / 2;

			int totalGapsHeight = (nrows - 1) * this.vgap;
			int heightWOInsets = parent.getHeight() - (insets.top + insets.bottom);
			int heightOnComponent = (heightWOInsets - totalGapsHeight) / nrows;
			// heightOnComponent = maxComponentHeight;
			int extraHeightAvailable = (heightWOInsets - (heightOnComponent * nrows + totalGapsHeight)) / 2;

			for (int c = 0, x = insets.left + extraWidthAvailable; c < ncols; c++, x += widthOnComponent + this.hgap) {
				for (int r = 0, y = insets.top + extraHeightAvailable; r < nrows; r++, y += heightOnComponent
						+ this.vgap) {
					int i = r * ncols + c;
					if (i < ncomponents) {
						parent.getComponent(i).setBounds(x, y, widthOnComponent, heightOnComponent);
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		return getClass().getName() + "[hgap=" + this.hgap + ",vgap=" + this.vgap + "]";
	}

}
