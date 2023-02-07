package Buildings;

public class MainBuilding extends Building {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;
	
	public static MainBuilding mainBuilding;
	
	@Override
	public void start() {
		if(mainBuilding != null) {
			System.err.println("[IG-ERROR] A main building already exists! Destroying..");
			gameObject.destroy();
			
			return;
		} else {
			mainBuilding = this;
		}
	}
}