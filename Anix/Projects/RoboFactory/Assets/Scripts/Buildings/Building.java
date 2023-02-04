package Buildings;

import java.util.ArrayList;
import java.util.List;

import com.Anix.Behaviours.Behaviour;

import Enums.ItemType;

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
	
 	public String getName() {
		return getClass().getSimpleName();
	}
}