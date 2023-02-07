package com.Anix.Objects;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.Anix.Annotation.ScriptAble;
import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.Camera;
import com.Anix.Engine.Editor;
import com.Anix.Engine.Graphics.Mesh;
import com.Anix.GUI.Windows.Console;
import com.Anix.Main.Core;
import com.Anix.Math.Matrix4f;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.SceneManager.Scene;
import com.Anix.SceneManager.SceneManager;

@ScriptAble
public class GameObject /*extends Entity*/ implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name = "null";
	private boolean isEnabled = true;
	public boolean isStatic, shouldBeRemoved;
	private byte isDirty;
	
	public UUID uuid;
	
	private Vector3f position = new Vector3f();//, localposition = new Vector3f();
	private Vector3f rotation = new Vector3f();//, localRotation = new Vector3f();
	private Vector3f scale = new Vector3f()   ;//, localScale    = new Vector3f();
	
	private transient Mesh mesh;
	private transient Matrix4f transform;
	
	private GameObject parent;
	
	private transient List<GameObject> children = new ArrayList<GameObject>();
	private List<Behaviour> behaviours = new ArrayList<Behaviour>();
	
	public GameObject() {
		uuid = UUID.randomUUID();
		
		this.transform = Matrix4f.transform(this);
	}
	
	public GameObject(String name) {
		this.name = name;
		this.position = new Vector3f();
		this.rotation = new Vector3f();
		this.scale = new Vector3f(1);
		uuid = UUID.randomUUID();
		
		Scene scene = SceneManager.getCurrentScene();
		
		if(scene == null) {
			System.err.println("[ERROR] Couldn't add gameobject with the name of " + name + " to the scene since there isn't any open.");
			
			return;
		}
		
		scene.addObject(this);
		
		this.transform = Matrix4f.transform(this);
	}
	
	public GameObject(String name, Vector3f position) {
		this.name = name;
		this.position = position;
		this.rotation = new Vector3f();
		this.scale = new Vector3f(1);
		uuid = UUID.randomUUID();
		
		Scene scene = SceneManager.getCurrentScene();
		
		if(scene == null) {
			System.err.println("[ERROR] Couldn't add gameobject with the name of " + name + " to the scene since there isn't any open.");
			
			return;
		}
		
		scene.addObject(this);
		
		this.transform = Matrix4f.transform(this);
	}
	
	public GameObject(String name, Vector3f position, Vector3f rotation, Vector3f scale) {
		this.name = name;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		
		uuid = UUID.randomUUID();
		
		Scene scene = SceneManager.getCurrentScene();
		
		if(scene == null) {
			System.err.println("[ERROR] Couldn't add gameobject with the name of " + name + " to the scene since there isn't any open.");
			
			return;
		}
		
		scene.addObject(this);
		
		this.transform = Matrix4f.transform(this);
	}
	
	public GameObject(String name, Vector3f position, Vector3f rotation, Vector3f scale, GameObject parent) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		
		this.name = name;
		uuid = UUID.randomUUID();
		setParent(parent);
		
		Scene scene = SceneManager.getCurrentScene();
		
		if(scene == null) {
			System.err.println("[ERROR] Couldn't add gameobject with the name of " + name + " to the scene since there isn't any open.");
			
			return;
		}
		
		scene.addObject(this);
		
		this.transform = Matrix4f.transform(this);
	}
	
	public GameObject(String name, Vector3f position, Vector3f rotation, Vector3f scale, boolean addToScene) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		
		this.name = name;
		uuid = UUID.randomUUID();
		
		if(addToScene) {
			Scene scene = SceneManager.getCurrentScene();
			
			if(scene == null) {
				System.err.println("[ERROR] Couldn't add gameobject with the name of " + name + " to the scene since there isn't any open.");
				
				return;
			}
			
			scene.addObject(this);
		} else {
			for(int i = 0; i < behaviours.size(); i++) {
				if(behaviours.get(i).isEnabled) {
					behaviours.get(i).awake();
					behaviours.get(i).start();
				}
			}
		}
		
		this.transform = Matrix4f.transform(this);
	}
	
	public GameObject(Vector3f position, Vector3f rotation, Vector3f scale, Mesh mesh) {
		this.name = "";
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.mesh = mesh;
		
		uuid = UUID.randomUUID();
		
		Scene scene = SceneManager.getCurrentScene();
		
		if(scene == null) {
			System.err.println("[ERROR] Couldn't add gameobject with the name of " + name + " to the scene since there isn't any open.");
			
			return;
		}
		
		scene.addObject(this);
		
		this.transform = Matrix4f.transform(this);
	}
	
	public GameObject(Vector3f position, Vector3f rotation, Vector3f scale, GameObject parent) {
		this.name = "";
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		
		uuid = UUID.randomUUID();
		setParent(parent);
		
		Scene scene = SceneManager.getCurrentScene();
		
		if(scene == null) {
			System.err.println("[ERROR] Couldn't add gameobject with the name of " + name + " to the scene since there isn't any open.");
			
			return;
		}
		
		scene.addObject(this);
		
		this.transform = Matrix4f.transform(this);
	}
	
	public GameObject(String name, Vector3f position, Vector3f rotation, Vector3f scale, Mesh mesh) {
		this.name = name;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.mesh = mesh;
		
		uuid = UUID.randomUUID();
		
		Scene scene = SceneManager.getCurrentScene();
		
		if(scene == null) {
			System.err.println("[ERROR] Couldn't add gameobject with the name of " + name + " to the scene since there isn't any open.");
			
			return;
		}
		
		scene.addObject(this);
		
		this.transform = Matrix4f.transform(this);
	}

	public GameObject(String name, Vector3f position, Vector3f rotation, Vector3f scale, Mesh mesh, boolean addToScene) {
		this.name = name;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.mesh = mesh;
		uuid = UUID.randomUUID();

		if(addToScene) {
			Scene scene = SceneManager.getCurrentScene();
			
			if(scene == null) {
				System.err.println("[ERROR] Couldn't add gameobject with the name of " + name + " to the scene since there isn't any open.");
				
				return;
			}
			
			scene.addObject(this);
		}
		
		this.transform = Matrix4f.transform(this);
	}
	
	public GameObject(String name, Vector3f position, Vector3f rotation, Vector3f scale, Mesh mesh, GameObject parent) {
		this.name = name;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.mesh = mesh;
		this.uuid = UUID.randomUUID();
		
		setParent(parent);
		
		Scene scene = SceneManager.getCurrentScene();
		
		if(scene == null) {
			System.err.println("[ERROR] Couldn't add gameobject with the name of " + name + " to the scene since there isn't any open.");
			
			return;
		}
		
		scene.addObject(this);
		
		this.transform = Matrix4f.transform(this);
	}
	
	public static GameObject find(String name) {
		for(int i = 0; i < SceneManager.getCurrentScene().getGameObjects().size(); i++) {
			if(SceneManager.getCurrentScene().getGameObjects().get(i).getName().equals(name)) {
				return SceneManager.getCurrentScene().getGameObjects().get(i);
			}
		}
		
		return null;
	}
	
	public static GameObject find(UUID uuid) {
		for(int i = 0; i < SceneManager.getCurrentScene().getGameObjects().size(); i++) {
			if(SceneManager.getCurrentScene().getGameObjects().get(i).uuid.equals(uuid)) {
				return SceneManager.getCurrentScene().getGameObjects().get(i);
			}
		}
		
		return null;
	}
	
	@Override
	public GameObject clone() {
		try {
			GameObject clone = (GameObject)super.clone();
			clone.position = position.copy();
			clone.rotation = rotation.copy();
			clone.scale = scale.copy();
			
			List<GameObject> children = new ArrayList<>();
			
			if(this.children == null)
				this.children = new ArrayList<>();
			
			clone.setBehaviours(getBehaviours().stream().map(d -> {
				try {
					return d.getClass().getConstructor().newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
				
				return null;
			}).collect(Collectors.toCollection(ArrayList::new)));
			
			for(int i = 0; i < behaviours.size() || i < this.children.size(); i++) {
				if(clone.getBehaviours().size() > i) {
					clone.getBehaviours().get(i).gameObject = clone;
					
					if(clone.getBehaviours().get(i).isEnabled)
						clone.getBehaviours().get(i).awake();
					
					if(Editor.isPlaying()) {
						clone.getBehaviours().get(i).start();
					}
				}
				
				if(this.children.size() > i)
					children.add(this.children.get(i));
			}
			
			clone.uuid = UUID.randomUUID();
			clone.children = children;
			
			return clone;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void destroy() {
		destroy(false);
	}
	
	public void destroy(boolean destoryMesh) {
		if(Camera.main != null && uuid.equals(Camera.main.gameObject.uuid)) {
			Camera.main.gameObject.uuid = null;
			Camera.main = null;
		}
		
		if(children != null) {
			for(int i = 0; i < children.size(); i++) {
				if(children.get(i) != null)
					children.get(i).destroy();
			}
		}
		
		isEnabled = false;
		shouldBeRemoved = true;
		
		for(int i = 0; i < getBehaviours().size(); i++) {
			try {				
				Core.updateAble.remove(getBehaviours().get(i));
				Core.renderAble.remove(getBehaviours().get(i));
				
				getBehaviours().get(i).onDestroy();
			} catch(Exception e) {
				CharArrayWriter cw = new CharArrayWriter();
			    PrintWriter w = new PrintWriter(cw);
			    e.printStackTrace(w);
			    w.close();
			    String trace = cw.toString();
				
				System.err.print("[ERROR] " + getBehaviours().get(i).getName() + " because " + trace);
				Console.LogErr("[ERROR] " + getBehaviours().get(i).getName() + " because " + trace);
			}
		}
		
		behaviours.clear();
		
		setParent(null);
		
		if(mesh != null && destoryMesh)
			mesh.destroy();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public final List<Behaviour> getBehaviours() {
		return Collections.unmodifiableList(behaviours);
	}
	
	public void setBehaviours(List<Behaviour> behaviours) {
		/*if(this.behaviours.isEmpty() && !behaviours.isEmpty()) {
			if(!Core.updateAbleObjects.contains(this))
				Core.updateAbleObjects.add(this);
		}*/
		
		this.behaviours = behaviours;
	}
	
	public Behaviour getBehaviour(String behaviourName) {
		for(int i = 0; i < behaviours.size(); i++) {
			if(behaviours.get(i).getName().equals(behaviourName))
				return behaviours.get(i);
		}
		
		return null;
	}
	
	public Behaviour getBehaviour(Class<? extends Behaviour> behaviour) {
		return behaviours.stream().filter(b -> b.getClass().equals(behaviour)).findFirst().orElse(null);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Behaviour> T addBehaviour(Class<T> behaviour) {
		try {
			return (T) addBehaviour(behaviour.getDeclaredConstructor().newInstance());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Behaviour addBehaviour(Behaviour behaviour) {
		if(behaviour == null) {
			System.err.println("[ERROR] Cannot add a null behaviour!");
			
			return null;
		}
		
		for(int i = 0; i < behaviours.size(); i++) {
			if(behaviours.get(i).getName().equals(behaviour.getName())) {
				System.err.println("[ERROR] This behaviour already exists in this GameObject!");
				
				return null;
			}
		}
		
		//if(behaviours.isEmpty()) {
		//	Core.updateAbleObjects.add(this);
		//}
		
		behaviour.setGameObject(this);
		
		try {
			if(behaviour.isEnabled) {
				behaviour.awake();
				
				if(Editor.isPlaying()) {
					behaviour.start();
				}
			}
		} catch(Exception e) {
			CharArrayWriter cw = new CharArrayWriter();
			PrintWriter w = new PrintWriter(cw);
			e.printStackTrace(w);
			w.close();
			String trace = cw.toString();
			
			System.err.println("[ERROR] " + behaviour.getName() + " because " + trace);
			Console.LogErr("[ERROR] " + behaviour.getName() + " because " + trace);
		}
		
		behaviours.add(behaviour);
		
		return behaviour;
	}
	
	public void removeBehaviour(Behaviour behaviour) {
		try {
			behaviour.onRemove();
		} catch(Exception e) {
			CharArrayWriter cw = new CharArrayWriter();
		    PrintWriter w = new PrintWriter(cw);
		    e.printStackTrace(w);
		    w.close();
		    String trace = cw.toString();
			
			System.err.print("[ERROR] " + behaviour.getName() + " because " + trace);
			Console.LogErr("[ERROR] " + behaviour.getName() + " because " + trace);
		}
		
		this.behaviours.remove(behaviour);
		
		if(behaviours.isEmpty()) {
			//Core.updateAbleObjects.remove(this);
			Core.updateAble.remove(behaviour);
			Core.renderAble.remove(behaviour);
		}
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}
	
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	public void resetDirty() {
		isDirty = 0;
	}
	
	public boolean isDirty() {
		return isDirty == 1;
	}
	
	private void updateTransformation() {
		if(rotation.getY() > 360) {
			rotation.setY(0);
		}

		if(rotation.getY() < -360) {
			rotation.setY(0);
		}

		if(!(rotation.getX() >= -90)) {
			this.rotation.setX(-90);
		}

		if(!(rotation.getX() <= 90)) {
			this.rotation.setX(90);
		}
		
		updateTransform();
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(Vector2f position) {
		this.setPosition(position.x, position.y);
	}
	
	public void setPosition(Vector3f position) {
		isDirty = 1;
		
		this.position = position;
		
		updateTransformation();
	}
	
	public void setPosition(float x, float y) {
		isDirty = 1;
		
		this.position.x = x;
		this.position.y = y;
		
		updateTransformation();
	}
	
	public void setPosition(float x, float y, float z) {
		isDirty = 1;
		
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
		
		updateTransformation();
	}
	
	public void addPosition(float x, float y, float z) {
		isDirty = 1;
		
		position.x += x;
		position.y += y;
		position.z += z;
		
		updateTransformation();
	}
	
	public void addPosition(Vector2f value) {
		addPosition(value.x, value.y);
	}
	
	public void addPosition(float x, float y) {
		isDirty = 1;
		
		position.x += x;
		position.y += y;
		
		updateTransformation();
	}
	
	public final Vector3f getRotation() {
		return rotation;
	}
	
	public void setRotation(Vector3f rotation) {
		isDirty = 1;
		
		this.rotation = rotation;
		
		updateTransformation();
	}
	
	public void setRotation(float x, float y, float z) {
		isDirty = 1;
		
		this.rotation.x = x;
		this.rotation.y = y;
		this.rotation.z = z;
		
		updateTransformation();
	}
	
	public void rotate(float x, float y, float z) {
		isDirty = 1;
		
		rotation.x += x;
		rotation.y += y;
		rotation.z += z;
		
		updateTransformation();
	}
	
	public final Vector3f getScale() {
		return scale;
	}
	
	public void setScale(Vector3f scale) {
		this.scale = scale;
		
		updateTransformation();
	}
	
	public void setScale(float value) {
		this.scale.x = value;
		this.scale.y = value;
		this.scale.z = value;
		
		updateTransformation();
	}
	
	public void setScale(float x, float y, float z) {
		this.scale.x = x;
		this.scale.y = y;
		this.scale.z = z;
		
		updateTransformation();
	}
	
	public boolean hasParent() {
		return parent != null;
	}
	
	public GameObject getParent() {
		return parent;
	}
	
	public void setParent(GameObject parent) {
		if(hasParent() && this.parent != parent) {
			if(this.parent.children == null)
				this.parent.children = new ArrayList<>();
			
			this.parent.children.remove(this);
		}
		
		this.parent = parent;
		
		if(parent != null) {
			if(parent.shouldBeRemoved) {
				this.parent = null;
				
				return;
			}
			
			parent.addChild(this);
		}
		
		/*if(parent != null && !parent.children.contains(this)) {
			System.err.println("?? " + name);
			
			parent.children.add(this);
		}*/
	}
	
	public boolean hasChildren() {
		if(children == null) {
			children = new ArrayList<>();
			
			return false;
		}
		
		return children.size() > 0;
	}
	
	public List<GameObject> getChildren() {
		return children;
	}
	
	public void addChild(GameObject obj) {
		if(this.children == null) {
			this.children = new ArrayList<>();
		}
		
		if(obj.hasParent() && obj.parent != this) {
			obj.setParent(this);
			
			return;
		}
		
		if(!children.contains(obj)) {
			children.add(obj);
		}
	}
	
	public Mesh getMesh() {
		return mesh;	
	}
	
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}
	
	public Matrix4f getTransform() {
		return transform;
	}

	public void updateTransform() {
		transform = Matrix4f.transform(this);
	}
}
