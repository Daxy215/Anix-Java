package WorldGeneration;

import com.Anix.Behaviours.Behaviour;

public class BiomeGenerator extends Behaviour {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;
	
	public enum BiomeType {
		OCEAN, BEACH, FOREST, JUNGLE, SAVANNAH, DESERT, SNOW
	}
	
	public static BiomeType getBiomeAt(int nx, int ny) {
		float e = (1.00f * noiseE(1 * nx, 1 * ny)
			   + 1.00f * noiseE(2 * nx, 2 * ny)
			   + 0.27f * noiseE(4 * nx, 4 * ny)
			   + 0.09f * noiseE(8 * nx, 8 * ny)
			   + 0.09f * noiseE(16 * nx, 16 * ny)
			   + 0.03f * noiseE(32 * nx, 32 * ny));
		e = e / (1.00f + 1.00f + 0.00f + 0.00f + 0.00f + 0.00f);
		e = (float) Math.pow(e, 0.5f);
		
		return getBiome(e);
	}
	
	private static float noiseE(float x, float y) {
		return World.fs.GetPerlin(x, y);
	}
	
	private static BiomeType getBiome(double e) {
		/*if     (e < 0.1) return BiomeType.OCEAN;
		else if(e < 0.2) return BiomeType.BEACH;
		else if(e < 0.3) return BiomeType.FOREST;//Forest
		else if(e < 0.5) return BiomeType.JUNGLE;
		else if(e < 0.7) return BiomeType.SAVANNAH;
		else if(e < 0.9) return BiomeType.DESERT;//Desert
		else return BiomeType.SNOW;*/
		
		if     (e < 0.1) return BiomeType.OCEAN;
		else if(e < 0.2) return BiomeType.BEACH;
		else if(e < 0.3) return BiomeType.FOREST;//Forest
		else if(e < 0.5) return BiomeType.JUNGLE;
		else if(e < 0.7) return BiomeType.SAVANNAH;
		else if(e < 0.9) return BiomeType.DESERT;//Desert
		else return BiomeType.SNOW;
	}
}