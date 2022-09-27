package fr.emotion.servermanager.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;

import fr.emotion.servermanager.MainServerManager;
import fr.emotion.servermanager.References;
import fr.emotion.servermanager.components.PropertieLabel;

public abstract class PropertieManager
{
	private static ArrayList<String> worlds = new ArrayList<String>();
	private static ArrayList<Propertie> propertieList = new ArrayList<Propertie>();
	private static ArrayList<Propertie> wikiPropertieList = new ArrayList<Propertie>();
	private static String currentWorld = "world";

	public static final void readProperties(String path)
	{
		if (!worlds.isEmpty())
			worlds.clear();
		if (!propertieList.isEmpty())
			propertieList.clear();

		File propertiesFile = Paths.get(path).toFile();

		for (File file : propertiesFile.getParentFile().listFiles())
		{
			if (file.isDirectory())
			{
				if (!file.getName().contains(".old") && Paths.get(file.getPath() + "/level.dat").toFile().exists())
				{
					String world = file.getName();

					if (world.contains("'") & !world.contains("\\'"))
						world = world.replaceAll("'", "\\'");

					worlds.add(world);
				}
			}
		}

		try (Reader reader = new java.io.FileReader(propertiesFile); BufferedReader br = new BufferedReader(reader); LineNumberReader lnr = new LineNumberReader(br))
		{
			String line;

			while (lnr.ready())
			{
				line = lnr.readLine();

				if (line.matches("^[^#]*=.*$"))
				{
					Propertie propertie;

					if (line.matches("^[^#]*=.+$"))
					{
						String key = line.split("=")[0];
						String value = line.split("=")[1];

						propertie = new Propertie(key, value);

						if (value.matches("^(true|false)$"))
							propertie.setInfo("boolean");
						else if (value.matches("^\\d+$"))
							propertie.setInfo("integer");
						else
						{
							if (key.equals("gamemode"))
								propertie.setInfo("gamemodePicker");
							else if (key.equals("level-name"))
							{
								propertie.setInfo("levelPicker");
								currentWorld = value;
							}
							else if (key.equals("difficulty"))
								propertie.setInfo("difficultyPicker");
							else if (key.equals("level-type"))
								propertie.setInfo("typePicker");
							else
								propertie.setInfo("string");
						}
					}
					else
					{
						propertie = new Propertie(line.substring(0, line.length() - 1), "");
						propertie.setInfo("string");
					}

					propertieList.add(propertie);
				}
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		if (wikiPropertieList.isEmpty())
			// Look over the official Wiki and scrap infos
		{
			try
			{
				URL url = new URL(References.wikiURL);
				URLConnection urlCo = url.openConnection();

				try (InputStream is = urlCo.getInputStream(); InputStreamReader isr = new InputStreamReader(is, "UTF-8"))
				{
					int numCharsRead;
					char[] charArray = new char[1024];
					StringBuffer sb = new StringBuffer();

					while ((numCharsRead = isr.read(charArray)) > 0)
						sb.append(charArray, 0, numCharsRead);

					String result = sb.toString();
					Matcher wikiMatcher = References.wikiPattern.matcher(result);

					if (wikiMatcher.find())
						result = wikiMatcher.group(1);

					Matcher propertieMatcher = References.propertiePattern.matcher(result);

					while (propertieMatcher.find())
					{
						String[] params = { "", "", "", "" };
						String str = propertieMatcher.group(1).replaceAll("<[^>]+>", "");
						str = str.replaceAll("\\s{2,}", "\n");

						String[] line = str.split("\n");

						for (int i = 1; i < line.length; i++)
						{
							if (line[i].contains("more information needed"))
								line[i] = line[i].split("&").length > 0 ? line[i].split("&")[0] : "";

							if (i > 3)
							{
								line[i] = line[i].replaceAll("\\.[\\s+]", ".<br>");
								params[3] += line[i] + (i == line.length - 1 ? "" : "<br>");
							}
							else
								params[i - 1] = line[i];
						}

						wikiPropertieList.add(new Propertie(params[0], params[1], params[2], params[3]));
					}
				}
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		for (Propertie propertie : propertieList)
			propertie.lookOverWiki(wikiPropertieList);

	}

	public static void setProperties(String path)
	{
		if (hasProperties())
			clearProperties();

		readProperties(path);

		int height = 0;

		for (Propertie propertie : propertieList)
		{
			PropertieLabel component = new PropertieLabel(propertie);
			MainServerManager.propertiePanel.add(component);
			height += 50;
		}

		height += 10;

		MainServerManager.propertiePanel.setPreferredSize(new Dimension(700 / 2, height));
		MainServerManager.panel.revalidate();
	}

	public static void saveProperties(String path)
	{
		if (MainServerManager.propertiePanel.getComponentCount() > 0)
		{
			try (Writer writer = new FileWriter(path); BufferedWriter bw = new BufferedWriter(writer))
			{
				Date date = new Date();
				DateFormat dateFormatter = new SimpleDateFormat("MM_dd_yyyy");

				bw.write("#Minecraft server properties\n");
				bw.write("#" + date + "\n");
				bw.write("#Edited by Emotion's Server Manager\n");

				for (Component component : MainServerManager.propertiePanel.getComponents())
				{
					if (component instanceof PropertieLabel)
					{
						PropertieLabel propertieComponent = (PropertieLabel) component;
						String line = propertieComponent.getName() + "=" + propertieComponent.getValue() + "\n";

						if (propertieComponent.getName().equals("level-name"))
						{
							if (MainServerManager.panel.addDay() & !propertieComponent.getValue().matches("^.+\\[\\d{2}_\\d{2}_\\d{4}\\]$"))
								line = propertieComponent.getName() + "=" + propertieComponent.getValue() + " [" + dateFormatter.format(date) + "]\n";

							currentWorld = propertieComponent.getValue();
						}

						bw.write(line);
					}
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		if (MainServerManager.panel.addDay())
			setProperties(path);
	}

	public static void clearProperties()
	{
		MainServerManager.propertiePanel.removeAll();
		MainServerManager.propertiePanel.setPreferredSize(new Dimension(700 / 2, 480));
		MainServerManager.panel.revalidate();
	}

	public static boolean hasProperties()
	{
		return (MainServerManager.propertiePanel.getComponentCount() > 0);
	}

	public static ArrayList<String> getWorlds()
	{
		return worlds;
	}

	public static String getCurrentWorld()
	{
		return currentWorld;
	}
}
