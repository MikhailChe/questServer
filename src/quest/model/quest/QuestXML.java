package quest.model.quest;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;

import quest.model.common.classes.MicroUnit;

/**
 * Основной класс представления конфигурационного XML. Атоматически генерируется
 * на основании XML и наоборот: XML конфигурация может быть сгенерирована по
 * объекту этого класса
 * 
 * @author Mikhail
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class QuestXML {
	@XmlAttribute(required = true, name = "name")
	String name = "Имя квеста";

	@XmlElementWrapper(name = "MCUs", nillable = true)
	public List<MicroUnit> units = new ArrayList<>();

	@Override
	public String toString() {
		return this.name;
	}

	public static QuestXML loadXML(InputStream xml) {
		return JAXB.unmarshal(xml, QuestXML.class);
	}

	public static QuestXML loadXML(File xml) {
		return JAXB.unmarshal(xml, QuestXML.class);
	}

	public QuestXML saveXML(File xml) {
		JAXB.marshal(this, xml);
		return this;
	}

	public QuestXML saveXML(OutputStream xml) {
		JAXB.marshal(this, xml);
		return this;
	}

	public QuestXML saveXML() {
		this.saveXML(new File("quest.xml"));
		return this;
	}
}
