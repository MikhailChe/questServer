package quest.model.common.classes.fields;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Класс для хранения набора изменяемых параметров микроконтроллера и того как
 * это должно выглядеть на GUI Служить в основном для изменения отображения в
 * графическом интерфейсе Объекты класса автоматически генерируются при чтении
 * основного XML
 * 
 * @author Mikhail
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class PropertyGroup {
	public static enum Align {
		HORIZONTAL, VERTICAL
	}

	public static enum Style {
		div, table
	}

	@XmlAttribute
	public Align align = Align.HORIZONTAL;

	@XmlAttribute
	public String name = "Group name";

	@XmlAttribute
	public Style style = Style.div;

	@XmlElement(nillable = false)
	public List<Property> property = new ArrayList<>();

	@XmlElement(nillable = false)
	public List<PropertyGroup> group = new ArrayList<>();

	public List<Property> getProperties() {
		List<Property> props = new ArrayList<>(this.property);

		for (PropertyGroup grp : this.group) {
			props.addAll(grp.getProperties());
		}
		return props;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
