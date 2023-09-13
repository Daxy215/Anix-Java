import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;

public class VoxelData {
	public static final int chunkWidth  = 5;
	public static final int chunkHeight = 15;
	
	public static final int textureAtlasSizeInBlocks = 4;
	
	public static final Vector3f[] voxelVerts = new Vector3f[] {
		new Vector3f(0, 0, 0),
		new Vector3f(1, 0, 0),
		new Vector3f(1, 1, 0),
		new Vector3f(0, 1, 0),
		new Vector3f(0, 0, 1),
		new Vector3f(1, 0, 1),
		new Vector3f(1, 1, 1),
		new Vector3f(0, 1, 1)
	};
	
	public static final Vector3f[] faceChecks = new Vector3f[] {
		new Vector3f(0, 0, -1), // Back
		new Vector3f(0, 0, 1), // Front
		new Vector3f(0, 1, 0), // Top
		new Vector3f(0, -1, 0), // Bottom
		new Vector3f(-1, 0, 0), // Left
		new Vector3f(1, 0, 0), // Right
	};
	
	public static final int[][] voxelTris = new int[][] {
		{0, 3, 1, 2}, // Back face
		{5, 6, 4, 7}, // Front face
		{3, 7, 2, 6}, // Top faces
		{1, 5, 0, 4}, // Bottom face
		{4, 7, 0, 3}, // Left face
		{1, 2, 5, 6}, // Right face
	};
	
	public static final Vector2f[] voxelUvs = new Vector2f[] {
		new Vector2f(0, 0),
		new Vector2f(0, 1),
		new Vector2f(1, 0),
		new Vector2f(1, 1),
	};

	public static float normalizedBlockTextureSize() {
		return 1f / ((float)textureAtlasSizeInBlocks);
	}
}