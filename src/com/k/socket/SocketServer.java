package com.k.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * C/S架构的服务端对象。
 */
public class SocketServer
{

	/**
	 * 要处理客户端发来的对象，并返回一个对象，可实现该接口。
	 */
	public interface ObjectAction
	{
		Object doAction(Object rev);
	}

	public static final class DefaultObjectAction implements ObjectAction
	{
		public Object doAction(Object rev)
		{
			System.out.println("处理并返回：" + rev);
			return rev;
		}
	}

	public static void main(String[] args)
	{
		int port = 7676;
		SocketServer server = new SocketServer(port);
		server.start();
	}

	private int port;
	private volatile boolean running = false;
	private long receiveTimeDelay = 3000;
	private ConcurrentHashMap<Class<Object>, ObjectAction> actionMapping = new ConcurrentHashMap<Class<Object>, ObjectAction>();
	private Thread connWatchDog;

	public SocketServer(int port)
	{
		this.port = port;
	}

	public void start()
	{
		if (running)
			return;
		running = true;
		connWatchDog = new Thread(new ConnWatchDog());
		connWatchDog.start();
	}

	@SuppressWarnings("deprecation")
	public void stop()
	{
		if (running)
			running = false;
		if (connWatchDog != null)
			connWatchDog.stop();
	}

	public void addActionMap(Class<Object> cls, ObjectAction action)
	{
		actionMapping.put(cls, action);
	}

	class ConnWatchDog implements Runnable
	{
		public void run()
		{
			try
			{
				ServerSocket ss = new ServerSocket(port, 5);
				while (running)
				{
					Socket s = ss.accept();
					// 新起一个线程去处理收到的消息
					new Thread(new SocketAction(s)).start();
				}
				ss.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				SocketServer.this.stop();
			}

		}
	}

	class SocketAction implements Runnable
	{
		Socket s;
		boolean run = true;
		long lastReceiveTime = System.currentTimeMillis();

		public SocketAction(Socket s)
		{
			this.s = s;
		}

		public void run()
		{
			while (running && run)
			{
				try
				{
					InputStream in = s.getInputStream();
					if (in.available() > 0)
					{
						BufferedReader bf = new BufferedReader(new InputStreamReader(in, "UTF-8"));
						// 最好在将字节流转换为字符流的时候 进行转码
						StringBuffer buffer = new StringBuffer();
						String line = "";
						while ((line = bf.readLine()) != null)
						{
							buffer.append(line);
						}
						System.out.println("接收：\t" + buffer.toString());

						// TODO:暂时不回复
						// ObjectInputStream ois = new ObjectInputStream(in);
						// Object obj = ois.readObject();
						// lastReceiveTime = System.currentTimeMillis();
						// System.out.println("接收：\t" + obj);
						// ObjectAction oa = actionMapping.get(obj.getClass());
						// oa = oa == null ? new DefaultObjectAction() : oa;
						// Object out = oa.doAction(obj);
						// 返回信息
						// if (out != null)
						// {
						// ObjectOutputStream oos = new
						// ObjectOutputStream(s.getOutputStream());
						// oos.writeObject(out);
						// oos.flush();
						// }
					}
					else
					{
						Thread.sleep(10);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					overThis();
				}
			}
		}

		/**
		 * 关闭连接
		 * 
		 */
		private void overThis()
		{
			if (run)
				run = false;
			if (s != null)
			{
				try
				{
					s.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			System.out.println("关闭：" + s.getRemoteSocketAddress());
		}

	}

}
