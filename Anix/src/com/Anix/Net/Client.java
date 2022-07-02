/*package com.Anix.Net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import com.Anix.Behaviours.Behaviour;
import com.Anix.IO.Application;
import com.Anix.Main.Core;
import com.Anix.Math.Vector3f;
import com.Anix.Net.Packets.Packet00Login;

//TODO: Add unAddable(to a gameobject) attontation thingy.??
//Completely forgot what that is lol
public final class Client extends Behaviour implements Runnable {
	private static final long serialVersionUID = 0L;
	
	private int port;
	
	private InetAddress iNetAddress;
	private DatagramSocket socket;
	
	private Consumer<ServerPlayer> onJoin, onLeave;
	
	public static Client instance = null;
	
	public Client(int port, InetAddress iNetAddress, Consumer<ServerPlayer> onJoin, Consumer<ServerPlayer> onLeave) {
		this.port = port;
		this.iNetAddress = iNetAddress;
		this.onJoin = onJoin;
		this.onLeave = onLeave;
		
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		if(instance != null && instance != this) {
			System.err.println("[ERROR] A client instance already exists! Destroying..");
			
			socket.close();
			
			return;
		}
		
		instance = this;
		Server.instance = new Server();
		
		Thread thread = new Thread(this);
		thread.setName("Client Thread");
		thread.start();
	}
	
	@Override
	public void run() {
		System.out.println("Client Side started on port " + port);
		
		while(!GLFW.glfwWindowShouldClose(Application.getWindow())) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			
			try {
				socket.receive(packet);
			} catch(IOException e) {
				e.printStackTrace();
			}
			
			String packetName = new String(data).trim();
			
			parsePacket(data, packetName.substring(0, 2), packetName, packet.getAddress(), packet.getPort());
		}
	}
	
	 //*
	 //* This will be removed into a better method :D
	 //*
	public void render() {
		if(Core.getMasterRenderer() != null) {
			for(int i = 0; i < Server.instance.connectedClients.size(); i++) {
				Core.getMasterRenderer().render(Server.instance.connectedClients.get(i));
			}
		}
	}
	
	private void parsePacket(byte[] data, String packetId, String packetName, InetAddress address, int port) {
		Packet packet = Packet.lookupPacket(packetId);
		
		if(packet == null) {
			System.err.println("[ERROR] #61 Couldn't find a packet with an ID of: "
								+ (packetId) + " - " + packetName);
			
			return;
		}
		
		packet.handleClient(data, port, address);
	}
	
	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, iNetAddress, port);
		
		if(socket.isClosed())
			return;
		
		try {
			socket.send(packet);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void handleLogin(Packet00Login packet, InetAddress address, int port) {
		Server.instance.addConnection(new ServerPlayer(packet.getName(), port,
				new Vector3f(packet.getX(), packet.getY(), packet.getZ()),
				new Vector3f(packet.getRx(), packet.getRy(), packet.getRz()),
				new Vector3f(packet.getSx(), packet.getSy(), packet.getSz()), address), packet);
		
	}
	
	public int getPort() {
		return port;
	}

	public InetAddress getiNetAddress() {
		return iNetAddress;
	}

	public Consumer<ServerPlayer> getOnJoin() {
		return onJoin;
	}

	public Consumer<ServerPlayer> getOnLeave() {
		return onLeave;
	}
}*/
