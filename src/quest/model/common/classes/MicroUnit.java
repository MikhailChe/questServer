package quest.model.common.classes;

import static quest.controller.log.QLog.MsgType.ERROR;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
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
		for (Property field : property) {
			if (field.address == address) {
				return field.getName();
			}
		}
		return null;
	}

	public Object getField(int address) {
		for (Property field : property) {
			if (field.address == address) {
				return field.getValue();
			}
		}
		return null;
	}

	public void setField(int address, Object val) {
		for (Property field : property) {
			if (field.address == address) {
				field.setValue(val);
			}
		}
	}

	public void updateField(int address, Object newValue) {
		Object oldValue = getField(address);
		setField(address, newValue);
		pcs.firePropertyChange(getFieldName(address), oldValue, newValue);
	}

	public void setField(int address, byte[] val) {
		for (Property field : property) {
			if (field.address == address) {
				System.out.println("Setting value");
				field.setValue(val);
			}
		}
	}

	public void updateField(int address, byte[] val) {
		System.out.println(this.getName() + " update " + address);
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

	public String toString() {
		return String.format("%-20s\t%s%n\t%s", this.name, this.innerAddress, this.property);
	}

	@Override
	public void processInput(byte[] data) {
		PacketData pack = new PacketData(data);
		if (pack.data.length > 0) {
			this.updateField(pack.perifiral, pack.data);
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);
	}

	protected static void send(DatagramPacket p) {
		if (QuestStarter.udpServer != null) {
			if (QuestStarter.udpServer.socket != null) {
				try {
					QuestStarter.udpServer.socket.send(p);
				} catch (IOException e) {
					QLog.inst().print("Не получилось отправить UDP пакет:" + e.getLocalizedMessage(), ERROR);
				}
			}
		}
	}

	@Deprecated
	public static List<MicroUnit> getMicrounits(Object o) {
		Field[] fields = o.getClass().getFields();
		List<MicroUnit> list = new ArrayList<>();

		Arrays.asList(fields).stream().filter(f -> MicroUnit.class.isAssignableFrom(f.getType()))
				.filter(f -> InputByteProcessor.class.isAssignableFrom(f.getType())).map(f -> {
					try {
						return f.get(o);
					} catch (IllegalAccessException | IllegalArgumentException e) {
						QLog.inst().print(e.getLocalizedMessage(), ERROR);
					}
					return null;
				}).forEach(a -> {
					if (a instanceof MicroUnit) {
						list.add((MicroUnit) a);
					}
				});

		return list;
	}

	@Deprecated
	public static short shortFromByteArray(byte[] arr) {
		int a = 0;
		for (int i = 0; i < arr.length; i++) {
			a += Byte.toUnsignedInt(arr[i]) << (i * 8);
		}
		return (short) a;
	}

	@Deprecated
	public static boolean boolFromByteArray(byte[] arr) {
		return arr[0] != 0 ? true : false;
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
