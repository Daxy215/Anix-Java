package Buildings;

public class Generator extends Building {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;
	
	public float currentElectricity;
	
	public float maxCapacity;
	
	public Generator() {
		
	}
	
	public Generator(float maxCapcity) {
		this.maxCapacity = maxCapcity;
	}
}