package Buildings;

import java.util.ArrayList;
import java.util.List;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.SpriteRenderer;
import com.Anix.Engine.Editor;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

import Enums.ItemType;
import Managers.BuilderManager;
import Managers.WorldManager;
import Managers.BuilderManager.PlacementData;

public abstract class Building extends Behaviour {
	private static final long serialVersionUID = 1L;	
	
	public static class Requirement {
		public int amount;
		public ItemType type;
		
		public Requirement() {
			
		}
		
		public Requirement(int amount, ItemType type) {
			this.amount = amount;
			this.type = type;
		}
	}
	
	public int currentLevel = 0;
	
	public float health;
	public float electricityRequired, currentElectricity;
	
	public boolean isRotateable;
	public boolean isUnlocked = true;
	
	public List<List<Requirement>> requirements = new ArrayList<List<Requirement>>();
	
	public Building() {
		
	}
	
	public void updatePlacements(PlacementData placementData) {}
	public void startPlacing(PlacementData placementData) {}
	public void endPlacing(PlacementData placementData) {}
	public void placeBuilding(Vector2f pos) {
		GameObject obj = new GameObject(getName(), new Vector3f(pos.x, pos.y, -2));
		SpriteRenderer sr = new SpriteRenderer();
		sr.spriteName = getName() + "0.png";
		sr.material = WorldManager.material;
		
		obj.addBehaviour(sr);
		obj.getMesh().setMaterial(WorldManager.material);
		obj.addBehaviour(Editor.getBehaviour(getName()));
		
		BuilderManager.placedBuildings.add(pos);
	}
	
 	public String getName() {
		return getClass().getSimpleName();
	}
}