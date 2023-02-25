package Buildings;

import com.Anix.Behaviours.Camera;
import com.Anix.GUI.UI;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Math.Color;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;

import Managers.BuilderManager;
import Managers.BuilderManager.ElectricityProduceData;
import Managers.BuilderManager.PlacementData;

public class Cable extends Building {
	private static final long serialVersionUID = 1L;
	
	public Building source, target;
	
	public Cable() {
		
	}
	
	public Cable(int maxElectricity) {
		this.maxElectricity = maxElectricity;
	}
	
	@Override
	public void start() {
		requestUpdate();
	}
	
	@Override
	public void update() {
		if(source != null && target != null) {
			Vector3f sPos = Camera.main.convertWorldToScreenSpace(source.gameObject.getPosition());
			Vector3f tPos = Camera.main.convertWorldToScreenSpace(target.gameObject.getPosition());
			
			UI.drawline(sPos.x, sPos.y, 0.01f, tPos.x, tPos.y, 0.01f, currentElectricity > 0 ? Color.red : Color.black, 50);
		}
	}
	
	@Override
	public void updatePlacements(PlacementData placementData) {
		if(source != null && target == null) {
			Vector3f bPos = Camera.main.convertWorldToScreenSpace(source.gameObject.getPosition());
			
			UI.drawline(bPos.x, bPos.y, 0.01f, (float)Input.getMouseX(), (float)Input.getMouseY(), 0.01f, Color.red, 50);
		} else if(source == null && target != null) {
			Vector3f bPos = Camera.main.convertWorldToScreenSpace(target.gameObject.getPosition());
			//Vector2f p = Camera.main.convertScreenToWorldSpace();
			
			//UI.addLine(target.gameObject.getPosition().x, target.gameObject.getPosition().y, p.x, p.y, Color.red);
			UI.drawline(bPos.x, bPos.y, 0.01f, (float)Input.getMouseX(), (float)Input.getMouseY(), 0.01f, Color.red, 50);
		} else if(source != null && target != null) {
			Cable c = (Cable) BuilderManager.get(this);
			c.source = source;
			c.target = target;
			source = null;
			target = null;
			
			if(c.source instanceof SourceAble) {
				//TODO: Check if such a connection already exists.
				((SourceAble)c.source).addCable(c);
			}
			
			if(c.target instanceof SourceAble) {
				//TODO: Check if such a connection already exists.
				((SourceAble)c.target).addCable(c);
			}
			
			BuilderManager.cables.add(c);
			placementData.cancel();
		}
		
		if(Input.isMouseButtonDown(KeyCode.Mouse1)) {
			source = null;
			target = null;
			placementData.cancel();
		}
	}
	
	@Override
	public void startPlacing(PlacementData placementData) {
		ElectricityProduceData data = BuilderManager.getNearest(Camera.main.convertScreenToWorldSpace(), 2, BuilderManager.electricityProcedurable);
		
		if(data == null) {
			placementData.cancel();
			
			return;
		}
		
		Building b = data.building;
		
		if(source == null && data.canBeSource) {
			source = b;
		} else if(source != null && source != b || !data.canBeSource) {
			target = b;
		} else {
			//Rest everything.
			source = null;
			target = null;
			
			placementData.cancel();
		}
	}
}
