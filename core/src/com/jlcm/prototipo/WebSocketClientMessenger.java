package com.jlcm.prototipo;

import java.lang.reflect.InvocationTargetException;

import javax.swing.text.StyledEditorKit.ForegroundAction;

public class WebSocketClientMessenger
{
	ComClient cc;
	
	ClientMessageReceiver receiver;
	
	//For Bidirectional Communication mode
	public WebSocketClientMessenger(String ip, int port, int device, ClientMessageReceiver receiver)
	{
		if (device == 0)
		{
			//Only available on the HTML project - ClientMSG class
			cc = new GWTClient(ip, port, this);
		}
		else if (device == 2)
		{
			System.out.println("YOUR ON iOS");
			System.out.println("YOUR ON iOS");
			System.out.println("YOUR ON iOS");

		}
		else
		{
			//Only available on the JAVA-ANDROID project    maybe ios?
			try {
				cc = (ComClient) Class.forName("com.jlcm.prototipo.WSClient").getConstructor(String.class, Integer.TYPE, WebSocketClientMessenger.class, Integer.TYPE).newInstance(ip, port, this, device);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Java Websockets BEING CALLED FROM NON JAVA PLATFORM (or WSClient just missing)");
			}
		}
//		this.c = c; //To call the methods of the the upper level class	
		this.receiver = receiver;
	}


	public void onMessage(String message)
	{
		
		receiver.onMessageRecieved(message);
		
//		String [] values = message.split("\\s+"); //splitter with the " " separator
		
		//int ClientID = Integer.valueOf(values[0]); //Check of the ID (not required)	
				
		//Calls to the upper level class methods
//		if (values[0].equals("POSITION")) //POSITION player_id pos_x pos_y
//		{
//			c.changePosition(Integer.valueOf(values[1]), Integer.valueOf(values[2]), Integer.valueOf(values[3]));
//		}
	}
	
	public void onConnect(long time)
	{
		receiver.onConnect(time);
	}


	public boolean sendMessage(String message)
	{		
		if (cc != null && cc.isConnected())
		{
			//Only send message
			return (cc.sendMsg(message));
		}	
		else return false; 
	}
	
	public int getId()
	{
		return (cc.getId());
	}
	
	//get name from client class
	
	//one method for each messages / actions that the client can do
	
	public void close()
	{
		cc.close();
	}
}