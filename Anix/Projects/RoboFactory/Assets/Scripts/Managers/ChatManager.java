import java.util.ArrayList;
import java.util.List;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Math.Color;

public class ChatManager extends Behaviour {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;	
	
	private static class Text {
		private String text;
		private Color color;

		public Text(String text, Color color) {
			this.text = text;
			this.color = color;
		}

		public String getText() {
			return text;
		}

		public Color getColor() {
			return color;
		}
	}
	
	private static int currentIndex;
	
	private static long PERIOD = 4000L;
	private static long lastTime = System.currentTimeMillis() - PERIOD;
	
	private static boolean showChat;
	
	private static List<Text> texts = new ArrayList<Text>();
	
	@Override
	public void update() {
		if(showChat) {
			long thisTime = System.currentTimeMillis();
			
			if((thisTime - lastTime) >= PERIOD) {
				lastTime = thisTime;
				
				showChat = false;
			}
		}
	}
	
	@Override
	public void render() {
		if(!showChat)//&& !Core.instance.chatBox.isTyping())
			return;
		
		for(int i = texts.size() - 1; i >= 0; i--) {
			//UI.drawString(texts.get(i).getText(), 0, Application.getHeight() - (Core.instance.chatBox.isTyping() ? 80 : 40) - (i * 30), 0.75f, 0.75f, texts.get(i).color);
		}
	}
	
	public static void addText(String text, Color color) {
		lastTime = System.currentTimeMillis();
		showChat = true;
		
		String command = text.split(" ")[1];
		
		/*if(command.equalsIgnoreCase("chips")) {
			int amount = Integer.parseInt(text.split(" ")[2]);
			
			//Core.instance.player.playerUI.chips += amount;
		}*/
		
		texts.add(0, new Text(text, color));
		
		if(texts.size() >= 9) {
			texts.remove(8);
		}
	}
}