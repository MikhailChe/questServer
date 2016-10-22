package quest.view;

import static java.awt.Color.GREEN;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
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

	private static final int INIT_RETRY_DELAY_MS = 800;
	private static final int INIT_RETRY_TIMES = 1;

	private static final String START_LABEL = "Старт";
	private static final String CONTINUE_LABEL = "Продолжить";

	private static final int CELL_HEIGHT = 30;

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
		JButton startButton = new JButton(START_LABEL);
		final AtomicInteger controllersCount = new AtomicInteger(0);
		final AtomicBoolean anyErrorsFlag = new AtomicBoolean(false);

		for (MicroUnit unit : this.units) {
			final JPanel singleLine = new JPanel();
			singleLine.setLayout(new BoxLayout(singleLine, BoxLayout.LINE_AXIS));

			JLabel nameLabel = new JLabel(unit.getName());
			nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			nameLabel.setMinimumSize(new Dimension(200, CELL_HEIGHT));
			nameLabel.setMaximumSize(nameLabel.getMinimumSize());
			nameLabel.setPreferredSize(nameLabel.getMinimumSize());

			final JTextField addressField = new JTextField();
			try {
				addressField.setText(new InetSocketAddressXmlAdapter().marshal(unit.getAddress()));
			} catch (Exception e) {
				QLog.inst().print(e.getLocalizedMessage(), MsgType.ERROR);
			}

			final Runnable unitAddressUpdater = () -> {
				try {
					unit.setAddress(new InetSocketAddressXmlAdapter().unmarshal(addressField.getText()));
					QLog.inst().print(unit.getAddress().toString(), MsgType.INFO);
					addressField.setBackground(Color.WHITE);
				} catch (Exception e) {
					QLog.inst().print(e.getLocalizedMessage(), MsgType.ERROR);
					addressField.setBackground(new Color(255, 128, 128));
				}
			};

			addressField.addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent e) {
					unitAddressUpdater.run();
				}

				@Override
				public void focusLost(FocusEvent e) {
					unitAddressUpdater.run();
				}
			});
			addressField.addActionListener((event) -> {
				unitAddressUpdater.run();
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

					final AtomicInteger counter = new AtomicInteger(INIT_RETRY_TIMES);
					final Timer retryInitTimer = new Timer(INIT_RETRY_DELAY_MS, null);
					retryInitTimer.addActionListener((retryInitActionEvent) -> {
						QLog.inst().print("Повторяем инициализацию", MsgType.INFO);

						int currentCtr = counter.getAndDecrement();
						if (currentCtr <= 0) {
							retryInitTimer.stop();
							initLabel.setText("Инициализация - ОШИБКА");
							initLabel.setForeground(Color.RED);
							anyErrorsFlag.set(true);
							controllersCount.incrementAndGet();
						} else {
							unit.initialize();
							initLabel.setText("Инициализация - " + (currentCtr));
						}
					});
					retryInitTimer.setRepeats(true);
					retryInitTimer.start();

					final PropertyChangeListener pcl = new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							initLabel.setText("Инициализация - ОК");
							initLabel.setForeground(GREEN);
							unit.removePropertyChangeListener(this);
							retryInitTimer.stop();
							controllersCount.incrementAndGet();
						};
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
		startButton.addActionListener((e) -> {
			startButton.setEnabled(false);
		});
		startToLaunchTimer.addActionListener((startToLaunchTimerEvent) -> {
			if (controllersCount.get() >= this.units.size()) {
				if (anyErrorsFlag.get()) {
					startButton.setText(CONTINUE_LABEL);
					startButton.setEnabled(true);
					startButton.addActionListener((continueActionEvent) -> {
						if (continueActionEvent.getActionCommand().equals(CONTINUE_LABEL)) {
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
