import com.Anix.Behaviours.Behaviour;
import com.Anix.GUI.Windows.Console;

public class World extends Behaviour {
	public static class BlockType {
		public String blockName;
		public boolean isSolid;
		
		// Texture Values
		public int backFaceTexture;
		public int frontFaceTexture;
		public int topFaceTexture;
		public int bottomFaceTexture;
		public int leftFaceTexture;
		public int rightFaceTexture;
		
		public BlockType(String blockName, boolean isSolid, int backFaceTexture, int frontFaceTexture,
				int topFaceTexture, int bottomFaceTexture, int leftFaceTexture, int rightFaceTexture) {
			this.blockName = blockName;
			this.isSolid = isSolid;
			this.backFaceTexture = backFaceTexture;
			this.frontFaceTexture = frontFaceTexture;
			this.topFaceTexture = topFaceTexture;
			this.bottomFaceTexture = bottomFaceTexture;
			this.leftFaceTexture = leftFaceTexture;
			this.rightFaceTexture = rightFaceTexture;
		}
		
		public int getTextureId(int faceIndex) {
			switch(faceIndex) {
			case 0:
				return backFaceTexture;
			case 1:
				return frontFaceTexture;
			case 2:
				return topFaceTexture;
			case 3:
				return bottomFaceTexture;
			case 4:
				return leftFaceTexture;
			case 5:
				return rightFaceTexture;
			default:
				Console.Log("[ERROR] getTextureId; Invalid face index");
				
				return 0;
			}
		}
	}
	
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;	
	
	public static BlockType[] blockTypes;
	
	@Override
	public void awake() {
		blockTypes = new BlockType[] {
			new BlockType("Bedrock",   true, 9, 9, 9, 9, 9, 9),
			new BlockType("Stone",     true, 0, 0, 0, 0, 0, 0),
			new BlockType("Grass",     true, 2, 2, 7, 1, 2, 2),
			new BlockType("Furance",   true, 13, 12, 15, 15, 13, 13),
		};
	}
}