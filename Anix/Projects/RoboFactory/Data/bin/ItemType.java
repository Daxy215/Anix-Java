package Enums;

public enum ItemType {
	Log("A pretty log.", 20, true), Stone("Just some stone.."),
	Metal("It's metal.."), IronBar(), IronPlate(),
	Lithium("Lithium to..?", 0, false),
	Sappling("Sapplings are pretty, no?", 60, true), Biomass("Some good burning material.", 7, true),
	CopperWire("Wire, yup, just a wire."), Cable("A cable to connect things."),
	
	Chip(), Gear(), Motor();
	
    public String description;
    public int burnRate;
    public boolean burnAble;
	
    ItemType() {
    	description = "";
    	burnRate = 0;
    	burnAble = false;
    }
    
    private ItemType(String description) {
    	this.description = description;
    	this.burnRate = 0;
    	this.burnAble = false;
    }
    
    private ItemType(String description, int burnRate, boolean burnAble) {
    	this.description = description;
    	this.burnRate = burnRate;
    	this.burnAble = burnAble;
    }
}