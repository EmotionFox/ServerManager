package fr.emotion.servermanager;

import java.awt.Image;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import fr.emotion.servermanager.enums.RequestType;
import fr.emotion.servermanager.panels.ManagerPanel;
import fr.emotion.servermanager.utils.ServerManager;

public class ManagerFrame extends JFrame
{
	private static final long serialVersionUID = 1L;

	private Image image;
	private int x = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width / 2 - (References.managerDim.width / 2);
	private int y = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height / 2 - (References.managerDim.height / 2);

	public ManagerFrame(ManagerPanel panel)
	{
		try
		{
			image = ImageIO.read(getClass().getResource("/resources/images/ESMIcon.png"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		this.setTitle("Emotion's Server Manager");
		this.setIconImage(image);
		this.setResizable(false);
		this.setContentPane(panel);
		this.pack();

		this.setLocation(x = References.prefs.getInt("windowX", x), y = References.prefs.getInt("windowY", y));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	@Override
	protected void processWindowEvent(WindowEvent e)
	{
		/*
		 * id 201 = when closing
		 */
		if (e.getID() == 201)
		{
			References.prefs.putInt("windowX", this.getX());
			References.prefs.putInt("windowY", this.getY());

			if (ServerManager.processReady() && !ServerManager.serverReady())
			{
				JOptionPane.showMessageDialog(this, "You can't exit during server's start or stop.", "Server Manager", JOptionPane.WARNING_MESSAGE);
				return;
			}

			if (ServerManager.processReady())
				ServerManager.resetServer(0, RequestType.STOP);
		}

		super.processWindowEvent(e);
	}
}
