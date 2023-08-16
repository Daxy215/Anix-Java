import com.Anix.Engine.Graphics.Vertex;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;
import java.util.Arrays;

public class Chunk {
	public static enum BlockType {
		Air, Grass, Dirt, Stone
	}
	
	public byte LODLevel;
	
	public int x, y, z;
	
	private BlockType[][][] blocks;
	
	public Chunk() {
		
	}
	
	public Chunk(byte LODLevel, int x, int y, int z) {
		this.LODLevel = LODLevel;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void generateBlocks() {
		blocks = new BlockType[World.chunkSizeX+2][World.chunkSizeY][World.chunkSizeZ+2];
		
		for(int xx = 0; xx < World.chunkSizeX+2; xx++) {
			for(int yy = 0; yy < World.chunkSizeY; yy++) {
				for(int zz = 0; zz < World.chunkSizeZ+2; zz++) {
					if(blocks == null)
						return;
					
					blocks[xx][yy][zz] = GetBlockType(xx+x-1, yy+y, zz+z-1);
					
					/*float val = World.fs.GetNoise(xx+x, yy+y, zz+z);
					 
					//System.out.println("val " + val);g
					
					if(val > 0.5f)
						blocks[xx][yy][zz] = BlockType.Grass;
					else
						blocks[xx][yy][zz] = BlockType.Air;*/
				}
			}
		}
	}
	
	public void generateVertices(World.Data data) {
	    generateBlocks();

	    if (blocks == null)
	        return;

	    for (int x = 1; x < World.chunkSizeX + 1; x++) {
	        for (int z = 1; z < World.chunkSizeZ + 1; z++) {
	            for (int y = 0; y < World.chunkSizeY; y++) {
	                BlockType block = blocks[x][y][z];

	                if(block == BlockType.Air)
						continue;
	                
	                int size = data.vertices.size();
					
					generateVertices(x, y, z, data);
					generateIndices(size, data);
	            }
	        }
	    }
	}

	public void generateVertices(int x, int y, int z, World.Data data) {
		//Back
		if(blocks[x][y][z - 1] == BlockType.Air) {
			if(LODLevel == 0) {
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, -0.5f+z), World.normal, World.topLeftCoord));
				//data.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, -0.5f+z), World.normal, bottomLeftCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, -0.5f+z), World.normal, bottomRightCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, -0.5f+z), World.normal, topRightCoord));
			} else if(LODLevel == 1) {
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, -0.5f+z), World.normal, World.topLeftCoord));
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, -0.5f+z), World.normal, World.bottomLeftCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, -0.5f+z), World.normal, bottomRightCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, -0.5f+z), World.normal, topRightCoord));
			} else if(LODLevel == 2) {
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, -0.5f+z), World.normal, World.topLeftCoord));
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, -0.5f+z), World.normal, World.bottomLeftCoord));
				data.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, -0.5f+z), World.normal, World.bottomRightCoord));
				data.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, -0.5f+z), World.normal, World.topRightCoord));
			}
		}
		
		//Front
		if(blocks[x][y][z + 1] == BlockType.Air) {
			if(LODLevel == 0) {
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y,  0.5f+z), World.normal, World.topLeftCoord));
				//data.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y,  0.5f+z), World.normal, bottomLeftCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y,  0.5f+z), World.normal, bottomRightCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y,  0.5f+z), World.normal, topRightCoord));
			} else if(LODLevel == 1) {
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y,  0.5f+z), World.normal, World.topLeftCoord));
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y,  0.5f+z), World.normal, World.bottomLeftCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y,  0.5f+z), World.normal, bottomRightCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y,  0.5f+z), World.normal, topRightCoord));
			} else if(LODLevel == 2) {
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y,  0.5f+z), World.normal, World.topLeftCoord));
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y,  0.5f+z), World.normal, World.bottomLeftCoord));
				data.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y,  0.5f+z), World.normal, World.bottomRightCoord));
				data.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y,  0.5f+z), World.normal, World.topRightCoord));
			}
		}
		
		//RIGHT
		if(blocks[x + 1][y][z] == BlockType.Air) {
			if(LODLevel == 0) {
				data.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, -0.5f+z), World.normal, World.topLeftCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, -0.5f+z), World.normal, bottomLeftCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y,  0.5f+z), World.normal, bottomRightCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y,  0.5f+z), World.normal, topRightCoord));
			} else if(LODLevel == 1) {
				data.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, -0.5f+z), World.normal, World.topLeftCoord));
				data.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, -0.5f+z), World.normal, World.bottomLeftCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y,  0.5f+z), World.normal, bottomRightCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y,  0.5f+z), World.normal, topRightCoord));
			} else if(LODLevel == 2) {
				data.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, -0.5f+z), World.normal, World.topLeftCoord));
				data.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, -0.5f+z), World.normal, World.bottomLeftCoord));
				data.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y,  0.5f+z), World.normal,World. bottomRightCoord));
				data.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y,  0.5f+z), World.normal, World.topRightCoord));
			}
		}
		
		//LEFT
		if(blocks[x - 1][y][z] == BlockType.Air) {
			if(LODLevel == 0) {
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, -0.5f+z), World.normal, World.topLeftCoord));
				//data.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, -0.5f+z), World.normal, bottomLeftCoord));
				//data.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y,  0.5f+z), World.normal, bottomRightCoord));
				//data.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y,  0.5f+z), World.normal, topRightCoord));
			} else if(LODLevel == 1) {
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, -0.5f+z), World.normal, World.topLeftCoord));
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, -0.5f+z), World.normal, World.bottomLeftCoord));
				//data.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y,  0.5f+z), World.normal, bottomRightCoord));
				//data.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y,  0.5f+z), World.normal, topRightCoord));
			} else if(LODLevel == 2) {
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, -0.5f+z), World.normal, World.topLeftCoord));
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, -0.5f+z), World.normal, World.bottomLeftCoord));
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y,  0.5f+z), World.normal, World.bottomRightCoord));
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y,  0.5f+z), World.normal, World.topRightCoord));
			}
		}
		
		//TOP
		if(y < World.chunkSizeY - 1 && blocks[x][y + 1][z] == BlockType.Air) {
			if(LODLevel == 0) {
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y,  0.5f+z), World.normal, World.topLeftCoord));
				//data.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, -0.5f+z), World.normal, bottomLeftCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, -0.5f+z), World.normal, bottomRightCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y,  0.5f+z), World.normal, topRightCoord));
			} else if(LODLevel == 1) {
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y,  0.5f+z), World.normal, World.topLeftCoord));
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, -0.5f+z), World.normal, World.bottomLeftCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, -0.5f+z), World.normal, bottomRightCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y,  0.5f+z), World.normal, topRightCoord));
			} else if(LODLevel == 2) {
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y,  0.5f+z), World.normal, World.topLeftCoord));
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x,  0.5f+y, -0.5f+z), World.normal, World.bottomLeftCoord));
				data.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y, -0.5f+z), World.normal, World.bottomRightCoord));
				data.vertices.add(new Vertex(new Vector3f( 0.5f+x,  0.5f+y,  0.5f+z), World.normal, World.topRightCoord));
			}
		}
		
		//BOTTOM
		if(y > 0 && blocks[x][y - 1][z] == BlockType.Air) {
			if(LODLevel == 0) {
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y,  0.5f+z), World.normal, World.topLeftCoord));
				//data.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, -0.5f+z), World.normal, bottomLeftCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, -0.5f+z), World.normal, bottomRightCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y,  0.5f+z), World.normal, topRightCoord));
			} else if(LODLevel == 1) {
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y,  0.5f+z), World.normal, World.topLeftCoord));
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, -0.5f+z), World.normal, World.bottomLeftCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, -0.5f+z), World.normal, bottomRightCoord));
				//data.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y,  0.5f+z), World.normal, topRightCoord));
			} else if(LODLevel == 2) {
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y,  0.5f+z), World.normal, World.topLeftCoord));
				data.vertices.add(new Vertex(new Vector3f(-0.5f+x, -0.5f+y, -0.5f+z), World.normal, World.bottomLeftCoord));
				data.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y, -0.5f+z), World.normal, World.bottomRightCoord));
				data.vertices.add(new Vertex(new Vector3f( 0.5f+x, -0.5f+y,  0.5f+z), World.normal, World.topRightCoord));
			}
		}
	}
	
	private void generateIndices(int tl, World.Data data) {
	    int sizeTl = 0;
	    
	    if (LODLevel == 0) {
	        sizeTl = 1;
	    } else if (LODLevel == 1) {
	        sizeTl = 2;
	    } else if (LODLevel == 2) {
	        sizeTl = 6;
	    }
	    
	    for (int i = 0; i < sizeTl; i++) {
	        int baseIndex = tl + i * 4;
	        data.indices.addAll(Arrays.asList(
	            baseIndex, baseIndex + 1, baseIndex + 2,
	            baseIndex, baseIndex + 2, baseIndex + 3
	        ));
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
        //float caveNoise1 = World.fs.GetPerlinFractal(f*5f, g*10f, h*5f) * 15;
        //float caveMask = World.fs.GetSimplex(f * .3f, h * .3f)+.3f * 15;
        
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
        
        //if(caveNoise1 > Math.max(caveMask, 0.2f))
        //   blockType = BlockType.Air;
        
        /*if(blockType != BlockType.Air)
            blockType = BlockType.Stone;*/

        //if(blockType == BlockType.Air && noise.GetSimplex(x * 4f, y * 4f, z*4f) < 0)
          //  blockType = BlockType.Dirt;

        //if(Mathf.PerlinNoise(x * .1f, z * .1f) * 10 + y < TerrainChunk.chunkHeight * .5f)
        //    return BlockType.Grass;

        return blockType;
    }
    
    public void destroy() {
    	blocks = null;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		Chunk other = (Chunk) obj;
		return x == other.x && y == other.y && z == other.z;
	}
}