package com.Anix.Engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import com.Anix.Behaviours.Animator2D;
import com.Anix.Behaviours.AudioPlayer;
import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.BoxCollider2D;
import com.Anix.Behaviours.Button;
import com.Anix.Behaviours.Camera;
import com.Anix.Behaviours.LightSource;
import com.Anix.Behaviours.MeshRenderer;
import com.Anix.Behaviours.Physics2D;
import com.Anix.Behaviours.SpriteRenderer;
import com.Anix.GUI.Windows.Console;
import com.Anix.IO.ProjectSettings;
import com.Anix.Main.Core;

//https://stackoverflow.com/questions/5830581/getting-a-directory-inside-a-jar
public final class Complier {
	private File classesDir;
	private File sourceDir;
	private URLClassLoader classLoader;
	
	private List<File> files = null;
	private List<File> directories = new ArrayList<>();
	
	public Complier() {
		files = new ArrayList<File>();
	}
	
	public void compile() throws Exception {
		files.clear();
		
		if (ProjectSettings.isEditor) {
			if (classesDir.listFiles() != null) {
				for (int i = 0; i < classesDir.listFiles().length; i++) {
					if (!classesDir.listFiles()[i].isDirectory()
							&& classesDir.listFiles()[i].getAbsolutePath().endsWith(".java")) {
						classesDir.listFiles()[i].delete();
					}
				}
				
				classesDir.delete();
			}
		}
		
		Editor.importedClasses.clear();
		
		Editor.importedClasses.add(new AudioPlayer());
		Editor.importedClasses.add(new BoxCollider2D());
		Editor.importedClasses.add(new Animator2D());
		Editor.importedClasses.add(new Button());
		Editor.importedClasses.add(new Camera());
		Editor.importedClasses.add(new LightSource());
		Editor.importedClasses.add(new MeshRenderer());
		Editor.importedClasses.add(new Physics2D());
		Editor.importedClasses.add(new SpriteRenderer());
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.getDefault(), null);
		List<JavaFileObject> javaObjects = null;
		
		if(ProjectSettings.isEditor) {
			javaObjects = scanRecursivelyForJavaObjects(sourceDir, fileManager);
		} else {
			javaObjects = scanRecursivelyForJavaObjects(classesDir, fileManager);
		}
		
		if (javaObjects == null || javaObjects.size() == 0) {
			System.err.println("none :(");
			
			return;
		}
		
		String fileSeparator = System.getProperty("file.separator");
		
		directories.add(new File("C:/Users/smsmk/Desktop/Game Engine/Anix.jar"));
		directories.add(new File(System.getProperty("user.dir") + fileSeparator + Core.getProjectName() + fileSeparator + "Data" + fileSeparator + "bin"));
		
		File dir = new File("D:/GitHub/Anix-Java/Anix/includes");
		
		getExternalLibraries(dir);
		
		fileManager.setLocation(StandardLocation.CLASS_PATH, directories);
		
		String[] compileOptions = new String[]{"-d", classesDir.getAbsolutePath()};
		Iterable<String> compilationOptions = Arrays.asList(compileOptions);
		
		CompilationTask compilerTask = compiler.getTask(null, fileManager, diagnostics, compilationOptions, null, javaObjects);
		
		if (!compilerTask.call()) {
			for(Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
				System.err.format("[Error] on line %d in %s", diagnostic.getLineNumber(), diagnostic);
				Console.LogErr("[Error] on line " + diagnostic.getLineNumber() + " in " + diagnostic + "\n");
			}
			
			System.out.println();
		}
		
		URL[] directoryURLs = directories.stream().map(d -> {
			try {
				return d.toURI().toURL();
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
 			}
			
			return null;
		}).toArray(URL[]::new);
		
		classLoader = URLClassLoader.newInstance(directoryURLs);
		Thread.currentThread().setContextClassLoader(classLoader);
		
		for(File f : files) {
			try {
				String packageName = extractPackageNameFromSourceCode(f);
				String className = f.getName().split(".java")[0].trim();
				String fullyQualifiedName = packageName + "." + className;
				
				//TODO:
				//if(FileUtils.isJavaClassFile(Paths.get(f.getAbsolutePath()))) {
					//System.err.println("[Complier] Skipping over " + f.getName() + " as it isn't a type of class.");
					
					//continue;
				//}
				
				Class<?> clazz = null;
				
				try {
					clazz = Class.forName(fullyQualifiedName, true, classLoader);
				} catch(ClassNotFoundException e) {
					clazz = Class.forName(className, true, classLoader);
				}
				
				if(clazz != null && !Modifier.isAbstract(clazz.getModifiers())) {
					try {
						Object obj = clazz.getConstructor().newInstance();
						
						try {
							if(obj instanceof Behaviour) {
								Editor.importedClasses.add((Behaviour)obj);
							}
							
							System.out.println("Successfully loaded class with the name of: " +  f.getName().split(".java")[0]);
						} catch(Exception e) {
							System.err.println("[ERROR] " + clazz.getSimpleName() + " : " + e.getMessage());
						}
					} catch(NoSuchMethodException e) {
						System.err.println("[ERROR] Please include a default constructor for the class with the name of: " + f.getName().split(".java")[0]);
					} catch(Exception e) {
						System.err.println("Class name; " + f.getName());
						e.printStackTrace();
					}
				}
			} catch(ClassNotFoundException e) {
				//System.err.println("[ERROR] " + f.getName().split(".java")[0] + " isn't of type 'CLASS'. ");
				//System.err.println("[ERROR] " + file.getKey().getName().split(".java")[0] + " isn't of type 'CLASS'. ");
				e.printStackTrace();
			} catch(Exception | NoClassDefFoundError e) {
				System.err.println("[ERORR] [TSH] Couldn't load a class with the name of: " +
						f.getName().split(".java")[0] + " because of " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	private static String extractPackageNameFromSourceCode(File javaFile) throws IOException {
		StringBuilder source = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(javaFile));
		String line = reader.readLine();
		
		while (line != null) {
			source.append(line).append("\n");
			line = reader.readLine();
		}
		
		reader.close();
		
		int packageIndex = source.indexOf("package");
		
		if (packageIndex == -1) {
			return "";
		}
		
		int semicolonIndex = source.indexOf(";", packageIndex);
		
		if (semicolonIndex == -1) {
			return "";
		}
		
		return source.substring(packageIndex + 7, semicolonIndex).trim();
	}
	
	private List<File> getExternalLibraries(File dir) {
		List<File> filesList = new ArrayList<>();
		File[] files = dir.listFiles();
		
		if (files == null)
			return null;
		
		for (File file : files) {
		    if (file.isDirectory()) {
		        directories.addAll(getExternalLibraries(file));
		    } else if (file.isFile() && file.getName().toLowerCase().endsWith(".jar")) {
		        directories.add(file);
		    }
		}
		
		return filesList;
	}
	
	private List<JavaFileObject> scanRecursivelyForJavaObjects(File dir, StandardJavaFileManager fileManager) {
		List<JavaFileObject> javaObjects = new LinkedList<JavaFileObject>();
		File[] files = dir.listFiles();
		
		if (files == null)
			return null;
		
		for (File file : files) {
			if (file.isDirectory()) {
				directories.add(file);
				javaObjects.addAll(scanRecursivelyForJavaObjects(file, fileManager));
			} else if (file.isFile() && file.getName().toLowerCase().endsWith(".java")) {
				javaObjects.add(readJavaObject(file, fileManager));
				this.files.add(file);
			}
		}
		
		return javaObjects;
	}
	
	private JavaFileObject readJavaObject(File file, StandardJavaFileManager fileManager) {
		Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjects(file);
		Iterator<? extends JavaFileObject> it = javaFileObjects.iterator();
		
		if (it.hasNext()) {
			return it.next();
		}
		
		throw new RuntimeException("[ERROR] [TSH] #110 Could not load " + file.getAbsolutePath() + " java file object!");
	}
	
	public File getClassesDir() {
		return classesDir;
	}
	
	public void setClassesDir(File classesDir) {
		this.classesDir = classesDir;
	}
	
	public File getSourceDir() {
		return sourceDir;
	}
	
	public void setSourceDir(File sourceDir) {
		this.sourceDir = sourceDir;
	}
}
