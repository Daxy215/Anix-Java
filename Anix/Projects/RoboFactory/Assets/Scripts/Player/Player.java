package Player;

import com.Anix.Behaviours.Behaviour;

import Managers.WorldManager;
import Player.Inventory.Inventory;

public class Player extends Behaviour {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;
	
	public static Inventory inventory;
	
	@Override
	public void awake() {
		inventory = (Inventory)gameObject.getBehaviour(Inventory.class);
	}
	
	@Override
	public void start() {
		gameObject.getMesh().setMaterial(WorldManager.material);
	}
}