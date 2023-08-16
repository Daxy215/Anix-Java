package com.Anix.Engine.Graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.Anix.Behaviours.Camera;
import com.Anix.IO.Application;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Math.Matrix4f;
import com.Anix.Math.Vector3f;
import com.Anix.Math.Vector4f;
import com.Anix.Objects.GameObject;

public final class MasterRenderer {
    private final Vector4f[] frustumPlanes = new Vector4f[6];
	
	//private boolean testRender;//, updateCombiedObjects;
	
	//private Core core;
	
	private Mesh mesh;
	private Shader shader;
	
	//private List<GameObject> entitiesToAdd = new ArrayList<>();
	private List<GameObject> combinedObjects = new ArrayList<>();
	private Map<Mesh, List<GameObject>> entities = new HashMap<Mesh, List<GameObject>>();
	
	public MasterRenderer(/*Core core, boolean testRender*/) {
		for (int i = 0; i < frustumPlanes.length; i++) {
            frustumPlanes[i] = new Vector4f();
        }
		
		//this.core = core;
		//this.testRender = testRender;
		
		//if(testRender) {
		//	combinedObjects = new ArrayList<>();
		//}
		
		//testShader = UI.loadTexture("/textures/AmbientOcclusionMap.png").getId();
		
		/*Thread thread = new Thread() {
			public void run() {
				System.err.println("starting");
				
				while(!Application.shouldClose()) {
					if(!updateCombiedObjects)
						continue;
					
					System.err.println("updating");
					
					combinedObjects.clear();
					
					Object[] meshes = null;
					
					try {
						meshes = entities.keySet().toArray();
					} catch(Exception e) {
						System.err.println("Error 3:( " + e.getMessage());
						
						continue;
					}
					
					for(int i = 0; i < meshes.length; i++) {
						mesh = (Mesh)meshes[i];
						
						if(!mesh.hasBeenCreated) {
							mesh.create();
						}
						
						List<GameObject> batch = entities.get(mesh);
						
						if(batch == null) {
							entities.remove(mesh);
							
							continue;
						}
						
						GameObject obj = combineObjects(batch);
						
						if(obj != null) {
							entities.remove(mesh);
							combinedObjects.add(obj);
						}
					}
					
					updateCombiedObjects = false;
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		thread.setName("MasterRenderer Thread");
		thread.start();*/
	}
	
	public void update() {
		//boolean addedNew = false;
		
		/*for(int i = 0; i < entitiesToAdd.size(); i++) {
			//addE(entitiesToAdd.get(i));
			
			entitiesToAdd.remove(i);
			i--;
			
			addedNew = true;
		}*/
		
		/*if(addedNew && testRender) {
			combinedObjects.clear();
			
			for(int i = 0; i < meshes.length; i++) {
				Mesh mesm = (Mesh)meshes[i];
				
				if(mesm != null)
					combinedObjects.add(combineObjects(entities.get(mesm)));
			}
		}*/
	}
	
	public void render() {
		//if(Input.isKeyDown(KeyCode.F)) {
		//	testRender = !testRender;
		//	System.err.println("testing rendering: " + testRender);
		//}
		
		//if(testRender) {
			//r2();
		//} else {
		r();
		//}
	}
	
	private void r() {
		if (Camera.main == null) {
		    return;
		}
		
		Matrix4f viewMatrix = Camera.main.getViewMatrix();
		Matrix4f projectionMatrix = Application.getProjectionMatrix();
		Matrix4f viewProjectionMatrix = Matrix4f.multiply(projectionMatrix, viewMatrix);
		
		updateFrustumPlanes(viewProjectionMatrix);
		
		long start = System.currentTimeMillis();
		int meshes = 0;

		for (Iterator<Map.Entry<Mesh, List<GameObject>>> iterator = entities.entrySet().iterator(); iterator.hasNext();) {
		    Map.Entry<Mesh, List<GameObject>> entry = iterator.next();
		    Mesh mesh = entry.getKey();
		    List<GameObject> batch = entry.getValue();
		    
		    if (mesh == null || mesh.hasBeenDestoried ||
		        mesh.vertices == null || mesh.vertices.length == 0 ||
		        mesh.indices == null || mesh.indices.length == 0 ||
		        !mesh.hasBeenCreated() || mesh.getMaterial() == null ||
		        mesh.getMaterial().getShader() == null || batch == null || batch.isEmpty()) {
		        iterator.remove();
		        continue;
		    }
		    
		    Shader shader = mesh.getMaterial().getShader();
		    shader.bind();
		    shader.setUniform("view", Camera.main.getViewMatrix());
		    shader.setUniform("projection", Application.getProjectionMatrix());
		    
		    prepareMesh(mesh);
		    
		    for (Iterator<GameObject> batchIterator = batch.iterator(); batchIterator.hasNext();) {
		        GameObject entity = batchIterator.next();
		        
		        if (entity == null || entity.shouldBeRemoved ||
		            entity.getMesh() == null || !entity.getMesh().equals(mesh) ||
		            !entity.isEnabled() || entity.getTransform() == null) {
		            batchIterator.remove();
		            continue;
		        }
		        
		        if (isObjectInFrustum(entity.getPosition())) {
			        shader.setUniform("model", entity.getTransform());
			        
			        meshes++;
			        
			        if (GLFW.glfwGetCurrentContext() != Application.getWindow()) {
			            System.err.println("cannot share between contexts.. " + GLFW.glfwGetCurrentContext() + " - " + Application.getWindow());
			            return;
			        }
			        
			        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndices().length, GL11.GL_UNSIGNED_INT, 0);
		        }
		    }
		    
		    unBindMesh(mesh);
		    shader.unbind();
		}
		
		if (Input.isKeyDown(KeyCode.L)) {
		    long elapsedTime = Math.abs(System.currentTimeMillis() - start);
		    int amount = entities.values().stream().mapToInt(List::size).sum();
		    int meshesAmount = entities.size();
		    System.err.println("Took " + elapsedTime + "ms to render " + amount + " entities. With (" + meshes + "/" + meshesAmount + ") mesh(es).");
		}

		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	/**
	 * Test rendering :)
	 */
	private void r2() {
		if(Camera.main == null) {
			return;
		}
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		int amount = 0;
		
		for(int l = 0; l < combinedObjects.size(); l++) {
			GameObject entity = combinedObjects.get(l);
			
			if(entity == null)
				continue;
			
			mesh = entity.getMesh();
			
			if(mesh == null) {
				entities.remove(mesh);

				continue;
			}

			shader = mesh.getMaterial().getShader();
			
			if(shader == null) {
				continue;
			}
			
			shader.bind();
			
			shader.setUniform("view", Camera.main.getViewMatrix());
			shader.setUniform("projection", Application.getProjectionMatrix());
			
			prepareMesh(mesh);
			
			//shader.setUniform("lightPosition", Camera.main.gameObject.getPosition());
			
			/*if(LightSource.lights.size() > 0) {
				LightSource light = LightSource.lights.get(0);
				
				shader.setUniform("lightPosition", light.gameObject.getPosition());
				shader.setUniform("lightColor", light.color);
				shader.setUniform("strength", light.strength);
			}*/
			
			shader.setUniform("color", mesh.getMaterial().getColor());
			
			if(entity.getTransform() == null) {
				System.err.println("[ERROR] Couldn't render a GameObject with the name of " + entity.getName());
				
				continue;
			}
			
			shader.setUniform("model", entity.getTransform());
			
			GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndices().length, GL11.GL_UNSIGNED_INT, 0);
			
			unBindMesh(mesh);
			
			shader.unbind();
			
			amount++;
		}
		
		if(Input.isKeyDown(KeyCode.K)) {
			System.err.println("render " + amount);
		}
	}
	
	private GameObject combineObjects(List<GameObject> batch) {
		if(batch.isEmpty())
			return null;
		
		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Integer> indices = new ArrayList<Integer>();
		
	    int offset = 0;
		
		System.err.println("looping through: " + batch.size());
		
		for(int j = 0; j < batch.size(); j++) {
			Matrix4f transformation = batch.get(j).getTransform();
			Mesh mesh = batch.get(j).getMesh();
			
			if(mesh == null)
				return null;
						
	        for(int index = 0; index < mesh.getVertices().length; index++) {
	        	Vector3f position = transformation.multiply(mesh.vertices[index].getPosition());
	            Vector3f normal = transformation.multiply(mesh.vertices[index].getNormal());
	        	
				Vertex vertex = new Vertex();
	            
	            vertex.setPosition(position);
	            vertex.setNormal(normal);
	            vertex.setTextureCoord(mesh.getVertices()[index].getTextureCoord());
	            
		        vertices.add(vertex);
	        }
	        
	        for(int i = 0; i < mesh.indices.length; i++) {
	        	indices.add(mesh.indices[i] + offset);
	        }
	        
	        offset += mesh.vertices.length;
		}
		
		System.err.println("For " + batch.size() + " batch(es). Created a mesh with " + vertices.size() + " vertices and " + indices.size() + " indices");
		
		Mesh mesh = new Mesh(batch.get(0).getMesh().getSprite());
		mesh.set(vertices, indices);
		mesh.create();
		
		return new GameObject("", new Vector3f(), new Vector3f(), new Vector3f(1), mesh, false);
	}
	
	public void render(GameObject gameObject) {
		if(gameObject.shouldBeRemoved || !gameObject.isEnabled() || gameObject.getMesh() == null) {
			return;
		}
		
		Shader shader = Shader.defaultShader;
		
		Mesh mesh = gameObject.getMesh();
		
		shader.bind();
		
		shader.setUniform("view", Matrix4f.view(Camera.main.gameObject.getPosition(), Camera.main.gameObject.getRotation()));
		shader.setUniform("projection", Application.getProjectionMatrix());
		
		prepareMesh(mesh);
		
		shader.setUniform("color", mesh.getMaterial().getColor());
		shader.setUniform("model", gameObject.getTransform());
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndices().length, GL11.GL_UNSIGNED_INT, 0);
		//GL31.glDrawarr(GL11.GL_TRIANGLES, mesh.getIndices().length, GL11.GL_UNSIGNED_INT, 0, 2);
		
		unBindMesh(mesh);
		shader.unbind();
	}
	
	private void prepareMesh(Mesh mesh) {
		GL30.glBindVertexArray(mesh.getVAO());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.getIBO());
		
		if(mesh.getSprite() != null && mesh.getSprite().getTexture() != null) {
			if(mesh.getSprite().getTexture() == null)
				mesh.createTexture();
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getSprite().getTexture().getTextureID());
		}
	}
	
	private void unBindMesh(Mesh mesh) {
		GL30.glBindVertexArray(0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public void addEntity(GameObject entity) {
		if(entity == null) {
			System.err.println("[ERROR] Cannot add a null gameObject.");
			
			return;
		}
		
		Mesh mesh = entity.getMesh();
		
		List<GameObject> batch = entities.get(mesh);
		
		if(batch != null) {
			batch.add(entity);
		} else {
			List<GameObject> newBatch = new ArrayList<GameObject>();
			newBatch.add(entity);
			entities.put(mesh, newBatch);
		}
		
		//System.out.println("added: " + entity.getName() + " - " + entities.size());
	}
	
	public void updateFrustumPlanes(Matrix4f inverseProjectionView) {
        // Calculate the combined projection-view matrix
		//inverseProjectionView.invert();
        
        // Left plane
        frustumPlanes[0].set(inverseProjectionView.m30 + inverseProjectionView.m00,
                             inverseProjectionView.m31 + inverseProjectionView.m01,
                             inverseProjectionView.m32 + inverseProjectionView.m02,
                             inverseProjectionView.m33 + inverseProjectionView.m03);
        
        // Right plane
        frustumPlanes[1].set(inverseProjectionView.m30 - inverseProjectionView.m00,
                             inverseProjectionView.m31 - inverseProjectionView.m01,
                             inverseProjectionView.m32 - inverseProjectionView.m02,
                             inverseProjectionView.m33 - inverseProjectionView.m03);
        
        // Bottom plane
        frustumPlanes[2].set(inverseProjectionView.m30 + inverseProjectionView.m10,
                             inverseProjectionView.m31 + inverseProjectionView.m11,
                             inverseProjectionView.m32 + inverseProjectionView.m12,
                             inverseProjectionView.m33 + inverseProjectionView.m13);
        
        // Top plane
        frustumPlanes[3].set(inverseProjectionView.m30 - inverseProjectionView.m10,
                             inverseProjectionView.m31 - inverseProjectionView.m11,
                             inverseProjectionView.m32 - inverseProjectionView.m12,
                             inverseProjectionView.m33 - inverseProjectionView.m13)
        ;
        // Near plane
        frustumPlanes[4].set(inverseProjectionView.m30 + inverseProjectionView.m20,
                             inverseProjectionView.m31 + inverseProjectionView.m21,
                             inverseProjectionView.m32 + inverseProjectionView.m22,
                             inverseProjectionView.m33 + inverseProjectionView.m23);
        
        // Far plane
        frustumPlanes[5].set(inverseProjectionView.m30 - inverseProjectionView.m20,
                             inverseProjectionView.m31 - inverseProjectionView.m21,
                             inverseProjectionView.m32 - inverseProjectionView.m22,
                             inverseProjectionView.m33 - inverseProjectionView.m23);
        
        // Normalize the planes
        for (int i = 0; i < frustumPlanes.length; i++) {
            frustumPlanes[i] = Vector4f.normlize(frustumPlanes[i]);
        }
    }
	
	public boolean isObjectInFrustum(Vector3f objectPosition) {
        Vector4f objectPos4 = new Vector4f(objectPosition.x, objectPosition.y, objectPosition.z, 1.0f);
        
        for (Vector4f plane : frustumPlanes) {
            if (dot(plane, objectPos4) < 0) {
                return false;  // Object is outside the frustum
            }
        }
        
        return true;  // Object is inside the frustum
    }
	
	public static float dot(Vector4f vector1, Vector4f vector2) {
        return vector1.x * vector2.x + vector1.y * vector2.y + vector1.z * vector2.z + vector1.w * vector2.w;
    }
	
	public void destroy() {
		for(Mesh mesh : entities.keySet()) {
			mesh.destroy();
		}
		
		entities.clear();
	}
	
	public Map<Mesh, List<GameObject>> getEntities() {
		return entities;
	}
}
