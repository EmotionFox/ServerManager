package fr.emotion.servermanager.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.emotion.servermanager.components.Player;

public abstract class OpManager
{
	private static final Pattern jsonLine = Pattern.compile("\"(.+)\": \"(.+)\"");

	public static void getOps(File ops)
	{
		if (ops == null || !ops.exists())
			return;
		
		ServerManager.clearPlayers();

		try (Reader reader = new java.io.FileReader(ops); BufferedReader br = new BufferedReader(reader); LineNumberReader lnr = new LineNumberReader(br))
		{
			String line = "";

			while (lnr.ready())
			{
				line = lnr.readLine();

				Matcher matcher = jsonLine.matcher(line);

				if (matcher.find())
				{
					if (matcher.groupCount() == 2)
					{
						if (matcher.group(1).equals("name"))
						{
							String name = matcher.group(2).toLowerCase();
							ServerManager.addPlayer(new Player(name, 0, false, true));
						}
					}
				}
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
