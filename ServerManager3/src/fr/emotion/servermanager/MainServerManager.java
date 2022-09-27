package fr.emotion.servermanager;

import fr.emotion.servermanager.panels.ManagerPanel;
import fr.emotion.servermanager.panels.PropertiePanel;
import fr.emotion.servermanager.panels.ServerPanel;
import fr.emotion.servermanager.utils.ListManager;

public final class MainServerManager
{
	public static ServerPanel serverPanel;
	public static PropertiePanel propertiePanel;

	public static ManagerPanel panel;
	public static ManagerFrame frame;

	public static void main(String[] args)
	{
		serverPanel = new ServerPanel(References.panelDim);
		propertiePanel = new PropertiePanel(References.panelDim);

		panel = new ManagerPanel(References.managerDim, serverPanel, propertiePanel);
		frame = new ManagerFrame(panel);

		ListManager.init();
		References.init();
	}
}
