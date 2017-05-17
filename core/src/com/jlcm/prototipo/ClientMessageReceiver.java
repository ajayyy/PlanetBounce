package com.jlcm.prototipo;

public interface ClientMessageReceiver {
	public void onMessageRecieved(String message);
	
	public void onConnect(long difference);
}
