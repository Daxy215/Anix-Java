package Buildings;

import java.util.ArrayList;
import java.util.List;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.SpriteRenderer;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

import Enums.ItemType;
import Managers.BuilderManager;
import Managers.BuilderManager.PlacementData;
import Managers.WorldManager;

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
	
	protected float health;
	protected float electricityRequired, currentElectricity, maxElectricity;
	
	public boolean isRotateable;
	public boolean isUnlocked = true;
	
	public List<List<Requirement>> requirements = new ArrayList<List<Requirement>>();
	
	public Building() {
		
	}
	
	public boolean canBePlaced(Vector2f pos) {
		return !BuilderManager.placedBuildingsPositions.contains(pos);
	}
	
	public void updatePlacements(PlacementData placementData) {}
	
	public void startPlacing(PlacementData placementData) {
		placeBuilding(placementData.startPos, this);
		placementData.cancel();
	}
	
	public Building placeBuilding(Vector2f pos, Building b) {
		GameObject obj = new GameObject(getName(), new Vector3f(pos.x, pos.y, -2), BuilderManager.placeHolder.getRotation().copy(), new Vector3f(1));
		SpriteRenderer sr = new SpriteRenderer();
		sr.spriteName = getName() + "0.png";
		sr.material = WorldManager.material;
		
		obj.addBehaviour(sr);
		obj.getMesh().setMaterial(WorldManager.material);
		Building bu = (Building)obj.addBehaviour(b);
		
		BuilderManager.addToPositions(pos.copy());
		
		return bu;
	}
	
 	public String getName() {
		return getClass().getSimpleName();
	}
}
