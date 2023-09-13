package com.Anix.Behaviours;

import com.Anix.IO.Application;
import com.Anix.Math.Color;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;

public class Button extends Behaviour {
	private static final long serialVersionUID = 7906923458955219326L;
	
	public String id = "button id";
	public String text = "Button";
	
	public Vector3f position = new Vector3f();
	public Vector3f textOffset = new Vector3f();
	public Vector2f size = new Vector2f(128, 32);
	public Vector2f textSize = new Vector2f(0.5f);
	public Color color = Color.white;
	public Color textColor = Color.black;
	
	@Override
	public void render() {
		//double mouseX = Input.getMouseX() - Application.getStartX();
		//double mouseY = Input.getMouseY() - Application.getStartY();
		
		/*boolean isHovering = UI.drawButton(position.x + Application.getStartX(), position.y + Application.getStartY(), position.z, size.x, size.y,
				new Text(text, textSize, new Vector3f(textOffset.x, textOffset.y, textOffset.z + -0.1f), textColor), color);
		
		if(!Editor.isPlaying() && isHovering && Input.isMouseButton(KeyCode.Mouse0)) {
			position.x = (float)mouseX - (size.x * 0.5f);
			position.y = (float)mouseY - (size.y * 0.5f);
			
			position.x = Math.max(0, position.x);
			position.x = Math.min(Application.getWidth() - size.x, position.x);
			position.y = Math.max(0, position.y);
			position.y = Math.min(Application.getHeight() - size.y, position.y);
		}
		
		if(Editor.isPlaying() && isHovering) {
			int clickType = -1;
			
			if(Input.isMouseButtonDown(KeyCode.Mouse0)) {
				clickType = 0;
			} else if(Input.isMouseButtonDown(KeyCode.Mouse1)) {
				clickType = 1;
			}
			
			if(clickType == -1)
				return;
			
			if(gameObject.hasParent()) {
				for(int i = 0; i < gameObject.getParent().getBehaviours().size(); i++) {
					gameObject.getParent().getBehaviours().get(i).onButtonClicked(id, clickType);
				}
			} else {
				Console.LogErr("[ERROR] Cannot register on click event for the button with the id of " + id + " because it doesn't contain a parent.");
			}
		}*/
	}
	
	@Override
	public void onValueChanged(String fieldName, String oldValue, String newValue) {
		position.x = Math.max(0, position.x);
		position.x = Math.min(Application.getWidth() - size.x, position.x);
		position.y = Math.max(0, position.y);
		position.y = Math.min(Application.getHeight() - size.y, position.y);
	}
}
