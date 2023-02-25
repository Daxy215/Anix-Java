package Buildings;

public class Generator extends SourceAble {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;
	
	public float currentCapacity;
	public float maxElectricityCapacity;
	
	public Generator() {
		
	}
	
	public Generator(float maxElectricity) {
		this.maxElectricity = maxElectricity;
	}
}
