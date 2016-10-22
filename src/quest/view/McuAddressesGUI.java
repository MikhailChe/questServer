package quest.view;

import java.awt.GridLayout;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import quest.controller.log.QLog;
import quest.controller.log.QLog.MsgType;
import quest.model.common.classes.MicroUnit;
import quest.model.common.classes.MicroUnit.InetSocketAddressXmlAdapter;

public class McuAddressesGUI extends JPanel {
	private static final long serialVersionUID = -1842318840231517558L;

	List<MicroUnit> units;

	Mainframe frame;

	public McuAddressesGUI(List<MicroUnit> units, Mainframe frame) {
		super(new GridLayout(0, 1));
		this.units = units;
		this.frame = frame;
		SwingUtilities.invokeLater(this::createAndShowGui);
	}

	public void createAndShowGui() {
		JButton startButton = new JButton("Cтарт");
		final AtomicInteger controllersCount = new AtomicInteger(0);
		final AtomicBoolean anyErrorsFlag = new AtomicBoolean(false);
		for (MicroUnit unit : this.units) {
			final JPanel singleLine = new JPanel(new GridLayout(1, 0));
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

			JLabel nameLabel = new JLabel(unit.getName());
			nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);

			JLabel initLabel = new JLabel("");
			startButton.addActionListener((e) -> {
				if (e.getActionCommand().equals("Старт")) {
					addressField.setEnabled(false);
					unit.initialize();
					initLabel.setText("Инициализация");

					final AtomicInteger counter = new AtomicInteger(3);
					final Timer retryInitTimer = new Timer(3000, null);
					retryInitTimer.addActionListener((retryInitActionEvent) -> {
						QLog.inst().print("Сработал таймер", MsgType.INFO);

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

			singleLine.add(nameLabel);
			singleLine.add(addressField);
			singleLine.add(initLabel);

			add(singleLine);
		}
		Timer timer = new Timer(1000, null);
		timer.addActionListener((startToLaunchTimerEvent) -> {
			if (controllersCount.get() >= this.units.size()) {
				if (anyErrorsFlag.get()) {
					startButton.setText("Продолжить...");
					startButton.addActionListener((continueActionEven) -> {
						if (continueActionEven.getActionCommand().equals("Продолжить..."))
							this.frame.setContentPane(new MCULists(this.units));
					});
				} else {
					this.frame.setContentPane(new MCULists(this.units));
				}
				timer.stop();
			}
		});
		timer.start();
		add(startButton);
	}
}
