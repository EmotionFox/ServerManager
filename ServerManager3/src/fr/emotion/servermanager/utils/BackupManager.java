package fr.emotion.servermanager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import fr.emotion.servermanager.MainServerManager;
import fr.emotion.servermanager.References;

public abstract class BackupManager
{
	public static ArrayList<String> getList()
	{
		String parent = MainServerManager.serverPanel.getJarFolder();
		String world = PropertieManager.getCurrentWorld();

		File dirFile = Paths.get(parent, "backup", world).toFile();

		ArrayList<String> list = new ArrayList<String>();

		if (dirFile == null || !dirFile.exists())
			return list;

		Matcher saveMatcher;

		for (File file : dirFile.listFiles())
		{
			if (file.isFile() && file.getName().contains(".zip"))
			{
				String name = file.getName();

				saveMatcher = References.savePattern.matcher(name);

				if (saveMatcher.find())
					name = saveMatcher.group(1);

				list.add(name);
			}
		}

		return list;
	}

	public static void saveZip(String name)
	{
		String parent = MainServerManager.serverPanel.getJarFolder();
		String world = PropertieManager.getCurrentWorld();

		Path sourcePath = Paths.get(parent, world);
		Path dirsPath;
		Path zipPath;

		if (name.isEmpty())
		{
			dirsPath = Paths.get(parent, "backup", world);
			zipPath = Paths.get(parent, "backup", world, "auto_automatic.zip");
		}
		else
		{
			String newName = name.replaceAll("\\s", "_");

			if (newName.isEmpty())
				newName = "0";

			System.out.println("Renaming zip to: " + newName);

			dirsPath = Paths.get(parent, "backup", world);
			zipPath = Paths.get(parent, "backup", world, "manual_" + newName + ".zip");
		}

		if (!dirsPath.toFile().exists())
		{
			dirsPath.toFile().mkdirs();
		}

		Map<String, String> env = new HashMap<>();
		env.put("create", "true");

		URI.create("jar:file:/" + zipPath.toUri());

		zip(sourcePath.toFile(), zipPath.toFile());
	}

	public static void zip(File dir, File zip)
	{
		URI base = dir.toURI();
		Deque<File> queue = new LinkedList<File>();
		queue.push(dir);

		System.out.println(dir.getAbsolutePath());

		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip)))
		{
			int size = 0;

			while (!queue.isEmpty())
			{
				dir = queue.pop();

				for (File file : dir.listFiles())
				{
					String name = base.relativize(file.toURI()).getPath();

					if (file.isDirectory())
					{
						queue.push(file);
						name = name.endsWith("/") ? name : name + "/";
						zos.putNextEntry(new ZipEntry(name));
					}
					else if (!name.contains(".lock"))
					{
						zos.putNextEntry(new ZipEntry(name));
						Files.copy(file.toPath(), zos);
						zos.closeEntry();
					}

					if (queue.size() > size)
						size = queue.size();
				}

				float pourcent = 100 - ((queue.size() * 100) / size);

				System.out.println("Saving... " + Math.round(pourcent) + "%");
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return;
		} catch (IOException e)
		{
			e.printStackTrace();
			return;
		}

		ServerManager.getOutput().sendCommand(References.msgCommand + "@a [{\"text\":\"<ESM>\",\"color\":\"aqua\"}, {\"text\":\" Save of \",\"color\":\"red\"}, {\"text\":\""
				+ PropertieManager.getCurrentWorld() + "\",\"color\":\"aqua\"}, {\"text\":\" done.\",\"color\":\"red\"}]");

		MainServerManager.serverPanel.setTemplate(2);
	}

	public static boolean unZip(String name)
	{
		String parent = MainServerManager.serverPanel.getJarFolder();
		String world = PropertieManager.getCurrentWorld();

		String dirPath = parent + "/" + world;
		File zipFile;

		if (name.contains("auto"))
			zipFile = Paths.get(parent, "backup", world, "auto_automatic.zip").toFile();
		else
			zipFile = Paths.get(parent, "backup", world, "manual_" + name + ".zip").toFile();

		if (zipFile != null && !zipFile.exists())
		{
			ServerManager.getOutput().sendCommand(References.msgCommand
					+ "@a [{\"text\":\"<ESM>\",\"color\":\"aqua\"}, {\"text\":\" Save does \",\"color\":\"red\"}, {\"text\":\"not exist\",\"color\":\"dark_purple\"}, {\"text\":\" anymore.\",\"color\":\"red\"}]");
			return false;
		}

		File dirFile = Paths.get(dirPath).toFile();

		if (dirFile != null && dirFile.exists())
		{
			File oldBack = new File(dirPath + ".old");

			if (oldBack != null && oldBack.exists())
				oldBack.delete();

			dirFile.renameTo(oldBack);
		}

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile)))
		{
			ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null)
			{
				if (entry.isDirectory())
					Files.createDirectories(Paths.get(dirPath, entry.getName()));
				else
					Files.copy(zis, Paths.get(dirPath, entry.getName()), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return true;
	}
}
