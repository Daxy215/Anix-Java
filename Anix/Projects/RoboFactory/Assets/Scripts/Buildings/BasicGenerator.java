package Buildings;

import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.IO.Time;
import com.Anix.Math.Vector2f;

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
	
	public BasicGenerator(float maxCapcity) {
		super(maxCapcity);
	}
	
	@Override
	public void start() {
		requestUpdate();
		
		inventory = (Inventory) gameObject.addBehaviour(new Inventory());
		inventory.sizeX = 1;
		inventory.sizeY = 1;
		
		inventory.updateSlots();
	}
	
	@Override
	public void update() {
		if(Input.isKeyDown(KeyCode.J))
			inventory.toggle();
		
		if(inventory.isEmpty())
			return;
		
		timer += Time.deltaTime;
		
		if(timer > 1.0f) {
			timer = 0;
			
			if(inventory.removeItem(0, inventory.slots.get(0).getItem().getItemType().burnRate)) {
				currentElectricity = maxCapacity;
			}
		}
	}
	
	@Override
	public void startPlacing(PlacementData placementData) {
		placeBuilding(placementData.startPos);
	}
}