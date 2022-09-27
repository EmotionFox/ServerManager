package fr.emotion.servermanager.components;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import fr.emotion.servermanager.MainServerManager;
import fr.emotion.servermanager.utils.ServerManager;

public class ServerMenuItem extends JMenuItem implements ActionListener
{
	private static final long serialVersionUID = 1L;

	private String name;
	private String path;

	public ServerMenuItem(String name, String path)
	{
		this.name = name;
		this.path = path;
		this.setPreferredSize(new Dimension((700 / 4) - 3, 25));
		this.setText(name);
		this.addActionListener(this);
	}

	public void setServer(String name, String path)
	{
		this.name = name;
		this.setText(name);
		this.path = path;
	}

	public void setName(String name)
	{
		this.name = name;
		this.setText(this.name);
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public String getName()
	{
		return this.name;
	}

	public String getPath()
	{
		return this.path;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (ServerManager.processReady())
			JOptionPane.showMessageDialog(MainServerManager.serverPanel, "Cannot perform will server is running.", "Server Manager", JOptionPane.WARNING_MESSAGE);
		else
			MainServerManager.serverPanel.setServer(name, path);
	}
}
