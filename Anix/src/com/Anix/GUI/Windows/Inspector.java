package com.Anix.GUI.Windows;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.function.Consumer;

import com.Anix.Annotation.Header;
import com.Anix.Behaviours.Behaviour;
import com.Anix.Engine.Editor;
import com.Anix.GUI.GUI;
import com.Anix.IO.Application;
import com.Anix.IO.AutoCorrector;
import com.Anix.Math.Color;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;

/**
 * Add, if field has /** in it, then,<br>
 * on hover, it should show it as a tooltip.<br>
 * pretty sure it's impossible ._.<br>
 * <br>
 * Redo this entire class because,<br>
 * It was made by an idiot old me aka<br>
 * all the code sucks :D<br>
 */
public final class Inspector {
	private float startX = 0, startY = 25;
	private float width = 300, height = 0;
	private int counter;
	
	private AutoCorrector autoCorrector;
	private GUI gui;

	public Inspector(GUI gui) {
		this.gui = gui;
	}

	public void init() {
		autoCorrector = new AutoCorrector();
	}

	ImString behName = new ImString("", 256);

	public void render() {
		startX = Application.getFullWidth() - width;
		height = Application.getHeight();

		ImGui.setNextWindowPos(startX, startY);
		ImGui.setNextWindowSize(width, height);
		ImGui.setNextWindowSizeConstraints(250/*Min width*/, -1.0f, Application.getFullWidth() - gui.getHierarchy().getWidth() - /*Distance between screens - Padding*/ 20, -1.0f);

		ImGui.begin("Inspector", GUI.defaultFlags);

		drawObjectInformation();
		drawBehaviours();

		//TODO; Fix this.
		if(ImGui.beginDragDropTarget()) {
			Object o = ImGui.acceptDragDropPayload("Folder", ImGuiDragDropFlags.None);

			if(o != null) {
				System.err.println("AOYOYOYOYOO " + (o.toString()));
			}

			ImGui.endDragDropTarget();
		}

		for(int i = 0; i < 10; i++) {
			ImGui.spacing();
		}

		if(Hierarchy.selectedObject != null) {
			if(GUI.centeredButton("Add Behaviour", 150, 35, 0.35f)) {
				//Testing - TODO: Add a search bar.
				//Hierachy.selectedObject.addBehaviour(new Physics2D());

				ImGui.openPopup("addBehaviour");
			}
		}
		
		if (ImGui.beginPopup("addBehaviour")) {
			ImGui.inputText("##", behName);
			
			autoCorrector.root.children.clear();
			
			for(int i = 0; i < Editor.importedClasses.size(); i++) {
				autoCorrector.root.insert(Editor.importedClasses.get(i).getName());
			}
			
			List<String> behaviours = autoCorrector.suggest(behName.get());
			
			for(int i = 0; i < behaviours.size(); i++) {
				if(ImGui.button(behaviours.get(i))) {
					Hierarchy.selectedObject.addBehaviour(Editor.getBehaviour(behaviours.get(i)));
					
					ImGui.closeCurrentPopup();
				}
			}
			
			ImGui.endPopup();
		}
		
		width = ImGui.getWindowWidth();

		ImGui.end();

		counter = 0;
	}

	//Info
	ImString name = new ImString("", 256);
	float[] pos = new float[3];
	float[] rot = new float[3];
	float[] scl = new float[3];
	
	private void drawObjectInformation() {
		GameObject obj = Hierarchy.selectedObject;

		if(obj == null)
			return;

		ImGui.pushItemWidth(width - 100);

		//Name
		name.set(obj.getName());
		ImGui.text("Name:     ");
		ImGui.sameLine();

		ImGui.pushItemWidth(width - 125);

		ImGui.inputText("##", name);
		obj.setName(name.toString());

		ImGui.sameLine();

		boolean isStatic = obj.isStatic;

		ImGui.pushID("istaticjijoidijodfijdfijuofsdoijfd");

		if(ImGui.checkbox("", isStatic)) {
			System.err.println("IsStatic: " + isStatic);
			obj.isStatic = !isStatic;
		}

		ImGui.popID();
		ImGui.popItemWidth();

		//Position
		ImGui.pushID("Position" + obj.uuid);

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
		ImGui.pushID("Rotation" + obj.uuid);

		ImGui.text("Rotation: ");
		ImGui.sameLine();
		rot[0] = obj.getRotation().x;
		rot[1] = obj.getRotation().y;
		rot[2] = obj.getRotation().z;
		ImGui.dragFloat3("", rot, 0.1f);
		obj.setRotation(rot[0], rot[1], rot[2]);

		ImGui.popID();

		//Scale
		ImGui.pushID("Scale" + obj.uuid);

		ImGui.text("Scale:    ");
		ImGui.sameLine();
		scl[0] = obj.getScale().x;
		scl[1] = obj.getScale().y;
		scl[2] = obj.getScale().z;
		ImGui.dragFloat3("", scl, 0.1f);
		obj.setScale(scl[0], scl[1], scl[2]);

		ImGui.popID();
		ImGui.popItemWidth();

		ImGui.spacing();
		ImGui.separator();
	}
	
	void drawBehaviours() {
		if(Hierarchy.selectedObject == null)
			return;

		for(int i = 0; i < Hierarchy.selectedObject.getBehaviours().size(); i++) {
			Behaviour behaviour = Hierarchy.selectedObject.getBehaviours().get(i);

			//Checkbox to Enable/Disable Behaviour.
			ImGui.pushID("jghijogijoerofcef "  + i + " - " + counter);
			if(ImGui.checkbox("##", behaviour.isEnabled)) {
				behaviour.isEnabled = !behaviour.isEnabled;
			}
			ImGui.popID();

			ImGui.sameLine();

			int flags = ImGuiTreeNodeFlags.OpenOnArrow;
			flags |= ImGuiTreeNodeFlags.FramePadding;

			//if(selectedObject == object) {
			//	flags |= ImGuiTreeNodeFlags.Selected;
			//}

			if(behaviour.getFields().length == 0) {
				flags |= ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen;
			}

			if(!behaviour.isEnabled) {
				ImGui.pushStyleColor(ImGuiCol.Text, 0.5f, 0.5f, 0.5f, 0.4f);
			}

			//TODO: Add flags - If behaviour has no fields. Don't show arrow.
			if(ImGui.treeNodeEx(behaviour.getName(), flags)) {
				try {
					drawFields(behaviour);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}

				if(behaviour.getFields().length != 0) {
					ImGui.treePop();
				}
			}

			/*if(ImGui.beginPopupContextItem()) {
				if(ImGui.button("Remove")) {
					Hierachy.selectedObject.removeBehaviour(behaviour);
				}

				ImGui.endPopup();
			}*/

			if(!behaviour.isEnabled) {
				ImGui.popStyleColor();
			}

			ImGui.spacing();
			ImGui.separator();
		}
	}
	
	private void drawFields(Behaviour behaviour) throws IllegalArgumentException, IllegalAccessException {
		for(int i = 0; i < behaviour.getFields().length; i++) {
			try {
				drawField(behaviour.getFields()[i], behaviour);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void drawFields(Object object) {
		if(object == null)
			return;

		for(int i = 0; i < object.getClass().getFields().length; i++) {
			try {
				if(Modifier.isStatic(object.getClass().getFields()[i].getModifiers()) || Modifier.isFinal((object.getClass().getFields()[i].getModifiers())))
					continue;
				System.err.println("drawing: " + object.getClass().getFields()[i].getName());
				drawField(object.getClass().getFields()[i], object);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	//Fields variables
	ImString sv = new ImString("", 256);
	int[] iv = new int[1];
	float[] fv = new float[3];
	float[] cv = new float[3];
	
	private void drawField(Field f, Object object) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
		f.setAccessible(true);
		
		Class<?> type = f.getType();
		String typeName = type.getSimpleName();
		
		Header header = f.getAnnotation(Header.class);
		
		if(header != null) {
			ImDrawList drawList = ImGui.getWindowDrawList();
			
			float x = ImGui.getCursorScreenPosX();
			float y = ImGui.getCursorScreenPosY();
			
			//33.15, 35.7, 43.35 - Window background.
			drawList.addRectFilled(x, y,
					width + x, ImGui.getTextLineHeight() + y,
					ImColor.intToColor(23, 25, 33, 255));
			
			ImGui.setCursorPosY(ImGui.getCursorPosY() - 1);
			ImGui.text(header.value());
		}
		
		ImGui.pushItemWidth(25);
		ImGui.text(f.getName() + ": ");
		ImGui.popItemWidth();
		
		ImGui.sameLine();
		
		ImGui.pushItemWidth(ImGui.getWindowWidth() * 0.65f);
		
		ImGui.pushID("Field: " + object.hashCode() + counter);
		
		if(type.isPrimitive()) {
			switch(typeName.toLowerCase()) {
			case "byte":
				iv[0] = f.getByte(object);
				
				if(ImGui.dragInt("", iv, 0.1f))
					f.set(object, iv[0]);
				break;
			case "int":
				iv[0] = f.getInt(object);
				
				if(ImGui.dragInt("", iv, 0.1f))
					f.set(object, iv[0]);
				break;
			case "long":
				iv[0] = (int) f.getLong(object);
				
				if(ImGui.dragInt("", iv, 0.1f))
					f.set(object, iv[0]);
				break;
			case "float":
				fv[0] = f.getFloat(object);
				
				if(ImGui.dragFloat("", fv, 0.1f))
					f.set(object, fv[0]);

				break;
			case "double":
				fv[0] = (float) f.getDouble(object);

				if(ImGui.dragFloat("", fv, 0.1f))
					f.set(object, fv[0]);
				break;
			case "boolean":
				boolean bv = f.getBoolean(object);

				if(ImGui.checkbox("", bv)) {
					f.set(object, !bv);
				}

				break;
			default:
				System.err.println("couldn't find " + typeName);
				ImGui.newLine();

				break;
			}
		} else if(type.getSimpleName().equalsIgnoreCase("string")) {
			if(f.get(object) != null)
				sv.set(f.get(object).toString());
			else
				sv.set("");

			if(ImGui.inputText("", sv))
				f.set(object, sv.toString());
		} else if(type.getSimpleName().equalsIgnoreCase("gameobject")) {
			if(((GameObject)f.get(object)) != null)
				ImGui.text(((GameObject)f.get(object)).getName());
			else
				ImGui.text("null");
		} else if(type.getSimpleName().equalsIgnoreCase("vector2f")) {
			if(f.get(object) != null) {
				fv[0] = ((Vector2f)f.get(object)).x;
				fv[1] = ((Vector2f)f.get(object)).y;
			} else {
				fv[0] = 0;
				fv[1] = 0;
			}

			if(ImGui.dragFloat2("", fv, 0.1f)) {
				Vector2f vec2 = new Vector2f(fv[0], fv[1]);

				f.set(object, vec2);
			}
		} else if(type.getSimpleName().equalsIgnoreCase("vector3f")) {
			if(f.get(object) != null) {
				fv[0] = ((Vector3f)f.get(object)).x;
				fv[1] = ((Vector3f)f.get(object)).y;
				fv[2] = ((Vector3f)f.get(object)).z;
			} else {
				fv[0] = 0;
				fv[1] = 0;
				fv[2] = 0;
			}

			if(ImGui.dragFloat3("", fv, 0.1f)) {
				Vector3f vec3 = new Vector3f(fv[0], fv[1], fv[2]);

				f.set(object, vec3);
			}
		} else if(type.isEnum()) {
			if(f.get(object) == null) {
				ImGui.popID();
				counter++;

				return;
			}

			if(ImGui.beginCombo("#", f.get(object).toString())) {
				Method value = type.getMethod("values");
				Object[] values = (Object[]) value.invoke(null, null);

				for(int i = 0; i < values.length; i++) {
					if(ImGui.selectable(values[i].toString())) {
						f.set(object, values[i]);
					}
				}

				ImGui.endCombo();
			}
		} else if(type.getSimpleName().equalsIgnoreCase("color")) {
			Color c = (Color)f.get(object);

			if(c != null) {
				cv[0] = c.r;
				cv[1] = c.g;
				cv[2] = c.b;
				ImGui.colorEdit3("##", cv);
				c.r = cv[0];
				c.g = cv[1];
				c.b = cv[2];

				f.set(object, c);
			}
		} else if(type.isArray()) {
			//System.err.println("it's array");
		} else if(type.equals(List.class)) {
			List<Object> list = (List<Object>) f.get(object);

			if(list != null) {
				if(ImGui.treeNodeEx(type.getSimpleName())) {
					//if(ImGui.collapsingHeader(type.getSimpleName())) {
					ImGui.treePop();
					ImGui.popItemWidth();
					ImGui.popID();

					counter++;

					drawList(f, list, object);

					/*for (int i = 0; i < list.size(); i++) {
					    Object element = list.get(i);

					    System.err.println("Element: " + element);
					}*/

					return;
				}
			}
		} else {
			//ImGui.newLine();

			if(ImGui.treeNodeEx(type.getSimpleName())) {
				//if(ImGui.collapsingHeader(type.getSimpleName())) {
				ImGui.treePop();
				ImGui.popItemWidth();
				ImGui.popID();
				
				counter++;

				drawFields(f.get(object));

				return;
			}
		}

		ImGui.popItemWidth();
		ImGui.popID();

		counter++;
	}
	
	private void drawList(Field f, List<Object> list, Object object) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
		ParameterizedType listType = (ParameterizedType) f.getGenericType();
		Class<?> listElementType = (Class<?>) listType.getActualTypeArguments()[0];
		String typeName = listElementType.getSimpleName();

		for(int i = 0; i < list.size(); i++) {
			Object element = list.get(i);
			
			if(listElementType.isPrimitive()) {
				switch(typeName.toLowerCase()) {
				case "byte":
					iv[0] = (byte) element;//f.getByte(object);

					if(ImGui.dragInt("", iv, 0.1f))
						list.set(i, iv[0]);
					break;
				case "int":
					iv[0] = (int) element;

					if(ImGui.dragInt("", iv, 0.1f))
						list.set(i, iv[0]);
					break;
				case "long":
					iv[0] = (int) element;

					if(ImGui.dragInt("", iv, 0.1f))
						list.set(i, iv[0]);
					break;
				case "float":
					fv[0] = f.getFloat(object);

					if(ImGui.dragFloat("", fv, 0.1f))
						list.set(i, fv[0]);

					break;
				case "double":
					fv[0] = (float) f.getDouble(object);

					if(ImGui.dragFloat("", fv, 0.1f))
						list.set(i, fv[0]);
					break;
				case "boolean":
					boolean bv = f.getBoolean(object);

					if(ImGui.checkbox("", bv)) {
						list.set(i, !bv);
					}

					break;
				default:
					System.err.println("couldn't find " + typeName);
					ImGui.newLine();

					break;
				}
			} else if(typeName.equalsIgnoreCase("string")) {
				if(element != null)
					sv.set(element);
				else
					sv.set("");
				
				if(ImGui.inputText("##", sv))
					list.set(i, sv.toString());
			} else if(typeName.equalsIgnoreCase("gameobject")) {
				if(((GameObject)f.get(object)) != null)
					ImGui.text(((GameObject)element).getName());
				else
					ImGui.text("null");
			} else if(typeName.equalsIgnoreCase("vector2f")) {
				if(element != null) {
					fv[0] = ((Vector2f)element).x;
					fv[1] = ((Vector2f)element).y;
				} else {
					fv[0] = 0;
					fv[1] = 0;
				}

				if(ImGui.dragFloat2("", fv, 0.1f)) {
					Vector2f vec2 = new Vector2f(fv[0], fv[1]);

					list.set(i, vec2);
				}
			} else if(typeName.equalsIgnoreCase("vector3f")) {
				if(element != null) {
					fv[0] = ((Vector3f)element).x;
					fv[1] = ((Vector3f)element).y;
					fv[2] = ((Vector3f)element).z;
				} else {
					fv[0] = 0;
					fv[1] = 0;
					fv[2] = 0;
				}

				if(ImGui.dragFloat3("", fv, 0.1f)) {
					Vector3f vec3 = new Vector3f(fv[0], fv[1], fv[2]);

					list.set(i, vec3);
				}
			} else if(listElementType.isEnum()) {
				if(element == null) {
					ImGui.popID();
					counter++;

					return;
				}

				if(ImGui.beginCombo("#", element.toString())) {
					Method value = listElementType.getMethod("values");
					Object[] values = (Object[]) value.invoke(null, null);
					
					for(int j = 0; j < values.length; j++) {
						if(ImGui.selectable(values[j].toString())) {
							list.set(i, values[j]);
						}
					}

					ImGui.endCombo();
				}
			} else if(typeName.equalsIgnoreCase("color")) {
				Color c = (Color)element;
				
				if(c != null) {
					cv[0] = c.r;
					cv[1] = c.g;
					cv[2] = c.b;
					ImGui.colorEdit3("##", cv);
					c.r = cv[0];
					c.g = cv[1];
					c.b = cv[2];
					
					list.set(i, c);
				}
			}
		}
		
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

	public float getStartX() {
		return startX;
	}

	public float getStartY() {
		return startY;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
}
