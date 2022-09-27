package fr.emotion.servermanager;

import java.awt.Color;
import java.awt.Dimension;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

public abstract class References
{
	public static final String style = "font-family:Open Sans;font-weight:normal;font-size:13pt;";
	public static final String msgCommand = "/tellraw ";

	public static final Color emoGreen = new Color(0x16C158);
	public static final Color emoOrange = new Color(0xC2A10C);
	public static final Color emoBlue = new Color(0x0CCFC5);

	public final static String wikiURL = "https://minecraft.fandom.com/wiki/Server.properties";

	public final static String propertieRegex = "<tr[^>]*>(.+?)</tr>";
	public final static String rangeRegex = "^integer \\(([\\d]+?)[–-]([\\d]+|\\(.+\\))\\)$";
	public final static String wikiRegex = "id=\"Java_Edition_3\"(.+?)id=\"Bedrock_Edition_3\"";
	public static final String urlRegex = "^(https|http)://.+.com.*$";
	public static final String saveRegex = "^.+_(.+).zip$";

	public final static Pattern propertiePattern = Pattern.compile(propertieRegex, Pattern.DOTALL);
	public final static Pattern wikiPattern = Pattern.compile(wikiRegex, Pattern.DOTALL);
	public final static Pattern savePattern = Pattern.compile(saveRegex);

	public static final double version = 2.1;

	public static final Preferences prefs = Preferences.userNodeForPackage(ManagerFrame.class);

	public static final Dimension managerDim = new Dimension(700, 480);
	public static final Dimension panelDim = new Dimension(managerDim.width / 2, managerDim.height - 25);

	public static int backupTime = 15;
	public static int allocatedRam = 2;

	public static final void init()
	{
		backupTime = prefs.getInt("backupTime", backupTime);
		allocatedRam = prefs.getInt("allocatedRam", allocatedRam);
	}

	public static final void setBackupTime(int time)
	{
		prefs.putInt("backupTime", time);
		backupTime = time;
	}

	public static final void setAllocatedRam(int ram)
	{
		prefs.putInt("allocatedRam", ram);
		allocatedRam = ram;
	}
}
