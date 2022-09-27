package fr.emotion.servermanager.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import fr.emotion.servermanager.MainServerManager;
import fr.emotion.servermanager.References;
import fr.emotion.servermanager.components.Player;
import fr.emotion.servermanager.enums.RequestType;

public abstract class ServerManager
{
	private static Process serverProcess;
	private static StreamWriter outputStream;
	private static StreamReader inputStream;
	private static boolean serverReady;
	private static String jarPath;
	private static File jarParent;
	private static ArrayList<Player> playerList = new ArrayList<Player>();
	private static Thread inThread;
	private static Thread outThread;
	public static boolean saved = false;

	public static void sendLog(String log)
	{
		if (log.contains("done"))
		{
			MainServerManager.serverPanel.setStatus(2);
			serverReady = true;
		}
		else if (log.contains("stopping the server"))
			serverReady = false;
		else if (log.contains("joined the game"))
		{
			String playerName = log.split(" ")[0];

			for (Player player : playerList)
			{
				if (player.getName().equals(playerName))
				{
					String text1 = "";
					String text2 = "";
					String color = "";

					if (player.isReconnecting() && player.isAdmin())
					{
						text1 = " Welcome back on your server ";
						text2 = " !";
						color = "yellow";
					}
					else if (player.isReconnecting())
					{
						text1 = " Welcome back ";
						text2 = " !";
						color = "green";
					}
					else if (player.isAdmin())
					{
						text1 = " Welcome on your server ";
						text2 = ", have fun!";
						color = "yellow";
						player.setReco(true);
					}
					else
					{
						text1 = " Welcome on the server ";
						text2 = ", enjoy!";
						color = "green";
						player.setReco(true);
					}

					outputStream.sendCommand(References.msgCommand + playerName + " [{\"text\":\"<ESM>\",\"color\":\"" + color + "\"}, {\"text\":\"" + text1 + "\",\"color\":\"red\"}, {\"text\":\""
							+ playerName + "\",\"color\":\"" + color + "\"}, {\"text\":\"" + text2 + "\",\"color\":\"red\"}]");

					player.setTime(System.currentTimeMillis());
					return;
				}
			}

			outputStream.sendCommand(References.msgCommand + playerName + " [{\"text\":\"<ESM>\",\"color\":\"green\"}, {\"text\":\" Welcome on the server \",\"color\":\"red\"}, {\"text\":\""
					+ playerName + "\",\"color\":\"green\"}, {\"text\":\", enjoy !\",\"color\":\"red\"}]");

			playerList.add(new Player(playerName, System.currentTimeMillis(), true));
		}
		else if (log.equals("saving the game (this may take a moment!)"))
			saved = false;
		else if (log.equals("saved the game"))
			saved = true;
	}

	public static void clearPlayers()
	{
		playerList.clear();
	}

	public static void addPlayer(Player player)
	{
		playerList.add(player);
	}

	public static void setProcess(String path, File parent)
	{
		jarPath = path;
		jarParent = parent;

		PropertieManager.saveProperties(jarParent + "\\server.properties");

		try
		{
			String[] commands = { "java", "-Xmx" + References.allocatedRam + "G", "-jar", path };
			serverProcess = Runtime.getRuntime().exec(commands, null, parent);
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		inputStream = new StreamReader(serverProcess.getInputStream());
		outputStream = new StreamWriter(serverProcess.getOutputStream());

		inThread = new Thread(inputStream);
		outThread = new Thread(outputStream);

		inThread.start();
		outThread.start();
	}

	public static void resetServer(int second, RequestType type)
	{
		if (second > 0)
		{
			while (second >= 0)
			{
				try
				{
					Thread.sleep(1000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}

				if (second <= 5 || (second <= 20 && second % 5 == 0) || (second > 20 && second <= 50 && second % 10 == 0) || (second > 50 && second % 50 == 0))
				{
					outputStream.sendCommand(References.msgCommand + "@a [{\"text\":\"<ESM>\",\"color\":\"" + (type == RequestType.STOP ? "green" : "gold") + "\"}, {\"text\":\" Server will "
							+ type.toString() + " in: \",\"color\":\"red\"}, {\"text\":\"" + second + "\",\"color\":\"" + (type == RequestType.STOP ? "green" : "gold")
							+ "\"}, {\"text\":\" sc.\",\"color\":\"red\"}]");
				}

				second--;
			}
		}

		stopServer(type);
	}

	public static void stopServer(RequestType type)
	{
		outputStream.sendCommand("/stop");

		while (inThread.isAlive() || outThread.isAlive() || serverProcess.isAlive())
		{
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		serverProcess.destroy();
		serverReady = false;

		PropertieManager.saveProperties(jarParent + "\\server.properties");

		switch (type)
		{
		case STOP:
			break;
		case REBOOT:
			setProcess(jarPath, jarParent);
			break;
		case RELOAD:
			boolean unZip = BackupManager.unZip(MainServerManager.serverPanel.getSave());

			if (unZip)
			{
				setProcess(jarPath, jarParent);
			}
			break;
		}
	}

	public static ArrayList<Player> getPlayers()
	{
		return playerList;
	}

	public static Process getProcess()
	{
		return serverProcess;
	}

	public static StreamWriter getOutput()
	{
		return outputStream;
	}

	public static boolean serverReady()
	{
		return serverReady;
	}

	public static boolean processReady()
	{
		if (serverProcess != null && serverProcess.isAlive())
			return true;
		else
			return false;
	}
}
