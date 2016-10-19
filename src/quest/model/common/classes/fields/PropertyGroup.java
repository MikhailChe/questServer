package quest.model.common.classes.fields;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class PropertyGroup {
	public static enum Align {
		HORIZONTAL, VERTICAL
	}

	@XmlAttribute
	public Align align = Align.HORIZONTAL;

	@XmlAttribute
	public String name = "Group name";

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

}
