package fr.emotion.servermanager.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import fr.emotion.servermanager.MainServerManager;
import fr.emotion.servermanager.References;

public class StreamWriter implements Runnable
{
	private final OutputStream output;
	private ArrayList<String> command = new ArrayList<String>();
	public static int smh[] = { 0, 0, 0 };

	public StreamWriter(OutputStream stream)
	{
		this.output = stream;
	}

	public void sendCommand(String cmd)
	{
		command.add(cmd);
	}
	
	public void sendCommand(String... cmd)
	{
		for(int i = 0; i < cmd.length; i++)
			command.add(cmd[i]);
	}
	
	@Override
	public void run()
	{
		smh[0] = 0;
		smh[1] = 0;
		smh[2] = 0;

		try (OutputStreamWriter writer = new OutputStreamWriter(output); BufferedWriter bw = new BufferedWriter(writer))
		{
			int mn = 0;

			while (ServerManager.processReady())
			{
				String[] removed = new String[0];

				if (command.size() > 0)
				{
					removed = new String[command.size()];

					for (int i = 0; i < command.size(); i++)
					{
						String cmd = command.get(i);

						System.out.println("Command send: " + cmd);
						bw.write(cmd);
						bw.newLine();
						bw.flush();

						removed[i] = cmd;
					}
				}

				if (removed.length > 0)
					for (String str : removed)
						command.remove(str);

				if (ServerManager.serverReady())
				{
					try
					{
						Thread.sleep(1000);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}

					smh[0] += 1;

					if (smh[0] >= 60)
					{
						smh[0] = 0;
						smh[1] += 1;
						mn++;

						if (smh[1] >= 60)
						{
							smh[1] = 0;
							smh[2] += 1;
						}
					}

					MainServerManager.serverPanel.setTime("(" + smh[2] + "h " + smh[1] + "mn " + smh[0] + "sc)");
					
					if (mn >= References.backupTime)
					{
						bw.write("/save-all");
						
						Thread thread = new Thread(new SaveRunnable());
						thread.start();
						
						bw.newLine();
						bw.flush();
						mn = 0;
					}
				}
			}

			MainServerManager.serverPanel.setStatus(0);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public class SaveRunnable implements Runnable
	{
		public void run()
		{
			do
			{
				try
				{
					Thread.sleep(1000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			} while(!ServerManager.saved);
			
			BackupManager.saveZip("");
		}
	}
}
