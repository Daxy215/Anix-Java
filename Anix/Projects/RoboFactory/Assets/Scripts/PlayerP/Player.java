package PlayerP;

import com.Anix.Behaviours.Behaviour;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;

import Enums.ItemType;
import Managers.WorldManager;
import PlayerP.InventoryManager.Inventory;

public class Player extends Behaviour {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;
	
	public static Inventory inventory;
	
	public Player() {
		
	}
	
	@Override
	public void awake() {
		inventory = (Inventory)gameObject.getBehaviour(Inventory.class);
	}
	
	@Override
	public void start() {
		gameObject.getMesh().setMaterial(WorldManager.material);
		
		requestUpdate();
	}
	
	@Override
	public void update() {
		if(Input.isKeyDown(KeyCode.I)) {
			inventory.showInventory = !inventory.showInventory;
		}
		
		if(Input.isKey(KeyCode.C))
			inventory.addItem(ItemType.Log, 1);
	}
}