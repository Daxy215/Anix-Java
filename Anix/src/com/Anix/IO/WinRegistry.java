package com.Anix.IO;

import java.io.IOException;
import java.util.Scanner;

/**
 * Sample Java Application to access to windows registry through 
 * the windows commandline application reg.exe
 * 
 * Just a sample application: 
 * 
 * Open your command prompt and enter REG /? to add missing features
 * 
 * https://www.codeproject.com/Questions/1238094/How-can-I-access-windows-registry-by-java
 */
class WinRegistry {
	/**
	 * Success status code
	 */
	private static final int REG_SUCCESS = 0;
	
	/**
	 * Failure status code
	 */
	//private static final int REG_FAILURE = 1;
	
	/**
	 * Implemented root-keys<br>
	 * HKLM HKEY_LOCAL_MACHINE elevated privileges needed<br>
	 * HKCU HKEY_CURRENT_USER <br>
	 * HKCR HKEY_CLASSES_ROOT elevated privileges needed<br>
	 * HKU HKEY_USER  <br>
	 * HKCC HKEY_CURRENT_CONFIG elevated privileges needed<br>
	 */
	public static enum WRKey {
		HKLM, HKCU, HKCR, HKU, HKCC
	}
	
	/**
	 * Registry data-types
	 */
	public static enum WRType {
        REG_SZ, REG_MULTI_SZ, REG_EXPAND_SZ,
        REG_DWORD, REG_QWORD, REG_BINARY, REG_NONE
	}
	
	/**
	 * Creates a new string for the registry cli.
	 * 
	 * @param hkey the  root key [ HKLM , HKCU ] 
	 * @param key the name of the key  SOFTWARE\WINDOWS
	 * @param valueName the name of the value
	 * @param data 
	 * @param type 
	 * @param force override an existing key ?
	 * 
	 * @see WRKey
	 * @see WRType
	 * @return the string value to the registry cli
	 */
	public String createRegString(WRKey hkey, String key, String valueName, byte[] data,  WRType type, boolean force) {
		String keyString = " " + hkey + "\\" + key; 
		String valueString = valueName!=null 	? " /v "+ valueName : "" ;
		String dataString =  data != null 		? (" " + (data.length > 0 ? " /d " + new String(data) : "")): "";
		String typeString = type != null 		? " /t " + type : "";
		
		return keyString + valueString + dataString + typeString +  (force ? " /f" : "");
	}
	
	/**
	 * Adds a new key to the windows registry
	 * 
	 * @param hkey The root-key [ HKLM , HKCU ] 
	 * @param key the key name to create eg. SOFTWARE\TEST\ABCD
	 * @return true on success
	 * @throws IOException
	 * @throws InterruptedException
	 * 
	 * @see WRKEY
	 * 	
	 */
	public boolean addKey(WRKey hkey, String key) throws IOException, InterruptedException {
		Process proc = Runtime.getRuntime().exec("REG ADD "+hkey+"\\" + key + " /f");
		proc.waitFor();
		
		return proc.exitValue() == REG_SUCCESS;
	}
	
	/**
	 * Adds a value to an existing registry key
	 * @param hkey The root-key [ HKLM , HKCU ] 
	 * @param key the key name to create eg. SOFTWARE\TEST\ABCD
	 * @param valueName the name of the value to add the data
	 * @param data the data as a byte array
	 * @param type the type of data [ REG_STRING ,REG_DWORD ]
	 * @return true on success
	 * @throws IOException
	 * @throws InterruptedException
	 * 
	 * @see WRKey
	 * @see WRType
	 */
	public boolean addValue(WRKey hkey, String key, String valueName, byte[] data,  WRType type) throws IOException, InterruptedException {
		String regString = createRegString(hkey, key, valueName, data, type, true);
		Process proc = Runtime.getRuntime().exec("REG ADD " + regString);
		proc.waitFor();
		
		return proc.exitValue() == REG_SUCCESS;
	}
	
	/**
	 * Shows a registry value
	 * @param hkey The root-key [ HKLM , HKCU ] 
	 * @param key the key name to open eg. SOFTWARE\TEST\ABCD
	 * @param valueName the name of the value 
	 * @return true on success
	 * @throws IOException
	 * @throws InterruptedException
	 * 
	 * @see WRKey
	 */
	public String readValue(WRKey hkey, String key, String valueName)  throws IOException, InterruptedException {
		String regString = createRegString(hkey, key, valueName, null, null, false);
		Process proc = Runtime.getRuntime().exec("REG QUERY " + regString);
		proc.waitFor();
		
		if(proc.exitValue() == REG_SUCCESS) {
			Scanner sc = new Scanner(proc.getInputStream());
			String str = "";
			
			do {
				str = sc.nextLine();
				
				if(str.trim().startsWith(valueName)) {
					String[] values = str.trim().split(" ");
					String value = values[values.length - 1];
					sc.close();
					
					return value;
				}
			} while(str != null && sc.hasNext());
		} else {
			Scanner sc = new Scanner(proc.getErrorStream());
			String str = "";
			
			do {
				str = sc.nextLine();
				System.err.println(str);
			} while(str != null && sc.hasNext());
			
			System.err.println("Query failure.. " + regString);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param hkey The root-key [ HKLM , HKCU ] 
	 * @param key the key name to open eg. SOFTWARE\TEST\ABCD
	 * @param valueName the name of the value 
	 * @param withChildren view all subdirectories
	 * @return true on success
	 * @throws IOException
	 * @throws InterruptedException
	 * 
	 * @see WRKey
	 */
	public boolean showAllValues(WRKey hkey, String key, String valueName, boolean withChildren) throws IOException, InterruptedException {
		String regString = createRegString(hkey,key,null,null,null,false);
		Process proc = Runtime.getRuntime().exec("REG QUERY " + regString + "\\" + valueName + " " + (withChildren? " /s" :" "));
		proc.waitFor();
		
		if(proc.exitValue() == REG_SUCCESS) {
			Scanner sc = new Scanner(proc.getInputStream());
			
			String str = "";
			do {
				str = sc.nextLine();
				System.out.println(str);
			} while(sc.hasNext() && str != null);
			
			if(sc!=null) {
				sc.close();
			}
		} else {
			System.err.println("Query failure..\n" + regString);
		}
		
		return proc.exitValue() == REG_SUCCESS;
	}
}