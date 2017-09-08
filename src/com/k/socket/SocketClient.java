package com.k.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * C/S架构的客户端对象，持有该对象，可以随时向服务端发送消息。
 */
public class SocketClient
{
	private String serverIp;
	private int port;
	private boolean keepAlive;
	private Socket socket;
	private boolean running = false;
	
	public static void main(String[] args)
	{
		SocketClient sc = new SocketClient("192.168.13.78", 7676, false);
		try
		{
			sc.start();
		}
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public SocketClient(String serverIp, int port, boolean keepAlive)
	{
		this.serverIp = serverIp;
		this.port = port;
		this.keepAlive = keepAlive;
	}

	public void start() throws UnknownHostException, IOException
	{
		if (running)
			return;
		socket = new Socket(serverIp, port);
		System.out.println("本地端口：" + socket.getLocalPort());
		running = true;
		new Thread(new ReceiveWatchDog()).start(); //监听返回信息
	}

	public void stop()
	{
		if (running)
			running = false;
		try
		{
			this.socket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void sendObject(Object obj) throws IOException
	{
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(obj);
		System.out.println("发送：\t" + obj);
		oos.flush();
	}

	/**
	 * 监听返回信息
	 * 
	 * @author ken_8
	 * 2017年9月8日 上午12:04:50
	 */
	class ReceiveWatchDog implements Runnable
	{
		public void run()
		{
			while (running)
			{
				try
				{
					InputStream in = socket.getInputStream();
					if (in.available() > 0)
					{
						BufferedReader bf = new BufferedReader(new InputStreamReader(in, "UTF-8"));
						// 最好在将字节流转换为字符流的时候 进行转码
						StringBuffer buffer = new StringBuffer();
						String line = "";
						while ((line = bf.readLine()) != null)
						{
							buffer.append(line);
							System.out.println("recv:" + line);
						}
						System.out.println("接收：\t" + buffer.toString());
					}
					else
					{
						Thread.sleep(10);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					SocketClient.this.stop();
				}
			}
		}
	}

}
