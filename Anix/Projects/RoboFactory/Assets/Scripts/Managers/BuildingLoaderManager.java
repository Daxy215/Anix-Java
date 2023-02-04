import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Engine.Editor;
import com.Anix.Engine.Utils.FileUtils;
import com.Anix.Objects.GameObject;

import Buildings.Archery;
import Buildings.Battery;
import Buildings.BiomassGenerator;
import Buildings.Building;
import Buildings.CoalGenerator;
import Buildings.Fence;
import Buildings.Furnace;
import Buildings.MainBuilding;
import Buildings.Miner;
import Buildings.Pole;
import Buildings.Rail;
import Buildings.SolarPanel;
import Buildings.Storage;
import Buildings.Turret;
import Enums.ItemType;

public class BuildingLoaderManager extends Behaviour {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;	
	
	//public static enum BuildingType {
	//	MainBuilding, Fence, Storage, Furnace, Fabricatron, BiomassGenerator, CoalGenerator, SolarPanel, Battery, Pole, Miner, Rail, Tower
	//}
	
	@Override
	public void start() {
		/*String path = "C:\\Users\\smsmk\\git\\Anix-Java\\Anix\\Projects\\RoboFactory\\Assets\\textures\\Buildings\\buildings.txt";
		
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
				
				BuildingType buildingType = BuildingType.valueOf(data[1].split(",")[0]);
				
				String name = data[2].split(",")[0];
				float health = Float.parseFloat(levelsData[0].split(":")[0]);
				boolean isRotateable = Boolean.parseBoolean(levelsData[0].split(":")[1]);
				float electrictyRequired = Float.parseFloat(levelsData[0].split(":")[2]);
				
				switch(buildingType) {
				case MainBuilding:
					MainBuilding building = new MainBuilding();
					building.health = health;
					building.isRotateable = isRotateable;
					building.electricityRequired = electrictyRequired;
					//building.showRadius = Integer.parseInt(levelsData[0].split(":")[3]);
					
					addRequirements(requirementData, building);
					BuilderManager.instance.buildings.add(building);
					
					break;
                case Fence:
                	Fence fence = new Fence();
                    fence.health = health;
                    fence.isRotateable = isRotateable;
                    fence.electricityRequired = electrictyRequired;
                    
                    addRequirements(requirementData, fence);
                    BuilderManager.instance.buildings.add(fence);
                    
                    break;
                case Storage:
                	Storage storage = new Storage();
                    storage.health = health;
                    storage.isRotateable = isRotateable;
                    storage.electricityRequired = electrictyRequired;
                    //storage.maxCarry = int.Parse(levelsData[0].Split(":")[3]);
                    
                    addRequirements(requirementData, storage);
                    BuilderManager.instance.buildings.add(storage);
                    
                    break;
                case Furnace:
                	Furnace furnace = new Furnace();
                    furnace.health = health;
                    furnace.isRotateable = isRotateable;
                    furnace.electricityRequired = electrictyRequired;
                    
                    addRequirements(requirementData, furnace);
                    BuilderManager.instance.buildings.add(furnace);
                    
                    break;
                case BiomassGenerator:
                	BiomassGenerator biomassGnerator = new BiomassGenerator();
                	biomassGnerator.health = health;
                	biomassGnerator.electricityRequired = electrictyRequired;
                	biomassGnerator.isRotateable = isRotateable;
                	biomassGnerator.maxCapacity = Float.parseFloat(levelsData[0].split(":")[3]);
                	
                    addRequirements(requirementData, biomassGnerator);
                    BuilderManager.instance.buildings.add(biomassGnerator);
                	
                	break;
                case CoalGenerator:
                	CoalGenerator generator = new CoalGenerator();
                    generator.health = health;
                    generator.isRotateable = isRotateable;
                    generator.electricityRequired = electrictyRequired;
                    
                    addRequirements(requirementData, generator);
                    BuilderManager.instance.buildings.add(generator);
                    
                    break;
                case SolarPanel:
                	SolarPanel panel = new SolarPanel();
                    panel.health = health;
                    panel.isRotateable = isRotateable;
                    panel.electricityRequired = electrictyRequired;
                    //panel.electricityGenerateRate = Float.parseFloat(levelsData[0].split(":")[3]);
                    //panel.electricityGenerationTime = Float.parseFloat(levelsData[0].split(":")[4]);
                    //panel.maxElctricity = Float.parseFloat(levelsData[0].split(":")[5]);
                    
                    addRequirements(requirementData, panel);
                    BuilderManager.instance.buildings.add(panel);
                    
                    break;
                case Battery:
                	Battery battery = new Battery();
                    battery.health = health;
                    battery.isRotateable = isRotateable;
                    battery.electricityRequired = electrictyRequired;
                    
                    addRequirements(requirementData, battery);
                    BuilderManager.instance.buildings.add(battery);
                    
                    break;
                case Pole:
                	Pole pole = new Pole();
                    pole.health = health;
                    pole.isRotateable = isRotateable;
                    pole.electricityRequired = electrictyRequired;
                    
                    addRequirements(requirementData, pole);
                    BuilderManager.instance.buildings.add(pole);
                    
                    break;
                case Miner:
                	Miner mine = new Miner();
                    mine.health = health;
                    mine.isRotateable = isRotateable;
                    mine.electricityRequired = electrictyRequired;
                    //mine.maxCarry = Integer.parseInt(levelsData[0].split(":")[3]);
                    //mine.timeBetweenMining = Long.parseLong(levelsData[0].split(":")[4]);
                    
                    addRequirements(requirementData, mine);
                    BuilderManager.instance.buildings.add(mine);
                    
                    break;
                case Rail:
                	Rail rail = new Rail();
                    rail.health = health;
                    rail.isRotateable = isRotateable;
                    rail.electricityRequired = electrictyRequired;
                    
                    addRequirements(requirementData, rail);
                    BuilderManager.instance.buildings.add(rail);
                    
                    break;
                case Tower:
                    switch (name) {
                        case "Archery":
                        	Archery archery = new Archery();
                            archery.health = health;
                            archery.isRotateable = isRotateable;
                            archery.electricityRequired = electrictyRequired;
                            
                            addRequirements(requirementData, archery);
                            BuilderManager.instance.buildings.add(archery);
                            
                            break;
                        case "Turret":
                        	Turret turret = new Turret();
                            turret.health = health;
                            turret.isRotateable = isRotateable;
                            turret.electricityRequired = electrictyRequired;
                            
                            addRequirements(requirementData, turret);
                            BuilderManager.instance.buildings.add(turret);
                            
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
		}*/
	}
	
	@Override
	public void update() {
		
	}
	
	public void addRequirements(String[] requirementData, Building building) {
		for(int i = 0; i < requirementData.length; i++) {			
			String[] requirementLevel = requirementData[i].split(",");
			
			for(int j = 0; j < requirementLevel.length; j++) {
				String[] types = requirementLevel[j].split(";");
				
				List<Building.Requirement> requirements = new ArrayList<Building.Requirement>();
				
				for(int l = 0; l < types.length; l++) {
					ItemType type = ItemType.valueOf(types[l].split(":")[0]);
					int amount = Integer.parseInt(types[l].split(":")[1]);
					
					//requirements.add(new Building.Requirement(amount, type));
				}
				
				building.requirements.add(requirements);
			}
		}
	}
}