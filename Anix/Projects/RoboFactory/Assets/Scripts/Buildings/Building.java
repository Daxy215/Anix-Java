package Buildings;

import java.util.ArrayList;
import java.util.List;

import com.Anix.Behaviours.Behaviour;

public abstract class Building extends Behaviour {
	private static final long serialVersionUID = 1L;	
	
	public static class Requirement {
		public int amount;
		
		public Requirement() {

		}

		public Requirement(int amount) {
			this.amount = amount;
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