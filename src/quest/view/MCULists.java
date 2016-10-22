package quest.view;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;

import quest.model.common.classes.MicroUnit;
import quest.view.layout.VerticalGridLayout;

public class MCULists extends JPanel implements Scrollable {
	private static final long serialVersionUID = -7633912783790852860L;
	final List<MicroUnit> units;
	final List<SingleMicroUnit> guiUnits = new ArrayList<>();

	public MCULists(List<MicroUnit> units) {
		setLayout(new VerticalGridLayout(10, 10));
		this.units = units;
		for (MicroUnit unit : units) {
			SingleMicroUnit smu = new SingleMicroUnit(unit);
			this.guiUnits.add(smu);
		}
		SwingUtilities.invokeLater(this::initAndShowGUI);
		validate();
	}

	public void initAndShowGUI() {
		for (SingleMicroUnit smu : this.guiUnits) {
			add(smu);
		}
		validate();
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 50;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

}
