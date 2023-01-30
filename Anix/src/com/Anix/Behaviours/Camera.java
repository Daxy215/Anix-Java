package com.Anix.Behaviours;

import com.Anix.Annotation.HideFromInspector;
import com.Anix.IO.Application;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.IO.ProjectSettings;
import com.Anix.Math.Color;
import com.Anix.Math.MathD;
import com.Anix.Math.Matrix4f;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Math.Vector4f;
import com.Anix.Objects.GameObject;

public class Camera extends Behaviour {
	private static final long serialVersionUID = 2993667212260007602L;
	
	public static enum ProjectionType {
		projection, orthographics, frustum
	}
	
	private static float mouseSensitivity = 0.20f, distance = 2.0f, horizontalAngle = 0, verticalAngle = 0;
	
	public Color skyColor = new Color(0.5294117647f, 0.80784313725f, 0.92156862745f);
	private Matrix4f viewMatrix;
	private float px, py, pz, rx, ry, rz;
	
	public ProjectionType projectionType = ProjectionType.projection;
	
	@HideFromInspector
	public transient static Camera main;
	
	public Camera() {
		
	}
	
	@Override
	public void awake() {
		if(main != null) {
			System.err.println("[ERROR] There's mulitple camers in one scene; Which is not supported!");
		} else {
			main = this;
		}
		
		if(ProjectSettings.projectType.equals(ProjectSettings.ProjectType.D2)) {
			projectionType = ProjectionType.orthographics;
		}
		
		px = gameObject.getPosition().x;
		px = gameObject.getPosition().y;
		px = gameObject.getPosition().z;
		rx = gameObject.getRotation().x;
		ry = gameObject.getRotation().y;
		rz = gameObject.getRotation().z;
		viewMatrix = Matrix4f.view(Camera.main.gameObject.getPosition(), Camera.main.gameObject.getRotation());
	}
	
	@Override
	public void render() {
		if(		px != gameObject.getPosition().x ||
				py != gameObject.getPosition().y ||
				pz != gameObject.getPosition().z ||
				
				rx != gameObject.getRotation().x ||
				ry != gameObject.getRotation().y ||
				rz != gameObject.getRotation().z) {
			viewMatrix = Matrix4f.view(Camera.main.gameObject.getPosition(), Camera.main.gameObject.getRotation());
		}
		
		px = gameObject.getPosition().x;
		py = gameObject.getPosition().y;
		pz = gameObject.getPosition().z;
		rx = gameObject.getRotation().x;
		ry = gameObject.getRotation().y;
		rz = gameObject.getRotation().z;
	}
	
	public void followObject2D(GameObject entity, float speed) {
		Vector2f position = entity.getPosition().getXY();
		
		gameObject.getPosition().setX(MathD.lerp(gameObject.getPosition().x, position.x, speed));
		gameObject.getPosition().setY(MathD.lerp(gameObject.getPosition().y, position.y, speed));
	}
	
	public void followObject2D(Vector2f position, float speed) {
		gameObject.getPosition().setX(MathD.lerp(gameObject.getPosition().x, position.x, speed));
		gameObject.getPosition().setY(MathD.lerp(gameObject.getPosition().y, position.y, speed));
	}
	
	public void followObject2D(Vector2f position, Vector2f offset, float speed) {
		gameObject.getPosition().setX(MathD.lerp(gameObject.getPosition().x + offset.x, position.x, speed));
		gameObject.getPosition().setY(MathD.lerp(gameObject.getPosition().y + offset.y, position.y, speed));
	}
	
	public void moveAroundAnObject(GameObject object) {
		float newMouseX = (float)Input.getMouseX();
		float newMouseY = (float)Input.getMouseY();

		float dx = (float)(newMouseX - Input.getLastMouseX());
		float dy = (float)(newMouseY - Input.getLastMouseY());
		
		if(Input.isMouseButton(KeyCode.Mouse0)) {
			horizontalAngle += dx * mouseSensitivity;
			verticalAngle -= dy * mouseSensitivity;
		}
		
		if(Input.isMouseButton(KeyCode.Mouse1)) {
			if(distance >= 1) {
				distance += dy * mouseSensitivity / 4;
			} else {
				distance = 1f;
			}
		}
		
		float horizontalDinstance = (float) (distance * Math.cos(Math.toRadians(verticalAngle)));
		float verticalDinstance = (float) (distance * Math.sin(Math.toRadians(verticalAngle)));
		
		float xOffset = (float) (horizontalDinstance * Math.sin(Math.toRadians(-horizontalAngle)));
		float zOffset = (float) (horizontalDinstance * Math.cos(Math.toRadians(-horizontalAngle)));
		
		gameObject.setPosition(object.getPosition().getX() + xOffset, object.getPosition().getY() - verticalDinstance, object.getPosition().getZ() + zOffset);
		gameObject.setRotation(verticalAngle, -horizontalAngle, 0);
	}
	
	public Vector2f convertScreenToWorldSpace() {
		double mouseX = Input.getMouseX() - (ProjectSettings.isEditor ? Application.getStartX() : 0), 
			   mosueY = Input.getMouseY() - (ProjectSettings.isEditor ? Application.getStartY() : 0);
		
		float width = Application.getWidth(), height = Application.getHeight();
		
		float cPosX = 0;
		float cPosY = gameObject.getPosition().y;
		
		Vector4f pos = new Vector4f((float)(mouseX / (width / 2.0f) - 1f), 1 - (float)(mosueY / (height / 2.0f)), 0, 1);
		
		Matrix4f model = gameObject.getTransform();
		Matrix4f projection = Application.getProjectionMatrix();
		
		Matrix4f mvp = projection.multiply(viewMatrix).multiply(model);
		mvp.invert();
		
		pos = mvp.multiply(pos);
		
		if(pos.w == 0.0f)
			pos.w = 1;
		
		pos.w = (1 - (Camera.main.projectionType.equals(ProjectionType.projection) ? + gameObject.getPosition().z : 0)) / pos.w;
		pos.x *= pos.w;
		pos.y *= pos.w;
		pos.z *= pos.w;
		
		if(Camera.main.projectionType.equals(ProjectionType.orthographics)) {
			cPosX = -cPosX;
			cPosY = -cPosY;
		}
		
		Vector2f rPos = new Vector2f(pos.x + -cPosX, pos.y + -cPosY);
		
		if(Camera.main.projectionType.equals(ProjectionType.projection))
			return new Vector2f(rPos.x, rPos.y);
		else
			return new Vector2f(rPos.x, rPos.y);
	}
	
	public Vector2f convertScreenToWorldSpace(double x, double y) {
		if(ProjectSettings.isEditor)
			x -= Application.getStartX(); y -= Application.getStartY();
		
		float width = Application.getWidth(), height = Application.getHeight();
		
		Vector2f cPos = gameObject.getPosition().getXY();
		
		Vector4f pos = new Vector4f((float)(x / (width / 2.0f) - 1f), 1 - (float)(y / (height / 2.0f)), 0, 1);
		
		Matrix4f model = gameObject.getTransform();
		Matrix4f projection = Application.getProjectionMatrix();
		
		Matrix4f mvp = projection.multiply(viewMatrix).multiply(model);
		mvp.invert();
		
		pos = mvp.multiply(pos);
		
		pos.w = (1f + (Camera.main.projectionType.equals(ProjectionType.projection) ? + gameObject.getPosition().z : 0)) / pos.w;
		pos.x *= pos.w;
		pos.y *= pos.w;
		pos.z *= pos.w;
		
		if(Camera.main.projectionType.equals(ProjectionType.orthographics)) {
			cPos.x = -cPos.x;
			cPos.y = -cPos.y;
		}
		
		Vector2f rPos = new Vector2f(pos.x + -cPos.x, -pos.y + -cPos.y);
		
		if(Camera.main.projectionType.equals(ProjectionType.projection))
			return new Vector2f(-rPos.x, -rPos.y);
		else
			return new Vector2f(rPos.x, rPos.y);
	}
	
	public Vector2f convertScreenToWorldSpace(float width, float height) {
		double mouseX = Input.getMouseX() - (ProjectSettings.isEditor ? Application.getStartX() : 0), mosueY = Input.getMouseY() - (ProjectSettings.isEditor ? Application.getStartY() : 0);
		
		Vector2f cPos = gameObject.getPosition().getXY();
		
		Vector4f pos = new Vector4f((float)(mouseX / (width / 2.0f) - 1f), 1 - (float)(mosueY / (height / 2.0f)), 0, 1);
		
		Matrix4f model = gameObject.getTransform();
		Matrix4f projection = Application.getProjectionMatrix();
		
		Matrix4f mvp = projection.multiply(viewMatrix).multiply(model);
		mvp.invert();
		
		pos = mvp.multiply(pos);
		
		pos.w = (1f + (Camera.main.projectionType.equals(ProjectionType.projection) ? + gameObject.getPosition().z : 0)) / pos.w;
		pos.x *= pos.w;
		pos.y *= pos.w;
		pos.z *= pos.w;
		
		if(Camera.main.projectionType.equals(ProjectionType.orthographics)) {
			cPos.x = -cPos.x;
			cPos.y = -cPos.y;
		}
		
		Vector2f rPos = new Vector2f(pos.x + -cPos.x, -pos.y + -cPos.y);
		
		if(Camera.main.projectionType.equals(ProjectionType.projection))
			return new Vector2f(-rPos.x, -rPos.y);
		else
			return new Vector2f(rPos.x, rPos.y);
	}
	
	public Vector2f convertScreenToWorldSpace(double x, double y, float width, float height) {
		if(!ProjectSettings.isEditor)
			x -= Application.getStartX(); y -= Application.getStartY();
		
		Vector2f cPos = gameObject.getPosition().getXY();
		
		Vector4f pos = new Vector4f((float)(x / (width / 2.0f) - 1f), 1 - (float)(y / (height / 2.0f)), 0, 1);
		
		//gameObject.updateTransform();
		Matrix4f model = Matrix4f.transform(gameObject);
		Matrix4f projection = Application.getProjectionMatrix();
		
		Matrix4f mvp = projection.multiply(viewMatrix).multiply(model);
		mvp.invert();
		
		pos = mvp.multiply(pos);
		
		pos.w = (1f + (Camera.main.projectionType.equals(ProjectionType.projection) ? + gameObject.getPosition().z : 0)) / pos.w;
		pos.x *= pos.w;
		pos.y *= pos.w;
		pos.z *= pos.w;
		
		if(Camera.main.projectionType.equals(ProjectionType.orthographics)) {
			cPos.x = -cPos.x;
			cPos.y = -cPos.y;
		}
		
		Vector2f rPos = new Vector2f(pos.x + -cPos.x, -pos.y + -cPos.y);
		
		if(Camera.main.projectionType.equals(ProjectionType.projection))
			return new Vector2f(-rPos.x, -rPos.y);
		else
			return new Vector2f(rPos.x, rPos.y);
	}
	
	public Vector3f convertWorldToScreenSpace(Matrix4f transform, Vector3f position) {
		Matrix4f projection = Application.getProjectionMatrix();
		
		Vector4f point4 = new Vector4f(position.x, position.y, position.z, 1);
		point4 = Matrix4f.transform(viewMatrix, point4, null);
		point4 = Matrix4f.transform(projection, point4, null);
		
		float pointx = point4.x;
		float pointy = -point4.y;
		float pointz = -point4.z;
		
		pointx /= point4.w;
		pointy /= point4.w;
		pointz /= point4.w;
		
		pointx = (pointx + 1) * (Application.getWidth() * 0.5f);
		pointy = (pointy + 1) * (Application.getHeight() * 0.5f);
		
		return new Vector3f(pointx + (ProjectSettings.isEditor ? Application.getStartX() : 0), pointy + (ProjectSettings.isEditor ? Application.getStartY() : 0), pointz);
	
		
		/*Matrix4f MVP = Matrix4f.multiply(Application.getProjectionMatrix(), viewMatrix).multiply(transform);
		Vector4f point4 = new Vector4f(position.x, position.y, position.z, 1);
		
		Vector4f point = MVP.multiply(point4);
		
		return new Vector3f(point.x / point.w * (Application.getFullWidth() / 2.0f + Application.getFullWidth() / 2.0f),
				point.y / point.w * (Application.getFullHeight() / 2.0f + Application.getFullHeight() / 2.0f),
				point.z / point.w);*/
	}
	
	public Vector3f convertWorldToScreenSpace(Vector3f position, Vector2f size) {
		Matrix4f projection = Application.getProjectionMatrix();
		
		Vector4f point4 = new Vector4f(position.x, position.y, position.z, 1);
		point4 = Matrix4f.transform(viewMatrix, point4, null);
		point4 = Matrix4f.transform(projection, point4, null);
		
		float pointx = point4.x;
		float pointy = -point4.y;
		float pointz = -point4.z;
				
		pointx /= point4.w;
		pointy /= point4.w;
		pointz /= point4.w;
		
		pointx = (pointx + 1) * (size.x * 0.5f);
		pointy = (pointy + 1) * (size.y * 0.5f);
		
		return new Vector3f(pointx, pointy, pointz);
	}
	
	@Override
	public void onEnable() {
		if(main == null) {
			main = this;
		}
	}
	
	@Override
	public void onDisable() {
		if(this.equals(main)) {
			main = null;
		}
	}
	
	@Override
	public void onRemove() {
		if(this.equals(main)) {
			main = null;
		}
	}
	
	@Override
	public void onDestroy() {
		if(this.equals(main)) {
			main = null;
		}
	}
	
	public Matrix4f getViewMatrix() {
		if(viewMatrix == null) {
			viewMatrix = Matrix4f.view(Camera.main.gameObject.getPosition(), Camera.main.gameObject.getRotation());
		}
		
		return viewMatrix;
	}
}
