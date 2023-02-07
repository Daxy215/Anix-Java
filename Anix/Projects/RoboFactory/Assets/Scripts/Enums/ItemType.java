package Enums;

public enum ItemType {
	Log("A pretty log.", 20, true), Stone("Just some stone..", 0, false),
	Metal("It's metal..", 0, false), IronBar("", 0, false), IronPlate("", 0, false),
	Lithium("Lithium to..?", 0, false),
	Sappling("Sapplings are pretty, no?", 60, true), Biomass("Some good burning material.", 7, true),
	CopperWire("Wire, yup, just a wire.", 0, false), Cable("A cable to connect things.", 0, false),
	
	Gear("", 0, false);
	
    public String description;
    public float burnRate;
    public boolean burnAble;
	
    private ItemType(String description, float burnRate, boolean burnAble) {
    	this.description = description;
    	this.burnRate = burnRate;
    	this.burnAble = burnAble;
    }
}