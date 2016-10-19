package quest.model.common.classes;

import static quest.controller.log.QLog.MsgType.ERROR;
import static quest.controller.log.QLog.MsgType.INFO;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import quest.controller.QuestStarter;
import quest.controller.log.QLog;
import quest.controller.net.Addresser;
import quest.model.common.classes.fields.Property;
import quest.model.common.classes.fields.PropertyGroup;
import quest.model.common.ifaces.InputByteProcessor;

@XmlAccessorType(XmlAccessType.NONE)
public class MicroUnit implements InputByteProcessor {
	@XmlElement
	String name;
	@XmlElement
	@XmlJavaTypeAdapter(value = InetSocketAddressXmlAdapter.class)
	InetSocketAddress innerAddress;

	@XmlElement
	public List<PropertyGroup> group = new ArrayList<>();

	private Map<Integer, Property> properties = null;

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
		Property prop = getProperty(address);
		if (prop != null) {
			prop.getName();
		}
		return null;
	}

	public void initProperties() {
		synchronized (this) {
			if (this.properties == null) {
				this.properties = new Hashtable<>();
				for (PropertyGroup grp : this.group) {
					for (Property prop : grp.getProperties()) {
						this.properties.put((int) prop.address, prop);
					}
				}
			}
		}
	}

	public Object getField(int address) {

		Property property = getProperty(address);
		if (property != null) {
			return property.getValue();
		}
		return null;
	}

	public void setField(int address, Object val) {

		Property property = getProperty(address);
		if (property != null) {
			property.setValue(val);
		}
	}

	public void updateField(int address, Object newValue) {
		// Object oldValue = getField(address);
		setField(address, newValue);
		this.pcs.firePropertyChange(getFieldName(address), null, newValue);
	}

	public void updateField(int address, byte[] val) {
		QLog.inst().print("Обновляю данные " + this.getName() + ":" + address, INFO);
		setField(address, val);
		this.pcs.firePropertyChange(this.getFieldName(address), null, this.getField(address));
	}

	public void setField(int address, byte[] val) {
		Property property = getProperty(address);
		property.setValue(val);
	}

	public List<Property> getProperties() {
		if (this.properties == null) {
			initProperties();
		}
		return new ArrayList<>(this.properties.values());

	}

	public Property getProperty(int address) {
		if (this.properties == null) {
			initProperties();
		}
		return this.properties.get(address);
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
		return String.format("%-20s\t%s", this.name, this.innerAddress);
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

	public static class InetSocketAddressXmlAdapter extends XmlAdapter<String, InetSocketAddress> {
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
