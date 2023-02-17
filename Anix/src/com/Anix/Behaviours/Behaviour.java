package com.Anix.Behaviours;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.Anix.Annotation.HideFromInspector;
import com.Anix.Engine.Editor;
import com.Anix.Main.Core;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;
import com.Anix.SceneManager.SceneManager;

public abstract class Behaviour extends Object implements Cloneable, Serializable {
	private static final long serialVersionUID = -143035466258379783L;
	
	@HideFromInspector
	public boolean isEnabled = true;
	
	@HideFromInspector
	public transient GameObject gameObject = null;
	
	public Behaviour() {}
	
	/**
	 * Called on added.
	 */	
	public void awake() {}
	
	/**
	 * Called on game start.
	 */
	public void start() {}
	
	public void update() {}
	
	public void requestUpdate() {
		if(Core.updateAble.contains(this))
			return;
		
		Core.updateAble.add(this);
	}
	
	public void render() {}
	
	public void requestRender() {
		if(Core.renderAble.contains(this))
			return;
		
		Core.renderAble.add(this);
	}
	
	public void onButtonClicked(String id, int clickType) {}
	
	public void onCollisionEnter(GameObject other) {}
	
	public void onCollisionStay(GameObject other) {}
	
	public void onCollisionExit(GameObject other) {}
	
	public void onValueChanged(String fieldName, String oldValue, String newValue) {}
	
	public void onRemove() {}
	
	public void onEnable() {}
	
	public void onDisable() {}
	
	public void onDestroy() {}
	
	@Override
	public Behaviour clone() {
		try {
			return (Behaviour)super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	public GameObject instantiate(GameObject object) {
		if(object == null) {
			System.err.println("[ERROR] Cannot instantiate a null object!");
			
			return null;
		}
		
		GameObject copy = null;
		
		try {
			copy = createCopy(object, object.getPosition(), object.getRotation(), object.getScale());
		} catch (CloneNotSupportedException e) {
			System.err.println("[ERROR] [TSH] Couldn't clone gameobject!");
			
			return null;
		}
		
		return copy;
	}
	
	public GameObject instantiate(GameObject object, Vector3f localPosition) {
		if(object == null) {
			System.err.println("[ERROR] Cannot instantiate a null object!");
			
			return null;
		}
		
		GameObject copy = null;
		
		try {
			copy = createCopy(object, localPosition, object.getRotation(), object.getScale());
		} catch (CloneNotSupportedException e) {
			System.err.println("[ERROR] [TSH] Couldn't clone gameobject!");
			
			return null;
		}
		return copy;
	}
	
	public GameObject instantiate(GameObject object, Vector3f localPosition, Vector3f rotation) {
		if(object == null) {
			System.err.println("[ERROR] Cannot instantiate a null object!");
			
			return null;
		}
		
		GameObject copy = null;
		
		try {
			copy = createCopy(object, localPosition, rotation, object.getScale());
		} catch (CloneNotSupportedException e) {
			System.err.println("[ERROR] [TSH] Couldn't clone gameobject!");
			
			return null;
		}
		
		return copy;
	}
	
	public GameObject instantiate(GameObject object, Vector3f localPosition, Vector3f rotation, Vector3f scale) {
		if(object == null) {
			System.err.println("[ERROR] Cannot instantiate a null object!");
			
			return null;
		}
		
		GameObject copy = null;
		
		try {
			copy = createCopy(object, localPosition, rotation, scale);
		} catch (CloneNotSupportedException e) {
			System.err.println("[ERROR] [TSH] Couldn't clone gameobject!");
			
			return null;
		}
		
		return copy;
	}
	
	private GameObject createCopy(GameObject object, Vector3f localPosition, Vector3f rotation, Vector3f scale) throws CloneNotSupportedException {
		GameObject copy = object.clone();
		
		copy.uuid = UUID.randomUUID();
		copy.setPosition(new Vector3f(localPosition.x, localPosition.y, localPosition.z));
		copy.setRotation(new Vector3f(rotation.x, rotation.y, rotation.z));
		copy.setScale(new Vector3f(scale.x, scale.y, scale.z));
		
		copy.setBehaviours(object.getBehaviours().stream().map(d -> {
			try {
				return d.getClass().getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			
			return null;
		}).collect(Collectors.toCollection(ArrayList::new)));
		
		for(int i = 0; i < copy.getBehaviours().size(); i++) {
			if(copy.getBehaviours().get(i) == null) {
				System.err.println("[ERROR] [TSH] Behaviour is null at: " + i + " for " + copy.getName());
				
				continue;
			}
			
			copy.getBehaviours().get(i).gameObject = copy;
			
			if(copy.getBehaviours().get(i).isEnabled)
				copy.getBehaviours().get(i).awake();
			
			if(Editor.isPlaying()) {
				copy.getBehaviours().get(i).start();
			}
		}
		
		SceneManager.getCurrentScene().addObject(copy);
		
		return copy;
	}
	
	public String getName() {
		return getClass().getSimpleName();
	}
	
	public void setGameObject(GameObject gameObject) {
		this.gameObject = gameObject;
	}
	
	public GameObject getGameObject() {
		return gameObject;
	}
	
	public Field[] getAllFields() {
		List<Field> fields = new ArrayList<Field>();
		
		Class<?> superClazz = getClass().getSuperclass();
		
		while(superClazz != null) {
			for(int i = 0; i < superClazz.getDeclaredFields().length; i++) {
				Field f = superClazz.getDeclaredFields()[i];
				
				if(f.getName().equalsIgnoreCase("serialversionuid"))
					continue;
				
				fields.add(f);
			}
			
			superClazz = superClazz.getSuperclass();
		}
		
		for(int i = 0; i < getClass().getDeclaredFields().length; i++) {
			Field f = getClass().getDeclaredFields()[i];
			
			if(f.getName().equalsIgnoreCase("serialversionuid"))
				continue;
			
			fields.add(f);
		}
		
		Field[] returnField = new Field[fields.size()];
		
		for(int i = 0; i < fields.size(); i++) {
			fields.get(i).setAccessible(true);
			returnField[i] = fields.get(i);
		}
		
		return returnField;
	}
	
	public Field[] getFields() {
		List<Field> fields = new ArrayList<Field>();
		
		Class<?> superClazz = getClass().getSuperclass();
		
		while(superClazz != null) {
			for(int i = 0; i < superClazz.getDeclaredFields().length; i++) {
				Field f = superClazz.getDeclaredFields()[i];
				
				if(f.getAnnotation(HideFromInspector.class) != null)
					continue;
				
				if(f.getName().equalsIgnoreCase("serialversionuid"))
					continue;
				
				//if(/*Modifier.isProtected(f.getModifiers()) || */Modifier.isFinal(f.getModifiers()) || Modifier.isStatic(f.getModifiers()))
					//continue;
				
				fields.add(f);
			}
			
			superClazz = superClazz.getSuperclass();
		}
		
		for(int i = 0; i < getClass().getFields().length; i++) {
			Field f = getClass().getFields()[i];
			
			if(f.getAnnotation(HideFromInspector.class) != null)
				continue;
			
			if(Modifier.isFinal(f.getModifiers()) || Modifier.isStatic(f.getModifiers()))
				continue;
			
			if(f.getName().equalsIgnoreCase("serialversionuid"))
				continue;
			
			fields.add(f);
		}
		
		Field[] returnField = new Field[fields.size()];
		
		for(int i = 0; i < fields.size(); i++) {
			returnField[i] = fields.get(i);
		}
		
		return returnField;
	}
}
