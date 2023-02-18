package Buildings;

import java.util.ArrayList;
import java.util.List;

import com.Anix.Behaviours.BoxCollider2D;
import com.Anix.IO.Time;
import com.Anix.Objects.GameObject;

import Buildings.Spelter.OreData;
import Managers.BuilderManager;
import Managers.BuilderManager.PlacementData;

public class Merger extends Building {
private static final long serialVersionUID = 1L;
	
	public float speed = 5f;
	
	public List<OreData> ores;
	
	public Merger() {
		
	}
	
	public Merger(float speed) {
		this.speed = speed;
	}
	
	@Override
	public void start() {
		ores = new ArrayList<>();
		
		if(Spelter.globalOres == null)
			Spelter.globalOres = new ArrayList<>();
		
		requestUpdate();
	}
	
	@Override
	public void update() {
		for(int i = 0; i < ores.size(); i++) {
			OreData d = ores.get(i);
			
			d.obj.addPosition(gameObject.getForward().x * speed * Time.deltaTime, gameObject.getForward().y * speed * Time.deltaTime);
		}
	}
	
	@Override
	public void onCollisionStay(GameObject other) {
		OreData d = new OreData(0, other);
		
		if(!Spelter.globalOres.contains(d) && !ores.contains(d) && other.getBehaviour(ConveyorBelt.class) == null) {
			ores.add(d);
			Spelter.globalOres.add(d);
		}
	}
	
	@Override
	public void onCollisionExit(GameObject other) {
		OreData d = new OreData(0, other);
		
		ores.remove(d);
		Spelter.globalOres.remove(d);
	}
	
	@Override
	public void startPlacing(PlacementData placementData) {
		Merger c = (Merger) placeBuilding(placementData.startPos, BuilderManager.get(this));
		placementData.cancel();
		
		c.gameObject.addBehaviour(new BoxCollider2D(true));
	}
}
