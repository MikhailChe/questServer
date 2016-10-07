package quest.model;

public class Quest {
	private static Quest instance;

	public static synchronized Quest inst() {
		if (instance == null) {
			instance = new Quest();
		}
		return instance;
	}

	public SportRings rings = new SportRings();

	public AlarmClock alarmClock = new AlarmClock();

	public StareCase starecase = new StareCase();

	public Cubes cubes = new Cubes();

	public ABCz abc = new ABCz();

	public FlyInfoPaper infoPaper = new FlyInfoPaper();

	public UvRobot robot = new UvRobot();

	public LockerKnocker locker = new LockerKnocker();

	public RuneAndSword sword = new RuneAndSword();

	public SwordInCage cage = new SwordInCage();

	public RingAndRed ringRed = new RingAndRed();

	public BoogieMan boogieMan = new BoogieMan();

	private Quest() {

	}

}
