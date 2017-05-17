package com.jlcm.prototipo;


public class WSClient implements ComClient 
{	
	public WSClient (String ip, int port, WebSocketClientMessenger c, int pC){
		
	}

	@Override
	public void connectClient(String ip) {
		
	}

	@Override
	public boolean sendMsg(String msg) {
		return false;
	}

	@Override
	public boolean isConnected() {
		return false;
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public void close() {
		
	}
	

}
