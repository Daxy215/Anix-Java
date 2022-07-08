package com.Anix.Engine;

import java.io.File;
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
import javax.tools.ToolProvider;

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

final class Complier {
	private File classesDir;
	private File sourceDir;
	private URLClassLoader classLoader;
	
	private List<File> files = null;
	//private List<String> failers = new ArrayList<String>();
	
	public Complier() {
		files = new ArrayList<File>();
	}
	
	public void compile() throws Exception {
		files.clear();
		//failers.clear();
		
		if(ProjectSettings.isEditor) {
			if(classesDir.listFiles() != null) {
				for(int i = 0; i < classesDir.listFiles().length; i++) {
					if(!classesDir.listFiles()[i].isDirectory() && classesDir.listFiles()[i].getAbsolutePath().endsWith(".java"))
						classesDir.listFiles()[i].delete();
				}
				
				if(ProjectSettings.isEditor)
					classesDir.delete();
			}
		}
		
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
			return;
		}
		
		String[] compileOptions = new String[]{"-d", classesDir.getAbsolutePath()};
		Iterable<String> compilationOptions = Arrays.asList(compileOptions);
		
		CompilationTask compilerTask = compiler.getTask(null, fileManager, diagnostics, compilationOptions, null, javaObjects);
		
		if (!compilerTask.call()) {
			for(Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
				System.err.format("[Error] on line %d in %s", diagnostic.getLineNumber(), diagnostic);
				Console.LogErr("[Error] on line " + diagnostic.getLineNumber() + " in " + diagnostic + "\n");
				
				/*String path = diagnostic.getSource().toString().split("\\[")[1].split("\\]")[0];
				String[] splitPath = path.split("\\\\");
				String name = splitPath[splitPath.length - 1];
				
				failers.add(name);*/
			}
			
			System.out.println();
			
			return;
		}
		
		Editor.importedClasses.clear();
		
		Editor.importedClasses.add(new AudioPlayer());
		Editor.importedClasses.add(new BoxCollider2D());
		Editor.importedClasses.add(new Button());
		Editor.importedClasses.add(new Camera());
		Editor.importedClasses.add(new LightSource());
		Editor.importedClasses.add(new MeshRenderer());
		Editor.importedClasses.add(new Physics2D());
		Editor.importedClasses.add(new SpriteRenderer());
		
		classLoader = new URLClassLoader(new URL[]{classesDir.toURI().toURL()});
		Thread.currentThread().setContextClassLoader(classLoader);
		/*Field scl = ClassLoader.class.getDeclaredField("scl"); // Get system class loader
        scl.setAccessible(true); // Set accessible
        scl.set(null, classLoader); // Update it to your class loader*/
		
		for(int i = 0; i < files.size(); i++) {
			try {
				//TODO: java.lang.ClassFormatError: Truncated class file?????
				Class<?> clazz = Class.forName(files.get(i).getName().split(".java")[0], true, classLoader);
				//addSoftwareLibrary(files.get(i));
				
				if(clazz != null) {
					Object obj = clazz.getConstructor().newInstance();
					
					try {
						if(obj instanceof Behaviour) {
							Editor.importedClasses.add((Behaviour)clazz.getConstructor().newInstance());
						}
						
						System.out.println("Successfully loaded class with the name of: " + files.get(i).getName().split(".java")[0]);
					} catch(Exception e) {
						System.err.println("[ERROR] " + clazz.getSimpleName() + " : " + e.getMessage());
					}
				}
			} catch(Exception | NoClassDefFoundError e) {
				System.err.println("[ERORR] [TSH] Couldn't load a class with the name of: " + files.get(i).getName().split(".java")[0] + " because of " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/*private void addSoftwareLibrary(File file) throws Exception {
		//ClassLoader urlClassLoader = ClassLoader.getSystemClassLoader();
		//DynamicURLClassLoader dynalLoader = new DynamicURLClassLoader(urlClassLoader);
		Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
	    method.setAccessible(true);
	    method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
	}*/
	
	private List<JavaFileObject> scanRecursivelyForJavaObjects(File dir, StandardJavaFileManager fileManager) {
		List<JavaFileObject> javaObjects = new LinkedList<JavaFileObject>();
		File[] files = dir.listFiles();
		
		if(files == null)
			return null;
		
		for (File file : files) {
			if (file.isDirectory()) {
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
