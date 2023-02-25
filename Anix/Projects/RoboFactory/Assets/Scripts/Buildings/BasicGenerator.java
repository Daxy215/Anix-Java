package Buildings;

import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.IO.Time;

import Managers.BuilderManager;
import Managers.BuilderManager.ElectricityProduceData;
import Managers.BuilderManager.PlacementData;
import PlayerP.InventoryManager.Inventory;

public class BasicGenerator extends Generator {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;
	
	private float timer;
	
	public Inventory inventory;
	
	public BasicGenerator() {
		super();
	}
	
	public BasicGenerator(float maxElectricity) {
		super(maxElectricity);
	}
	
	@Override
	public void start() {
		inventory = (Inventory) gameObject.addBehaviour(new Inventory());
		inventory.sizeX = 1;
		inventory.sizeY = 1;
		
		inventory.updateSlots();
		requestUpdate();
	}
	
	@Override
	public void update() {
		super.update();
		
		if(Input.isKeyDown(KeyCode.J))
			inventory.toggle();
		
		if(inventory.isEmpty())
			return;
		
		timer += Time.deltaTime;
		
		if(timer > 1.0f) {
			timer = 0;
			
			if(currentElectricity < maxElectricity && inventory.removeItem(0, inventory.slots.get(0).getItem().getItemType().burnRate)) {
				currentElectricity = maxElectricity;
			}
		}
	}
	
	@Override
	public void startPlacing(PlacementData placementData) {
		BasicGenerator g = (BasicGenerator) placeBuilding(placementData.startPos, BuilderManager.get(this));
		placementData.cancel();
		
		BuilderManager.electricityProcedurable.add(new ElectricityProduceData(1, true, g));
	}
}