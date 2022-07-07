package com.Anix.Engine.Graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.Anix.Behaviours.Camera;
import com.Anix.Behaviours.LightSource;
import com.Anix.IO.Application;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Math.Matrix4f;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

public final class MasterRenderer {
	//private float offsetX, offsetY;
	//private int matrixSize;
	
	//private GUI gui;
	
	private boolean testRender;
	
	private Mesh mesh;
	private Shader shader;
	
	//private List<GameObject> entitiesToAdd = new ArrayList<>();
	private List<GameObject> combinedObjects;
	private Map<Mesh, List<GameObject>> entities = new HashMap<Mesh, List<GameObject>>();
	
	public MasterRenderer(boolean testRender /*GUI gui*/) {
		//this.gui = gui;
		this.testRender = testRender;
		
		if(testRender) {
			combinedObjects = new ArrayList<>();
		}
		
		//matrixSize = (4 * 4);
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
		if(testRender) {
			r2();
		} else {
			r();
		}
	}
	
	/**
	 * [LWJGL] OpenGL debug message
	ID: 0x500
	Source: API
	Type: ERROR
	Severity: HIGH
	Message: GL_INVALID_ENUM error generated. Invalid target.
	 *	Fixed :D
	 */
	private void r() {
		if(Camera.main == null) {
			return;
		}
		
		//offsetX = gui.getHierachy().getStartX() + gui.getHierachy().getWidth();
		//offsetY = gui.getMenuBar().getStartY() + gui.getMenuBar().getHeight();
		
		//int buffer = GL15.glGenBuffers();
		//GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
		//GL15.glBufferData(GL15.GL_ARRAY_BUFFER, meshes.length * matrixSize, FloatBuffer.allocate(2), GL15.GL_STATIC_DRAW);
		
		Object[] meshes = null;
		
		try {
			meshes = entities.keySet().toArray();
		} catch(Exception e) {
			System.err.println("Error :( " + e.getMessage());
			
			return;
		}
		
		if(meshes == null)
			return;
		
		int amount = 0;
		long start = System.currentTimeMillis();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		for(int i = 0; i < meshes.length; i++) {
			mesh = (Mesh)meshes[i];
			
			if(mesh == null) {
				entities.remove(mesh);
				
				continue;
			}
			
			if(mesh.vertices.length == 0) {
				entities.remove(mesh);
				
				continue;
			}
			
			if(mesh.hasBeenDestoried) {
				entities.remove(mesh);
				
				continue;
			}
			
			if(!mesh.hasBeenCreated())
				mesh.create();
			
			shader = mesh.getMaterial().getShader();
			
			if(shader == null) {
				continue;
			}
			
			shader.bind();
			
			shader.setUniform("view", Camera.main.getViewMatrix());
			shader.setUniform("projection", Application.getProjectionMatrix());
			
			prepareMesh(mesh);
			
			/*light.Color = vec3(1.0, 1.0, 1.0);
		    light.AmbientIntensity = vec3(0.1, 0.1, 0.1);
		    light.DiffuseIntensity = vec3(0.8, 0.8, 0.8);
		    light.Position = vec3(20, 20, 20);*/
			
			shader.setUniform("lightPosition", Camera.main.gameObject.getPosition());
			
			//for(int k = 0; k < LightSource.lights.size(); k++) {
			/*if(LightSource.lights.size() > 0) {
				LightSource light = LightSource.lights.get(0);
				
				shader.setUniform("lightPosition", light.gameObject.getPosition());
				shader.setUniform("lightColor", light.color);
				shader.setUniform("strength", light.strength);
			}*/
			//}
			
			List<GameObject> batch = entities.get(mesh);
			
			if(batch == null) {
				entities.remove(mesh);
				
				return;
			}
			
			for(int j = 0; j < batch.size(); j++) {
				GameObject entity = batch.get(j);
				
				if(entity == null || entity.getMesh() == null) {
					entities.get(mesh).remove(entity);
					
					continue;
				}
				
				if(entity.shouldBeRemoved) {
					entities.get(mesh).remove(entity);
					
					continue;
				}
				
				if(entity.getMesh() != null && !entity.getMesh().equals(mesh)) { //If object's mesh has been changed. Update it.
					if(entities.get(mesh).remove(entity)) {
						addEntity(entity);
					}
					
					continue;
				}
				
				if(!entity.isEnabled()) {
					continue;
				}
								
				//if(MathD.distanceBetweenVector2(entity.getPosition().getXY(), Camera.main.gameObject.getPosition().getXY()) > 30 + Camera.main.gameObject.getPosition().z - 10)
				//	continue;
				
				//Slow method.
				/*Vector3f pos = Camera.main.convertWorldToScreenSpace(entity.getPosition());
				
				if(pos.x > Application.getFullWidth() + 64 || pos.x < -64
						|| pos.y > Application.getFullHeight() + 64|| pos.y < -64) {
					continue;
				}*/
				
				shader.setUniform("color", mesh.getMaterial().getColor());
				
				if(entity.getTransform() == null) {
					System.err.println("[ERROR] Couldn't render a GameObject with the name of " + entity.getName());
					
					continue;
				}
				
				shader.setUniform("model", entity.getTransform());
				
				//int buffer = ;
			    //glGenBuffers(1, &buffer);
			    //glBindBuffer(GL_ARRAY_BUFFER, buffer);
			    //glBufferData(GL_ARRAY_BUFFER, amount * sizeof(glm::mat4), &modelMatrices[0], GL_STATIC_DRAW);
				
				//FloatBuffer matrix = FloatBuffer.allocate(4 * 4);
				
				//int buffer = GL15.glGenBuffers();
				//GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
				//GL15.glBufferData(GL15.GL_ARRAY_BUFFER, entity.getTransform().getAll(), GL15.GL_STATIC_DRAW);
				
				if(GLFW.glfwGetCurrentContext() != Application.getWindow()) {
					System.err.println("cannot share between contexts.. " + GLFW.glfwGetCurrentContext() + " - " + Application.getWindow());
					
					return;
				}
				
				GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndices().length, GL11.GL_UNSIGNED_INT, 0);
			}
			
			//GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, mesh.getIndices().length, GL11.GL_UNSIGNED_INT, 0, batch.size());
			
			unBindMesh(mesh);
			
			shader.unbind();
			amount++;
		}
		
		if(Input.isKeyDown(KeyCode.L)) {
			System.err.println("Took " + (Math.abs(System.currentTimeMillis() - start) + "ms to render " + amount + " entities"));
		}
		//System.err.println("rendered: " + amount + " entities");
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	/**
	 * Test rendering :)
	 */
	private void r2() {
		if(Camera.main == null) {
			return;
		}
		
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
			
			shader.setUniform("view", Matrix4f.view(Camera.main.gameObject.getPosition(), Camera.main.gameObject.getRotation()));
			shader.setUniform("projection", Application.getProjectionMatrix());
			
			if(LightSource.lights.size() > 0) {
				LightSource light = LightSource.lights.get(0);
				
				shader.setUniform("lightPosition", light.gameObject.getPosition());
				shader.setUniform("lightColor", light.color);
				shader.setUniform("strength", light.strength);
			}
			
			prepareMesh(mesh);
			
			shader.setUniform("color", mesh.getMaterial().getColor());

			if(entity.getTransform() == null) {
				System.err.println("[ERROR] Couldn't render a GameObject with the name of " + entity.getName());

				continue;
			}

			shader.setUniform("model", entity.getTransform());

			GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndices().length, GL11.GL_UNSIGNED_INT, 0);

			unBindMesh(mesh);

			shader.unbind();
		}
	}
	
	@SuppressWarnings("unused")
	private GameObject combineObjects(List<GameObject> batch) {
		if(batch.isEmpty())
			return null;
		
		GameObject e = batch.get(0);
		
		int index = 0;
		
		while(e.getMesh() == null && index < batch.size()) {
			e = batch.get(index);
			
			index++;
		}
		
		GameObject entity = e.clone();
		Mesh mesh = entity.getMesh().clone();
		
		if(mesh == null) {
			return null;
		}
		
		List<Vertex> vertices = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		
		for(int j = 0; j < batch.size(); j++) {
			Vector3f pos = e.getPosition().copy();
			
			int vertsSize = mesh.getVertices().length;
			int indcSize = mesh.getIndices().length;
			
			for(int l = 0; l < vertsSize || l < indcSize; l++) {
				if(l < vertsSize) {
					//Vertex v = new Vertex(
					//		new Vector3f(pos.x + mesh.getVertices()[l].getPosition().x, pos.y + mesh.getVertices()[l].getPosition().y, mesh.getVertices()[l].getPosition().z),
					//		mesh.getVertices()[l].getTextureCoord());
					//if(e.getName().equals("Player"))
					//	System.err.println("pos: " + v.getPosition() + " " + batch.size());
					
					//vertices.add(v);
				}
				
				if(l < indcSize) {
					indices.add(mesh.getIndices()[l]);
				}
			}
			
			indices.set(indcSize - 2, 1);
			indices.set(indcSize - 1, 2);
		}
		
		mesh.set(vertices, indices);
		entity.setMesh(mesh);
		
		return entity;
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
		
		/*GL20.glEnableVertexAttribArray(2);
		GL20.glVertexAttribPointer(2, 4, GL11.GL_FLOAT, false, matrixSize, 0);
		GL20.glEnableVertexAttribArray(3);
		GL20.glVertexAttribPointer(3, 4, GL11.GL_FLOAT, false, matrixSize, (4));
		GL20.glEnableVertexAttribArray(4);
		GL20.glVertexAttribPointer(4, 4, GL11.GL_FLOAT, false, matrixSize, (2 * 4));
		GL20.glEnableVertexAttribArray(5);
		GL20.glVertexAttribPointer(5, 4, GL11.GL_FLOAT, false, matrixSize, (3 * 4));
		
		GL33.glVertexAttribDivisor(2, 1);
		GL33.glVertexAttribDivisor(3, 1);
		GL33.glVertexAttribDivisor(4, 1);
		GL33.glVertexAttribDivisor(5, 1);*/
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.getIBO());
		
		if(mesh.getSprite().getTexture() == null) {
			mesh.createTexture();
		}
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getSprite().getTexture().getTextureID());
	}
	
	private void unBindMesh(Mesh mesh) {
		GL30.glBindVertexArray(0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		//GL20.glDisableVertexAttribArray(3);
		//GL20.glDisableVertexAttribArray(4);
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
		
		//meshes = entities.keySet().toArray();
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
