package quest.model.quest1;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;

import quest.model.common.classes.MicroUnit;

@XmlAccessorType(XmlAccessType.NONE)
public class QuestXML {
	@XmlElementWrapper(name = "MCUs", nillable = true)
	public List<MicroUnit> units = new ArrayList<MicroUnit>();
}
