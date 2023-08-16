package com.Anix.GUI;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.awt.Font;
import java.awt.FontMetrics;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryStack;
import org.lwjglx.debug.org.lwjgl.opengl.GL20;
import org.lwjglx.debug.org.lwjgl.opengl.GL30;
import org.lwjglx.debug.org.lwjgl.opengl.GL31;

import com.Anix.IO.Application;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.IO.ProjectSettings;
import com.Anix.IO.ResourceLoader;
import com.Anix.Math.Color;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;

public final class UI {
	/*public static class Button {
		private boolean isHovering, isClicked, isRightClicked;

		public Button(boolean isHovering, boolean isClicked, boolean isRightClicked) {
			this.isHovering = isHovering;
			this.isClicked = isClicked;
			this.isRightClicked = isRightClicked;
		}

		public boolean isHovering() {
			return isHovering;
		}

		public boolean isClicked() {
			return isClicked;
		}
		
		public boolean isClickedDown() {
			return isRightClicked;
		}
	}*/
	
	public static abstract class Element {
		protected float[] instanceData;
		
		public abstract int getPrimitiveType();
		public abstract int getVertexCount();
		public float[] getInstanceData() {
			return instanceData;
		}
	}
	
	private static class Line extends Element {
		private float sx, sy, ex, ey;
		
		public Line(float sx, float sy, float ex, float ey, Color c) {
			this.sx = sx;
			this.sy = sy;
			this.ex = ex;
			this.ey = ey;
			
			instanceData = new float[] {
					sx, sy, c.r, c.g, c.b,
					ex, ey, c.r, c.g, c.b
			};
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(ex, ey, sx, sy);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Line other = (Line) obj;
			
			return Float.floatToIntBits(ex) == Float.floatToIntBits(other.ex)
					&& Float.floatToIntBits(ey) == Float.floatToIntBits(other.ey)
					&& Float.floatToIntBits(sx) == Float.floatToIntBits(other.sx)
					&& Float.floatToIntBits(sy) == Float.floatToIntBits(other.sy);
		}
		
		@Override
		public int getPrimitiveType() {
			return GL11.GL_LINES;
		}

		@Override
		public int getVertexCount() {
			return 2;
		}
	}
	
	private static class Button extends Element {
		public Button(float x, float y, float w, float h, Color c) {
			instanceData = new float[] {
					x, y, c.r, c.g, c.g,
					x + w, y, c.r, c.g, c.g,
					x + w, y + h, c.r, c.g, c.g,
					x, y + h, c.r, c.g, c.g,
			};
		}
		
		public int getPrimitiveType() {
			return GL11.GL_QUADS;
		}
		
		public int getVertexCount() {
			return 4;
		};
	}
	
	public static class Popup {
		private String ID;
		
		/*
		 * Menus.length * size.y.<br>
		 * Used to detect whether the mouse is hovering over the popup or not.
		 */
		private float height = -1;
		private byte lastPopup = -1, generated = 0;
		
		private Vector2f size;
		private Vector3f position;
		private Color color, textColor;
		
		private String[] menus;
		private Consumer<String> func;
		
		private List<Popup> supPopups = new ArrayList<Popup>();
		
		public Popup(String ID, Vector2f size, Vector3f position, Color color, Color textColor, String[] menus, Consumer<String> func) {
			this.ID = ID;
			this.size = size;
			this.position = position;
			this.color = color;
			this.textColor = textColor;
			this.menus = menus;
			this.func = func;
			
			height = (menus.length * size.y);
		}
		
		public void update() {
			renderPopup(this);
		}
		
		public void renderPopup(Popup popup) {
			boolean isHovering = drawButton(position.x, position.y, size.x * (supPopups.isEmpty() ? 1 : 2), size.y * menus.length);
			
			if(isHovering) {
				for(byte i = 0; i < popup.menus.length; i++) {
					boolean isHoveringP = drawButtonWithOutline(popup.position.x, popup.position.y + (i * size.y), popup.position.z, popup.size.x, popup.size.y, 1, 1, popup.menus[i].contains("<") ? popup.menus[i].split("<")[0] : popup.menus[i], 0, 0, -0.1f, 0.5f, 0.5f, popup.textColor, popup.color, Color.black);
					
					if(isHoveringP) {
						if(popup.menus[i].contains("<")) {
							if(popup.lastPopup != -1 && popup.lastPopup != i && popup.menus[popup.lastPopup].contains("<")) {//Is on another parent
								popup.generated = 0;
								popup.supPopups.clear();
							}
							
							popup.lastPopup = i;
							
							if(popup.generated == 0) {
								String[] subMenu = popup.menus[i].split("<")[1].split(";");
								subMenu[subMenu.length - 1] = subMenu[subMenu.length - 1].split(">")[0];
								
								popup.supPopups.add(new Popup(popup.ID + i, popup.size, new Vector3f(popup.position.x + popup.size.x, popup.position.y, popup.position.z), popup.color, popup.textColor, subMenu, popup.func));
								popup.generated = 1;
							}
						}
					}
					
					if(Input.isMouseButtonDown(KeyCode.Mouse0)) {
						popup.func.accept(popup.menus[i]);
						
						popups.remove(this);
						
						break;
					}
				}
				
				for(int i = 0; i < popup.supPopups.size(); i++) {
					renderPopup(popup.supPopups.get(i));
				}
			} else {
				popups.remove(this);
			}
		}

		public String getID() {
			return ID;
		}

		public float getHeight() {
			return height;
		}

		public Vector2f getSize() {
			return size;
		}

		public Vector3f getPosition() {
			return position;
		}

		public Color getColor() {
			return color;
		}

		public String[] getMenus() {
			return menus;
		}

		public Consumer<String> getFunc() {
			return func;
		}
	}
	
	public static class Toggle {
		private boolean value, isHovering;

		public Toggle(boolean value, boolean isHovering) {
			this.value = value;
			this.isHovering = isHovering;
		}

		public boolean getValue() {
			return value;
		}

		public boolean isHovering() {
			return isHovering;
		}
	}
	
	public static final class DropDown {
		public class DropDownClickEvent {
			private String id, buttonId;

			public DropDownClickEvent(String id, String buttonId) {
				this.id = id;
				this.buttonId = buttonId;
			}

			public String getId() {
				return id;
			}

			public String getButtonId() {
				return buttonId;
			}
		}

		public class DropDownEvent {
			private String id;

			private String buttonId;
			private DropDown dropDown;

			public DropDownEvent(String id, String buttonId, DropDown dropDown) {
				this.id = id;
				this.buttonId = buttonId;
				this.dropDown = dropDown;
			}

			public String getId() {
				return id;
			}

			public String getButtonId() {
				return buttonId;
			}

			public DropDown getDropDown() {
				return dropDown;
			}
		}

		public String id;
		private boolean toggleValue;

		private Vector3f position, toggleOffset;
		private Vector2f size, toggleSize;
		private Color textColor;

		private Texture toggleOn, toggleOff;

		public String[] children;
		private Consumer<DropDownClickEvent> func;
		private Consumer<DropDownEvent> onDraw;

		public DropDown(String id, Vector3f position, Vector3f toggleOffset, Vector2f size, Vector2f toggleSize, 
				Color textColor, Texture toggleOn, Texture toggleOff, String[] children, Consumer<DropDownClickEvent> func, Consumer<DropDownEvent> onDraw) {
			this.id = id;
			this.position = position;
			this.toggleOffset = toggleOffset;
			this.size = size;
			this.toggleSize = toggleSize;
			this.textColor = textColor;
			this.toggleOn = toggleOn;
			this.toggleOff = toggleOff;
			this.children = children;
			this.func = func;
			this.onDraw = onDraw;
		}

		public void update() {
			toggleValue = toggle(toggleOff, toggleOn, position.x + toggleOffset.x, position.y + toggleOffset.y, position.z + toggleOffset.z, toggleSize.x, toggleSize.y, toggleValue).value;

			if(toggleValue) {
				for(int i = 0; i < children.length; i++) {
					if(onDraw == null) {
						Color btnColor = new Color(Float.parseFloat(children[i].split(";")[1].split(" ")[0]), Float.parseFloat(children[i].split(";")[1].split(" ")[1]),
								Float.parseFloat(children[i].split(";")[1].split(" ")[2]));

						boolean isHovering = drawButtonWithOutline(position.x, position.y + (i * size.y) + size.y, position.z, size.x, size.y, 1, 1, children[i].split(";")[0], 0, 0, -0.1f, 0.5f, 0.5f, textColor, btnColor, Color.black);

						if(isHovering && Input.isMouseButtonDown(KeyCode.Mouse0)) {
							func.accept(new DropDownClickEvent(id, children[i]));
						}
					} else {
						onDraw.accept(new DropDownEvent(id, children[i], this));
					}
				}
			}
		}
	}
	
	private static final int POSITION_ATTRIB = 0, COLOR_ATTRIB = 1;
	//private int vao = -1, vbo, instanceVOB;
	
	private static List<Element> elements = new ArrayList<>();
	
	//Textures
	/**
	 * This texture will be used when a popup menu has a child.<br>
	 * It'll be displayed at the end.
	 */
	//public Texture popupSup;
	
	private static FontMetrics fontMatrices;
	public static Font defaultFont;
	
	public static List<Popup> popups = new ArrayList<Popup>();
	
	public static Map<TrueTypeFont, Color> trueTypeFonts = new HashMap<TrueTypeFont, Color>();
	
	public void init(String defaultFontName, int style, int size) {
		defaultFont = new Font(defaultFontName, style, size);
		fontMatrices = new FontMetrics(defaultFont) {private static final long serialVersionUID = 1L;};
		
		if(GLFW.glfwGetCurrentContext() != Application.getWindow()) {
			System.err.println("really?");
			return;
		}
		
		/*vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		
		vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL20.glVertexAttribPointer(POSITION_ATTRIB, 2, GL11.GL_FLOAT, false, 7 * 4, 0);
		GL20.glVertexAttribPointer(COLOR_ATTRIB, 3, GL11.GL_FLOAT, false, 7 * 4, 2 * 4);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		instanceVOB = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, instanceVOB);
		GL20.glVertexAttribPointer(POSITION_ATTRIB + 1, 2, GL11.GL_FLOAT, false, 7 * 4, 0);
        GL20.glVertexAttribPointer(COLOR_ATTRIB + 1, 3, GL11.GL_FLOAT, false, 7 * 4, 2 * 4);
        GL33.glVertexAttribDivisor(POSITION_ATTRIB + 1, 1);
        GL33.glVertexAttribDivisor(COLOR_ATTRIB + 1, 1);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        
        GL30.glBindVertexArray(0);*/
	}
	
	public static void update() {
		for(int i = popups.size() - 1; i >= 0; i--) {
			Popup popup = popups.get(i);

			if(!(Input.getMouseX() >= popup.getPosition().x && Input.getMouseX() <= popup.getPosition().x + popup.getSize().x 
					&& Input.getMouseY() >= popup.getPosition().y && Input.getMouseY() <= popup.getPosition().y + popup.getHeight())) {
				if(popup.supPopups.isEmpty()) {
					popups.remove(i);

					i--;

					continue;
				}
			}

			popup.update();
		}
	}
	
	public static void render() {
		if(defaultFont == null)
			return;
		
		//GL30.glBindVertexArray(vao);
		GL20.glEnableVertexAttribArray(POSITION_ATTRIB);
		GL20.glEnableVertexAttribArray(COLOR_ATTRIB);
		GL20.glEnableVertexAttribArray(POSITION_ATTRIB + 1);
		GL20.glEnableVertexAttribArray(COLOR_ATTRIB + 1);
		
		FloatBuffer instanceData = BufferUtils.createFloatBuffer(elements.size() * 7);
		
		for(Element element : elements) {
			instanceData.put(element.getInstanceData());
		}
		
		instanceData.flip();
		
		//GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, instanceVOB);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, instanceData, GL15.GL_DYNAMIC_DRAW);
		
		for(Element element : elements) {
			GL31.glDrawArraysInstanced(element.getPrimitiveType(), 0, element.getVertexCount(), elements.size());
		}
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	    GL20.glDisableVertexAttribArray(POSITION_ATTRIB + 1);
	    GL20.glDisableVertexAttribArray(COLOR_ATTRIB + 1);
	    GL20.glDisableVertexAttribArray(POSITION_ATTRIB);
	    GL20.glDisableVertexAttribArray(COLOR_ATTRIB);
	    GL30.glBindVertexArray(0);
	}
	
	public static void addLine(float sx, float sy, float ex, float ey, Color c) {
		Line l = new Line(sx, sx, ex, ex, c);
		
		if(!elements.contains(l))
			elements.add(l);
	}
	
	public static void addButton(float x, float y, float w, float h, Color c) {
		elements.add(new Button(x, y, w, h, c));
	}
	
	public static void addPopup(float z, Vector2f size, String ID, Color color, Color textColor, String[] menus, Consumer<String> func) {
		int index = -1;
		
		for(int i = popups.size() - 1; i >= 0; i--) {
			if(popups.get(i).getID().equals(ID)) {
				index = i;
			}
		}
		
		if(index == -1) {
			popups.add(new Popup(ID, size, new Vector3f((float)Input.getMouseX() - 2, (float)Input.getMouseY() - 2, z), color, textColor, menus, func));
		} else {
			popups.remove(index);
			popups.add(new Popup(ID, size, new Vector3f((float)Input.getMouseX() - 2, (float)Input.getMouseY() - 2, z), color, textColor, menus, func));
		}
	}
	
	public static Toggle toggle(Texture off, Texture on, float x, float y, float z, float width, float height, boolean value) {
		Texture texture = value ? on : off;
		
		boolean isHovering = drawButton(texture, x, y, z, width, height);

		if(isHovering && Input.isMouseButtonDown(KeyCode.Mouse0)) {
			return new Toggle(!value, isHovering);
		}

		return new Toggle(value, isHovering);
	}
	
	public static Toggle toggle(Texture off, Texture on, float x, float y, float z, float width, float height, boolean value, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor) {
		Texture texture = value ? on : off;

		boolean isHovering = drawButton(texture, x, y, z, width, height);
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);

		if(isHovering && Input.isMouseButtonDown(KeyCode.Mouse0)) {
			return new Toggle(!value, isHovering);
		}

		return new Toggle(value, isHovering);
	}
	
	public static Toggle toggle(Texture off, Texture on, float x, float y, float z, float width, float height, boolean value, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Vector2f offset, Color backgroundColor) {
		Texture texture = value ? on : off;

		drawButton(x, y, z, width + offset.x, height + offset.y, backgroundColor);
		boolean isHovering = drawButton(x, y, z, width, height, text, textOffsetX, textOffsetY, textOffsetZ, textSizeX, textSizeY, textColor, texture);

		if(isHovering && Input.isMouseButtonDown(KeyCode.Mouse0)) {
			return new Toggle(!value, isHovering);
		}

		return new Toggle(value, isHovering);
	}
	
	public static Toggle toggle(Texture off, Texture on, float x, float y, float z, float width, float height, boolean value, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Vector2f offset, Vector3f backgroundOffset, Color backgroundColor) {
		Texture texture = value ? on : off;

		drawButton(x + backgroundOffset.x, y + backgroundOffset.y, z + backgroundOffset.z, width + offset.x, height + offset.y, backgroundColor);
		boolean isHovering = drawButton(x, y, z, width, height, text, textOffsetX, textOffsetY, textOffsetZ, textSizeX, textSizeY, textColor, texture);
		
		if(isHovering && Input.isMouseButtonDown(KeyCode.Mouse0)) {
			return new Toggle(!value, isHovering);
		}

		return new Toggle(value, isHovering);
	}
	
	public void addDropDown() {

	}
	
	public static boolean drawButton(float x, float y, float width, float height, Color color) {
		drawBox(x, y, width, height, color);

		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(String text, float width, float height, float x, float y, float sizeX, float sizeY, Color textColor, Color boxColor) {
		drawBox(x, y, width, height, boxColor);
		drawString(text, x, y, 0, sizeX, sizeY, textColor);

		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(float x, float y, float width, float height, String text, float textOffsetX, float textOffsetY, float textSizeX, float textSizeY, Color textColor) {
		drawString(text, x + textOffsetX, y + textOffsetY, textSizeX, textSizeY, textColor);
		
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(float x, float y, float z, float width, float height, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor) {
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);

		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(String text, float width, float height, float x, float y, float sizeX, float sizeY, Color textColor) {
		drawString(text, x, y, 0, sizeX, sizeY, textColor);

		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(String text, float width, float height, float x, float y, float z, float sizeX, float sizeY, Color textColor) {
		drawString(text, x, y, z, sizeX, sizeY, textColor);

		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(float x, float y, float z, float width, float height, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Texture texture) {
		drawImage(texture.getTextureID(), x, y, z, width, height);
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);
		
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(float x, float y, float z, float width, float height, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Color color, Texture texture) {
		drawImage(texture.getTextureID(), color, x, y, z, width, height);
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);
		
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(float x, float y, float z, float width, float height, Texture texture) {
		drawImage(texture.getTextureID(), x, y, z, width, height);

		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(float x, float y, float width, float height, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Color color) {
		drawBox(x, y, width, height, color);
		drawString(text, x + textOffsetX, y + textOffsetY, textSizeX, textSizeY, textColor);
		
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(float x, float y, float z, float width, float height, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Color color) {
		drawBox(x, y, z, width, height, color);
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);
		
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();
		
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(float x, float y, float z, float width, float height, Color color) {
		drawBox(x, y, z, width, height, color);

		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	public static boolean drawButtonWithOutline(float x, float y, float z, float width, float height, float lineWidth, float lineHeight, Color color, Color lineColor) {
		drawBoxWithOutline(x, y, z, width, height, lineWidth, lineHeight, color, lineColor);
		
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();
		
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	public static boolean drawButtonWithOutline(float x, float y, float z, float width, float height, float lineWidth, float lineHeight, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Color color, Color lineColor) {
		drawBoxWithOutline(x, y, z, width, height, lineWidth, lineHeight, color, lineColor);
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);
		
		text = null;
		color = null;
		
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();
		
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	public static boolean drawButtonWithOutlineWithDepth(float x, float y, float z, float width, float height, float lineWidth, float lineHeight, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Color color, Color lineColor) {
		GL13.glDepthRange(0, 1);
		drawBoxWithOutline(x, y, z, width, height, lineWidth, lineHeight, color, lineColor);
		GL13.glDepthRange(0, 0.01);
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);

		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	public static boolean drawRoundedButtonWithOutLine(float x, float y, float z, float width, float height, float radius, float lineWidth, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Color color, Color lineColor) {
		boolean returnValue = drawRoundedButton(x, y, z, width, height, radius, lineWidth, color, lineColor);
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);

		return returnValue;
	}

	public static boolean drawRoundedButton(float x, float y, float z, float size_x, float size_y, float radius, float lineWidth, Color color, Color lineColor) {
		double PI = Math.PI;
		glPushMatrix();

		glColor3f(color.getRed(), color.getGreen(), color.getBlue());
		glTranslatef(0, 0, z);

		glBegin(GL13.GL_POLYGON);
		
		// top-left corner
		drawGLRoundedCorner(x, y + radius, 3 * PI / 2, PI / 2, radius);
		
		// top-right
		drawGLRoundedCorner(x + size_x - radius, y, 0.0, PI / 2, radius);

		// bottom-right
		drawGLRoundedCorner(x + size_x, y + size_y - radius, PI / 2, PI / 2, radius);

		// bottom-left
		drawGLRoundedCorner(x + radius, y + size_y, PI, PI / 2, radius);

		glEnd();
		
		glLoadIdentity();
		glPopMatrix();

		if(lineWidth > 0) {
			x += 1;
			z -= 0.1f;

			//Middle left - Top Left
			UI.drawline(x + radius, y, z, x - (radius * 0.2f), y + radius, z, lineColor, lineWidth);

			//Middle left
			UI.drawline(x - (radius * 0.2f), y + radius, z, x - (radius * 0.2f), y + (size_y - radius), z, lineColor, lineWidth);

			//Top Middle
			UI.drawline(x + radius, y, z, x + size_x - radius, y, z, lineColor, lineWidth);

			//Middle left - bottom left
			UI.drawline(x + radius, y + size_y, z, x - (radius * 0.2f), y - radius + size_y, z, lineColor, lineWidth);

			//Bottom Middle
			UI.drawline(x + radius, y + size_y, z, x + size_x - radius, y + size_y, z, lineColor, lineWidth);
			
			//Bottom right - Middle
			UI.drawline(x - radius + size_x, y + size_y, z, x - (radius * 0.2f) + size_x, y - radius + size_y, z, lineColor, lineWidth);

			//Middle right
			UI.drawline(x - (radius * 0.2f) + size_x, y + radius, z, x - (radius * 0.2f) + size_x, y + (size_y - radius), z, lineColor, lineWidth);

			//Middle right - Top right
			UI.drawline(x - radius + size_x, y, z, x - (radius * 0.2f) + size_x, y + radius, z, lineColor, lineWidth);
		}

		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();

		return mouseX >= x && mouseX <= x + size_x && mouseY >= y && mouseY <= y + size_y;
	}
	
	private static void drawGLRoundedCorner(float x, float y, double sa, double arc, float r) {
		int N_ROUNDING_PIECES = 4;
		double PI = Math.PI;

		// centre of the arc, for clockwise sense
		float cent_x = (float) (x + r * Math.cos(sa + PI / 2));
		float cent_y = (float) (y + r * Math.sin(sa + PI / 2));

		// build up piecemeal including end of the arc
		int n = (int) Math.ceil(N_ROUNDING_PIECES * arc / PI * 2);
		for (int i = 0; i <= n; i++) {
			double ang = sa + arc * (double)i  / (double)n;

			// compute the next point
			float next_x = (float) (cent_x + r * Math.sin(ang));
			float next_y = (float) (cent_y - r * Math.cos(ang));
			GL13.glVertex2f(next_x, next_y);
		}
	}

	public static boolean drawButton(float x, float y, float width, float height) {
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();
		
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(Texture texture, float x, float y, float z, float width, float height) {
		drawImage(texture.getTextureID(), x, y, z, width, height);
		
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();
		
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static boolean drawButton(int textureID, float x, float y, float z, float width, float height) {
		drawImage(textureID, x, y, z, width, height);
		
		double mouseX = Input.getMouseX();
		double mouseY = Input.getMouseY();
		
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public static void drawString(String text, float x, float y, float sizeX, float sizeY, Color color) {
		if(defaultFont == null) {
			System.err.println("[ERROR] You must call the initializion method first!");

			return;
		}

		//glEnable(GL_COLOR_MATERIAL);
		glEnable(GL_TEXTURE_2D);

		if(color == null)
			color = Color.white;

		boolean found = false;

		for(Map.Entry<TrueTypeFont, Color> entry : trueTypeFonts.entrySet()) {
			if(entry.getValue().equals(color)) {
				entry.getKey().drawString(x, y, text, sizeX, sizeY);
				
				found = true;

				break;
			}
		}

		if(!found) {
			trueTypeFonts.put(new TrueTypeFont(defaultFont, true, color), color);
			drawString(text, x, y, sizeX, sizeY, color);
		}

		//glDisable(GL_COLOR_MATERIAL);
		glDisable(GL_TEXTURE_2D);
	}

	public static TrueTypeFont drawString(String text, float x, float y, float z, float sizeX, float sizeY, Color color) {
		if(defaultFont == null) {
			System.err.println("[ERROR] You must call the initializion method first!");

			return null;
		}

		if(text == null) {
			System.err.println("[ERROR] You cannot draw empty text! " + x + " " + y);

			return null;
		}

		//glEnable(GL_COLOR_MATERIAL);
		glEnable(GL_TEXTURE_2D);

		if(color == null)
			color = Color.white;

		boolean found = false;

		for(Map.Entry<TrueTypeFont, Color> entry : trueTypeFonts.entrySet()) {
			if(entry.getValue().equals(color)) {
				entry.getKey().drawString(x, y, z, text, sizeX, sizeY);
				
				found = true;

				return entry.getKey();
			}
		}

		if(!found) {
			trueTypeFonts.put(new TrueTypeFont(defaultFont, true, color), color);
			return drawString(text, x, y, z, sizeX, sizeY, color);
		}

		//glDisable(GL_COLOR_MATERIAL);
		glDisable(GL_TEXTURE_2D);

		return null;
	}

	public static TrueTypeFont drawString(String text, float x, float y, float z, int format, float sizeX, float sizeY, Color color) {
		if(defaultFont == null) {
			System.err.println("[ERROR] You must call the initializion method first!");

			return null;
		}

		if(text == null) {
			System.err.println("[ERROR] You cannot draw empty text! " + x + " " + y);

			return null;
		}

		//glEnable(GL_COLOR_MATERIAL);
		glEnable(GL_TEXTURE_2D);

		if(color == null)
			color = Color.white;

		boolean found = false;

		for(Map.Entry<TrueTypeFont, Color> entry : trueTypeFonts.entrySet()) {
			if(entry.getValue().equals(color)) {
				entry.getKey().drawString(x, y, z, format, text, sizeX, sizeY);

				found = true;

				return entry.getKey();
			}
		}

		if(!found) {
			trueTypeFonts.put(new TrueTypeFont(defaultFont, true, color), color);
			drawString(text, x, y, z, format, sizeX, sizeY, color);
		}

		//glDisable(GL_COLOR_MATERIAL);
		glDisable(GL_TEXTURE_2D);

		return null;
	}

	public static void drawString(String text, float x, float y, float sizeX, float sizeY, int format, Color color) {
		if(defaultFont == null) {
			System.err.println("[ERROR] You must call the initializion method first!");

			return;
		}

		//glEnable(GL_COLOR_MATERIAL);
		glEnable(GL_TEXTURE_2D);

		if(color == null)
			color = Color.white;

		boolean found = false;

		for(Map.Entry<TrueTypeFont, Color> entry : trueTypeFonts.entrySet()) {
			if(entry.getValue().equals(color)) {
				entry.getKey().drawString(x, y, text, sizeX, sizeY, format);
				
				found = true;
				
				break;
			}
		}

		if(!found) {
			trueTypeFonts.put(new TrueTypeFont(defaultFont, true, color), color);
			drawString(text, x, y, sizeX, sizeY, format, color);
		}

		//glDisable(GL_COLOR_MATERIAL);
		glDisable(GL_TEXTURE_2D);
	}

	public static void drawBox(float x, float y, float width, float height, Color color) {
		glBindTexture(GL_TEXTURE_2D, 0);
		//glDisable(GL_TEXTURE_2D);

		//float[] currentColor = new float[4];
		//GL11.glGetFloatv(GL11.GL_CURRENT_COLOR, currentColor);

		glPushMatrix();
		glTranslatef(x, y, -1);

		glBegin(GL_QUADS);
		glColor3f(color.getRed(), color.getGreen(), color.getBlue());

		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, 0);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(width, 0);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(width, height);

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, height);
		glEnd();
		glPopMatrix();

		//GL11.glColor4f(currentColor[0], currentColor[1], currentColor[2], currentColor[3]);

		glLoadIdentity();
	}

	public static void drawBox(float x, float y,  float z, float width, float height, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, Color color) {
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);

		//float[] currentColor = new float[4];
		//GL11.glGetFloatv(GL11.GL_CURRENT_COLOR, currentColor);

		glBindTexture(GL_TEXTURE_2D, 0);
		glPushMatrix();
		glTranslatef(x, y, z);

		glBegin(GL_QUADS);
		glColor3f(color.getRed(), color.getGreen(), color.getBlue());

		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, 0);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(width, 0);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(width, height);

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, height);
		glEnd();
		glPopMatrix();

		//GL11.glColor4f(currentColor[0], currentColor[1], currentColor[2], currentColor[3]);

		glBindTexture(GL_TEXTURE_2D, 0);
		
		glLoadIdentity();
	}

	public static void drawBox(float x, float y,  float z, float width, float height, String text, float textOffsetX, float textOffsetY, float textOffsetZ, float textSizeX, float textSizeY, Color textColor, int r, int g, int b) {
		drawString(text, x + textOffsetX, y + textOffsetY, z + textOffsetZ, textSizeX, textSizeY, textColor);

		//float[] currentColor = new float[4];
		//GL11.glGetFloatv(GL11.GL_CURRENT_COLOR, currentColor);

		glBindTexture(GL_TEXTURE_2D, 0);
		glDisable(GL_TEXTURE_2D);

		glPushMatrix();
		glTranslatef(x, y, z);

		glBegin(GL_QUADS);
		glColor3f(r, g, b);

		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, 0);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(width, 0);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(width, height);

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, height);
		glEnd();
		glPopMatrix();

		//GL11.glColor4f(currentColor[0], currentColor[1], currentColor[2], currentColor[3]);

		glLoadIdentity();
	}

	public static void drawBox(float x, float y, float z, float width, float height, Color color) {
		//float[] currentColor = new float[4];
		//GL11.glGetFloatv(GL11.GL_CURRENT_COLOR, currentColor);
		
		glPushMatrix();
		
		glColor3f(color.getRed(), color.getGreen(), color.getBlue());
		glTranslatef(x, y, z);
		
		glBegin(GL_QUADS);
		
		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, 0);
		
		glTexCoord2f(1, 0); //Upper right
		glVertex2f(width, 0);
		
		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(width, height);
		
		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, height);
		glEnd();
		
		//GL11.glColor4f(currentColor[0], currentColor[1], currentColor[2], currentColor[3]);
		glPopMatrix();
		glLoadIdentity();
	}

	public static void drawBoxWithOutline(float x, float y, float z, float width, float height, float lineWidth, float lineHeight, Color color, Color outlineColor) {
		//Top Left to right
		drawBox(x, y, z, width, lineHeight, outlineColor);
		
		//Top left to bottom left
		drawBox(x, y, z, lineWidth, height, outlineColor);
		
		//Top right to bottom right
		drawBox(x + width - lineWidth, y, z, lineWidth, height, outlineColor);
		
		//Bottom left to right
		drawBox(x, y + height - lineHeight, z, width, lineHeight, outlineColor);
		
		//Actual box.
		drawBox(x, y, z, width, height, color);
	}
	
	public static void drawImage(Texture texture, float x, float y, float z) {
		glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
		glEnable(GL_TEXTURE_2D);
		
		glTranslatef(x, y, z);
		
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, 0);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(texture.getWidth(), 0);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(texture.getWidth(), texture.getHeight());

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, texture.getHeight());
		glEnd();

		glBindTexture(GL_TEXTURE_2D, 0);
		glDisable(GL_TEXTURE_2D);

		glLoadIdentity();
	}
	
	public static void drawImage(Texture texture, float x, float y, float z, float scaler) {
		glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
		glEnable(GL_TEXTURE_2D);

		glTranslatef(x, y, z);

		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, 0);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(texture.getWidth()*scaler, 0);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(texture.getWidth()*scaler, texture.getHeight()*scaler);

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, texture.getHeight()*scaler);
		glEnd();

		glBindTexture(GL_TEXTURE_2D, 0);
		glDisable(GL_TEXTURE_2D);

		glLoadIdentity();
	}
	
	public static void drawImage(int texture, float x, float y, float width, float height) {
		glBindTexture(GL_TEXTURE_2D, texture);
		glEnable(GL_TEXTURE_2D);

		glTranslatef(x, y, -1);

		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, 0);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(width, 0);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(width, height);

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, height);
		glEnd();

		glBindTexture(GL_TEXTURE_2D, 0);
		glDisable(GL_TEXTURE_2D);

		glLoadIdentity();
	}

	public static void drawImage(int texture, float x, float y, float z, float width, float height) {
		glBindTexture(GL_TEXTURE_2D, texture);
		glEnable(GL_TEXTURE_2D);

		glTranslatef(x, y, z);

		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, 0);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(width, 0);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(width, height);

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, height);
		glEnd();

		glBindTexture(GL_TEXTURE_2D, 0);
		glDisable(GL_TEXTURE_2D);

		glLoadIdentity();
	}

	public static void drawImage(int texture, Color color, float x, float y, float z, float width, float height) {
		//float[] currentColor = new float[4];
		//GL11.glGetFloatv(GL11.GL_CURRENT_COLOR, currentColor);

		glColor3f(color.getRed(), color.getGreen(), color.getBlue());
		glBindTexture(GL_TEXTURE_2D, texture);
		
		glPushMatrix();
		glTranslatef(x, y, z);

		glBegin(GL_QUADS);

		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, 0);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(width, 0);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(width, height);

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, height);
		glEnd();
		glPopMatrix();

		//GL11.glColor4f(currentColor[0], currentColor[1], currentColor[2], currentColor[3]);

		glBindTexture(GL_TEXTURE_2D, 0);

		glLoadIdentity();
	}

	public static void drawImageInverted(int texture, float x, float y, float width, float height) {
		glBindTexture(GL_TEXTURE_2D, texture);
		glEnable(GL_TEXTURE_2D);

		glTranslatef(x, y, -1);

		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, height);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(width, height);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(width, 0);

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, 0);
		glEnd();

		glBindTexture(GL_TEXTURE_2D, 0);
		glDisable(GL_TEXTURE_2D);

		glLoadIdentity();
	}

	public static void drawImageInverted(int texture, float x, float y, float z, float width, float height) {
		glBindTexture(GL_TEXTURE_2D, texture);
		glEnable(GL_TEXTURE_2D);

		glTranslatef(x, y, z);
		glColor3f(1, 1, 1);

		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); //Upper left
		glVertex2f(0, height);

		glTexCoord2f(1, 0); //Upper right
		glVertex2f(width, height);

		glTexCoord2f(1, 1); //Bottom right
		glVertex2f(width, 0);

		glTexCoord2f(0, 1); //Bottom left
		glVertex2f(0, 0);
		glEnd();

		glBindTexture(GL_TEXTURE_2D, 0);
		glDisable(GL_TEXTURE_2D);

		glLoadIdentity();
	}

	public static void drawSphere(float positionX, float positionY, double radius, Color color, int lats, int longs) {
		int i, j;
		for(i = 0; i <= lats; i++) {
			double lat0 = Math.PI * (-0.5 + (double) (i - 1) / lats);
			double z0  = Math.sin(lat0);
			double zr0 =  Math.cos(lat0);

			double lat1 = Math.PI * (-0.5 + (double) i / lats);
			double z1 = Math.sin(lat1);
			double zr1 = Math.cos(lat1);
			GL11.glPushMatrix();
			GL11.glTranslatef(positionX, positionY, 0);

			GL11.glBegin(GL11.GL_QUAD_STRIP);
			for(j = 0; j <= longs; j++) {
				double lng = 2 * Math.PI * (double) (j - 1) / longs;
				double x = Math.cos(lng);
				double y = Math.sin(lng);

				GL11.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
				GL11.glNormal3d(x * zr0, y * zr0, z0);
				GL11.glVertex3d(radius * x * zr0, radius * y * zr0, radius * z0);
				GL11.glNormal3d(x * zr1, y * zr1, z1);
				GL11.glVertex3d(radius * x * zr1, radius * y * zr1, radius * z1);
			}

			GL11.glEnd();
			GL11.glPopMatrix();
		}
	}

	public static void drawline(float startX, float startY, float startZ, float endX, float endY, float endZ, Color color, float lineWidth) {
		GL13.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
		
		GL11.glLineWidth(lineWidth);
		
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
		GL11.glVertex3f(startX, startY, startZ);
		GL11.glVertex3f(endX, endY, endZ);
		GL11.glEnd();
		
		GL11.glPopAttrib();
		GL13.glPopMatrix();
	}

	public static boolean drawlineButton(float startX, float startY, float startZ, float endX, float endY, float endZ, Color color, float lineWidth) {
		GL11.glLineWidth(lineWidth);
		
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
		GL11.glVertex3f(startX, startY, startZ);
		GL11.glVertex3f(endX, endY, endZ);
		GL11.glEnd();
		
		float xDif = endX - startX;
		float yDif = endY - startY;
		
		return Input.getMouseX() >= startX && Input.getMouseX() <= startY + (xDif == 0 ? lineWidth : xDif)
				&& Input.getMouseY() >= startY && Input.getMouseY() <= startY + (yDif == 0 ? lineWidth : yDif);
	}

	public static Texture loadTexture(String fileName) {
		Texture returnImage = null;

		try {
	        ByteBuffer image, imageBuffer = loadImageFromFile(fileName);

			if(imageBuffer == null) {
				System.err.println("[ERORR] Couldn't locate a texture with the name of " + fileName);

				return null;
			}

			try (MemoryStack stack = stackPush()) {
				IntBuffer w    = stack.mallocInt(1);
				IntBuffer h    = stack.mallocInt(1);
				IntBuffer comp = stack.mallocInt(1);

				// Use info to read image metadata without decoding the entire image.
				// We don't need this for this demo, just testing the API.
				if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
					System.err.println("[ERROR] Couldn't load an image with the name of: " + fileName);
				}

				// Decode the image
				image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);

				if (image == null) {
					throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
				}

				returnImage = new Texture(w.get(0), h.get(0), createTexture(w.get(0), h.get(0), comp.get(0), image));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnImage;
	}
	
	private static ByteBuffer loadImageFromFile(String fileName) throws IOException {
	    String[] possiblePaths = {
	        ProjectSettings.getProjectPath() + fileName,
	        fileName,
	        "resources/" + fileName,
	        "/" + fileName,
	        fileName.substring(9)
	    };
	    
	    ByteBuffer imageBuffer = null;
	    for (String path : possiblePaths) {
	        try {
	            imageBuffer = ResourceLoader.ioResourceToByteBuffer(path, 8 * 1024);
	            if (imageBuffer != null) {
	                break;
	            }
	        } catch (Exception ignored) {
	        }
	    }

	    if (imageBuffer == null) {
	        System.err.println("[ERROR] Couldn't locate a texture with the name of " + fileName);
	    }
	    return imageBuffer;
	}
	
	private static int createTexture(int width, int height, int components, ByteBuffer image) {
		int texID = glGenTextures();

	    glBindTexture(GL_TEXTURE_2D, texID);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

	    int format = (components == 3) ? GL_RGB : GL_RGBA;

	    glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, image);

	    generateMipmaps(format, width, height, image);

	    return texID;
	}
	
	private static void generateMipmaps(int format, int width, int height, ByteBuffer image) {
	    ByteBuffer inputPixels = image;
	    int mipmapLevel = 0;
	    
	    while (1 < width || 1 < height) {
	        int outputWidth = Math.max(1, width >> 1);
	        int outputHeight = Math.max(1, height >> 1);

	        ByteBuffer outputPixels = memAlloc(outputWidth * outputHeight * 4);
	        // ... (stbir_resize_uint8_generic call here)

	        glTexImage2D(GL_TEXTURE_2D, ++mipmapLevel, format, outputWidth, outputHeight, 0, format, GL_UNSIGNED_BYTE, outputPixels);

	        memFree(inputPixels);
	        inputPixels = outputPixels;
	        width = outputWidth;
	        height = outputHeight;
	    }

	    memFree(inputPixels);
	}
	
	public static Vector2f getScale(float width, float height, float scalerMatchWidthOrHeight) {
		Vector2f scalerReferenceResolution = new Vector2f(height, width);
		
		return new Vector2f((float)Math.pow(width/scalerReferenceResolution.x, 1f - scalerMatchWidthOrHeight),
				(float)Math.pow(height/scalerReferenceResolution.y, scalerMatchWidthOrHeight));
	}
	
	public static FontMetrics getFontMatrics() {
		return fontMatrices;
	}
}
