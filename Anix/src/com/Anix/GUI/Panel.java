package com.Anix.GUI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Math.Color;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;

public final class Panel implements Serializable {
	private static final long serialVersionUID = -6941712242531857931L;
	
	/**
	 * TODO: Remove this class.
	 */
	public static class Text {
		protected String text;

		protected int format;

		protected Vector2f size;
		protected Vector3f offset;
		protected Color color;

		
		public Text(String text, Vector2f size, Color color) {
			this.text = text;
			this.size = size;
			this.offset = new Vector3f();
			this.color = color;
		}
		
		@SuppressWarnings("unused")
		public Text(String text, int format, Vector2f size, Color color) {
			this.text = text;
			this.format = format;
			this.size = size;
			this.offset = new Vector3f();
			this.color = color;
		}

		@SuppressWarnings("unused")
		public Text(String text, Vector2f size, Vector3f offset, Color color) {
			this.text = text;
			this.size = size;
			this.offset = offset;
			this.color = color;
		}

		public Text(String text, int format, Vector2f size, Vector3f offset, Color color) {
			this.text = text;
			this.format = format;
			this.size = size;
			this.offset = offset;
			this.color = color;
		}

		public String getText() {
			return text;
		}

		public int getFormat() {
			return format;
		}

		public void setFormat(int format) {
			this.format = format;
		}

		public Vector2f getSize() {
			return size;
		}

		public Vector3f getOffset() {
			return offset;
		}

		public Color getColor() {
			return color;
		}

		public void setText(String text) {
			this.text = text;
		}

		public void setSize(Vector2f size) {
			this.size = size;
		}

		public void setOffset(Vector3f offset) {
			this.offset = offset;
		}

		public void setColor(Color color) {
			this.color = color;
		}
	}
	
	public static class Element {
		public String ID;

		public Vector3f position;
		public Vector2f size;

		public Element(String ID, Vector3f position, Vector2f size) {
			this.ID = ID;
			this.position = position;
			this.size = size;
		}
	}
	
	public final static class BorderSetting {
		public float width;
		
		public Color color;
		
		public BorderSetting(float width, Color color) {
			this.width = width;
			this.color = color;
		}
	}
	
	public static class Button extends Element {
		public Texture texture;
		
		public Text text;
		public ButtonSetting buttonSetting;
		public BorderSetting borderSetting;
		
		public Button(String ID, Texture texture, Vector3f position, Vector2f size, Text text, ButtonSetting buttonSetting,
				BorderSetting borderSetting) {
			super(ID, position, size);
			
			this.texture = texture;
			this.text = text;
			this.buttonSetting = buttonSetting;
			this.borderSetting = borderSetting;
		}
		
		public void onSelect() {
			
		}
		
		public void onClick() {
			
		}
	}
	
	public final static class ButtonSetting {
		public boolean interactAble, staySelected;
		
		public float roundedRadius;
		public Color unselectedColor, selectedColor, hoveredColor;
		
		public ButtonSetting(boolean interactAble, float roundedRadius, Color unselectedColor, Color selectedColor, Color hoveredColor) {
			this.interactAble = interactAble;
			this.roundedRadius = roundedRadius;
			this.unselectedColor = unselectedColor;
			this.selectedColor = selectedColor;
			this.hoveredColor = hoveredColor;
		}
		
		public ButtonSetting(boolean interactAble, boolean staySelected, float roundedRadius, Color unselectedColor, Color selectedColor, Color hoveredColor) {
			this.interactAble = interactAble;
			this.staySelected = staySelected;
			this.roundedRadius = roundedRadius;
			this.unselectedColor = unselectedColor;
			this.selectedColor = selectedColor;
			this.hoveredColor = hoveredColor;
		}
	}
	
	public static class TextInput extends Element {
		public String returnString;
		
		public int maxTextLength;
		
		public boolean isTyping;
		
		public Text defaultText;
		public ButtonSetting buttonSetting;
		public TextInputSetting setting;
		public BorderSetting borderSetting;
		
		public TextInput(String ID, int maxTextLength, Vector3f position, Vector2f size, Text defaultText, ButtonSetting buttonSetting,
				TextInputSetting setting, BorderSetting borderSetting) {
			super(ID, position, size);
			
			this.maxTextLength = maxTextLength;
			this.defaultText = defaultText;
			this.buttonSetting = buttonSetting;
			this.setting = setting;
			this.borderSetting = borderSetting;
			
			if(defaultText != null)
				returnString = defaultText.text;
			else {
				this.defaultText = new Text("", new Vector2f(0.5f), Color.black);
				
				returnString = "";
			}
		}
		
		public void onLetterRemove() {
			
		}
		
		public void onType(String newCharacter) {
			
		}
		
		public void onFinishedTyping() {
			
		}
	}
	
	public final static class TextInputSetting {
		public boolean shouldRest;
		public Color defaultColor, hoveredColor, TypingColor;
		
		public TextInputSetting(boolean shouldRest, Color defaultColor, Color hoveredColor, Color typingColor) {
			this.shouldRest = shouldRest;
			this.defaultColor = defaultColor;
			this.hoveredColor = hoveredColor;
			this.TypingColor = typingColor;
		}
	}
	
	public final static class TextInputEvent {
		public String id, returnString;
		
		public TextInputEvent(String id, String returnString) {
			this.id = id;
			this.returnString = returnString;
		}
	}
	
	//TextInput Settings
	private static final long timeToClear = 350;
	private static long curTimeToClear = timeToClear;
	private static long lastClearButtonTime;
	
	//public transient Button selectedButton;
	//private transient TextInput selectedTextInput;
	private static Element currentSelectedElement;
	private static Element hoveredElement = null;
	//private transient static GUI gui;
	
	//private transient Consumer<String> buttonConsumer;
	//private transient Consumer<TextInputEvent> textInputConsumer;
	
	public transient List<Button> buttons = new ArrayList<Button>();
	public transient List<TextInput> textInputs = new ArrayList<TextInput>();
	
	public static List<Element> allElements = new ArrayList<Element>();
	//public static List<Button> allButtons = new ArrayList<Button>();
	//public static List<TextInput> allTextInputs = new ArrayList<TextInput>();
	
	public Panel() {
		
	}
	
	public Panel(GUI gui) {
		//8Panel.gui = gui;
	}
	
	/*public Panel(Consumer<String> buttonConsumer, Consumer<TextInputEvent> textInputConsumer) {
		this.buttonConsumer = buttonConsumer;
		this.textInputConsumer = textInputConsumer;
	}*/
	
	public static void render() {
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();
		
		/*
		 * TODO: If button is on top of another button
		 * and the button gets selected, after clicking on it
		 * again, it'll select the button under it.
		 */
		
		Color color;
		
		for(int i = 0; i < allElements.size(); i++) {
			Element e = allElements.get(i);
			
			if(e instanceof Button) {
				Button btn = (Button)e;
				
				if(btn.buttonSetting == null || btn.borderSetting == null) {
					continue;
				}
				
				Vector3f pos = btn.position;
				
				color = btn.buttonSetting.unselectedColor;
				
				if(currentSelectedElement == null || !currentSelectedElement.equals(btn)) {
					boolean withinBounds = mouseX >= pos.x && mouseX <= pos.x + (btn.size.x)
							&& mouseY >= pos.y && mouseY <= pos.y + (btn.size.y);
					
					if(withinBounds && (hoveredElement == null || hoveredElement.equals(btn))) {
						color = btn.buttonSetting.hoveredColor;
						hoveredElement = btn;
						
						if(Input.isMouseButtonDown(KeyCode.Mouse0) && btn.buttonSetting.interactAble) {
							color = btn.buttonSetting.selectedColor;
							
							currentSelectedElement = btn;
							
							btn.onSelect();
							btn.onClick();
						}
					}
				} else if(btn.buttonSetting.staySelected) {
					btn.onSelect();
					color = btn.buttonSetting.selectedColor;
				} else {
					currentSelectedElement = null;
				}
				
				if(btn.texture == null) {
					if(btn.borderSetting.width == 0) {
						UI.drawButton(pos.x, pos.y, pos.z, btn.size.x, btn.size.y, color);
					} else {
						UI.drawRoundedButton(pos.x, pos.y, pos.z, btn.size.x, btn.size.y, btn.buttonSetting.roundedRadius, btn.borderSetting.width, color, btn.borderSetting.color);
					}
				} else {
					float w = btn.size.x == 0 ? btn.texture.getWidth() : btn.size.x;
					float h = btn.size.y == 0 ? btn.texture.getHeight() : btn.size.y;
					
					UI.drawRoundedButton(pos.x, pos.y, pos.z + 0.1f, w, h, btn.buttonSetting.roundedRadius, btn.borderSetting.width, color,
							btn.borderSetting.color);
					UI.drawImage(btn.texture.getTextureID(), pos.x, pos.y, pos.z, w * 2, h * 2);
				}
				
				if(btn.text != null) {
					UI.drawString(btn.text.getText(), pos.x + btn.text.getOffset().x, pos.y + btn.text.getOffset().y, pos.z + (btn.text.getOffset().z + -0.1f),
							btn.text.getFormat(), btn.text.getSize().x, btn.text.getSize().y, btn.text.getColor());	
				}
			} else if(e instanceof TextInput) {
				TextInput ti = (TextInput)e;
				Text t = ti.defaultText;
				
				boolean withinBounds = mouseX >= ti.position.x && mouseX <= ti.position.x + ti.size.x && mouseY >= ti.position.y && mouseY <= ti.position.y + ti.size.y;
				
				if(ti.isTyping) {
					color = ti.setting.TypingColor;
				} else {
					if(withinBounds && (hoveredElement == null || hoveredElement.equals(ti))) {
						color = ti.setting.hoveredColor;
						
						hoveredElement = ti;
						
						if(Input.isMouseButtonDown(KeyCode.Mouse0)) {
							ti.isTyping = true;
							
							if(ti.setting.shouldRest) {
								ti.returnString = "";
							}
							
							currentSelectedElement = ti;
						}
					} else {
						color = ti.setting.defaultColor;
					}
				}
				
				if(!(currentSelectedElement instanceof TextInput) && (hoveredElement == null || !hoveredElement.equals(ti))) {
					ti.isTyping = false;
					color = ti.setting.defaultColor;
				}
				
				/*ti.textButton =*/ UI.drawRoundedButtonWithOutLine(ti.position.x, ti.position.y, ti.position.z, ti.size.x, ti.size.y, ti.buttonSetting.roundedRadius, ti.borderSetting.width,
						ti.returnString, t.offset.x, t.offset.y, t.offset.z + -0.1f, t.getSize().x, t.getSize().y, t.color, color, ti.borderSetting.color);
				
				if(currentSelectedElement != null && currentSelectedElement.equals(ti)) {
					if(ti.defaultText.text.length() < ti.maxTextLength) {
						if(Input.isKeyDown(KeyCode.Return)) {
							ti.isTyping = false;
							
							ti.onFinishedTyping();
							
							if(ti.setting.shouldRest) {
								ti.returnString = ti.defaultText.text;
							}
							
							currentSelectedElement = null;
							
							return;
						}
						
						if(Input.isKeyDown(KeyCode.Backspace) ||Input.isKey(KeyCode.Backspace) && (Math.abs(System.currentTimeMillis() - lastClearButtonTime) > curTimeToClear)) {
							lastClearButtonTime = System.currentTimeMillis();
							
							curTimeToClear -= 50;
							
							if(ti.returnString.length() > 0) {
								ti.returnString = ti.returnString.substring(0, ti.returnString.length() - 1);
								
								ti.onLetterRemove();
								
								return;
							}
						}
						
						if(Input.isKeyUp(KeyCode.Backspace)) {
							curTimeToClear = timeToClear;
						}
						
						if(Input.isKeyDown(Input.getCurrentPressedKey()) && ti.returnString.length() < ti.maxTextLength) {
							//Space
							if(Input.getCurrentPressedKey() == 32) {
								ti.returnString += " ";
								
								return;
							}
							
							if(GLFW.glfwGetKeyName(Input.getCurrentPressedKey(), 1) != null) {
								ti.returnString += String.valueOf(GLFW.glfwGetKeyName(Input.getCurrentPressedKey(), 0));
								ti.onType(String.valueOf(GLFW.glfwGetKeyName(Input.getCurrentPressedKey(), 0)));
							}/* else {
								//System.err.println("[ERROR] Couldn't find key with the name of " + GLFW.glfwGetKeyName(Input.getCurrentPressedKey(), 0));
							}*/
						}
					}
				}
			}
		}
		
		hoveredElement = null;
	}
	
	public void addButton(Button button) {
		buttons.add(button);
		//allButtons.add(button);
		allElements.add(button);
		
		allElements.sort(new Comparator<Element>() {
			@Override
			public int compare(Element o1, Element o2) {
				return Float.compare(o1.position.z, o2.position.z);
			}
		});
	}
	
	public void addTextInput(TextInput textInput) {
		textInputs.add(textInput);
		//allTextInputs.add(textInput);
		allElements.add(textInput);
		
		allElements.sort(new Comparator<Element>() {
			@Override
			public int compare(Element o1, Element o2) {
				return Float.compare(o1.position.z, o2.position.z);
			}
		});
	}
	
	public void clear() {
		//selectedButton = null;
		//selectedTextInput = null;
		currentSelectedElement = null;
		
		for(int i = 0; i < buttons.size() + textInputs.size(); i++) {
			if(i < buttons.size())
				allElements.remove(buttons.get(i));
			
			if(i < textInputs.size())
				allElements.remove(textInputs.get(i));
		}
		
		buttons.clear();
		textInputs.clear();
	}
}
