package quest.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import quest.controller.QuestStarter;
import quest.controller.log.QLog;
import quest.controller.log.QLog.MsgType;
import quest.model.common.classes.MicroUnit;
import quest.model.common.classes.MicroUnit.InetSocketAddressXmlAdapter;

public class McuAddressesGUI extends JPanel implements Scrollable {
	private static final long serialVersionUID = -1842318840231517558L;

	List<MicroUnit> units;

	Mainframe frame;
	JPanel boxList;

	public McuAddressesGUI(List<MicroUnit> units, Mainframe frame) {
		super(new BorderLayout(4, 4));
		this.units = units;
		this.frame = frame;
		this.boxList = new JPanel();
		this.boxList.setLayout(new BoxLayout(this.boxList, BoxLayout.PAGE_AXIS));

		JScrollPane scrollPanelList = new JScrollPane(this.boxList);

		this.add(scrollPanelList, BorderLayout.CENTER);
		SwingUtilities.invokeLater(this::createAndShowGui);
	}

	public void createAndShowGui() {
		final String START_LABEL = "Старт";
		final String CONTINUE_LABEL = "Продолжить";
		JButton startButton = new JButton(START_LABEL);
		final AtomicInteger controllersCount = new AtomicInteger(0);
		final AtomicBoolean anyErrorsFlag = new AtomicBoolean(false);
		final int CELL_HEIGHT = 30;

		for (MicroUnit unit : this.units) {
			final JPanel singleLine = new JPanel();
			singleLine.setLayout(new BoxLayout(singleLine, BoxLayout.LINE_AXIS));

			JLabel nameLabel = new JLabel(unit.getName());
			nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			nameLabel.setMinimumSize(new Dimension(200, CELL_HEIGHT));
			nameLabel.setMaximumSize(nameLabel.getMinimumSize());
			nameLabel.setPreferredSize(nameLabel.getMinimumSize());

			final JTextField addressField = new JTextField(20);
			try {
				addressField.setText(new InetSocketAddressXmlAdapter().marshal(unit.getAddress()));
			} catch (Exception e) {
				QLog.inst().print(e.getLocalizedMessage(), MsgType.ERROR);
			}
			addressField.addActionListener((event) -> {
				try {
					unit.setAddress(new InetSocketAddressXmlAdapter().unmarshal(addressField.getText()));
					QLog.inst().print(unit.getAddress().toString(), MsgType.INFO);
				} catch (Exception e) {
					QLog.inst().print(e.getLocalizedMessage(), MsgType.ERROR);
				}
			});
			addressField.setMinimumSize(new Dimension(300, CELL_HEIGHT));
			addressField.setMaximumSize(addressField.getMinimumSize());
			addressField.setPreferredSize(addressField.getMinimumSize());

			JLabel initLabel = new JLabel("");
			startButton.addActionListener((startButtonActionEvent) -> {
				if (startButtonActionEvent.getActionCommand().equals(START_LABEL)) {
					addressField.setEnabled(false);
					unit.initialize();
					initLabel.setText("Инициализация");

					final AtomicInteger counter = new AtomicInteger(3);
					final Timer retryInitTimer = new Timer(500, null);
					retryInitTimer.addActionListener((retryInitActionEvent) -> {
						QLog.inst().print("Повторяем инициализацию", MsgType.INFO);

						int currentCtr = counter.getAndDecrement();
						if (currentCtr <= 0) {
							retryInitTimer.stop();
							initLabel.setText("Инициализация - ОШИБКА");
							anyErrorsFlag.set(true);
							controllersCount.incrementAndGet();
						} else {
							unit.initialize();
							initLabel.setText("Инициализация - " + (currentCtr));
						}
					});
					retryInitTimer.setRepeats(true);
					retryInitTimer.start();

					final PropertyChangeListener pcl = (initOkEevent) -> {
						initLabel.setText("Инициализация - ОК");
						unit.removePropertyChangeListener((PropertyChangeListener) this);
						retryInitTimer.stop();
						controllersCount.incrementAndGet();
					};
					unit.addPropertyChangeListener(pcl);
				}
			});
			initLabel.setMinimumSize(new Dimension(0, CELL_HEIGHT));
			initLabel.setMaximumSize(new Dimension(200, CELL_HEIGHT));
			initLabel.setPreferredSize(new Dimension(10, CELL_HEIGHT));

			// singleLine.add(Box.createHorizontalGlue());
			singleLine.add(nameLabel);
			singleLine.add(Box.createRigidArea(new Dimension(5, 0)));
			singleLine.add(addressField);
			singleLine.add(Box.createRigidArea(new Dimension(5, 0)));
			singleLine.add(initLabel);

			this.boxList.add(singleLine);
		}
		Timer startToLaunchTimer = new Timer(1000, null);
		startToLaunchTimer.addActionListener((startToLaunchTimerEvent) -> {
			if (controllersCount.get() >= this.units.size()) {
				if (anyErrorsFlag.get()) {
					startButton.setText(CONTINUE_LABEL);
					startButton.addActionListener((continueActionEven) -> {
						if (continueActionEven.getActionCommand().equals(CONTINUE_LABEL)) {
							SwingUtilities.invokeLater(() -> {
								this.frame.setContentPane(new MCULists(this.units));
								new Thread(() -> {
									QuestStarter.updateAllLoop(this.units);
								}).start();
								this.frame.showMe();
							});
						}
					});
				} else {
					SwingUtilities.invokeLater(() -> {
						this.frame.setContentPane(new MCULists(this.units));
						new Thread(() -> {
							QuestStarter.updateAllLoop(this.units);
						}).start();
						this.frame.showMe();
					});
				}
				startToLaunchTimer.stop();
			}
		});
		startToLaunchTimer.start();
		Box startBox = Box.createHorizontalBox();
		startBox.add(Box.createGlue());
		startBox.add(startButton);
		startBox.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		startBox.setAlignmentX(SwingConstants.RIGHT);
		startButton.setAlignmentX(SwingConstants.RIGHT);
		add(startBox, BorderLayout.SOUTH);
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		this.getPreferredSize();
		return null;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 0;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 0;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return true;
	}
}
