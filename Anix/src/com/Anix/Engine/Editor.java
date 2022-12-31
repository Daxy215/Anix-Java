package com.Anix.Engine;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.lwjgl.glfw.GLFW;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.Camera;
import com.Anix.Behaviours.LightSource;
import com.Anix.Engine.Graphics.Mesh;
import com.Anix.Engine.Graphics.Shader;
import com.Anix.GUI.Sprite;
import com.Anix.GUI.Texture;
import com.Anix.GUI.UI;
import com.Anix.GUI.Windows.Assets;
import com.Anix.GUI.Windows.Assets.Folder;
import com.Anix.GUI.Windows.Console;
import com.Anix.IO.Application;
import com.Anix.IO.ProjectSettings;
import com.Anix.IO.ProjectSettings.ProjectType;
import com.Anix.Main.Core;
import com.Anix.Objects.GameObject;
import com.Anix.SceneManager.Scene;
import com.Anix.SceneManager.SceneManager;

public final class Editor {
	private boolean lastIsFocused, once;
	
	private static boolean isPlaying;
	public static boolean canAddObjects = true;
	public static boolean isRunningViaJar;
	
	private static String workSpaceDirectory = System.getProperty("user.dir");
	private final String editorVersion = "2.5 beta";
	
	private static Path tempPath, texturesPath;
	private Path classesPath;
	
	private Scene startedScene;
	private Complier complier = new Complier();
	
	private Core core;
	
	//TODO: 'spritesToLoad' array will keep on being
	//added to, so find a way to make it so that it'll
	//only use it when the engine first starts.
	//private List<String> spritesToLoad = new ArrayList<String>();
	private List<Folder> linkedObjectsFolders = new ArrayList<Folder>();
	private static List<String> parentsToLoad = new ArrayList<String>();
	public static List<Behaviour> importedClasses = new ArrayList<Behaviour>();
	
	public Editor(Core core) {
		this.core = core;
	}
	
	public void init(String[] args) {
		if(args.length > 0) {
			int index = Integer.parseInt(args[0]);
			File[] projects = getProjects();
			core.setProjectName("Projects\\" + projects[index].getName());
		} else {
            ProjectSettings.projectType = ProjectType.D3;
			core.setProjectName("Projects\\Dino Lands");
		}
		
		workSpaceDirectory += "\\" + core.getProjectName() + "\\";
		
		InputStream is = Core.class.getResourceAsStream("/Project.Settings");
		
		if(is == null) {
			File configFile = new File(workSpaceDirectory + "config.properties");
			
			try {
				FileReader r = new FileReader(configFile);
				Properties p = new Properties();
				
				p.load(r);
				
				String recordedVersion = p.getProperty("Version");
				String type = p.getProperty("ProjectType");
				
				if(type != null)
					ProjectSettings.projectType = ProjectType.valueOf(type);
				
				if(!editorVersion.equals(recordedVersion)) {
					Console.LogWar("[Warning] Editor version isn't the same as the projects editor version! This could case some issues!");
				}
				
				r.close();
			} catch(FileNotFoundException e) {
				saveConfig(configFile);
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			}
		} else {
			ProjectSettings.load(new BufferedReader(new InputStreamReader(is)), false);
		}
		
		String fileSeparator = System.getProperty("file.separator");
		
		tempPath = Assets.createTempDirectory("Anix").toPath();
		texturesPath = Assets.createTempDirectory("Anix\\Textures").toPath();
		
		if(ProjectSettings.isEditor) {
			complier.setClassesDir(new File(Editor.getWorkSpaceDirectory() + "Data" + fileSeparator + "bin"));
			complier.setSourceDir(new File(Editor.getWorkSpaceDirectory() + "Assets"));
		} else {
			classesPath = Assets.createTempDirectory("Anix\\Classes").toPath();
			
			complier.setClassesDir(new File(classesPath.toString()));
		}
		
		isRunningViaJar = Objects.equals(this.getClass().getResource("Editor.class").getProtocol(), "jar");
	}
	
	public void update() {
		if(!ProjectSettings.isEditor)
			return;
		
		lastIsFocused = GLFW.glfwGetWindowAttrib(Application.getWindow(), GLFW.GLFW_FOCUSED) == 1;
		
		//To make sure it never reload twice.
		if(lastIsFocused && once) {
			once = false;
			
			if(!isPlaying) {
				reLoad();
				
				System.err.println("reloaded: " + Core.getMasterRenderer().getEntities().size());
			}
		} else if(!lastIsFocused) {
			if(!once) {
				saveProject();
			}
			
			once = true;
		}
	}
	
	public void saveScene(Scene currentScene) {
		if(!ProjectSettings.isEditor) {
			return;
		}
		
		if(Editor.isPlaying) {
			System.err.println("[ERROR] Cannot save during gameplay!");
			
			return;
		}
		
		if(currentScene.getGameObjects().size() <= 0) {
			return;
		}

		System.out.println("Saving.. " + currentScene.getName());

		File file = new File(currentScene.getFolder().getAbsolutePath());
		
		if(ProjectSettings.isEditor) {
			file.getParentFile().mkdirs();
			
			if(!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(file, false);
			
			saveGameObjects(currentScene, fos);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Succesfully saved " + currentScene.getName());
	}
	
	private void saveGameObjects(Scene currentScene, FileOutputStream fos) {
		try {
	    	ObjectOutputStream stream = new ObjectOutputStream(fos);
			
	    	stream.writeInt(currentScene.getGameObjects().size());
			
			for(int i = 0; i < currentScene.getGameObjects().size(); i++) {
				writeGameObject(currentScene.getGameObjects().get(i), stream);
			}
			
			stream.flush();
			stream.close();
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeGameObject(GameObject obj, ObjectOutputStream stream) throws IOException {
		writeObjectToFile(obj, stream);
	}
	
	public static void writeObjectToFile(Object data, ObjectOutputStream stream) {
	    try {
	        stream.writeObject(data);
	        
	        if(data instanceof GameObject) {
	    		stream.writeUTF((((GameObject)data).hasParent() ? ((GameObject)data).getParent().uuid.toString() : "null"));
	    	}
	    } catch(IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void saveScene() {
		if(!ProjectSettings.isEditor) {
			return;
		}

		if(SceneManager.getCurrentScene() == null) {
			return;
		}

		String fileSeparator = System.getProperty("file.separator");
		
		File fff = new File(Editor.getWorkSpaceDirectory() + "Data" + fileSeparator + "Data.bin");
		
		if(ProjectSettings.isEditor) {
			fff.getParentFile().mkdirs();

			if(!fff.exists()) {
				try {
					fff.createNewFile();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			FileOutputStream fos = new FileOutputStream(fff, false);
			DataOutputStream dos = new DataOutputStream(fos);

			dos.writeUTF("Last Scene:" + SceneManager.getCurrentScene().getName());

			dos.flush();
			dos.close();
			fos.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveProject() {
		if(Editor.isPlaying) {
			System.err.println("[ERROR] Cannot save during gameplay!");

			return;
		}

		for(int i = 0; i < SceneManager.getScenes().size(); i++) {
			saveScene(SceneManager.getScenes().get(i));
		}

		saveScene();
		saveConfig(new File(workSpaceDirectory + "config.properties"));
	}
	
	public void load(Scene currentScene) {
		if(currentScene == null) {
			System.err.println("[ERROR] Cannot load a null scene!");
			
			return;
		}
		
		System.out.println("Loading scene with the name of: " + currentScene.getName());
		
		try {
			complier.compile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//currentScene.getGameObjects().clear();
		currentScene.destroy();
		
		UUID lastSelectedUUID = core.getGUI().getHierachy().getSelectedObject() != null ? core.getGUI().getHierachy().getSelectedObject().uuid : null;
		core.getGUI().getHierachy().setSelectedObject(null);
		
		InputStream is = null;
		try {
			is = getInputStream(currentScene.getFolder().getAbsolutePath());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		if(is == null) {
			System.err.println("[ERROR] [TSH] Couldn't locate a scene with the name of: " + currentScene.getFolder().getName() + ". Perhaps been deleted?");
			
			return;
		}
		
		try {
			if(is.available() <= 0) {
				System.err.println("[ERROR] No data found in the scene with the name of " + currentScene.getName());
				
				is.close();
				
				return;
			}
			
			ObjectInputStream stream = getObjectStream(is);
			
			boolean canAdd = canAddObjects;
			canAddObjects = true;
			loadGameObjects(currentScene, stream);
			canAddObjects = canAdd;
			
			for(int i = 0; i < parentsToLoad.size(); i++) {
				GameObject obj = SceneManager.getCurrentScene().getGameObjectByUUID(UUID.fromString(parentsToLoad.get(i).split(";")[0]));
				
				if(obj == null) {
					continue;
				}
				
				if(obj.shouldBeRemoved) {
					continue;
				}
				
				GameObject parent = SceneManager.getCurrentScene().getGameObjectByUUID(UUID.fromString(parentsToLoad.get(i).split(";")[1]));
				
				if(parent == null) {
					obj.setParent(null);
					
					continue;
				}
				
				if(parent.shouldBeRemoved) {
					obj.setParent(null);
					
					continue;
				}
				
				obj.setParent(parent);
			}
			
			for(int i = 0; i < currentScene.getGameObjects().size(); i++) {
				for(int l = 0; l < currentScene.getGameObjects().get(i).getBehaviours().size(); l++) {
					Behaviour beh = currentScene.getGameObjects().get(i).getBehaviours().get(l);
					
					for(int k = 0; k < beh.getFields().length; k++) {
						Field f = beh.getFields()[k];
						String typeName = f.getType().getSimpleName();
						
						if(typeName.equalsIgnoreCase("gameobject")) {
							try {
								GameObject go = (GameObject)f.get(beh);
								
								if(go != null) {
									GameObject nGo = GameObject.find(go.uuid);
									f.set(beh, nGo);
								} else {
									f.set(beh, null);
								}
							} catch (IllegalArgumentException | IllegalAccessException e) {
								e.printStackTrace();
							}
						} else {
							System.err.println("[ERROR] [TSH] Couldn't find a type of: " + typeName);
						}
					}
				}
			}
			
			if(lastSelectedUUID != null) {
				core.getGUI().getHierachy().setSelectedObject(currentScene.getGameObjectByUUID(lastSelectedUUID));
			}
			
			stream.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Scene successfully loaded!");
	}
	
	public void loadGameObjects(Scene currentScene, ObjectInputStream stream) throws IOException {
		int size = stream.readInt();
		
		for(int i = 0; i < size; i++) {
			GameObject obj = (GameObject)readObjectFromFile(stream);
			
			String parentInfo = "";
			try {
				parentInfo = stream.readUTF();
			} catch(Exception e) {
				continue;
			}
			
			if(obj == null) {
				System.err.println("[ERROR] [TSH] #546 Couldn't load gameobject..");
				
				continue;
			}
			
			if(obj.shouldBeRemoved) {
				continue;
			}
			
			obj.updateTransform();
			
			if(!parentInfo.equals("null")) {
				parentsToLoad.add(obj.uuid + ";" + parentInfo);
			}
			
			if(!obj.getBehaviours().isEmpty())
				Core.updateAbleObjects.add(obj);
			
			for(int j = 0; j < obj.getBehaviours().size(); j++) {
				Behaviour behaviour = obj.getBehaviours().get(j);
				
				behaviour.setGameObject(obj);
				
				if(!behaviour.isEnabled)
					continue;
				
				try {
					behaviour.awake();
				} catch(Exception | Error e) {
					CharArrayWriter cw = new CharArrayWriter();
					PrintWriter w = new PrintWriter(cw);
					e.printStackTrace(w);
					w.close();
					String trace = cw.toString();
					
					Console.LogErr("[ERROR] " + obj.getBehaviours().get(j).getName() + " because " + trace);
				}
				
				if(isPlaying) {
					try {
						behaviour.start();
					} catch(Exception | Error e) {
						CharArrayWriter cw = new CharArrayWriter();
						PrintWriter w = new PrintWriter(cw);
						e.printStackTrace(w);
						w.close();
						String trace = cw.toString();
						
						Console.LogErr("[ERROR] " + obj.getBehaviours().get(j).getName() + " because " + trace);
					}
				}
			}
			
			currentScene.addObject(obj);
		}
	}
	
	public static Object readObjectFromFile(InputStream is) {
	    return readObjectFromFile(getObjectStream(is));
	}
	
	public static Object readObjectFromFile(ObjectInputStream stream) {
	    try {
	        return stream.readObject();
	    } catch (Exception e) {
	    	e.printStackTrace(System.err);
	    	return null;
		}
	}
	
	public static InputStream getInputStream(File file) throws FileNotFoundException {
		return getInputStream(file.getAbsolutePath());
	}
	
	public static InputStream getInputStream(String path) throws FileNotFoundException {
		InputStream is = null;
		
		if(isRunningViaJar && !Files.exists(Paths.get(path))) {
			InputStream s = Editor.class.getResourceAsStream((path.startsWith("/") ? "" : "/") + path);
			
			if(s == null)
				s = Editor.class.getResourceAsStream(path);
			
			return s;
		}
		
		if(ProjectSettings.isEditor) {
			File file = new File(path);
			
			is = new FileInputStream(file);
		} else {
			File file = new File(path);
			Path newPath = null;
			System.err.println("path: " + Files.isDirectory(Paths.get(path)));
			if(!Files.isDirectory(Paths.get(path))) {
				newPath = Assets.createTempFile(tempPath + "\\" + file.getName()).toPath();
				System.err.println("created new file: " + newPath.toString());
			} else {
				newPath = Paths.get(path);
			}
			
			is = Editor.class.getResourceAsStream(newPath.toString());
		}
		
		return is;
	}
	
	public static ObjectInputStream getObjectStream(InputStream is) {
		ObjectInputStream stream = null;
		
		if(is == null) {
			return null;
		}
		
		try {
			stream = new ObjectInputStream(is) {
				@Override
				public Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
					try {
						return Thread.currentThread().getContextClassLoader().loadClass(desc.getName());
					} catch (Exception e) {
						if(!desc.getName().equals("[F")) { //Float
							System.err.println("[ERROR] [TSH] Couldn't find a class with the name of " + desc.getName() + "\n" + e.getMessage());
						}
					}
					
					// Fall back (e.g. for primClasses)
					return super.resolveClass(desc);
				}
			};
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return stream;
	}
	
	public void saveConfig(File f) {
		try {
			f.getParentFile().mkdirs();
			
			if (!f.exists()) {
			    f.createNewFile();
			}
			
			Properties p = new Properties();
			p.setProperty("Version", editorVersion);
			p.setProperty("ProjectType", ProjectSettings.projectType.name());
			//p.setProperty("perviousSize", Application.getFullWidth() + " " + Application.getFullHeight());
			
			FileWriter writer = new FileWriter(f);
			p.store(writer, "Project Configuration\nPlease do not change anything unless if you know what you're doing.");
			writer.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reLoad() {
		Core.updateAbleObjects.clear();
		core.getGUI().getConsole().clear();
		Core.meshManager.clear();
		LightSource.lights.clear();
		Core.getSprites().clear();
		//Core.getMasterRenderer().destroy();
		Core.getMasterRenderer().getEntities().clear();
		Application.setFOV(70);
		Camera.main = null;
		Shader.shaders.clear();
		Application.setMouseState(false);
		
		//canAddObjects = true;
		
		System.out.println("Reloading the project..");
		
		try {
			complier.compile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!isPlaying) {
			for(int i = 0; i < SceneManager.getScenes().size(); i++) {
				saveScene(SceneManager.getScenes().get(i));
			}
			
			String fullPath = System.getProperty("user.dir");
			String fileSeparator = System.getProperty("file.separator");
			
			//To update files.
			core.getGUI().getAssets().getFolders().clear();
			
			File file = new File(fullPath + fileSeparator + core.getProjectName() + fileSeparator + "Assets");
			
			if(file.exists() && file.listFiles() != null) {
				for(int i = 0; i < file.listFiles().length; i++) {
					addFolder(file.listFiles()[i].getAbsolutePath(), null);
				}
			}
		}
		
		if(isPlaying) {
			setIsPlaying(false);
		} else {
			if(Core.getMasterRenderer() != null) {
				Core.getMasterRenderer().getEntities().clear();
			}
			
			if(SceneManager.getCurrentScene() != null) {
				load(SceneManager.getCurrentScene());
			}
		}
		
		//canAddObjects = false;
		System.gc();
		
		System.out.println("Successfully re-loaded the project with " + SceneManager.getScenes().size() + " scene(s)!");
	}
	
	public void destroy() {
		Runtime.getRuntime().addShutdownHook(new Thread(
				new Runnable() {
					@Override
					public void run() {
						try {
							if(!Files.exists(tempPath))
								return;
							
							Files.walkFileTree(tempPath, new SimpleFileVisitor<Path>() {
								@Override
								public FileVisitResult visitFile(Path file,
										BasicFileAttributes attrs)
												throws IOException {
									Files.delete(file);
									return FileVisitResult.CONTINUE;
								}
								@Override
								public FileVisitResult postVisitDirectory(Path dir, IOException e)
										throws IOException {
									if (e == null) {
										Files.delete(dir);
										return FileVisitResult.CONTINUE;
									}
									// directory iteration failed
									throw e;
								}
							});
						} catch (IOException e) {
							throw new RuntimeException("Failed to delete " + tempPath, e);
						}
					}}));
	}
	
	private void createBytes(String path) {
		InputStream stream = null;
		
		try {
			stream = getInputStream(path);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		
		if(stream == null) {
			System.err.println("[ERROR] [TSH] Couldn't find class with the path of: " + path);
			
			return;
		}
		
		String seperator = System.getProperty("line.separator");
		
		String[] name = null;
		
		if(ProjectSettings.isEditor) {
			name = path.replaceAll(Pattern.quote("\\"), "\\\\").split("\\\\");
		} else {
			name = path.split("/");
		}
		
		String fileSeparator = System.getProperty("file.separator");
		
		Path srcPath = null;
		
		if(ProjectSettings.isEditor) {
			srcPath = Paths.get(System.getProperty("user.dir") + fileSeparator + core.getProjectName() + fileSeparator + "Data" + fileSeparator + "bin", name[name.length - 1]);
			
			if(!Files.exists(Paths.get(System.getProperty("user.dir") + fileSeparator + core.getProjectName() + fileSeparator + "Data" + fileSeparator + "bin"))) {
				try {
					Files.createDirectory((Paths.get(System.getProperty("user.dir") + fileSeparator + core.getProjectName() + fileSeparator + "Data" + fileSeparator + "bin")));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			if(!Files.exists(srcPath)) {
				try {
					Files.createFile(srcPath);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			srcPath = Paths.get(classesPath.toString(), name[name.length - 1]);
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		
		try {
			if(!ProjectSettings.isEditor) {
				Assets.createTempFile(classesPath.toString() + "\\" + name[name.length - 1]);
			}
			
			Files.write(srcPath, reader.lines().collect(Collectors.joining(seperator)).getBytes(StandardCharsets.UTF_8));	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Behaviour getBehaviour(String behaviourName) {
		for(int i = Editor.importedClasses.size() - 1; i >= 0; i--) {
			String name = "";
			
			if(behaviourName.contains("\\")) {
				String[] fullName = behaviourName.split("\\\\");

				behaviourName = fullName[fullName.length - 1];
			}
			
			if(Editor.importedClasses.get(i).getName().contains("\\")) {
				String[] fullName = Editor.importedClasses.get(i).getName().split("\\\\");

				name = fullName[fullName.length - 1];
			} else {
				name = Editor.importedClasses.get(i).getName();
			}
			
			behaviourName = behaviourName.contains(".") ? behaviourName.split("\\.")[0] : behaviourName;
			name = name.contains(".") ? name.split("\\.")[0] : name;
			
			if(name.equalsIgnoreCase(behaviourName)) {
				try {
					Behaviour beh = Editor.importedClasses.get(i).getClass().getConstructor().newInstance();

					return beh;
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.err.println("[ERROR] #1291 Couldn't find a script with the name of " + behaviourName);
		
		return null;
	}
	
	public void loadProject() {
		if(ProjectSettings.isEditor) {
			String fullPath = System.getProperty("user.dir");
			String fileSeparator = System.getProperty("file.separator");
			
			//Loading assets
			File file = new File(fullPath + fileSeparator + core.getProjectName() + fileSeparator + "Assets");
			
			if(file.exists() && file.listFiles() != null) {
				for(int i = 0; i < file.listFiles().length; i++) {
					addFolder(file.listFiles()[i].getAbsolutePath(), null);
				}
			}
			
			File fff = new File(fullPath + fileSeparator + core.getProjectName() + fileSeparator + "Data" + fileSeparator + "Data.bin");
			fff.getParentFile().mkdirs();
			
			if(fff.exists()) {
				try {
					FileInputStream fis = new FileInputStream(fff);
					DataInputStream dis = new DataInputStream(fis);
					
					if(dis.available() > 0) {
						String lastSceneName = dis.readUTF().split(":")[1];
						SceneManager.loadScene(lastSceneName);
					}
					
					fis.close();
					dis.close();
				} catch(IOException e) {  
					e.printStackTrace();
				}
			}
		} else {
			try {
				String me = Editor.class.getName().replace(".", "/") + ".class";
				URL dirURL = Editor.class.getClassLoader().getResource(me);
				
				if(dirURL.getProtocol().equals("jar")) {
					String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
					JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
					Enumeration<JarEntry> entries = jar.entries();
					
					while(entries.hasMoreElements()) {
						JarEntry je = entries.nextElement();
						String name = je.getName();
						String[] realName = name.replace("/", "\\").split("\\\\");
						String actualName = realName[realName.length - 1];
						
						if(name.startsWith("Assets")) {
							String extension = name.split("[.]")[1];
							
							if(extension.equalsIgnoreCase("java")) {
								createBytes(name);
							} else if(extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg")) {
								InputStream is = jar.getInputStream(je);
								System.err.println("loading in: " + actualName);
								
								try {
									Files.copy(is, Paths.get(texturesPath + "\\" + actualName.split("[.]")[0] + "." + extension), StandardCopyOption.REPLACE_EXISTING);
								} catch (IOException e) {
									e.printStackTrace();
								}
								
								File file = new File(texturesPath + "\\" + actualName.split("[.]")[0] + "." + extension);
								
								Sprite s = new Sprite(file.getName(), file.getAbsolutePath(), UI.loadTexture(file.getAbsolutePath()));
								Core.getSprites().add(s);
								Mesh mesh = new Mesh(s);
								Core.meshManager.addMesh(mesh);
								
								is.close();
							} else if(extension.equalsIgnoreCase("scene")) {
								String[] absolutePath = name.split("/");
								
								System.out.println("Adding a new scene with the name of " + absolutePath[absolutePath.length - 1].split("\\.")[0]);
								
								SceneManager.addScene(new Scene(absolutePath[absolutePath.length - 1].split("\\.")[0], new Folder(name, null)));
							} else if(extension.equalsIgnoreCase("glsl")) {
								String[] absolutePath = name.split("/");
								String sName = absolutePath[absolutePath.length - 1].split("\\.")[0];
								
								System.out.println("Loading a shader with the name of: " + sName);
								
								new Shader(sName, new BufferedReader(new InputStreamReader(Editor.class.getResourceAsStream("/" + name))));
								
								System.out.println("Successfully loaded a shader with the name of: " + sName);
							}
						} else if(name.endsWith("Data.bin")) {
							InputStream is = jar.getInputStream(je);
							DataInputStream dis = new DataInputStream(is);
							String data = dis.readUTF();
							
							String lastSceneName = data.split(":")[1];
							SceneManager.loadScene(lastSceneName);
							
							is.close();
							dis.close();
						}
					}
					
					load(SceneManager.getCurrentScene());
					
					jar.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private File[] getProjects() {
		String fullPath = System.getProperty("user.dir");
		
		Path path = Paths.get(fullPath + "\\Projects");
		File file = new File(fullPath + "\\Projects\\");
		
		if(!file.exists()) {
			try {
				Files.createDirectory(path);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		return file.listFiles(File::isDirectory);
	}
	
	//TODO: Future me, please make this better..
	//jeez it has such trashy code
	public Folder addFolder(String fullPath, Folder parent) {
		if(!ProjectSettings.isEditor) {
			return null;
		}
		
		Texture texture = null;
		
		if(!fullPath.contains("\\") && !fullPath.contains("/")) {//It's a normal folder.
			String folderName = fullPath;
			
			String full = System.getProperty("user.dir");
			String fileSeparator = System.getProperty("file.separator");
			
			fullPath = full + fileSeparator + core.getProjectName() + fileSeparator + "Assets" + fileSeparator;
			
			if(core.getGUI().getAssets().getInFolder() != null) {
				String path = core.getGUI().getAssets().getInFolder().getName();
				
				Folder curFolder = core.getGUI().getAssets().getInFolder();
				
				while(curFolder != null) {
					path += fileSeparator + curFolder.getName();
					
					curFolder = curFolder.getParentFolder();
				}
				
				String[] fullPath2 = path.split("\\\\");
				
				path = "";
				
				for(int i = fullPath2.length - 1; i > 0; i--) {
					path += fileSeparator + fullPath2[i];
				}
				
				fullPath += path;
			}
			
			String seperator = fullPath.charAt(fullPath.length() - 1) == '\\' ? "" : fileSeparator;
			
			fullPath += seperator + folderName;
		}
		
		if(!Assets.isValidPath(fullPath)) {
			Console.LogErr("[ERROR] Invaild folder path. " + fullPath);
			
			return null;
		}
	
		String[] absolutePath = fullPath.split("\\\\");
		
		String extension = "";
		
		if(fullPath.contains(".")) {
			extension = absolutePath[absolutePath.length - 1];
			extension = extension.contains(".") ? extension.split("\\.")[1] : extension;
		}
		
		if(extension.endsWith("png") || extension.endsWith("jpg")) {
			texture = UI.loadTexture(fullPath);
		} else if(extension.equalsIgnoreCase("java")) {
			texture = UI.loadTexture("resources/Icons/Script.png");
		} else {
			texture = UI.loadTexture("resources/Icons/Folder.png");
		}
		
		Folder folder = new Folder(fullPath, texture);
		folder.getParentFile().mkdirs();
		//System.out.println("Adding a folder with the path of: " + fullPath + " inside " + (parent != null ? parent.getAbsolutePath() : "nothing"));
		
		//Create the file if it doesn't exists
		if(!folder.exists()) {
			if(extension.length() <= 0) {
				Path path = Paths.get(fullPath);
				
				try {
					Files.createDirectories(path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					folder.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(folder.isDirectory()) {
			File[] files = folder.listFiles();
			
			if(files != null) {
				Folder parentFolder = null;
				
				for(int i = 0; i < files.length; i++) {
					if(i > 0) {
						File f = files[i];
						
						if(!parentFolder.getAbsolutePath().equalsIgnoreCase(f.getAbsolutePath().substring(0,
								f.getAbsolutePath().length() - f.getName().length() - 1))) {
							parentFolder = folder;
						}
						
						parentFolder = addFolder(files[i].getAbsolutePath(), parentFolder);
					} else {
						parentFolder = addFolder(files[i].getAbsolutePath(), folder);
					}
				}
			}
		} else {
			if(extension.equalsIgnoreCase("java")) {
				createBytes(fullPath);
			} else if(extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg")) {
				try {
					Files.copy(folder.toPath(), Paths.get(texturesPath + "\\" + folder.getName().split("[.]")[0] + "." + extension), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				File f = new File(texturesPath + "\\" + folder.getName().split("[.]")[0] + "." + extension);
				
				Sprite s = new Sprite(f.getName(), f.getAbsolutePath(), UI.loadTexture(f.getAbsolutePath()));
				Core.getSprites().add(s);
				Mesh mesh = new Mesh(s);
				
				Core.meshManager.addMesh(mesh);
			} else if(extension.equalsIgnoreCase("scene")) {
				System.out.println("Adding a new scene with the name of " + absolutePath[absolutePath.length - 1].split("\\.")[0]);
				
				SceneManager.addScene(new Scene(absolutePath[absolutePath.length - 1].split("\\.")[0], folder));
			} else if(extension.equalsIgnoreCase("glsl")) {
				String sName = absolutePath[absolutePath.length - 1].split("\\.")[0];
				
				System.err.println("[DEBUG] Adding a shader with the name of " + sName);
				
				new Shader(sName, folder.getAbsolutePath());
			}
		}
		
		if(parent == null) {
			core.getGUI().getAssets().getFolders().add(folder);
		} else {
			folder.setParentFolder(parent);
			parent.subFolders.add(folder);
		}
		
		//Update linked objects
		linkedObjectsFolders.add(folder);
		
		return folder;
	}
	
	public static final String getWorkSpaceDirectory() {
		return workSpaceDirectory;
	}
	
	public String getVersion() {
		return editorVersion;
	}
	
	public static boolean isPlaying() {
		return isPlaying;
	}
	
	public void setIsPlaying(boolean isPlaying) {
		if(Editor.isPlaying == isPlaying)
			return;
		
		core.getGUI().getConsole().clear();
		Application.setFOV(70);
		Core.meshManager.clear();
		
		Application.setMouseState(false);
		Application.setCursorIcon(null);
		
		//Started playing
		if(isPlaying) {
			startedScene = SceneManager.getCurrentScene();
			
			for(int i = 0; i < SceneManager.getScenes().size(); i++) {
				saveScene(SceneManager.getScenes().get(i));
			}
			
			System.out.println("Started playing!");
			
			UI.popups.clear();
			
			for(int i = 0; i < SceneManager.getCurrentScene().getGameObjects().size(); i++) {
				GameObject obj = SceneManager.getCurrentScene().getGameObjects().get(i);
				
				for(int j = 0; j < obj.getBehaviours().size(); j++) {
					if(!obj.getBehaviours().get(j).isEnabled)
						continue;
					
					try {
						obj.getBehaviours().get(j).start();
					} catch(Exception | Error e) {
						CharArrayWriter cw = new CharArrayWriter();
						PrintWriter w = new PrintWriter(cw);
						e.printStackTrace(w);
						w.close();
						String trace = cw.toString();
						
						Console.LogErr("[ERROR] " + obj.getBehaviours().get(j).getName() + " at " + trace);
					}
				}
			}
		}
		
		Editor.isPlaying = isPlaying;
		
		//Was playing
		if(!isPlaying) {
			canAddObjects = false;
			
			if(Core.getMasterRenderer() != null) {
				Core.getMasterRenderer().destroy();
				//Core.getMasterRenderer().getEntities().clear();
			}
			
			if(SceneManager.getCurrentScene() != null) {
				if(startedScene != null && !SceneManager.getCurrentScene().getName().equals(startedScene.getName()))
					SceneManager.loadScene(startedScene.getName());
				
				SceneManager.getCurrentScene().destroy();
			}
			
			reLoad();
			
			System.out.println("Stopped playing!");			
		}
		
		canAddObjects = true;
		
		System.gc();
	}
}