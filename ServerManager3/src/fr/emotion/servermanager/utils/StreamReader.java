package fr.emotion.servermanager.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.emotion.servermanager.MainServerManager;

public class StreamReader implements Runnable
{
	private static final Pattern namePattern = Pattern.compile("<(.+)>");
	private final InputStream input;
	public boolean serverReady = false;

	public StreamReader(InputStream stream)
	{
		this.input = stream;
	}

	@Override
	public void run()
	{
		String log = "";
		String copy = "";

		try (InputStreamReader reader = new InputStreamReader(input); BufferedReader br = new BufferedReader(reader))
		{
			MainServerManager.serverPanel.setTemplate(2);
			MainServerManager.serverPanel.setStatus(1);

			while ((log = br.readLine()) != null && !log.isEmpty())
			{
				if (log != copy)
				{
					copy = log;

					log = log.replaceFirst("\\[.*\\]: ", "").toLowerCase();

					ServerManager.sendLog(log);

					if (ServerManager.serverReady())
					{
						Matcher nameMatcher = namePattern.matcher(log);

						if (nameMatcher.find())
						{
							Thread logThread = new Thread(new LogListener(log));
							logThread.start();
						}
					}
				}
			}

			MainServerManager.serverPanel.setTemplate(1);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
