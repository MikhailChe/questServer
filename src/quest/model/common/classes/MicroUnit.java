package quest.model.common.classes;

import static quest.controller.log.QLog.MsgType.ERROR;
import static quest.controller.log.QLog.MsgType.INFO;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import quest.controller.QuestStarter;
import quest.controller.log.QLog;
import quest.controller.net.Addresser;
import quest.model.common.classes.fields.Property;
import quest.model.common.ifaces.InputByteProcessor;

@XmlAccessorType(XmlAccessType.NONE)
public class MicroUnit implements InputByteProcessor {
	@XmlElement
	String name;
	@XmlElement
	@XmlJavaTypeAdapter(value = InetSocketAddressXmlAdapter.class)
	InetSocketAddress innerAddress;
	@XmlElement
	public List<Property> property = new ArrayList<>();

	PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	List<PropertyChangeListener> listeners = new ArrayList<>();

	public MicroUnit() {
		this(null, "Микроконторллер");
	}

	public MicroUnit(InetSocketAddress addr, String name) {
		this.innerAddress = addr;
		this.name = name;

	}

	public void initialize() {
		QLog.inst().print("Запрос данных от микроконтроллера " + this.getName() + " " + this.getAddress(), INFO);
		send(datagramForData(0, false, new byte[] {}));
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public InetSocketAddress getAddress() {
		return this.innerAddress;
	}

	public void setAddress(InetSocketAddress addr) {
		this.innerAddress = addr;
	}

	public String getFieldName(int address) {
		for (Property field : this.property) {
			if (field.address == address) {
				return field.getName();
			}
		}
		return null;
	}

	public Object getField(int address) {
		for (Property field : this.property) {
			if (field.address == address) {
				return field.getValue();
			}
		}
		return null;
	}

	public void setField(int address, Object val) {
		for (Property field : this.property) {
			if (field.address == address) {
				field.setValue(val);
			}
		}
	}

	public void updateField(int address, Object newValue) {
		// Object oldValue = getField(address);
		setField(address, newValue);
		this.pcs.firePropertyChange(getFieldName(address), null, newValue);
	}

	public void setField(int address, byte[] val) {
		for (Property field : this.property) {
			if (field.address == address) {
				field.setValue(val);
			}
		}
	}

	public void updateField(int address, byte[] val) {
		QLog.inst().print("Обновляю данные " + this.getName() + ":" + address, INFO);
		setField(address, val);
	}

	public void requestRemoteUpdate(int address, boolean o) {
		send(datagramForData(address, true, o));
	}

	public void requestRemoteUpdate(int address, byte o) {
		send(datagramForData(address, true, o));
	}

	public void requestRemoteUpdate(int address, short o) {
		send(datagramForData(address, true, o));
	}

	protected DatagramPacket datagramForData(int perifiral, boolean write, byte[] data) {
		byte[] outputData = new PacketData((byte) perifiral, write, data).getBytes();
		return new DatagramPacket(outputData, outputData.length, this.innerAddress);
	}

	protected DatagramPacket datagramForData(int perifiral, boolean write, boolean val) {
		byte[] outputData = new PacketData((byte) perifiral, write, val).getBytes();
		return new DatagramPacket(outputData, outputData.length, this.innerAddress);
	}

	protected DatagramPacket datagramForData(int perifiral, boolean write, byte val) {
		byte[] outputData = new PacketData((byte) perifiral, write, val).getBytes();
		return new DatagramPacket(outputData, outputData.length, this.innerAddress);
	}

	protected DatagramPacket datagramForData(int perifiral, boolean write, short val) {
		byte[] outputData = new PacketData((byte) perifiral, write, val).getBytes();
		return new DatagramPacket(outputData, outputData.length, this.innerAddress);
	}

	protected DatagramPacket datagramForData(int perifiral, boolean write, int val) {
		byte[] outputData = new PacketData((byte) perifiral, write, val).getBytes();
		return new DatagramPacket(outputData, outputData.length, this.innerAddress);
	}

	@Override
	public String toString() {
		return String.format("%-20s\t%s%n\t%s", this.name, this.innerAddress, this.property);
	}

	@Override
	public void processInput(byte[] data) {
		PacketData pack = new PacketData(data);
		QLog.inst().print("Пришли новые данные для " + this.getName(), INFO);
		if (pack.perifiral == 0) {
			// this.initialize();
			// TODO: Особый случай. Принудительная инициализация
			// микроконтроллера по запросу от самого микроконтроллера
		} else if (pack.data.length > 0) {
			this.updateField(pack.perifiral, pack.data);
			this.pcs.firePropertyChange(this.getFieldName(pack.perifiral), null, this.getField(pack.perifiral));
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		this.pcs.addPropertyChangeListener(pcl);
	}

	protected static void send(DatagramPacket p) {
		if (QuestStarter.udpServer != null) {
			if (QuestStarter.udpServer.socket != null) {
				try {
					QuestStarter.udpServer.socket.send(p);
					QLog.inst().print("Отправлен UDP: " + p.getSocketAddress(), INFO);
				} catch (IOException e) {
					QLog.inst().print("Не получилось отправить UDP пакет:" + e.getLocalizedMessage(), ERROR);
				}
			}
		}
	}

	static class InetSocketAddressXmlAdapter extends XmlAdapter<String, InetSocketAddress> {
		public InetSocketAddressXmlAdapter() {
			super();
		}

		@Override
		public InetSocketAddress unmarshal(String v) throws Exception {
			String[] addrPort = v.split(":");
			return Addresser.getSocketAddress(addrPort[0], Integer.parseInt(addrPort[1]));
		}

		@Override
		public String marshal(InetSocketAddress v) throws Exception {
			return v.getHostString() + ":" + v.getPort();
		}

	}
}
