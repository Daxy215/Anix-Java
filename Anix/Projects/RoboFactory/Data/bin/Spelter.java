package Buildings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.Anix.Behaviours.BoxCollider2D;
import com.Anix.IO.Time;
import com.Anix.Math.Vector2f;
import com.Anix.Objects.GameObject;

import Managers.BuilderManager;
import Managers.BuilderManager.PlacementData;

public class Spelter extends Building {
	private static final long serialVersionUID = 1L;
	
	public static class OreData {
		public int direction;
		public GameObject obj;
		
		public OreData(int direction, GameObject obj) {
			this.direction = direction;
			this.obj = obj;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			OreData other = (OreData) obj;
			return Objects.equals(this.obj, other.obj);
		}
	}
	
	public int currentDirection;
	public float speed = 5f;
	
	public List<OreData> ores;
	//TODO: Merge this with the 'ConveyorBelt' class.
	public static List<OreData> globalOres = new ArrayList<>();
	
	public Spelter() {
		
	}
	
	public Spelter(float speed) {
		this.speed = speed;
	}
	
	@Override
	public void start() {
		ores = new ArrayList<>();
		
		if(globalOres == null)
			globalOres = new ArrayList<>();
		
		requestUpdate();
	}
	
	@Override
	public void update() {
		for(int i = 0; i < ores.size(); i++) {
			OreData d = ores.get(i);
			
			Vector2f dir = d.direction == 0 ? gameObject.getForward() :
				(d.direction == 1 ? gameObject.getRight() : gameObject.getRight().mul(-1));
			
			d.obj.addPosition(dir.x * speed * Time.deltaTime, dir.y * speed * Time.deltaTime);
		}
	}
	
	@Override
	public void onCollisionStay(GameObject other) {
		OreData d = new OreData(currentDirection, other);
		
		if(!globalOres.contains(d) && !ores.contains(d) && other.getBehaviour(ConveyorBelt.class) == null) {
			ores.add(d);
			globalOres.add(d);
			currentDirection++;
			
			if(currentDirection > 2)
				currentDirection = 0;
		}
	}
	
	@Override
	public void onCollisionExit(GameObject other) {
		OreData d = new OreData(currentDirection, other);
		
		ores.remove(d);
		globalOres.remove(d);
	}
	
	@Override
	public void startPlacing(PlacementData placementData) {
		Spelter c = (Spelter) placeBuilding(placementData.startPos, BuilderManager.get(this));
		placementData.cancel();
		
		c.gameObject.addBehaviour(new BoxCollider2D(true));
	}
}