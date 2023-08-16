import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.Camera;
import com.Anix.Engine.Graphics.Mesh;
import com.Anix.Engine.Graphics.Shader;
import com.Anix.IO.Application;
import com.Anix.Math.MathD;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

public class CustomRenderer extends Behaviour {
	/*
	 * Please ignore this tyvm :)
	 */
	private static final long serialVersionUID = 1L;	

	@Override
	//Will be called on game start
	public void start() {
		//TODO: Code here..
	}

	@Override
	//Will be called once per tick
	public void update() {
		//TODO: Code here..
	}

	public void render() {
		if(Camera.main == null || World.chunks == null) {
			return;
		}
		
		for(Map.Entry<Chunk, GameObject> entry : World.chunks.entrySet()) {
			GameObject entity = entry.getValue();
			Mesh mesh = entity.getMesh();
			
			if(mesh == null) {
				continue;
			}
			
			if(mesh.hasBeenDestoried()) {
				World.chunks.remove(entry.getKey());
				
				continue;
			}
			
			if(mesh.getVertices() == null || mesh.getVertices().length == 0
					|| mesh.getIndices() == null || mesh.getIndices().length == 0) {
				continue;
			}
			
			if(!mesh.hasBeenCreated())
				continue;
			
			Shader shader = mesh.getMaterial().getShader();
			
			if(shader == null) {
				World.chunks.remove(entry.getKey());
				
				continue;
			}
			
			shader.bind();
			
			shader.setUniform("view", Camera.main.getViewMatrix());
			shader.setUniform("projection", Application.getProjectionMatrix());
			shader.setUniform("color", mesh.getMaterial().getColor());
			
			prepareMesh(mesh);
			
			if(entity == null || entity.getMesh() == null) {
				World.chunks.remove(entry.getKey());
				
				continue;
			}
			
			if(entity.shouldBeRemoved) {
				World.chunks.remove(entry.getKey());
				
				continue;
			}
			
			if(!entity.isEnabled()) {
				continue;
			}
			
        	//if(MathD.distanceBetweenVector3(entry.getKey().x, entry.getKey().y, entry.getKey().z, Camera.main.gameObject.getPosition()) > World.renderDistanceX * 25) {
        	//	entry.getKey().destroy();
        	//	entry.getValue().destroy(true);
        	//	World.chunks.remove(entry.getKey());
        		
        	//	continue;
        	//}
			
			//Slow method.
			/*Vector3f pos = Camera.main.convertWorldToScreenSpace(entity.getPosition());
			
			if(pos.x > Application.getFullWidth() + 64 || pos.x < -64
					|| pos.y > Application.getFullHeight() + 64 || pos.y < -64) {
				continue;
			}*/
			
			if(entity.getTransform() == null) {
				entity.updateTransform();

				continue;
			}

			shader.setUniform("model", entity.getTransform());

			GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndices().length, GL11.GL_UNSIGNED_INT, 0);

			unBindMesh(mesh);
			
			shader.unbind();
		}
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

	public void destroy() {

	}
}