package Buildings;

import com.Anix.Behaviours.BoxCollider2D;
import com.Anix.Behaviours.SpriteRenderer;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.IO.Time;
import com.Anix.Main.Core;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

import Enums.ItemType;
import Managers.BuilderManager;
import Managers.BuilderManager.PlacementData;
import PlayerP.InventoryManager.Inventory;

public class Furnace extends Building {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;
	
	public float time, timer = 3;
	public Inventory inventory;
	
	@Override
	public void start() {
		inventory = (Inventory)gameObject.addBehaviour(Inventory.class);
		
		inventory.sizeX = 1;
		inventory.sizeY = 1;
		inventory.updateSlots();
	
		requestUpdate();
	}
	
	@Override
	public void update() {
		if(Input.isKeyDown(KeyCode.E))
			inventory.toggle();
		
		if(inventory.isEmpty())
			return;
		
		time += Time.deltaTime;
		
		if(time > 2) {
			time = 0;
			
			ItemType type = inventory.slots.get(0).getItem().getItemType();
			
			if(inventory.removeItem(0, 1)) {
				//TODO: Get cooked version instead.
				GameObject obj = new GameObject(type.name(), gameObject.getPosition().copy().add(0, -1, -1), new Vector3f(), new Vector3f(0.5f));
				obj.setTag("oreC");
				SpriteRenderer sr = new SpriteRenderer();
				sr.spriteName = type.name() + ".png";
				
				obj.addBehaviour(sr);
				obj.addBehaviour(new BoxCollider2D(true));
				
				Core.getMasterRenderer().addEntity(obj);
			}
		}
	}
	
	@Override
	public void onCollisionEnter(GameObject other) {
		if(other.getTag().equals("ore")) {
			other.destroy();
			
			inventory.addItem(ItemType.valueOf(other.getName()), 1);
		}
	}
	
	@Override
	public void startPlacing(PlacementData placementData) {
		Furnace c = (Furnace) placeBuilding(placementData.startPos, BuilderManager.get(this));
		placementData.cancel();
		
		c.gameObject.addBehaviour(new BoxCollider2D(true));
	}
}