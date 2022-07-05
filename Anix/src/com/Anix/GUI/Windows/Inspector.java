package com.Anix.GUI.Windows;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.Physics2D;
import com.Anix.GUI.GUI;
import com.Anix.IO.Application;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

import imgui.ImGui;
import imgui.type.ImString;

/**
 * TODOAdd, if field has /** in it, then,
 * on hover, it should show it as a tooltip.
 * pretty sure it's impossible ._.
 * 
 * Redo this entire class because,
 * It was made by an idiot old me aka
 * all the code sucks :D
 */
public final class Inspector {
	private int startX = 0, startY = 25;
	private int width = 250, height = 0;
	private int counter;
	
	public Inspector() {
		
	}

	public void init() {
		
	}
	
	public void render() {
		startX = Application.getFullWidth() - width;
		height = Application.getFullHeight() - 250;//assets

		ImGui.setNextWindowPos(startX, startY);
		ImGui.setNextWindowSize(width, height);
		
		ImGui.begin("Inspector", GUI.defaultFlags);
		
		drawObjectInformation();
		drawBehaviours();
		
		for(int i = 0; i < 10; i++) {
			ImGui.spacing();
		}
		
		if(Hierachy.selectedObject != null) {
			if(GUI.centeredButton("Add Behaviour", 150, 35, 0.35f)) {
				//Testing - TODO: Add a search bar.
				Hierachy.selectedObject.addBehaviour(new Physics2D());
			}
		}
		
		ImGui.end();
		
		counter = 0;
	}

	//Info
	ImString name = new ImString("", 256);
	float[] pos = new float[3];
	float[] rot = new float[3];
	float[] scl = new float[3];

	private void drawObjectInformation() {
		GameObject obj = Hierachy.selectedObject;

		if(obj == null)
			return;
		
		//Name
		name.set(obj.getName());
		ImGui.text("Name:     ");
		ImGui.sameLine();
		ImGui.inputText("##", name);
		obj.setName(name.toString());
		
		//Position
		ImGui.pushID("Position");
		
		ImGui.text("Position: ");
		ImGui.sameLine();
		//float[] pos = new float[] {obj.getPosition().x, obj.getPosition().y, obj.getPosition().z};
		pos[0] = obj.getPosition().x;
		pos[1] = obj.getPosition().y;
		pos[2] = obj.getPosition().z;
		ImGui.dragFloat3("", pos, 0.1f);
		obj.setPosition(pos[0], pos[1], pos[2]);
		
		ImGui.popID();
		
		//Rotation
		ImGui.pushID("Rotation");
		
		ImGui.text("Rotation: ");
		ImGui.sameLine();
		rot[0] = obj.getRotation().x;
		rot[1] = obj.getRotation().y;
		rot[2] = obj.getRotation().z;
		ImGui.dragFloat3("", rot, 0.1f);
		obj.setRotation(rot[0], rot[1], rot[2]);
		
		ImGui.popID();
		
		//Scale
		ImGui.pushID("Scale");
		
		ImGui.text("Scale:    ");
		ImGui.sameLine();
		scl[0] = obj.getScale().x;
		scl[1] = obj.getScale().y;
		scl[2] = obj.getScale().z;
		ImGui.dragFloat3("", scl, 0.1f);
		obj.setScale(scl[0], scl[1], scl[2]);
		
		ImGui.popID();
		
		ImGui.spacing();
		ImGui.separator();
	}
	
	void drawBehaviours() {
		if(Hierachy.selectedObject == null)
			return;
		
		for(int i = 0; i < Hierachy.selectedObject.getBehaviours().size(); i++) {
			Behaviour behaviour = Hierachy.selectedObject.getBehaviours().get(i);
			
			if(ImGui.treeNode(behaviour.getName())) {
				try {
					drawFields(behaviour);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
				
				ImGui.treePop();
			}
			
			ImGui.spacing();
			ImGui.separator();
		}
	}
	
	private void drawFields(Behaviour behaviour) throws IllegalArgumentException, IllegalAccessException {
		for(int i = 0; i < behaviour.getFields().length; i++) {
			Field f = behaviour.getFields()[i];
			
			drawField(f, behaviour);
		}
	}

	//Fields variables
	ImString sv = new ImString("", 256);
	float[] fv = new float[3];
	float[] cv = new float[3];
	
	private void drawField(Field f, Object object) throws IllegalArgumentException, IllegalAccessException {
		Class<?> type = f.getType();
		String typeName = type.getSimpleName();

		ImGui.text(f.getName() + ": ");
		ImGui.sameLine();
		
		ImGui.pushID("Field: " + object.hashCode() + counter);
		
		if(type.isPrimitive()) {
			switch(typeName.toLowerCase()) {
			case "byte":
				
				break;
			case "int":

				break;
			case "long":

				break;
			case "float":
				fv[0] = f.getFloat(object);
				
				if(ImGui.dragFloat("", fv, 0.1f))
					f.set(object, fv[0]);
				
				break;
			case "double":
				
				break;
			case "boolean":
				boolean bv = f.getBoolean(object);
				
				if(ImGui.checkbox("", bv)) {
					f.set(object, !bv);
				}

				break;
			default:
				ImGui.newLine();

				break;
			}
		} else if(type.getSimpleName().equalsIgnoreCase("string")) {
			sv.set(f.get(object).toString());
			
			if(ImGui.inputText(typeName, sv))
				f.set(object, sv.toString());
		} else if(type.getSimpleName().equalsIgnoreCase("gameobject")) {
			ImGui.text(((GameObject)f.get(object)).getName());
		} else if(type.getSimpleName().equalsIgnoreCase("vector2f")) {
			fv[0] = ((Vector2f)f.get(object)).x;
			fv[1] = ((Vector2f)f.get(object)).y;
			
			if(ImGui.dragFloat2("", fv, 0.1f)) {
				Vector2f vec2 = new Vector2f(fv[0], fv[1]);
				
				f.set(object, vec2);
			}
		} else if(type.getSimpleName().equalsIgnoreCase("vector3f")) {
			fv[0] = ((Vector3f)f.get(object)).x;
			fv[1] = ((Vector3f)f.get(object)).y;
			fv[2] = ((Vector3f)f.get(object)).z;
			
			if(ImGui.dragFloat3("", fv, 0.1f)) {
				Vector3f vec3 = new Vector3f(fv[0], fv[1], fv[2]);
				
				f.set(object, vec3);
			}
		}/* else if(type.getSimpleName().equalsIgnoreCase("color")) {
			System.err.println(object.getClass().getPackageName());
			Color c = (Color)object;
			cv[0] = c.r;
			cv[1] = c.g;
			cv[2] = c.b;
			ImGui.colorEdit3("#", cv);
			c.r = cv[0];
			c.r = cv[1];
			c.r = cv[2];
			
			f.set(object, c);
		} */else {
			ImGui.newLine();
			
			if(ImGui.treeNodeEx(type.getSimpleName())) {
			//if(ImGui.collapsingHeader(type.getSimpleName())) {
				
				ImGui.treePop();
				
				for(int i = 0; i < type.getFields().length; i++) {
					if(Modifier.isStatic(type.getFields()[i].getModifiers()))
						continue;
					
					try {
						drawField(type.getFields()[i], type);
					} catch(Exception e) {
						//TODO: Can not get float field com.Anix.Math.Color.r on java.lang.Class
						//System.err.println("fd " + type.getFields()[i].getType() + " - " + object.getClass());
						//e.printStackTrace(System.err);
					}
				}
			}
		}
		
		ImGui.popID();
		
		counter++;
	}
	
	public static Consumer<Integer> toConsumer(Object annotated, Method m) {
		return param -> {
			try {
				m.invoke(annotated, param);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException();
			}
		};
	}
	
	public int getStartX() {
		return startX;
	}

	public int getStartY() {
		return startY;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
