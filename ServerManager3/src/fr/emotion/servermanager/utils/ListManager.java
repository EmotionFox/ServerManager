package fr.emotion.servermanager.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import fr.emotion.servermanager.MainServerManager;
import fr.emotion.servermanager.components.ServerMenuItem;

public abstract class ListManager
{
	private static final Path source = Paths.get("./server-list.properties");
	private static final List<ServerMenuItem> serverList = new ArrayList<ServerMenuItem>();

	public static void init()
	{
		if (source.toFile().exists())
			reload();
	}

	public static void addServer(String name, String path)
	{
		boolean isNew = true;

		for (int i = 0; i < serverList.size(); i++)
		{
			if (serverList.get(i).getName().equals(name))
			{
				serverList.get(i).setPath(path);
				isNew = false;
			}
		}

		if (isNew)
		{
			serverList.add(new ServerMenuItem(name, path));
			JOptionPane.showMessageDialog(MainServerManager.panel, "The server has been added.");
		}
		else
		{
			JOptionPane.showMessageDialog(MainServerManager.panel, "The server has been override.");
		}

		save();
	}

	public static boolean removeServer(String name)
	{
		int index = -1;

		for (int i = 0; i < serverList.size(); i++)
		{
			if (serverList.get(i).getName().equals(name))
				index = i;
		}

		if (index != -1)
		{
			serverList.remove(index);
			MainServerManager.serverPanel.resetServer(true);
			JOptionPane.showMessageDialog(MainServerManager.panel, "The server: " + name + ", has been removed.", "Server Manager", JOptionPane.INFORMATION_MESSAGE);
			save();
			return true;
		}
		else
		{
			JOptionPane.showMessageDialog(MainServerManager.panel, "Unable to delete: " + (name.isEmpty() ? "blank" : name) + ".", "Server Manager", JOptionPane.WARNING_MESSAGE);
			return false;
		}
	}

	private static void reload()
	{
		try (Reader reader = new FileReader(source.toFile()); BufferedReader br = new BufferedReader(reader))
		{
			String line = "";
			String name = "";

			while ((line = br.readLine()) != null)
			{
				if (line.matches("^[^#].+=.*$"))
				{
					if (line.contains("profile-name="))
						name = line.split("=")[1];
					else if (line.contains("jar-path="))
						serverList.add(new ServerMenuItem(name.isEmpty() ? "The... UNKNOW server" : name, line.split("=")[1]));
				}
			}

			MainServerManager.panel.setServer(serverList);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static void save()
	{
		try (Writer writer = new FileWriter(source.toFile()); BufferedWriter bw = new BufferedWriter(writer))
		{
			String line = "";

			line += "#Emotion servers list\n";

			for (ServerMenuItem server : serverList)
			{
				line += "profile-name=" + server.getName() + "\n";
				line += "jar-path=" + server.getPath() + "\n";
			}

			bw.write(line);
			MainServerManager.panel.setServer(serverList);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
