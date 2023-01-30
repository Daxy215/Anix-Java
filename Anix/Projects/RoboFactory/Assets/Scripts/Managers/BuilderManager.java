import java.util.ArrayList;
import java.util.List;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.Camera;
import com.Anix.Behaviours.SpriteRenderer;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

import Buildings.Building;

public class BuilderManager extends Behaviour {
	/*
	 * Please ignore this tyvm :)
	 */
	private static final long serialVersionUID = 1L;	
	
	public boolean isBuilding;
	public Building selectedBuilding;
	
	private GameObject placeHolder;
	
	public static BuilderManager instance;
	
	public List<Vector2f> placedBuildings;
	public List<Building> buildings;
	
	@Override
	public void awake() {
		buildings = new ArrayList<Building>();
		placedBuildings = new ArrayList<>();
	}
	
	@Override
	public void start() {
		instance = this;
		
		placeHolder = new GameObject("PlaceHolder");
		placeHolder.setIsEnabled(false);
		placeHolder.addBehaviour(SpriteRenderer.class).spriteName = "hoveredArea.png";
	}
	
	@Override
	public void update() {
		if(Input.isKeyDown(KeyCode.T)) {
			isBuilding = !isBuilding;
		}
		
		placeHolder.setIsEnabled(isBuilding);
		
		if(!isBuilding)
			return;
		
		if(selectedBuilding == null)
			selectedBuilding = buildings.get(0);
		
		Vector2f cord = Camera.main.convertScreenToWorldSpace();
		Vector2f pos = new Vector2f(Math.round(cord.x), Math.round(cord.y));
		placeHolder.setPosition(pos.x, pos.y);
		
		if(Input.isMouseButtonDown(KeyCode.Mouse0)) {
			if(selectedBuilding != null && !placedBuildings.contains(pos)) {
				System.err.println("Placing..");
				
				GameObject obj = new GameObject(selectedBuilding.getName(), new Vector3f(pos.x, pos.y, 2));
				SpriteRenderer sr = new SpriteRenderer();
				sr.spriteName = selectedBuilding.getName() + "0.png";
				
				obj.addBehaviour(sr);
				
				placedBuildings.add(pos);
			}
		}
	}
}