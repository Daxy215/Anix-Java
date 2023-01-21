package com.Anix.IO;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.BufferUtils;

import com.Anix.Engine.Editor;
import com.Anix.Main.Core;

public class ResourceLoader {
	//TODO: In this UI, use this instead.
	public static ByteBuffer loadImageToByteBuffer(String file) {
		ByteBuffer imageBuffer;
		
		try {
			imageBuffer = ioResourceToByteBuffer(file, 128 * 128);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		IntBuffer w = BufferUtils.createIntBuffer(1);
		IntBuffer h = BufferUtils.createIntBuffer(1);
		IntBuffer c = BufferUtils.createIntBuffer(1);
		
		// Use info to read image metadata without decoding the entire image.
		if (!stbi_info_from_memory(imageBuffer, w, h, c)) {
			throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
		}
		
		// Decode the image
		ByteBuffer image = stbi_load_from_memory(imageBuffer, w, h, c, 0);
		
		if (image == null) {
			throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
		}
		
		return image;
	}
	
	public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
		ByteBuffer buffer;
		
		Path path = Paths.get(resource);
		
		//if(Editor.isRunningViaJar && !Files.exists(path)) {
			//String[] absolutePath = resource.replace("/", "\\").split("\\\\");
			
			//InputStream is = Editor.getInputStream(resource);
			
			//final String tempLocation = "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Local\\Temp\\" + absolutePath[absolutePath.length - 1];
	        
	        //Files.copy(is, Paths.get(tempLocation), StandardCopyOption.REPLACE_EXISTING);
			
			//resource = tempLocation;
		//}
		
		if(resource.endsWith(".png") || resource.endsWith(".jpg")) {
			String[] name = resource.replace("/", "\\").split("\\\\");
			String actualName = name[name.length - 1];
			
			//TODO: Use getName instead.
			for(int i = 0; i < Core.getSprites().size(); i++) {
				if(Core.getSprites().get(i).getName().equalsIgnoreCase(actualName.toLowerCase())) {
					resource = Core.getSprites().get(i).getPath();
				}
			}
		}
		
		path = Paths.get(resource);
		
		if (Files.isReadable(path)) {
			try(SeekableByteChannel fc = Files.newByteChannel(path)) {
				buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
				while (fc.read(buffer) != -1) {
					;
				}
			}
		} else {
			try (InputStream source = Editor.getInputStream(resource);
					ReadableByteChannel rbc = Channels.newChannel(source)) {
				buffer = BufferUtils.createByteBuffer(bufferSize);

				while (true) {
					int bytes = rbc.read(buffer);
					if(bytes == -1) {
						break;
					}

					if(buffer.remaining() == 0) {
						buffer = resizeBuffer(buffer, buffer.capacity() * 2);
					}
				}
			}
		}

		((Buffer)buffer).flip();
		return buffer;
	}

	public static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		((Buffer)buffer).flip();
		newBuffer.put(buffer);
		return newBuffer;
	}
}