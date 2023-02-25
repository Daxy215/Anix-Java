package Buildings;

import Managers.BuilderManager;
import Managers.BuilderManager.ElectricityProduceData;
import Managers.BuilderManager.PlacementData;

public class Pole extends SourceAble {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;
	
	public Pole() {
		
	}
	
	public Pole(int maxElectricity) {
		this.maxElectricity = maxElectricity;
	}
	
	@Override
	public void start() {
		requestUpdate();
	}
	
	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public void startPlacing(PlacementData placementData) {
		Pole p = (Pole) placeBuilding(placementData.startPos, BuilderManager.get(this));
		placementData.cancel();
		
		BuilderManager.electricityProcedurable.add(new ElectricityProduceData(4, true, p));
	}
}