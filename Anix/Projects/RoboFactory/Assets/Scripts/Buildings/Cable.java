package Buildings;

import com.Anix.GUI.UI;
import com.Anix.IO.Input;
import com.Anix.Math.Color;
import com.Anix.Math.Vector2f;

import Managers.BuilderManager.PlacementData;

public class Cable extends Building {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void start() {
		requestUpdate();
	}
	
	@Override
	public void update() {
		
	}
	
	@Override
	public void updatePlacements(PlacementData placementData) {
		UI.drawline(placementData.startPosScreenSpace.x, placementData.startPosScreenSpace.y, -0.1f,
				(float)Input.getMouseX(), (float)Input.getMouseY(), -0.1f, Color.black, 5);
	}
	
	@Override
	public void startPlacing(PlacementData placementData) {
		
	}
	
	@Override
	public void endPlacing(PlacementData placementData) {
		
	}
}
