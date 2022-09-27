package fr.emotion.servermanager.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import fr.emotion.servermanager.MainServerManager;
import fr.emotion.servermanager.References;
import fr.emotion.servermanager.components.ServerMenuItem;
import fr.emotion.servermanager.utils.ListManager;
import fr.emotion.servermanager.utils.ServerManager;

public class ManagerPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;

	private final JMenuItem newItem = new JMenuItem("New (CTRL + N)");
	private final JMenuItem saveItem = new JMenuItem("Save (CTRL + S)");
	private final JMenuItem deleteItem = new JMenuItem("Delete (CTRL + D)");
	private final JMenuItem folderItem = new JMenuItem("Open Folder (CTRL + O)");
	private final JMenuItem backupItem = new JMenuItem("Backup Frenquency");
	private final JMenuItem ramItem = new JMenuItem("Allocated Ram");
	private final JMenuItem aboutItem = new JMenuItem("About (CTRL + B)");
	private final JMenuItem[] items = { newItem, saveItem, deleteItem, folderItem, backupItem, ramItem, aboutItem };

	private final JCheckBoxMenuItem setDay = new JCheckBoxMenuItem("Inject Date");

	private final JMenu serverMenu = new JMenu("Server Actions");
	private final JMenu listMenu = new JMenu("Servers List");
	private final JMenu optionMenu = new JMenu("Options");
	private final JMenu extraMenu = new JMenu("Extras");

	private final JMenuBar menuBar = new JMenuBar();

	private ServerPanel serverPanel;
	private JScrollPane scrollPane;

	private Image background;

	private int template = 0;
	private Color color = Color.white;

	public ManagerPanel(Dimension dim, ServerPanel serverPanel, PropertiePanel propertiePanel)
	{
		try
		{
			background = ImageIO.read(getClass().getResource("/resources/images/background_v6.png"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		this.serverPanel = serverPanel;
		this.scrollPane = new JScrollPane(propertiePanel);

		this.setSize(dim);
		this.setPreferredSize(dim);
		this.setLayout(new BorderLayout());

		Dimension menuDim = new Dimension(this.getWidth() / 4, 25);

		setItem(menuDim);

		folderItem.setEnabled(false);

		serverMenu.setPreferredSize(menuDim);
		serverMenu.add(newItem);
		serverMenu.add(saveItem);
		serverMenu.add(deleteItem);
		serverMenu.setEnabled(false);

		listMenu.setPreferredSize(menuDim);

		optionMenu.setPreferredSize(menuDim);
		optionMenu.add(setDay);
		optionMenu.add(backupItem);
		optionMenu.add(ramItem);
		optionMenu.setEnabled(false);

		extraMenu.setPreferredSize(menuDim);
		extraMenu.add(folderItem);
		extraMenu.add(aboutItem);

		menuBar.setPreferredSize(new Dimension(this.getWidth(), 25));
		menuBar.add(serverMenu);
		menuBar.add(listMenu);
		menuBar.add(optionMenu);
		menuBar.add(extraMenu);

		scrollPane.setPreferredSize(new Dimension(this.getWidth() / 2, this.getHeight()));
		scrollPane.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, Color.black));
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.getVerticalScrollBar().setUnitIncrement(50);

		this.add(menuBar, BorderLayout.NORTH);
		this.add(this.serverPanel, BorderLayout.WEST);
		this.add(this.scrollPane, BorderLayout.EAST);

		this.keyBind(KeyEvent.VK_N, "new", (evt) -> {
			newItem.doClick();
		});
		this.keyBind(KeyEvent.VK_S, "save", (evt) -> {
			saveItem.doClick();
		});
		this.keyBind(KeyEvent.VK_D, "delete", (evt) -> {
			deleteItem.doClick();
		});
		this.keyBind(KeyEvent.VK_O, "open", (evt) -> {
			folderItem.doClick();
		});
		this.keyBind(KeyEvent.VK_B, "about", (evt) -> {
			aboutItem.doClick();
		});
		this.keyBind(KeyEvent.VK_S, KeyEvent.ALT_DOWN_MASK, "saveProperties", (evt) -> {
			serverPanel.saveButton.doClick();
		});
	}

	public void setItem(Dimension dim)
	{
		Dimension itemDim = new Dimension(dim.width - 3, dim.height);

		for (JMenuItem item : items)
		{
			String name = item.getText().replaceAll(" (.+)$", "");
			item.setName(name);
			item.setPreferredSize(itemDim);
			item.addActionListener(this);
		}

		setDay.setPreferredSize(itemDim);
		setDay.setToolTipText("Inject the date into the world name, format : [mm_dd_yyyy]");
		setDay.addActionListener(this);
	}

	public void setServer(List<ServerMenuItem> serverList)
	{
		listMenu.removeAll();

		for (ServerMenuItem server : serverList)
		{
			listMenu.add(server);
		}
	}

	public void setTemplate(int template)
	{
		this.template = template;

		switch (template)
		{
		case 0:
			serverMenu.setEnabled(false);
			folderItem.setEnabled(false);
			optionMenu.setEnabled(false);
			break;
		case 1:
			serverMenu.setEnabled(true);
			folderItem.setEnabled(false);
			optionMenu.setEnabled(false);
			break;
		case 2:
			serverMenu.setEnabled(true);
			folderItem.setEnabled(true);
			optionMenu.setEnabled(true);
			break;
		}
	}

	public void setColor(Color color)
	{
		this.color = color;
		this.revalidate();
		this.repaint();
	}

	public int getTemplate()
	{
		return this.template;
	}

	public void keyBind(int key, String id, ActionListener listener)
	{
		keyBind(key, KeyEvent.CTRL_DOWN_MASK, false, id, listener);
	}

	public void keyBind(int key, int modifiers, String id, ActionListener listener)
	{
		keyBind(key, modifiers, false, id, listener);
	}

	public void keyBind(int key, int modifiers, boolean release, String id, ActionListener listener)
	{
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key, modifiers, release), id);
		this.getActionMap().put(id, new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				listener.actionPerformed(e);
			}
		});
	}

	public boolean addDay()
	{
		return setDay.isSelected();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() instanceof JMenuItem && e.getSource() != setDay)
		{
			JMenuItem source = (JMenuItem) e.getSource();
			String name = source.getName();

			switch (name)
			{
			case "New":
				if (ServerManager.processReady())
					JOptionPane.showMessageDialog(MainServerManager.serverPanel, "Cannot perform will server is running.", "Server Manager", JOptionPane.WARNING_MESSAGE);
				else
					serverPanel.resetServer(true);
				break;
			case "Save":
				if (ServerManager.processReady())
					JOptionPane.showMessageDialog(MainServerManager.serverPanel, "Cannot perform will server is running.", "Server Manager", JOptionPane.WARNING_MESSAGE);
				else
				{
					if (serverPanel.getProfileName().isEmpty() && !serverPanel.getJarPath().isEmpty())
					{
						JOptionPane.showMessageDialog(this, "No server name entered. I'll choose a good one for you.", "Server Manager", JOptionPane.WARNING_MESSAGE);
						ListManager.addServer("The... UNKNOW server", serverPanel.getJarPath());
						serverPanel.setProfileName("The... UNKNOW server");
						return;
					}
					ListManager.addServer(serverPanel.getProfileName(), serverPanel.getJarPath());
				}
				break;
			case "Delete":
				if (ServerManager.processReady())
					JOptionPane.showMessageDialog(MainServerManager.serverPanel, "Cannot perform will server is running.", "Server Manager", JOptionPane.WARNING_MESSAGE);
				else
				{
					if (ListManager.removeServer(serverPanel.getProfileName()))
						this.setTemplate(0);
				}
				break;
			case "Open":
				if (Desktop.isDesktopSupported())
				{
					try
					{
						Desktop.getDesktop().open(Paths.get(serverPanel.getJarPath()).toFile().getParentFile());
					} catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
				break;
			case "Backup":
				String frequency = this.inputInt("Backup Frequency (minute) :", 15);

				if (frequency != null)
					References.setBackupTime(Integer.valueOf(frequency));
				break;
			case "Allocated":
				String ram = this.inputInt("Allocated Ram (gigabit) :", 2);

				if (ram != null)
					References.setAllocatedRam(Integer.valueOf(ram));
				break;
			case "About":
				JEditorPane ep = new JEditorPane("text/html", "<html><body style=\"" + References.style + "\"><center><h1>Minecraft Server Manager v" + References.version + "</h1>"
						+ "Created by: EmotionFox" + "<br><strong><a href=\"https://github.com/EmotionFox\">GitHub/EmotionFox</a></strong></center></body></html>");
				ep.setOpaque(false);
				ep.setEditable(false);

				ep.addHyperlinkListener(new HyperlinkListener()
				{
					@Override
					public void hyperlinkUpdate(HyperlinkEvent e)
					{
						if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
							try
							{
								Desktop.getDesktop().browse(e.getURL().toURI());
							} catch (IOException e1)
							{
								e1.printStackTrace();
							} catch (URISyntaxException e1)
							{
								e1.printStackTrace();
							}
					}
				});

				JOptionPane.showMessageDialog(this, ep, "Server Manager", 1);
				break;
			}
		}
	}

	public String inputInt(String name, int defaultValue)
	{
		String value = "";
		JLabel question = new JLabel(name);
		question.setToolTipText("Default Value : " + defaultValue);

		do
		{
			value = JOptionPane.showInputDialog(this, question, "Server Manager", 3);
		} while (value != null && !value.matches("^\\d+$"));

		return value;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(background, 0, 25, this.getWidth(), this.getHeight(), this);

		GradientPaint gp = new GradientPaint(0, this.getHeight() / 2, new Color(0, 0, 0, 0), 0, this.getHeight(), color);

		g2d.setPaint(gp);
		g2d.fillRect(0, 25, this.getWidth(), this.getHeight() - 25);
	}
}
