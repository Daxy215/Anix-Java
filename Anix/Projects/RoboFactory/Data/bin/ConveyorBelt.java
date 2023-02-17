package Buildings;

import java.util.ArrayList;
import java.util.List;

import com.Anix.Behaviours.BoxCollider2D;
import com.Anix.IO.Time;
import com.Anix.Objects.GameObject;

import Managers.BuilderManager;
import Managers.BuilderManager.PlacementData;

public class ConveyorBelt extends Building {
	private static final long serialVersionUID = 1L;
	
	public float speed = 5f;
	public float time, timer;
	
	public List<GameObject> ores;
	public static List<GameObject> globalOres = new ArrayList<>();
	
	public ConveyorBelt() {
		
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
			ores.get(i).addPosition(gameObject.getForward().x * speed * Time.deltaTime, gameObject.getForward().y * speed * Time.deltaTime);
		}
		
		timer += Time.deltaTime;
		
		if(timer > 1) {
			timer = 0;
		}
	}
	
	@Override
	public void onCollisionStay(GameObject other) {
		if(!globalOres.contains(other) && !ores.contains(other) && other.getBehaviour(ConveyorBelt.class) == null) {
			ores.add(other);
			globalOres.add(other);
		}
	}
	
	@Override
	public void onCollisionExit(GameObject other) {
		ores.remove(other);
		globalOres.remove(other);
	}
	
	@Override
	public void startPlacing(PlacementData placementData) {
		ConveyorBelt c = (ConveyorBelt) placeBuilding(placementData.startPos, BuilderManager.get(this));
		placementData.cancel();
		
		c.gameObject.addBehaviour(new BoxCollider2D(true));
	}
}