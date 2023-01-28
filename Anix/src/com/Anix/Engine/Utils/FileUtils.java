package com.Anix.Engine.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {
	public static String loadAsString(String path) {
		StringBuilder result = new StringBuilder();
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(FileUtils.class.getResourceAsStream(path)));
			
			String line = "";
			
			while((line = reader.readLine()) != null) {
				result.append(line).append("\n");
			}
		} catch(IOException e) {
			System.err.println("[ERROR] Couldn't find the file at " + path);
		} catch(NullPointerException e) {
			System.err.println("[ERROR] Couldn't find the file at " + path);
		}
		
		return result.toString();
	}
	
	public static String loadAsString(InputStream is) {
		StringBuilder result = new StringBuilder();
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			
			String line = "";
			
			while((line = reader.readLine()) != null) {
				result.append(line).append("\n");
			}
		} catch(Exception e) {
			System.err.println("[ERROR] Couldn't find the file at ");
		}
		
		return result.toString();
	}
	
	
	public static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
