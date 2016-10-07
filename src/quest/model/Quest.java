package quest.model;

public class Quest {
	private static Quest instance;

	public static synchronized Quest inst() {
		if (instance == null) {
			instance = new Quest();
		}
		return instance;
	}

	public TenzoController	tenzo		= new TenzoController();
	public InfoPaper		infoPaper	= new InfoPaper();

	private Quest() {

	}

}


