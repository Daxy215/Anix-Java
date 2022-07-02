package com.Anix.IO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoCorrector {
	public class Node {
        Map<Character, Node> children;
        char c;
        boolean isWord;
 
        public Node(char c) {
            this.c = c;
            children = new HashMap<>();
        }
        
        public Node() {
            children = new HashMap<>();
        }
 
        public void insert(String word) {
            if (word == null || word.isEmpty())
                return;
            
            char firstChar = word.charAt(0);
            Node child = children.get(firstChar);
            
            if (child == null) {
                child = new Node(firstChar);
                children.put(firstChar, child);
            }
            
            if (word.length() > 1)
                child.insert(word.substring(1));
            else
                child.isWord = true;
        }
    }
	
	private Node root;
    
    public AutoCorrector(List<String> words) {
        root = new Node();
        
        for (String word : words)
            root.insert(word);
 
    }
 
    public boolean find(String prefix, boolean exact) {
    	Node lastNode = root;
    	
        for (char c : prefix.toCharArray()) {
            lastNode = lastNode.children.get(c);
            
            if (lastNode == null)
                return false;
        }
        
        return !exact || lastNode.isWord;
    }
 
    public boolean find(String prefix) {
        return find(prefix, false);
    }
 
    public void suggestHelper(Node root, List<String> list, StringBuffer curr) {
        if (root.isWord) {
            list.add(curr.toString());
        }
 
        if (root.children == null || root.children.isEmpty())
            return;
        	
        for (Node child : root.children.values()) {
            suggestHelper(child, list, curr.append(child.c));
            curr.setLength(curr.length() - 1);
        }
    }
 
    public List<String> suggest(String prefix) {
        List<String> list = new ArrayList<>();
        Node lastNode = root;
        StringBuffer curr = new StringBuffer();
        
        for (char c : prefix.toCharArray()) {
            lastNode = lastNode.children.get(c);
            if (lastNode == null)
                return list;
            curr.append(c);
        }
        
        suggestHelper(lastNode, list, curr);
        
        return list;
    }
}
