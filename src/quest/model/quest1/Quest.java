package quest.model.quest1;

import quest.controller.net.Addresser;

public class Quest {
	private static Quest instance;

	public static synchronized Quest inst() {
		if (instance == null) {
			instance = new Quest();
		}
		return instance;
	}

	public SportRings rings = new SportRings(Addresser.getSocketAddress("192.168.243.2", 49));

	public AlarmClock alarmClock = new AlarmClock();

	public StareCase starecase = new StareCase();

	public Cubes cubes = new Cubes();

	public ABCz abc = new ABCz();

	public FlyInfoPaper infoPaper = new FlyInfoPaper(Addresser.getSocketAddress("192.168.243.3", 49));

	public UvRobot robot = new UvRobot();

	public LockerKnocker locker = new LockerKnocker();

	public RuneAndSword sword = new RuneAndSword();

	public SwordInCage cage = new SwordInCage();

	public RingAndRed ringRed = new RingAndRed();

	public BoogieMan boogieMan = new BoogieMan();

	private Quest() {

	}

}
