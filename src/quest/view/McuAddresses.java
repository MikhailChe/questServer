package quest.view;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import quest.controller.log.QLog;
import quest.controller.log.QLog.MsgType;
import quest.model.common.classes.MicroUnit;
import quest.model.common.classes.MicroUnit.InetSocketAddressXmlAdapter;

public class McuAddresses extends JPanel {
	private static final long serialVersionUID = -1842318840231517558L;

	List<MicroUnit> units;

	public McuAddresses(List<MicroUnit> units) {
		super(new GridLayout(0, 1));
		this.units = units;

		SwingUtilities.invokeLater(this::createAndShowGui);
	}

	public void createAndShowGui() {
		JButton startButton = new JButton("Cтарт");
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
			startButton.addActionListener((e) -> {
				addressField.setEnabled(false);
				unit.initialize();
			});

			JLabel nameLabel = new JLabel(unit.getName());
			nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);

			JLabel initLabel = new JLabel("");
			startButton.addActionListener((e) -> {
				initLabel.setText("Инициализация");
			});
			unit.addPropertyChangeListener((e) -> {
				initLabel.setText("Инициализация - ОК");
			});

			singleLine.add(nameLabel);
			singleLine.add(addressField);
			singleLine.add(initLabel);

			add(singleLine);
		}
		add(startButton);
	}
}
