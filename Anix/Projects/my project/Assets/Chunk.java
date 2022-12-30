import com.Anix.Engine.Graphics.Vertex;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;

public class Chunk {
	public static enum BlockType {
		Air, Grass, Dirt, Stone
	}
	
	public float x, y, z;
	
	private BlockType[][][] blocks;
	
	public Chunk() {
		
	}
	
	public Chunk(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		blocks = new BlockType[World.chunkSizeX][World.chunkSizeY][World.chunkSizeZ];
		
		for(int xx = 0; xx < World.chunkSizeX; xx++) {
			for(int yy = 0; yy < World.chunkSizeY; yy++) {
				for(int zz = 0; zz < World.chunkSizeZ; zz++) {
					blocks[xx][yy][zz] = GetBlockType(xx+x, yy+y, zz+z);
				}
			}
		}		
	}
	
	public void generateVertices() {
		BlockType block = null;
		
		System.out.println("Before Size: " + (World.vertices.size()));
		
		for(int x = 0; x < World.chunkSizeX; x++) {
			for(int y = 0; y < World.chunkSizeY; y++) {
				for(int z = 0; z < World.chunkSizeZ; z++) {
					block = blocks[x][y][z];
					
					if(block == BlockType.Air)
						continue;
					
					int size = World.vertices.size();
					
					generateVertices(x, y, z);
					
					int tl = World.vertices.size() - 4 * ((World.vertices.size() - size) / 4);
					
					for(int i = 0; i < 6; i++) {
						World.indices.add(tl + i * 4);
						World.indices.add(tl + i * 4 + 1);
						World.indices.add(tl + i * 4 + 2);
						World.indices.add(tl + i * 4);
						World.indices.add(tl + i * 4 + 2);
						World.indices.add(tl + i * 4 + 3);
					}
				}
			}
		}
		
		System.out.println("Size: " + (World.vertices.size()));
	}
	
	private static Vector3f normal = new Vector3f(0, 0, -1);
	
	private static Vector2f topLeftCoord 	 = new Vector2f(0, 0);
	private static Vector2f bottomLeftCoord  = new Vector2f(0, 1);
	private static Vector2f bottomRightCoord = new Vector2f(1, 1);
	private static Vector2f topRightCoord 	 = new Vector2f(1, 0);
	
	public void generateVertices(int x, int y, int z) {
		//Back
		if(z > 0 && blocks[x][y][z - 1] == BlockType.Air) {
			World.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, -0.5f+z), normal, topLeftCoord));
			World.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, -0.5f+z), normal, bottomLeftCoord));
			World.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, -0.5f+z), normal, bottomRightCoord));
			World.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, -0.5f+z), normal, topRightCoord));
		}
		
		//Front
		if(z < World.chunkSizeZ - 1 && blocks[x][y][z + 1] == BlockType.Air) {
			World.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y,  0.5f+z), normal, topLeftCoord));
			World.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y,  0.5f+z), normal, bottomLeftCoord));
			World.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y,  0.5f+z), normal, bottomRightCoord));
			World.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y,  0.5f+z), normal, topRightCoord));
		}
		
		//RIGHT
		if(x < World.chunkSizeX - 1 && blocks[x + 1][y][z] == BlockType.Air) {
			World.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, -0.5f+z), normal, topLeftCoord));
			World.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, -0.5f+z), normal, bottomLeftCoord));
			World.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y,  0.5f+z), normal, bottomRightCoord));
			World.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y,  0.5f+z), normal, topRightCoord));
		}
		
		//LEFT
		if(x > 0 && blocks[x - 1][y][z] == BlockType.Air) {
			World.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, -0.5f+z), normal, topLeftCoord));
			World.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, -0.5f+z), normal, bottomLeftCoord));
			World.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y,  0.5f+z), normal, bottomRightCoord));
			World.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y,  0.5f+z), normal, topRightCoord));
		}
		
		//TOP
		if(y < World.chunkSizeY - 1 && blocks[x][y + 1][z] == BlockType.Air) {
			World.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y,  0.5f+z), normal, topLeftCoord));
			World.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, -0.5f+z), normal, bottomLeftCoord));
			World.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, -0.5f+z), normal, bottomRightCoord));
			World.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y,  0.5f+z), normal, topRightCoord));
		}
		
		//BOTTOM
		if(y > 0 && blocks[x][y - 1][z] == BlockType.Air) {
			World.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y,  0.5f+z), normal, topLeftCoord));
			World.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, -0.5f+z), normal, bottomLeftCoord));
			World.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, -0.5f+z), normal, bottomRightCoord));
			World.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y,  0.5f+z), normal, topRightCoord));
		}
	}
	
	//get the block type at a specific coordinate
    BlockType GetBlockType(float f, float g, float h) {
        //print(noise.GetSimplex(x, z));
        float simplex1 = World.fs.GetSimplex(f*.8f, h*.8f)*10;
        float simplex2 = World.fs.GetSimplex(f * 3f, h * 3f) * 10*(World.fs.GetSimplex(f*.3f, h*.3f)+.5f);

        float heightMap = simplex1 + simplex2;

        //add the 2d noise to the middle of the terrain chunk
        float baseLandHeight = World.chunkSizeY * .5f + heightMap;

        //3d noise for caves and overhangs and such
        float caveNoise1 = World.fs.GetPerlinFractal(f*5f, g*10f, h*5f);
        float caveMask = World.fs.GetSimplex(f * .3f, h * .3f)+.3f;

        //stone layer heightmap
        float simplexStone1 = World.fs.GetSimplex(f * 1f, h * 1f) * 10;
        float simplexStone2 = (World.fs.GetSimplex(f * 5f, h * 5f)+.5f) * 20 * (World.fs.GetSimplex(f * .3f, h * .3f) + .5f);

        float stoneHeightMap = simplexStone1 + simplexStone2;
        float baseStoneHeight = World.chunkSizeY * .25f + stoneHeightMap;


        //float cliffThing = noise.GetSimplex(x * 1f, z * 1f, y) * 10;
        //float cliffThingMask = noise.GetSimplex(x * .4f, z * .4f) + .3f;
        
        BlockType blockType = BlockType.Air;

        //under the surface, dirt block
        if(g <= baseLandHeight) {
            blockType = BlockType.Dirt;

            //just on the surface, use a grass type
            if(g > baseLandHeight - 1/* && y > WaterChunk.waterHeight-2*/)
                blockType = BlockType.Grass;

            if(g <= baseStoneHeight)
                blockType = BlockType.Stone;
        }


        if(caveNoise1 > Math.max(caveMask, .2f))
            blockType = BlockType.Air;

        /*if(blockType != BlockType.Air)
            blockType = BlockType.Stone;*/

        //if(blockType == BlockType.Air && noise.GetSimplex(x * 4f, y * 4f, z*4f) < 0)
          //  blockType = BlockType.Dirt;

        //if(Mathf.PerlinNoise(x * .1f, z * .1f) * 10 + y < TerrainChunk.chunkHeight * .5f)
        //    return BlockType.Grass;

        return blockType;
    }
}