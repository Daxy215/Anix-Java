package com.Anix.Engine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;

public class UndoManager {
	public static class Action {
		private int size;
		
		private Consumer<Object[]> onUndo;
		
		private List<Object> data = new ArrayList<>();
		
		public Action(int size, Consumer<Object[]> onUndo) {
			this.size = size;
			this.onUndo = onUndo;
			
			UndoManager.addAction(this);
		}
		
		public Action(int size, Consumer<Object[]> onUndo, List<Object> data) {
			this.size = size;
			this.onUndo = onUndo;
			this.data = data;
			
			UndoManager.addAction(this);
		}
		
		public int getSize() {
			return size;
		}
		
		public void undo(Object[] data) {
			onUndo.accept(data);
		}
		
		public void addData(Object data) {
			this.data.add(data);
		}
	}
	
	private static List<Action> actions = new ArrayList<Action>();
	
	public void update() {
		if(actions.isEmpty())
			return;
		
		if(Input.isKey(KeyCode.LeftControl) && Input.isKeyDown(KeyCode.Z)) {
			Action action = actions.get(actions.size() - 1);
			
			if(action.data.isEmpty())
				return;
			
			if(action.data.size() % action.size == 1) {
				System.err.println("[ERROR] [Undo Manager] An incorruption has occurred.");
			
				actions.remove(action);
				
				return;
			}
			
			Object[] data = new Object[action.size];
			int index = 0;
			
			for(int i = action.data.size() - action.size; i < action.data.size(); i++) {
				data[index++] = action.data.get(i);
			}
			
			action.undo(data);
			
			for(int i = action.data.size() - action.size; i < action.data.size(); i++) {
				action.data.remove(i);
				
				i--;
			}
 		}
	}
	
	public static void addAction(Action action) {
		actions.add(action);
	}
}
