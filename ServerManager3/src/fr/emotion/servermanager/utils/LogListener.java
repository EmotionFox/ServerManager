package fr.emotion.servermanager.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.emotion.servermanager.References;
import fr.emotion.servermanager.components.Player;

public class LogListener implements Runnable
{
	private static final Pattern namePattern = Pattern.compile("<(.+)>");
	private String log = "";
	private String saveName = "";
	private Player player;

	public LogListener(String log)
	{
		System.out.println("Here for: " + log);
		this.log = log;
	}

	@Override
	public void run()
	{
		String line = getCommand(log);

		if (player == null)
			return;

		switch (line)
		{
		case "help":

			ServerManager.getOutput().sendCommand(References.msgCommand + player.getName()
					+ " [{\"text\":\"<ESM>\",\"color\":\"aqua\"}, {\"text\":\" List of commands are : \",\"color\":\"red\"}, {\"text\":\"!time;\",\"color\":\"aqua\"},{\"text\":\" !save <name>\",\"color\":\"yellow\"}, {\"text\":\"!\",\"color\":\"red\"}]");
			break;
		case "time":
			int[] smh = { 0, 0, 0 };
			long timeN = System.currentTimeMillis() - player.getTime();
			int timeS = (int) (timeN / 1000);

			smh[2] = timeS / 3600;
			timeS -= smh[2] * 3600;
			smh[1] = timeS / 60;
			timeS -= smh[1] * 60;
			smh[0] = timeS;

			String timeF = smh[2] + "h " + smh[1] + "mn " + smh[0] + "sc";

			ServerManager.getOutput()
					.sendCommand(References.msgCommand + player.getName() + " [{\"text\":\"<ESM>\",\"color\":\"light_purple\"}, {\"text\":\" You'r playing since \",\"color\":\"red\"}, {\"text\":\""
							+ timeF + "\",\"color\":\"light_purple\"}, {\"text\":\".\",\"color\":\"red\"}]");
			break;
		case "add":
			player.setTime(player.getTime() + 100000);
		case "save":
			if (player.isAdmin())
			{
				if (!saveName.matches("^\\w+$"))
				{
					ServerManager.getOutput().sendCommand(References.msgCommand + player.getName()
							+ " [{\"text\":\"<ESM>\",\"color\":\"dark_red\"}, {\"text\":\" Save name \",\"color\":\"red\"}, {\"text\":\"incorrect\",\"color\":\"dark_red\"}, {\"text\":\" must be digit only.\",\"color\":\"red\"}]");
					break;
				}

				ServerManager.getOutput().sendCommand("/save-all");

				do
				{
					try
					{
						Thread.sleep(1000);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				} while (!ServerManager.saved);

				BackupManager.saveZip(saveName);
			}
			else
				ServerManager.getOutput().sendCommand(References.msgCommand + player.getName()
						+ " [{\"text\":\"<ESM>\",\"color\":\"yellow\"}, {\"text\":\" You must be \",\"color\":\"red\"}, {\"text\":\"admin\",\"color\":\"yellow\"}, {\"text\":\" to trigger a save.\",\"color\":\"red\"}]");
			break;
		}
	}

	public String getCommand(String value)
	{
		Matcher nameMatcher = namePattern.matcher(log);
		String caller = "";

		if (nameMatcher.find())
			caller = nameMatcher.group(1);

		for (Player player : ServerManager.getPlayers())
		{
			if (caller.equals(player.getName()))
				this.player = player;
		}

		if (log.matches("^<.+> !help$"))
			return "help";
		else if (log.matches("^<.+> !time$"))
			return "time";
		else if (log.matches("^<.+> !save .+$"))
		{
			saveName = log.split(" !save ")[1];
			return "save";
		}

		return "";
	}
}
