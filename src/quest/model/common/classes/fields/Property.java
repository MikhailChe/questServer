package quest.model.common.classes.fields;

import static quest.controller.log.QLog.MsgType.WARNING;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import quest.controller.log.QLog;

@XmlAccessorType(XmlAccessType.NONE)
public class Property implements Comparable<Property> {
	@XmlElement
	public byte address;
	@XmlElement
	String name;
	@XmlElement
	volatile private Object val;
	@XmlElement
	Class<?> type = Object.class;
	@XmlElement
	boolean editable;

	public Property() {
		this((byte) 0, "Стд. свойство", null, false);
	}

	Property(byte address, String name, Object val, boolean editable) {
		this.address = address;
		this.name = name;
		this.val = val;
		if (val != null) {
			this.type = val.getClass();
		}
		this.editable = editable;
	}

	Property(byte address, String name, Class<?> type, boolean editable) {
		this.address = address;
		this.name = name;
		this.type = type;
		this.editable = editable;
	}

	public Class<?> getType() {
		return type;
	}

	public Object getValue() {
		return val;
	}

	public void setValue(Object val2) {
		if (type == null) {
			this.val = val2;

			if (val2 != null) {
				this.type = val2.getClass();
			}
		} else {
			if (type.isInstance(val2)) {
				this.val = val2;
			} else {
				QLog.inst().print("Value not isntanceof " + type + ", is instance of" + val2.getClass().getSimpleName(),
						WARNING);

			}
		}
	}

	public void setValue(byte[] array) {
		System.out.println("Byte array here (for property)");
		if (array.length > 0) {
			if (type.equals(Boolean.class)) {
				Boolean boolval = (array[0] != 0 ? true : false);
				this.val = boolval;
			} else if (type.equals(Byte.class)) {
				Byte byteval = array[0];
				this.val = byteval;
			} else if (type.equals(Short.class)) {
				Short shortval = (short) (Byte.toUnsignedInt(array[0]) + (Byte.toUnsignedInt(array[1]) << 8));
				this.val = shortval;
			} else {
				QLog.inst().print("Тип данных поля не поддерживается.", WARNING);
			}
		} else {
			QLog.inst().print("Массив не содержит данные", WARNING);
		}
	}

	public boolean isEditable() {
		return editable;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public int compareTo(Property o) {
		if (o == null)
			return 1;

		return this.address - o.address;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof Property))
			return false;

		Property f = (Property) o;

		if (f.address == this.address)
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return Byte.hashCode(address);
	}

	public String toString() {

		return String.format("%s%s %s", (editable ? "*" : ""), type.getSimpleName().toString(), val);
	}

}
