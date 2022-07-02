package com.Anix.GUI.Windows;

import static org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_inputBox;

import java.awt.Desktop;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Consumer;

import com.Anix.Annotation.Header;
import com.Anix.Annotation.ScriptAble;
import com.Anix.Annotation.Type;
import com.Anix.Behaviours.Behaviour;
import com.Anix.Engine.Editor;
import com.Anix.Engine.Graphics.Material;
import com.Anix.Engine.Graphics.Shader;
import com.Anix.GUI.GUI;
import com.Anix.GUI.Panel;
import com.Anix.GUI.Panel.BorderSetting;
import com.Anix.GUI.Panel.Button;
import com.Anix.GUI.Panel.ButtonSetting;
import com.Anix.GUI.Panel.Text;
import com.Anix.GUI.Panel.TextInput;
import com.Anix.GUI.Panel.TextInputSetting;
import com.Anix.GUI.Texture;
import com.Anix.GUI.UI;
import com.Anix.GUI.UI.Toggle;
import com.Anix.GUI.Windows.Assets.Folder;
import com.Anix.IO.Application;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Main.Core;
import com.Anix.Math.Color;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Math.Vector4f;
import com.Anix.Objects.GameObject;

/**
 * TODO: Add, if field has /** in it, then,
 * on hover, it should show it as a tooltip.
 * pretty sure it's impossible ._.
 * 
 * TODO: Redo this entire class because,
 * It was made by an idiot old me aka
 * all the code sucks :D
 */
public final class Inspector {
	private int scrollX;
	
	private int startX = 0, startY = 25;
	private int width = 250, height = 0;
	private final float lineWidth = 1f, lineHeight = 1f;
	
	private float headerOffsets;
	private long lastScroll;
	
	private boolean updateBehaviours;
	private boolean canBeRemoved, shouldUpdate = true, lock;
	private boolean clicked, isPrefab;
	
	private GameObject lastGameObject, currentSelectedGameObject;
	private Behaviour selectedBehaviour;
	private Texture on, off;
	private Panel panel;
	
	private TextInputSetting textInputSetting = new TextInputSetting(false, Color.lightGray, Color.red, Color.cyan);
	
	private ButtonSetting behaviourButtonSetting = new ButtonSetting(true, true, 0, Color.silver, Color.cyan, Color.gray);
	private BorderSetting behaviourBorderSettings = new BorderSetting(lineWidth, Color.black);
	
	private ButtonSetting fieldsButtonSetting = new ButtonSetting(true, true, 0, Color.lightGray, Color.DARK_GRAY, new Color(0.6f));
	private BorderSetting fieldsBorderSettings = new BorderSetting(lineWidth, Color.black);
	
	private Core core;
	
	private List<String> scriptCode = new ArrayList<String>();
	
	public Inspector(Core core) {
		this.core = core;
	}
	
	public void init() {
		off = UI.loadTexture("resources/GUI/toggle-off.png");
		on = UI.loadTexture("resources/GUI/toggle-on.png");
		
		panel = new Panel();
	}
	
	//New version :)
	public void update(boolean v) throws IllegalArgumentException, IllegalAccessException {
		startX = Application.getFullWidth() - width;
		height = Application.getFullHeight() - (core.getGUI().getAssets().getStartX() + core.getGUI().getAssets().getHeight()) - startY;
		
		UI.drawButtonWithOutline(startX, startY, -0.2f, width, 25, lineWidth, lineHeight, "Inspector", 0, 0, -0.1f, 0.5f, 0.5f, Color.black, new Color(0.65f), Color.black);
		Toggle t = UI.toggle(off, on, startX + Application.getFullWidth() - 16, startY - 25 + (16 * 0.25f), -0.4f, 16, 16, lock);
		lock = t.getValue();
		
		currentSelectedGameObject = core.getGUI().getHierachy().getSelectedObject();
		
		float fieldHeight = 25;
		float paddingBetweenFields = 5;
		float paddingBetweenBehaviours = 5;
		float currentYPos = 0;
		
		if(currentSelectedGameObject != null && currentSelectedGameObject != lastGameObject || updateBehaviours) {
			panel.clear();
			updateBehaviours = false;
			
			//Object Information
			//Name
			//panel.addButton(new Button("", null, new Vector3f(startX - width, startY, -0.3f),
			//		new Vector2f(width, 25), new Text("Name: " + currentSelectedGameObject.getName(),
			//new Vector2f(0.5f), Color.black), behaviourButtonSetting, behaviourBorderSettings));
			
			int textHeight = UI.getFontMatrics().getHeight();
			
			//Background
			panel.addButton(new Button("BG", null, new Vector3f(startX + (lineWidth * 2), startY + 25, -0.3f),
					new Vector2f(width - (lineWidth * 4), textHeight*4),
					null,
					new ButtonSetting(false, 0, Color.silver, Color.cyan, Color.gray), behaviourBorderSettings));
			
			//Name
			panel.addTextInput(new TextInput("Name", 60, new Vector3f(startX + (lineWidth * 4), startY + (25), -0.4f),
					new Vector2f(width - (lineWidth * 6), textHeight), new Text(currentSelectedGameObject.getName(), new Vector2f(0.5f), Color.black),
					behaviourButtonSetting, textInputSetting, behaviourBorderSettings) {
				
				@Override
				public void onLetterRemove() {
					currentSelectedGameObject.setName(returnString);	
				}
				
				@Override
				public void onType(String newCharacter) {
					currentSelectedGameObject.setName(returnString);
				}

				@Override
				public void onFinishedTyping() {
					currentSelectedGameObject.setName(returnString);
				}
			});
			
			//Position
			panel.addTextInput(new TextInput("Position", 60, new Vector3f(startX + (lineWidth * 4), startY + (25*2) + 2, -0.4f),
					new Vector2f(width - (lineWidth * 6), textHeight), new Text(currentSelectedGameObject.getPosition().copy().toString(), new Vector2f(0.5f), Color.black),
					behaviourButtonSetting, textInputSetting, behaviourBorderSettings) {
				@Override
				public void onType(String newCharacter) {
					
				}

				@Override
				public void onFinishedTyping() {
					
				}
			});
			/*panel.addButton(new Button("", null, new Vector3f(startX + (lineWidth * 4) + (50 * 0), startY + (25 * 2) + 8 + 5, -0.4f),
					new Vector2f(45, 25),
					null,
					new ButtonSetting(false, 0, Color.silver, Color.cyan, Color.gray), behaviourBorderSettings));*/
			
			float yOffset = startY + 25 + (textHeight * 4) + paddingBetweenBehaviours;
			
			//Behaviours
			float width = this.width - (lineWidth * 4);
			float xPos = startX + (lineWidth * 2);
			
			for(int i = 0; i < currentSelectedGameObject.getBehaviours().size(); i++) {
				Behaviour b = currentSelectedGameObject.getBehaviours().get(i);
				Field[] fields = b.getFields();
				
				String name = b.getClass().getSimpleName();
				
				float yPos = startY + currentYPos + /*Padding*/(i * paddingBetweenBehaviours) + yOffset;
				
				for(int j = 0; j < fields.length; j++) {
					Field f = fields[j];
					
					if(f == null) {
						continue;
					}
					
					//Class<?> type = f.getType();
					String fieldName = f.getName();
					//String typeName = type.getSimpleName();
					String value = "";
					
					if(f.get(b) != null) {
						value = f.get(b).toString();
					} else {
						value = "null";
					}
					
					//Field Button
					panel.addButton(new Button(name + ":" + j, null, new Vector3f(xPos + (lineWidth * 2), yPos /*StartY*/ + (fieldHeight + 2) + (j * paddingBetweenFields) + (j * fieldHeight), -0.4f),
							new Vector2f(width - (lineWidth * 4), fieldHeight),
							new Text(fieldName + ": " + value, new Vector2f(0.5f), Color.black), 
							fieldsButtonSetting, fieldsBorderSettings) {
						
						@Override
						public void onClick() {
							
						}
					});
				}
				
				float height = (fields.length * fieldHeight) + /*Field Padding*/ (paddingBetweenFields * fields.length) + /*Fields Start Y*/ (fieldHeight + 2);
				//Behaviour Button
				panel.addButton(new Button(name, null, new Vector3f(xPos, yPos, -0.3f),
						new Vector2f(width, height), new Text(name, new Vector2f(0.5f), Color.black), 
						behaviourButtonSetting, behaviourBorderSettings) {
					@Override
					public void onSelect() {
						if(Input.isKeyDown(KeyCode.Delete)) {
							currentSelectedGameObject.removeBehaviour(b);
							
							updateBehaviours = true;
						}
					}
				});
				
				currentYPos += height;
			}
			
			//Add behaviour button
			panel.addButton(new Button("", null, new Vector3f(xPos + (this.width * 0.5f - (150 * 0.5f)), currentYPos + 60 + yOffset, -0.3f), new Vector2f(150, 35), 
					new Text("Add Behaviour", new Vector2f(0.6f), new Vector3f(6, 0), Color.black),
					new ButtonSetting(true, false, 0, Color.silver, Color.cyan, Color.gray), behaviourBorderSettings) {
				
				@Override
				public void onClick() {
					//TODO: Add support to mulitple windows.
					
					String behaviourName = tinyfd_inputBox("Behaviour Name!", "What Behaviour would you like to add?", "");
					
					if(behaviourName != null) {
						Behaviour beh = Editor.getBehaviour(behaviourName);
						
						currentSelectedGameObject.addBehaviour(beh);
						
						updateBehaviours = true;
					}
				}
			});
		}
		
		//Panel
		if(!UI.drawButtonWithOutline(startX, startY, 0.3f, width, height, lineWidth, lineHeight, Color.gray, Color.black)) {
			if(core.getDraggedObject() != null && Input.isMouseButtonUp(KeyCode.Mouse0)) {
				canBeRemoved = true;
			}
		} else {
			if(core.getDraggedObject() instanceof Folder && Input.isMouseButtonUp(KeyCode.Mouse0) && currentSelectedGameObject != null) {
				//Add dragged java class.
				File file = new File(((Folder)core.getDraggedObject()).getAbsolutePath());
				
				if(file.getAbsolutePath().contains(".")) {
					String extention = file.getAbsolutePath().split("\\.")[1];
					
					if(extention.equalsIgnoreCase("java")) {
						String[] path = file.getAbsolutePath().replace('\\','/').split("\\.")[0].split("/");
						String name = path[path.length - 1];
						
						currentSelectedGameObject.addBehaviour(Editor.getBehaviour(name));
					}
				}
			}
			
			if(Input.getScrollY() != 0) {
				scrollX += Input.getScrollY() * 3;
				
				lastScroll = System.currentTimeMillis();
			}
			
			if(Input.isKeyDown(KeyCode.Delete)) {
				if(selectedBehaviour != null) {
					selectedBehaviour.onRemove();
					currentSelectedGameObject.removeBehaviour(selectedBehaviour);
					selectedBehaviour = null;
				}
			}
		}
		
		if(System.currentTimeMillis() - lastScroll > 5000) {
			if(scrollX > 0) {
				scrollX--;
			} else if(scrollX < 0) {
				scrollX++;
			}
		}
		
		lastGameObject = currentSelectedGameObject;
	}
	
	public void update() {
		height = Application.getFullHeight() - (core.getGUI().getAssets().getStartX() + core.getGUI().getAssets().getHeight()) - startY;
		
		UI.drawButtonWithOutline(startX + Application.getFullWidth() - width, startY - 25, -0.2f, width, 25, lineWidth, lineHeight, "Inspector", 0, 0, -0.1f, 0.5f, 0.5f, Color.black, new Color(0.65f), Color.black);
		Toggle t = UI.toggle(off, on, startX + Application.getFullWidth() - 16, startY - 25 + (16 * 0.25f), -0.4f, 16, 16, lock);
		lock = t.getValue();
		
		GameObject obj = core.getGUI().getHierachy().getSelectedObject();
		Folder folder = core.getGUI().getAssets().getSelectedFolder();
		
		//TODO: This will keep reading, aka too much data.
		if(folder != null && folder.getAbsolutePath().endsWith(".gameobject")) {
			try {
				obj = (GameObject) Editor.readObjectFromFile(Editor.getInputStream(folder.getAbsolutePath()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(lock && lastGameObject != null) {
			obj = lastGameObject;
		}
		
		if(obj != null && (lock || core.getLastSelectedObject() != null && core.getLastSelectedObject().equals(obj) 
				|| folder != null && folder.getAbsolutePath().endsWith(".gameobject"))) {
			if(obj != null)
				isPrefab =false;// Files.exists(Paths.get(Editor.getWorkSpaceDirectory() + "Data\\" + obj.uuid + ".bin"));
			 
			//Object name
			if(UI.drawButtonWithOutline((startX + Application.getFullWidth() - (width - 10) + (lineWidth * 2)), startY + (lineHeight * 2) + scrollX, 0.2f,
					50 + (lineWidth * 2), (UI.getFontMatrics().getHeight() * 0.5f) + (lineHeight * 4), lineWidth, lineHeight,
					"Name:", 1.5f, -2.0f, -0.1f, 0.5f, 0.5f, Color.black, Color.lightGray, Color.black) && Input.isMouseButtonDown(KeyCode.Mouse0)) {
				String newValue = tinyfd_inputBox("Renaming object!", "What would you like to rename your object to?", "");
				
				if(newValue != null) {
					if(folder != null) {
						try {
							Field f = obj.getClass().getDeclaredField("name");
							f.setAccessible(true);
							updateField(new File(folder.getAbsolutePath()), obj, newValue, f);
						} catch (NoSuchFieldException | SecurityException e) {
							e.printStackTrace();
						}
					} else if(!isPrefab) {
						obj.setName(newValue);
					}
				}
			}
			
			UI.drawButtonWithOutline(startX + Application.getFullWidth() - (width - 10) + (lineWidth * 2) + 80, startY + (lineHeight * 2) + scrollX, 0.2f,
					(float)(UI.getFontMatrics().getStringBounds(obj.getName(), null).getWidth() * 0.5f) + (lineWidth * 2), 
					(UI.getFontMatrics().getHeight() * 0.5f) + (lineHeight * 4), lineWidth, lineHeight,
					obj.getName(), 1.5f, -2.0f, -0.1f, 0.5f, 0.5f, Color.black, Color.lightGray, Color.black);
			
			//Object Position
			UI.drawButtonWithOutline(startX + Application.getFullWidth() - (width - 10) + (lineWidth * 2), startY + 25 + (lineHeight * 2) + scrollX, 0.2f,
					62 + (lineWidth * 2), (UI.getFontMatrics().getHeight() * 0.5f) + (lineHeight * 4), lineWidth, lineHeight,
					"Position:", 1.5f, -2.0f, -0.1f, 0.5f, 0.5f, Color.black, Color.lightGray, Color.black);
			
			if(GUI.drawFieldVector3f(obj.getPosition(), 50,
					(startX + Application.getFullWidth() - (width - 10) + (lineWidth * 2)) + (80), startY + 25 + (lineHeight * 2) + scrollX, 0.2f,
					40 + (lineWidth * 2), (UI.getFontMatrics().getHeight() * 0.5f) + (lineHeight * 4), lineWidth, lineHeight, Color.lightGray, Color.black)) {
				String newValue = tinyfd_inputBox("Position!", "new pos?", "");
				
				if(newValue != null) {
					String[] values = newValue.split(" ");
					
					try {
						if(folder != null) {
							try {
								Field f = obj.getClass().getDeclaredField("position");
								f.setAccessible(true);
								updateField(new File(folder.getAbsolutePath()), obj, new Vector3f(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2])), f);
							} catch (NoSuchFieldException | SecurityException e) {
								e.printStackTrace();
							}
						} else if(!isPrefab) {
							obj.setPosition(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]));
						}
					} catch(Exception e) {
						
					}
				}
			}
			
			//Object Rotation
			UI.drawButtonWithOutline(startX + Application.getFullWidth() - (width - 10) + (lineWidth * 2), startY + 50 + (lineHeight * 2) + scrollX, 0.2f,
					65 + (lineWidth * 2), (UI.getFontMatrics().getHeight() * 0.5f) + (lineHeight * 4), lineWidth, lineHeight,
					"Rotation:", 1.5f, -2.0f, -0.1f, 0.5f, 0.5f, Color.black, Color.lightGray, Color.black);
			
			if(GUI.drawFieldVector3f(obj.getRotation(), 50,
					(startX + Application.getFullWidth() - (width - 10) + (lineWidth * 2)) + (80), startY + 50 + (lineHeight * 2) + scrollX, 0.2f,
					40 + (lineWidth * 2), (UI.getFontMatrics().getHeight() * 0.5f) + (lineHeight * 4), lineWidth, lineHeight, Color.lightGray, Color.black)) {
				String newValue = tinyfd_inputBox("Rotation!", "What would you like to rename your object to?", "");
				
				if(newValue != null) {
					String[] values = newValue.split(" ");
					
					try {
						if(folder != null) {
							try {
								Field f = obj.getClass().getDeclaredField("rotation");
								f.setAccessible(true);
								updateField(new File(folder.getAbsolutePath()), obj, new Vector3f(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2])), f);
							} catch (NoSuchFieldException | SecurityException e) {
								e.printStackTrace();
							}
						} else if(!isPrefab) {
							obj.setRotation(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]));
						}
					} catch(Exception e) {
						
					}
				}
			}
			
			//Object Scale
			UI.drawButtonWithOutline(startX + Application.getFullWidth() - (width - 10) + (lineWidth * 2), startY + 75 + (lineHeight * 2) + scrollX, 0.2f,
					45 + (lineWidth * 2), (UI.getFontMatrics().getHeight() * 0.5f) + (lineHeight * 4), lineWidth, lineHeight,
					"Scale:", 1.5f, -2.0f, -0.1f, 0.5f, 0.5f, Color.black, Color.lightGray, Color.black);
			
			if(GUI.drawFieldVector3f(obj.getScale(), 50,
					(startX + Application.getFullWidth() - (width - 10) + (lineWidth * 2)) + (80), startY + 75 + (lineHeight * 2) + scrollX, 0.2f,
					40 + (lineWidth * 2), (UI.getFontMatrics().getHeight() * 0.5f) + (lineHeight * 4), lineWidth, lineHeight, Color.lightGray, Color.black)) {
				String newValue = tinyfd_inputBox("Scale!", "What would you like to rename your object to?", "");
				
				if(newValue != null) {
					String[] values = newValue.split(" ");
					
					try {
						if(folder != null) {
							try {
								Field f = obj.getClass().getDeclaredField("scale");
								f.setAccessible(true);
								updateField(new File(folder.getAbsolutePath()), obj, new Vector3f(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2])), f);
							} catch (NoSuchFieldException | SecurityException e) {
								e.printStackTrace();
							}
						} else if(!isPrefab) {
							obj.setScale(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]));
						}
					} catch(Exception e) {
						
					}
				}
			}
			
			float height = 25;
			float fieldHeight = 25;
			float curYPos = startY + 110;
			
			for(int i = 0; i < obj.getBehaviours().size(); i++) {
				clicked = false;
				boolean toggleClicked = false;
				
				Behaviour b = obj.getBehaviours().get(i);
				Field[] fields = b.getFields();
				
				String name = b.getClass().getSimpleName();
				
				try {
					if(drawFields(null, fields, b, height + curYPos + scrollX, height)) {
						toggleClicked = true;
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				Toggle toggle = UI.toggle(off, on,
						startX + Application.getFullWidth() - 16 - (lineWidth * 2), curYPos + (lineHeight * 2) + scrollX, 0.1f,
						16, 16, b.isEnabled);
				
				//If new value of the boolean isn't equal to old "isEnabled" value.
				if(toggle.getValue() != b.isEnabled) {
					b.isEnabled = toggle.getValue();
					
					if(!b.isEnabled) {
						b.onDisable();
					} else {
						b.onEnable();
					}
				}
				
				if(toggle.isHovering() && Input.isMouseButtonDown(KeyCode.Mouse0)) {
					toggleClicked = true;
				}
				
				if(UI.drawButtonWithOutline(startX + Application.getFullWidth() - width + (lineWidth * 2), curYPos + (lineHeight * 2) + scrollX, 0.3f, 
						width - (lineWidth * 4), height + (fields.length * (fieldHeight + 3)) + 5 + headerOffsets, lineWidth, lineHeight, name, 0, 0, -0.1f, 0.5f, 0.5f, Color.black,
						b.equals(selectedBehaviour) ? Color.cyan : Color.silver, Color.black)&& Input.isMouseButtonDown(KeyCode.Mouse0)) {
					if(!clicked && !toggleClicked) {
						if(b.equals(selectedBehaviour) && Input.doubleClicked()) { //Open if selected.
							if(Desktop.isDesktopSupported()) {
								try {
									String fullPath = "";
									
									for(int j = 0; j < Editor.importedClasses.size(); j++) {
										if(Editor.importedClasses.get(j).getName().equals(b.getName())) {
											//that's the class name not the path.
											fullPath = Editor.importedClasses.get(j).getName();
										}
									}
									
									if(fullPath.length() > 0) {
										Desktop.getDesktop().edit(new File(core.getGUI().getAssets().getFolder(fullPath + ".java").getAbsolutePath()));
									} else {
										Console.LogErr("[ERROR] [TSH] #132 Couldn't find a script with the name of " + b.getClass().getSimpleName());
									}
								} catch (IOException | IllegalArgumentException | NullPointerException e) {
									//e.printStackTrace();
									Console.LogErr("[ERROR] Opening a file is through the inspector is not yet supported!");
								}
							} else {
								Console.LogErr("[ERROR] Used operator is not supported! This game engine is made for Windows only! (For now at least..)");
							}
						} else if(Input.isMouseButtonDown(KeyCode.Mouse0)) { //Select behaviour.
							selectedBehaviour = obj.getBehaviours().get(i);
							shouldUpdate = true;
						}
					}
				}
				
				curYPos += fields.length * (fieldHeight + 3) + height + 16 + headerOffsets;
				headerOffsets = 0;
			}
			
			drawAddBehaviour(obj, curYPos);
		} else if(folder != null && core.getLastSelectedObject() != null && core.getLastSelectedObject().equals(folder)) {
			String[] extentionArr = folder.getAbsolutePath().split("\\.");
			
			String extention = extentionArr[extentionArr.length - 1];
			
			if(extention.equals("material")) {
				Object j = null;
				try {
					j = Editor.readObjectFromFile(Editor.getInputStream(folder.getAbsolutePath()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if(j instanceof Material) {
					Material material = (Material) j;
					
					drawFields(folder, material.getClass().getFields(), material, startY + lineHeight, 25);
				} else {
					System.err.println("wha");
				}
			} else if(extention.equals("java")) {
				if(shouldUpdate) {
					shouldUpdate = false;
					
					scriptCode.clear();
					
					String path = folder.getAbsolutePath();
					
					if(path == null)
						return;
					
					File file = new File(path);
					Scanner myReader = null;
					
					try {
						myReader = new Scanner(file);
					} catch (FileNotFoundException e) {
						System.err.println("[ERROR] [TSH] #248 Script file was deleted.");
						
						return;
					}
					
					while (myReader.hasNextLine()) {
						//String spaces = "";
						String line = myReader.nextLine();

						for(int i = 0; i < line.length(); i++) {
							/*if(line.charAt(i) == '\r') {
								spaces += "\r";
							} else if(line.charAt(i) == '\n') {
								spaces += "\n";
							}*/

							//spaces += line.charAt(i);
						}

						scriptCode.add(/*spaces + */line);
					}

					myReader.close();
				}
				
				UI.drawBox(startX + Application.getFullWidth() - width + (lineWidth * 2), startY + (lineHeight * 2), 0.1f, width - (lineWidth * 4), UI.getFontMatrics().getHeight() * scriptCode.size() - (lineHeight * 4), Color.lightGray);
				
				for(int i = 0; i < scriptCode.size(); i++) {
					UI.drawString(scriptCode.get(i), startX + Application.getFullWidth() - width + (lineWidth * 2), startY + i * UI.getFontMatrics().getHeight() + (lineHeight * 2) + scrollX, 0.45f, 0.45f, Color.black);
				}
			}
		}

		if(!lock) {
			lastGameObject = obj;
		}

		//Panel
		if(!UI.drawButtonWithOutline(startX + Application.getFullWidth() - width, startY, 0.3f, width, height, lineWidth, lineHeight, Color.gray, Color.black)
				&& Input.isMouseButtonDown(KeyCode.Mouse0)) {
			if(core.getDraggedObject() != null && Input.isMouseButtonUp(KeyCode.Mouse0)) {
				canBeRemoved = true;
			}
		} else {
			if(core.getDraggedObject() instanceof Folder && Input.isMouseButtonUp(KeyCode.Mouse0) && obj != null) {
				//Add dragged java class.
				File file = new File(((Folder)core.getDraggedObject()).getAbsolutePath());
				
				if(file.getAbsolutePath().contains(".")) {
					String extention = file.getAbsolutePath().split("\\.")[1];
					
					if(extention.equalsIgnoreCase("java")) {
						String[] path = file.getAbsolutePath().replace('\\','/').split("\\.")[0].split("/");
						String name = path[path.length - 1];
						
						if(!isPrefab) {
							obj.addBehaviour(Editor.getBehaviour(name));
						} else {
							//TODO
							System.err.println("not yet :(");
						}
					}
				}
			}
			
			if(Input.getScrollY() != 0) {
				scrollX += Input.getScrollY() * 3;
				
				lastScroll = System.currentTimeMillis();
			}
			
			if(Input.isKeyDown(KeyCode.Delete) && !isPrefab) {
				//TODO: IF PREFAB, REMOVE DATA FROM FOLDER.
				if(selectedBehaviour != null) {
					selectedBehaviour.onRemove();
					obj.removeBehaviour(selectedBehaviour);
					selectedBehaviour = null;
				}
			}
		}
		
		if(System.currentTimeMillis() - lastScroll > 5000) {
			if(scrollX > 0) {
				scrollX--;
			} else if(scrollX < 0) {
				scrollX++;
			}
		}
	}
	
	public void render() {
		
	}
	
	private boolean drawFields(Folder folder, Field[] fields, Object object, float y, float height/*, Behaviour b, int i, float y, float height*/) {
		float headerOffset = 0;
		boolean returnValue = false;
		boolean isScriptAble = object.getClass().getAnnotation(ScriptAble.class) != null;
		
		File file = null;
		
		if(folder != null)
			file = new File(folder.getAbsolutePath());
		
		for(int j = 0; j < fields.length; j++) {
			try {
				Field f = fields[j];
				
				if(f == null) {
					continue;
				}
				
				Class<?> type = f.getType();
				String fieldName = f.getName();
				String typeName = type.getSimpleName();
				String value = "";
				
				if(f.get(object) != null) {
					value = f.get(object).toString();
				} else {
					value = "null";
				}
				
				if(typeName.toLowerCase().equals("boolean")) {
					Toggle toggle = UI.toggle(off, on,
							startX + Application.getFullWidth() - 16 - (lineHeight * 6), y + (j * 28) + (lineHeight * 6) + headerOffset + 4, 0.2f,
							16, 16, Boolean.parseBoolean(value),
							fieldName, -((width - (lineWidth * 12)) - 16), -4, -0.2f, 0.5f, 0.5f, Color.black,
							new Vector2f(-(width + (lineWidth * 6)), height - 16), new Vector3f(16, -4, 0.1f), Color.lightGray);
					
					f.set(object, toggle.getValue());
					
					if(toggle.isHovering() && Input.isMouseButtonDown(KeyCode.Mouse0)) {
						returnValue = true;
					}
					
					continue;
				} else if(!value.equals("null")) {
					if(typeName.equalsIgnoreCase("gameobject")) {
						value = ((GameObject)f.get(object)).getName();
					} else if(typeName.equalsIgnoreCase("color")) {
						value = ((Color)f.get(object)).getRed() + " " + ((Color)f.get(object)).getGreen() + " " + ((Color)f.get(object)).getBlue();
					} else if(typeName.equalsIgnoreCase("shader")) {
						value = ((Shader)f.get(object)).getName();
					} else if(typeName.equalsIgnoreCase("material")) {
						value = "(" + ((Material)f.get(object)).getShader().getName() + ") Material";
					} else {
						if(!type.isPrimitive() && !typeName.equals("String")) {
							if(typeName.equals("List")) {
								
							} else {
								if(Input.isKey(KeyCode.J)) {
									System.err.println("object: " + typeName);
									
									headerOffset += type.getFields().length  + (j * 28) + (lineHeight * 6);
									headerOffsets += type.getFields().length  + (j * 28) + (lineHeight * 6);
									
									drawFields(folder, type.getFields(), y  + (j * 28) + (lineHeight * 6) + headerOffset, height, headerOffset);
								}
							}
						}
					}
					
					if(type.getSuperclass() != null) {
						if(type.getSuperclass().getSimpleName().equalsIgnoreCase("behaviour")) {
							if(((Behaviour)f.get(object)).gameObject != null)
								value = "(" + ((Behaviour)f.get(object)).gameObject.getName() + ")" + ((Behaviour)f.get(object)).getName();
						}
					}
				}
				
				Header header = f.getAnnotation(Header.class);
				
				if(header != null) {
					UI.drawButtonWithOutline(startX + Application.getFullWidth() - width + (lineWidth * 6), y + (j * 28) + (lineHeight * 6) + headerOffset, 0.2f,
							width - (lineWidth * 12), height, 1, 1, header.value(), 0, 0, -0.1f, 0.5f, 0.5f, Color.black, new Color(0.6f), Color.black);
					
					headerOffset += (UI.getFontMatrics().getHeight() * 0.5f) + 8;
					headerOffsets += (UI.getFontMatrics().getHeight() * 0.5f) + 8;
				}
				
				boolean isHovering = UI.drawButton(startX + Application.getFullWidth() - width + (lineWidth * 6),
						y + (j * 28) + (lineHeight * 6) + headerOffset, 0.2f,
						width - (lineWidth * 12), height, fieldName + ": " + value, 0, 0, -0.1f, 0.5f, 0.5f, Color.black, Color.lightGray);
				
				//On object dragged into field.
				if(isHovering && Input.isMouseButtonUp(KeyCode.Mouse0))
					handleDragging(value, object, f);
				
				if(isHovering && Input.isMouseButtonDown(KeyCode.Mouse0)) {
					String newValue = tinyfd_inputBox("Changing value!", "What would you like to change the value to?", "");
					
					if(newValue == null)
						continue;
					
					String oldValue = value;
					
					switch(typeName) {
					case "String":
						if(isScriptAble) {
							updateField(file, object, newValue, f);
						} else {
							f.set(object, newValue);
						}
						
						break;
					case "byte":
						if(isScriptAble) {
							updateField(file, object, Byte.parseByte(newValue), f);
						} else {
							f.set(object, Byte.parseByte(newValue));
						}
						
						break;
					case "int":
						if(isScriptAble) {
							updateField(file, object, Integer.valueOf(newValue), f);
						} else {
							f.set(object, Integer.valueOf(newValue));
						}
						
						break;
					case "long":
						if(isScriptAble) {
							updateField(file, object, Long.valueOf(newValue), f);
						} else {
							f.set(object, Long.valueOf(newValue));
						}
						
						break;
					case "float":
						if(isScriptAble) {
							updateField(file, object, Float.parseFloat(newValue), f);
						} else {
							f.set(object, Float.parseFloat(newValue));
						}
						
						break;
					case "double":
						if(isScriptAble) {
							updateField(file, object, Double.parseDouble(newValue), f);
						} else {
							f.set(object, Double.parseDouble(newValue));
						}
						
						break;
					case "boolean":
						if(isScriptAble) {
							updateField(file, object, Boolean.parseBoolean(newValue), f);
						} else {
							f.set(object, Boolean.parseBoolean(newValue));
						}
						
						break;
					case "Vector2f":
						String[] vector = newValue.split(" ");
						
						if(isScriptAble) {
							updateField(file, object, new Vector2f(Float.parseFloat(vector[0]), Float.parseFloat(vector[1])), f);
						} else {
							f.set(object, new Vector2f(Float.parseFloat(vector[0]), Float.parseFloat(vector[1])));
						}
						
						break;
					case "Vector3f":
						vector = newValue.split(" ");
						
						if(isScriptAble) {
							updateField(file, object, new Vector3f(Float.parseFloat(vector[0]), Float.parseFloat(vector[1]), Float.parseFloat(vector[2])), f);
						} else {
							f.set(object, new Vector3f(Float.parseFloat(vector[0]), Float.parseFloat(vector[1]), Float.parseFloat(vector[2])));
						}
						
						break;
					case "Vector4f":
						vector = newValue.split(" ");
						
						if(isScriptAble) {
							updateField(file, object, new Vector4f(Float.parseFloat(vector[0]), Float.parseFloat(vector[1]), Float.parseFloat(vector[2]), Float.parseFloat(vector[3])), f);
						} else {
							f.set(object, new Vector4f(Float.parseFloat(vector[0]), Float.parseFloat(vector[1]), Float.parseFloat(vector[2]), Float.parseFloat(vector[3])));
						}
						break;
					case "Color":
						vector = newValue.split(" ");
						
						if(isScriptAble) {
							updateField(file, object, new Color(Float.parseFloat(vector[0]), Float.parseFloat(vector[1]), Float.parseFloat(vector[2])), f);
						} else {
							f.set(object, new Color(Float.parseFloat(vector[0]), Float.parseFloat(vector[1]), Float.parseFloat(vector[2])));
						}
						
						break;
					default:
						System.err.println("[ERORR] Couldn't find value type of " + typeName);
						
						break;
					}
					
					if(object instanceof Behaviour) {
						if(!oldValue.equals(newValue)) {
							((Behaviour)object).onValueChanged(fieldName, oldValue, newValue);
						}
					}
					
					clicked = true;
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return returnValue;
	}
	
	private void handleDragging(String value, Object object, Field f) throws IllegalArgumentException, IllegalAccessException, IOException {
		Class<?> type = f.getType();
		String fieldName = f.getName();
		String typeName = type.getSimpleName();
		
		if(core.getDraggedObject() != null) {
			//If dragged object type is equal to field type.
			if(type.getSimpleName().equalsIgnoreCase(core.getDraggedObject().getClass().getSimpleName())) {
				if(isPrefab) {
					//TODO:
					
					return;
				}
				
				f.set(object, core.getDraggedObject());
				
				return;
			}
			
			//If dragged object is a gameobject
			if(core.getDraggedObject() instanceof GameObject) {
				GameObject obj = (GameObject)core.getDraggedObject();

				for(int k = 0; k < obj.getBehaviours().size(); k++) {
					//If type is a behaviour that's inside the gameobjects.
					if(obj.getBehaviours().get(k).getClass().getSimpleName().equalsIgnoreCase(type.getSimpleName())) {
						if(isPrefab) {
							//TODO:
							
							continue;
						}
						
						f.set(object, obj.getBehaviours().get(k));
						
						continue;
					}
				}
			}
		}
		
		if(core.getDraggedObject() instanceof Folder) {
			File file = new File(((Folder)core.getDraggedObject()).getAbsolutePath());
			
			String extension = file.getAbsolutePath().split("[.]")[1];
			
			//Basically a scriptable folder.
			if(core.getLastSelectedObject() instanceof Folder) {
				File selectedFolder = new File(((Folder)core.getLastSelectedObject()).getAbsolutePath());
				
				//Special cases
				if(extension.equalsIgnoreCase("glsl") && typeName.equalsIgnoreCase("shader")) {
					String shaderName = file.getName().split("[.]")[0];
					
					Shader shader = Shader.getShader(shaderName);
					
					if(shader == null) {
						System.err.println("[ERROR] [TSH] #575 Couldn't find a shader with the name of: " + shaderName);
						
						return;
					}
					
					linkObject(core.getLastSelectedObject(), file, fieldName);
					updateField(selectedFolder, object, shader, f);
				}
			}
			
			//If dragged folder extension type is same as the field type.
			if(extension.toLowerCase().equals(typeName.toLowerCase())) {
				f.set(object, Editor.readObjectFromFile(Editor.getInputStream(file.getAbsolutePath())));
				
				linkObject(object, file, fieldName);
			}
			
			Type typeA = f.getAnnotation(Type.class);
			
			if(typeA != null) {
				String[] values = typeA.values();
				for(int k = 0; k < values.length; k++) {
					if(values[k].equalsIgnoreCase(extension)) {
						switch(extension.toLowerCase()) {
						case "png":
						case "jpg":
							String[] absolutePath = file.getAbsolutePath().split("\\\\");
							String folderName = absolutePath[absolutePath.length - 1];
							
							if(!value.equals(folderName)) {
								if(isPrefab) {
									//TODO:
								} else {
									f.set(object, folderName);
								}
								
								if(object instanceof Behaviour)
									((Behaviour)object).onValueChanged(fieldName, value, folderName);
							}
							
							break;
						case "prefab":
							try {
								FileInputStream fis = new FileInputStream(file);
								DataInputStream dis = new DataInputStream(fis);
								
								String data = dis.readUTF();
								
								if(typeName.equalsIgnoreCase("gameobject")) {
									if(isPrefab) {
										//TODO:
									} else {
										f.set(object, Editor.readObjectFromFile(Editor.getInputStream(file.getAbsolutePath())));
									}
								}
								
								String fullPath = System.getProperty("user.dir");
								String fileSeparator = System.getProperty("file.separator");

								String path = fullPath + fileSeparator + core.getProjectName() + fileSeparator + "Data" + fileSeparator + object.getClass().getSimpleName() + ".bin";
								
								File ff = new File(path);
								
								FileOutputStream fos = new FileOutputStream(ff, false);
								DataOutputStream dos = new DataOutputStream(fos);
								
								dos.writeUTF("<" + ((Folder)core.getDraggedObject()).getAbsolutePath() + ";" + data.split(";")[1].split("<")[0] + ";" + fieldName + ">");
								
								linkObject(object, file, fieldName);
								
								dos.flush();
								fos.flush();
								dos.close();
								fos.close();
								dis.close();
								fis.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							break;
						default:
							System.err.println("[ERROR] Couldn't find extension type of " + extension);

							break;
						}
					}
				}
			}
		}
		
		core.setDraggedObject(null);
	}
	
	public void linkObject(Object object, File file, String fieldName) throws IOException {
		File draggedFile = new File(Editor.getWorkSpaceDirectory() + "Data\\" + 
				(object instanceof GameObject ? ((GameObject)object).uuid : file.getName().split("[.]")[0]) + ".bin");
		
		draggedFile.getParentFile().mkdirs();
		if(!draggedFile.exists()) draggedFile.createNewFile();
		
		DataInputStream dis = new DataInputStream(Editor.getInputStream(draggedFile.getAbsolutePath()));
		
		int size = 0;
		
		if(dis.available() > 0) {
			size = dis.readInt();
		}
		
		String[] utfs = new String[size];
		
		for(int i = 0; i < size; i++) {
			utfs[i] = "";
			utfs[i] += dis.readUTF();
		}
		
		new FileOutputStream(draggedFile.getAbsoluteFile()).close();
		
		DataOutputStream dos2 = new DataOutputStream(new FileOutputStream(draggedFile, false));
		
		dos2.writeInt(size + 1);
		
		for(int i = 0; i < utfs.length; i++) {
			dos2.writeUTF(utfs[i]);
		}
		
		//Prefab.
		if(fieldName == null) {
			if(object instanceof GameObject) {
				dos2.writeUTF("Prefab;" + ((GameObject)object).uuid + ";" + file.getAbsolutePath());
			} else {
				System.err.println("#735 Bruh shouldn't even be a thing :(");
			}
			
			dos2.close();
			
			return;
		}
		if(object instanceof Folder) {
			dos2.writeUTF("Folder;" + ((Folder)object).getAbsolutePath() + ";" + file.getAbsolutePath() + ";" + fieldName);
		} else {
			dos2.writeUTF("GameObject;" + (((Behaviour)object).gameObject.uuid) + ";" + ((Behaviour)object).getName() + ";" + fieldName);
		}
		
		dos2.close();
	}
	
	private void drawAddBehaviour(GameObject obj, float curYPos) {
		if(UI.drawButtonWithOutline(startX + Application.getFullWidth() - (width - (width * 0.25f)), curYPos + 35 + scrollX, 0.2f, 150, 35, lineWidth, lineHeight, "Add Behaviour", 5, 0, -0.1f, 0.5f, 0.5f, Color.black, Color.lightGray, Color.black)
				&& Input.isMouseButtonDown(KeyCode.Mouse0)) {
			String behaviourName = tinyfd_inputBox("Behaviour Name!", "What Behaviour would you like to add?", "");
			
			if(behaviourName != null) {
				Behaviour beh = Editor.getBehaviour(behaviourName);
				
				obj.addBehaviour(beh);
				
				if(isPrefab) {
					try {
						addBehaviourToPrefab(obj, beh);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void addBehaviourToPrefab(GameObject object, Behaviour behaviour) throws IOException {
		File dataFile = new File(Editor.getWorkSpaceDirectory() + "Data\\" + object.uuid + ".bin");
		
		if(dataFile.exists()) {
			DataInputStream dis = new DataInputStream(Editor.getInputStream(dataFile.getAbsolutePath()));
			
			if(dis.available() <= 0)
				return;
			
			int size = dis.readInt();
			String path = "";
			GameObject obj = null;
			
			for(int i = 0; i < size; i++) {
				String data = dis.readUTF();
				
				String[] datas = data.split(";");
				
				if(datas[0].equals("Prefab")) {
					UUID uuid = UUID.fromString(datas[1]);
					path = datas[2];
					
					GameObject gameObject = GameObject.find(uuid);
					
					if(gameObject == null) {
						System.err.println("Why? " + uuid);
						
						continue;
					}
					
					if(gameObject == object)
						continue;
					
					gameObject.addBehaviour(behaviour);
					obj = gameObject;
				} else {
					System.err.println("[ERROR] [TSH] #892 Also shouldn't possibly happen ._.");
				}
			}
			
			Editor.writeObjectToFile(obj, new ObjectOutputStream(new FileOutputStream(path, false)));
		} else {
			System.err.println("[ERROR] [TSH] #874 Couldn't locate prefab folder - Got deleted?");
		}
	}
	
	public static void updateField(File objectFolder, Object object, Object value, Field f) {
		try {
			if(object.getClass().getAnnotation(ScriptAble.class) != null) {
				f.set(object, value);
				
				Editor.writeObjectToFile(object, new ObjectOutputStream(new FileOutputStream(objectFolder.getAbsolutePath(), false)));
				
				File dataFile = new File(Editor.getWorkSpaceDirectory() + "Data\\" +
						(object instanceof GameObject ? ((GameObject)object).uuid : objectFolder.getName().split("[.]")[0]) + ".bin");
				
				if(dataFile.exists()) {
					DataInputStream dis = new DataInputStream(Editor.getInputStream(dataFile.getAbsolutePath()));
					
					if(dis.available() <= 0)
						return;
					
					int size = dis.readInt();
					
					for(int i = 0; i < size; i++) {
						String data = dis.readUTF();
						
						String[] datas = data.split(";");
						
						if(datas[0].equals("Prefab")) {
							String uuid = datas[1];
							
							GameObject gameObject = GameObject.find(UUID.fromString(uuid));
							
							if(gameObject == null) {
								System.err.println("Why? " + uuid);
								
								continue;
							}
							
							String fieldName = f.getName();
							
							//Speical cases :D
							if(fieldName.equals("name") || fieldName.equals("position") 
									|| fieldName.equals("rotation") || fieldName.equals("scale")) {
								try {
									Field field = gameObject.getClass().getDeclaredField(fieldName);
									field.setAccessible(true);
									
									field.set(gameObject, value);
									
									//gameObject.getClass().getField(fieldName).set(gameObject, value);
								} catch (NoSuchFieldException | SecurityException e) {
									e.printStackTrace();
								}
							} else { //Change of a behaviour.
								System.err.println("Behaviour thingis?");
							}
						} else if(datas[0].equals("GameObject")) {
							String uuid = datas[1];				
							String behaviourName = datas[2];
							String fieldName = datas[3];
							
							GameObject gameObject = GameObject.find(UUID.fromString(uuid));
							
							//Probably won't needed? because upon
							//loading a scene, it'll reload it anyways?
							//no clue, gl future me :D
							if(gameObject == null) {
								//TODO: Place into a list, and update later.
								System.err.println("#764 It's in the 'TODO' list.. :D");
								
								return;
							}
							
							Behaviour b = gameObject.getBehaviour(behaviourName);
							Field[] fields = b.getFields();
							Field field = null;
							
							for(int j = 0; j < fields.length; j++) {
								if(fields[j].getName().equals(fieldName)) {
									field = fields[j];
									
									break;
								}
							}
							
							field.set(b, Editor.readObjectFromFile(Editor.getInputStream(objectFolder.getAbsolutePath())));
						}/* else { //It's a folder, won't be needed :D
							
						}*/
					}
				}
			} else {
				f.set(object, value);
			}
		} catch (IOException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
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

	public void setShouldUpdate(boolean shouldUpdate) {
		this.shouldUpdate = shouldUpdate;
	}
	
	public boolean canBeRemoved() {
		return canBeRemoved;
	}
	
	public void setCanBeRemoved(boolean canBeRemoved) {
		this.canBeRemoved = canBeRemoved;
	}
}
