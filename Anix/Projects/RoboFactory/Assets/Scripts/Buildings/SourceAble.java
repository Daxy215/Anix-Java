package Buildings;

import java.util.ArrayList;
import java.util.List;

public class SourceAble extends Building {
	private static final long serialVersionUID = 1;
	
	public static class CableData {
		public boolean isSource;
		public Cable cable;
		
		public CableData(boolean isSource, Cable cable) {
			this.isSource = isSource;
			this.cable = cable;
		}
	}
	
	protected List<CableData> connectedCables = new ArrayList<>();
	
	@Override
	public void awake() {
		if(connectedCables == null)
			connectedCables = new ArrayList<>();
	}
	
	@Override
	public void update() {
		for(int i = 0; i < connectedCables.size(); i++) {
			CableData data = connectedCables.get(i);
			
			if(data.isSource) { //Give electricity
				if(currentElectricity > 0 && data.cable.currentElectricity < data.cable.maxElectricity) {
					data.cable.currentElectricity++;
					currentElectricity--;
				}
			} else { //Take electricity
				if(data.cable.currentElectricity > 0 && currentElectricity < maxElectricity) {
					currentElectricity++;
					data.cable.currentElectricity--;
				}
			}
		}
	}
	
	public void addCable(Cable c) {
		connectedCables.add(new CableData(c.source == this ? true : false, c));
	}
}
