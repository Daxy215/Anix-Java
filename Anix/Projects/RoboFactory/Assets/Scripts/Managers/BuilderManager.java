import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Engine.Editor;
import com.Anix.Engine.Utils.FileUtils;
import com.Anix.Objects.GameObject;

public class BuilderManager extends Behaviour {
	/*
	 * Please ignore this tyvm :)
	 */
	private static final long serialVersionUID = 1L;	

	public enum BuildingType {
		MainBuilding, Fence, Storage, Furnace, CoalGenerator, SolarPanel, Battery, Pole, Miner, Rail, Tower
	}

	@Override
	public void start() {
		String path = "C:\\Users\\smsmk\\Documents\\Anix-Java\\Anix\\Projects\\RoboFactory\\Assets\\textures\\Buildings\\buildings.txt";

		try {
			FileInputStream is = (FileInputStream) Editor.getInputStream(path);
			String[] fileData = FileUtils.loadAsString(is).split("\n");

			for(int i = 0; i < fileData.length; i++) {
				String line = fileData[i];

				if(line.startsWith("#") || line.isEmpty())
					continue;

				String[] data = line.split(":");
				String[] levelsData = line.split("levels<")[1].split(">")[0].split(",");
				String[] requirementData = line.split("requirements<")[1].split(">")[0].split(",");

				BuildingType buildingType = BuildingType.valueOf(data[0].split(",")[0]);

				String name = data[2].split(",")[0];
				float health = Float.parseFloat(levelsData[0].split(":")[0]);
				boolean isRotateable = Boolean.parseBoolean(levelsData[0].split(":")[1]);
				float electrictyRequired = Float.parseFloat(levelsData[0].split(":")[2]);

				GameObject obj = new GameObject(name);

				switch(buildingType) {
				case MainBuilding:
					Buildings.MainBuilding building = obj.addBehaviour(Buildings.MainBuilding.class);
					building.health = health;
					building.electricityRequired = electrictyRequired;
					building.isRotateable = isRotateable;
					//building.showRadius = Integer.parseInt(levelsData[0].split(":")[3]);
					
					addRequirements(requirementData, building);
					//BuilderManager.instance.buildings.Add(building);

					break;
                case Fence:
                	Buildings.Fence fence = obj.addBehaviour(Buildings.Fence.class);
                    fence.health = health;
                    fence.electricityRequired = electrictyRequired;
                    fence.isRotateable = isRotateable;
                    
                    addRequirements(requirementData, fence);
                    //BuilderManager.instance.buildings.Add(fence);
                    
                    break;
                case Storage:
                	Buildings.Storage storage = obj.addBehaviour(Buildings.Storage.class);
                    storage.health = health;
                    storage.electricityRequired = electrictyRequired;
                    storage.isRotateable = isRotateable;
                    //storage.maxCarry = int.Parse(levelsData[0].Split(":")[3]);

                    addRequirements(requirementData, storage);
                   // BuilderManager.instance.buildings.Add(storage);

                    break;
                case Furnace:
                	Buildings.Furnace furnace = obj.addBehaviour(Buildings.Furnace.class);
                    furnace.health = health;
                    furnace.electricityRequired = electrictyRequired;
                    furnace.isRotateable = isRotateable;

                    addRequirements(requirementData, furnace);
                    //BuilderManager.instance.buildings.Add(furnace);

                    break;
                case CoalGenerator:
                	Buildings.CoalGenerator generator = obj.addBehaviour(Buildings.CoalGenerator.class);
                    generator.health = health;
                    generator.electricityRequired = electrictyRequired;
                    generator.isRotateable = isRotateable;
                    
                    addRequirements(requirementData, generator);
                    //BuilderManager.instance.buildings.Add(generator);
                    
                    break;
                case SolarPanel:
                	Buildings.SolarPanel panel = obj.addBehaviour(Buildings.SolarPanel.class);
                    panel.health = health;
                    panel.electricityRequired = electrictyRequired;
                    panel.isRotateable = isRotateable;
                    //panel.electricityGenerateRate = Float.parseFloat(levelsData[0].split(":")[3]);
                    //panel.electricityGenerationTime = Float.parseFloat(levelsData[0].split(":")[4]);
                    //panel.maxElctricity = Float.parseFloat(levelsData[0].split(":")[5]);

                    addRequirements(requirementData, panel);
                    //BuilderManager.instance.buildings.Add(panel);

                    break;
                case Battery:
                	Buildings.Battery battery = obj.addBehaviour(Buildings.Battery.class);
                    battery.health = health;
                    battery.electricityRequired = electrictyRequired;
                    battery.isRotateable = isRotateable;
                    
                    addRequirements(requirementData, battery);
                    //BuilderManager.instance.buildings.Add(battery);

                    break;
                case Pole:
                	Buildings.Pole pole = obj.addBehaviour(Buildings.Pole.class);
                    pole.health = health;
                    pole.electricityRequired = electrictyRequired;
                    pole.isRotateable = isRotateable;

                    addRequirements(requirementData, pole);
                    //BuilderManager.instance.buildings.Add(pole);

                    break;
                case Miner:
                	Buildings.Miner mine = obj.addBehaviour(Buildings.Miner.class);
                    mine.health = health;
                    mine.electricityRequired = electrictyRequired;
                    mine.isRotateable = isRotateable;
                    //mine.maxCarry = Integer.parseInt(levelsData[0].split(":")[3]);
                    //mine.timeBetweenMining = Long.parseLong(levelsData[0].split(":")[4]);

                    addRequirements(requirementData, mine);
                    //BuilderManager.instance.buildings.Add(mine);

                    break;
                case Rail:
                	Buildings.Rail rail = obj.addBehaviour(Buildings.Rail.class);
                    rail.health = health;
                    rail.electricityRequired = electrictyRequired;
                    rail.isRotateable = isRotateable;

                    addRequirements(requirementData, rail);
                   // BuilderManager.instance.buildings.Add(rail);

                    break;
                case Tower:
                    switch (name) {
                        case "Archery":
                        	Buildings.Archery archery = obj.addBehaviour(Buildings.Archery.class);
                            archery.health = health;
                            archery.electricityRequired = electrictyRequired;
                            archery.isRotateable = isRotateable;

                            addRequirements(requirementData, archery);
                            //BuilderManager.instance.buildings.Add(archery);

                            break;
                        case "Turret":
                        	Buildings.Turret turret = obj.addBehaviour(Buildings.Turret.class);
                            turret.health = health;
                            turret.electricityRequired = electrictyRequired;
                            turret.isRotateable = isRotateable;

                            addRequirements(requirementData, turret);
                            //BuilderManager.instance.buildings.Add(turret);
                            
                            break;
                        default:
                            System.err.println("[ERORR] Couldn't find tower name " + name);
                            
                            break;
                    }

                    break;
				default:
					System.err.println("[ERROR] Couldn't find building type of " + buildingType.name());

					break;
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update() {

	}
	
	public void addRequirements(String[] requirementData, Buildings.Building building) {
		for(int i = 0; i < requirementData.length; i++) {			
			String[] requirementLevel = requirementData[i].split(",");
			
			for(int j = 0; j < requirementLevel.length; j++) {
				String[] types = requirementLevel[j].split(";");
				
				List<Buildings.Building.Requirement> requirements = new ArrayList<Buildings.Building.Requirement>();
				
				for(int l = 0; l < types.length; l++) {
					//Item.ItemType type = Item.ItemType.valueOf(types[l].Split(":")[0]);
					int amount = Integer.parseInt(types[l].split(":")[1]);
					
					requirements.add(new Buildings.Building.Requirement(amount/*, type*/));
				}
				
				building.requirements.add(requirements);
			}
		}
	}
}