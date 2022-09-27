package fr.emotion.servermanager.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.ColorUIResource;

import fr.emotion.servermanager.MainServerManager;
import fr.emotion.servermanager.References;
import fr.emotion.servermanager.enums.RequestType;
import fr.emotion.servermanager.utils.BackupManager;
import fr.emotion.servermanager.utils.OpManager;
import fr.emotion.servermanager.utils.PropertieManager;
import fr.emotion.servermanager.utils.ServerManager;

public class ServerPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;

	private final JLabel setName = new JLabel("Server Name :");
	private final JLabel setPath = new JLabel("Jar Path :");
	private final JLabel serverStatus = new JLabel("Server Status : Offline");
	private final JLabel[] labels = { setName, setPath };

	private final JTextField profileName = new JTextField();
	private final JTextField jarPath = new JTextField();
	private final JTextField[] textFields = { profileName, jarPath };

	private final JFileChooser fileChooser = new JFileChooser();

	private final JSeparator separator_1 = new JSeparator();
	private final JSeparator separator_2 = new JSeparator();

	private final JComboBox<String> saveBox = new JComboBox<>();

	private final JButton jarButton = new JButton("Select Jar Path");
	private final JButton launchButton = new JButton("Launch");
	private final JButton stopButton = new JButton("Stop");
	private final JButton rebootButton = new JButton("Reboot");
	private final JButton reloadButton = new JButton("Reload");
	public final JButton saveButton = new JButton("Save Properties (ALT + S)");
	private final JButton[] buttons = { jarButton, launchButton, stopButton, rebootButton, reloadButton, saveButton };

	private File jarFile;

	private ImageIcon labelIcon;

	private ImageIcon button_disabled;
	private ImageIcon button_off;
	private ImageIcon button_over;
	private ImageIcon button_on;

	private ImageIcon small_button_disabled;
	private ImageIcon small_button_off;
	private ImageIcon small_button_over;
	private ImageIcon small_button_on;

	private ImageIcon server_off;
	private ImageIcon server_on;
	private ImageIcon server_running;

	public ServerPanel(Dimension dim)
	{
		try
		{
			labelIcon = new ImageIcon(getClass().getResource("/resources/images/label.png"));

			button_disabled = new ImageIcon(getClass().getResource("/resources/images/button_disabled.png"));
			button_off = new ImageIcon(getClass().getResource("/resources/images/button_off.png"));
			button_over = new ImageIcon(getClass().getResource("/resources/images/button_over.png"));
			button_on = new ImageIcon(getClass().getResource("/resources/images/button_on.png"));

			small_button_disabled = new ImageIcon(getClass().getResource("/resources/images/small_button_disabled.png"));
			small_button_off = new ImageIcon(getClass().getResource("/resources/images/small_button_off.png"));
			small_button_over = new ImageIcon(getClass().getResource("/resources/images/small_button_over.png"));
			small_button_on = new ImageIcon(getClass().getResource("/resources/images/small_button_on.png"));

			server_off = new ImageIcon(getClass().getResource("/resources/images/server_off.png"));
			server_on = new ImageIcon(getClass().getResource("/resources/images/server_on.png"));
			server_running = new ImageIcon(getClass().getResource("/resources/images/server_running.png"));
		} catch (NullPointerException e)
		{
			e.printStackTrace();
		}

		this.setPreferredSize(dim);
		this.setOpaque(false);

		UIManager.put("Button.disabledText", new ColorUIResource(Color.gray));

		for (JLabel label : labels)
		{
			label.setPreferredSize(new Dimension(340, 30));
			label.setFont(new Font("Open Sans", Font.PLAIN, 16));
			label.setHorizontalTextPosition(JLabel.CENTER);
			label.setIcon(labelIcon);
			label.setForeground(Color.white);
		}

		for (JTextField field : textFields)
		{
			field.setPreferredSize(new Dimension(340, 30));
			field.setFont(new Font("Open Sans", Font.PLAIN, 16));
			field.setHorizontalAlignment(JTextField.CENTER);
			field.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.black));
			field.setForeground(Color.white);
			field.setOpaque(false);
		}

		profileName.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{}

			@Override
			public void keyReleased(KeyEvent e)
			{
				if (MainServerManager.panel.getTemplate() != 2)
				{
					if (!Character.isLetter(e.getKeyChar()) & !Character.isDigit(e.getKeyChar()) & profileName.getText().length() < 1)
						MainServerManager.panel.setTemplate(0);
					else
						MainServerManager.panel.setTemplate(1);
				}
			}

			@Override
			public void keyPressed(KeyEvent e)
			{}
		});

		jarPath.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{}

			@Override
			public void keyReleased(KeyEvent e)
			{}

			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == 10)
				{
					setJar(Paths.get(jarPath.getText()).toFile());
				}
			}
		});

		String name = "";

		for (JButton button : buttons)
		{
			if (button == launchButton || button == stopButton || button == rebootButton)
				button.setPreferredSize(new Dimension(340 / 3 - 3, 30));
			else
				button.setPreferredSize(new Dimension(340, 30));

			if (button.getText().contains(" "))
				name = button.getText().split(" ")[0].toLowerCase();
			else
				name = button.getText().toLowerCase();
			button.setName(name);

			if (button == launchButton || button == stopButton || button == rebootButton)
			{
				button.setIcon(small_button_off);
				button.setDisabledIcon(small_button_disabled);
				button.setRolloverIcon(small_button_over);
				button.setPressedIcon(small_button_on);
			}
			else
			{
				button.setIcon(button_off);
				button.setDisabledIcon(button_disabled);
				button.setRolloverIcon(button_over);
				button.setPressedIcon(button_on);
			}

			button.setFont(new Font("Open Sans", Font.PLAIN, 16));
			button.setHorizontalTextPosition(JButton.CENTER);
			button.setForeground(Color.white);
			button.setOpaque(false);
			button.setContentAreaFilled(false);
			button.setBorderPainted(false);
			button.addActionListener(this);
			button.setFocusable(false);
		}

		launchButton.setEnabled(false);
		stopButton.setEnabled(false);
		rebootButton.setEnabled(false);
		reloadButton.setEnabled(false);
		saveButton.setEnabled(false);

		fileChooser.addActionListener(this);
		fileChooser.setFileFilter(new FileNameExtensionFilter("Java Archive", "jar"));
		fileChooser.setAcceptAllFileFilterUsed(false);

		separator_1.setPreferredSize(new Dimension(340, 30));
		separator_1.setUI(null);
		separator_2.setPreferredSize(new Dimension(340, 30));
		separator_2.setUI(null);

		saveBox.setPreferredSize(new Dimension(340, 30));
		saveBox.setFont(new Font("Open Sans", Font.PLAIN, 16));
		saveBox.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
		saveBox.setEditable(false);
		saveBox.setEnabled(false);

		serverStatus.setPreferredSize(new Dimension(340, 60));
		serverStatus.setOpaque(true);
		serverStatus.setFont(new Font("Open Sans", Font.PLAIN, 16));
		serverStatus.setHorizontalTextPosition(JLabel.CENTER);
		serverStatus.setForeground(Color.white);
		serverStatus.setIcon(server_off);
		serverStatus.setOpaque(false);

		this.add(setName);
		this.add(profileName);
		this.add(setPath);
		this.add(jarPath);
		this.add(jarButton);
		this.add(separator_1);
		this.add(launchButton);
		this.add(stopButton);
		this.add(rebootButton);
		this.add(reloadButton);
		this.add(saveBox);
		this.add(separator_2);
		this.add(saveButton);
		this.add(serverStatus);
	}

	public String getProfileName()
	{
		return profileName.getText();
	}

	public String getJarPath()
	{
		if (jarFile != null && jarFile.exists())
			return jarFile.getAbsolutePath();
		else
			return "";
	}

	public String getJarFolder()
	{
		if (jarFile != null && jarFile.exists())
			return jarFile.getParent().toString();
		else
			return "";
	}

	public String getSave()
	{
		System.out.println("Active item: " + (String) saveBox.getEditor().getItem() + " | Selected Item: " + (String) saveBox.getSelectedItem());
		return (String) saveBox.getEditor().getItem();
	}

	public void setServer(String name, String path)
	{
		profileName.setText(name);
		jarPath.setText(path);

		if (!path.isEmpty())
		{
			jarFile = new File(path);
			setJar(jarFile);
		}
		else
		{
			jarFile = null;
			PropertieManager.clearProperties();
		}
	}

	public void setProfileName(String name)
	{
		profileName.setText(name);
	}

	public void setTemplate(int template)
	{
		switch (template)
		{
		case 0:
			launchButton.setEnabled(false);
			stopButton.setEnabled(false);
			rebootButton.setEnabled(false);
			reloadButton.setEnabled(false);

			if (saveBox.getComponentCount() > 0)
				saveBox.removeAllItems();

			saveBox.setEnabled(false);
			break;
		case 1:
			launchButton.setEnabled(true);
			stopButton.setEnabled(false);
			rebootButton.setEnabled(false);
			reloadButton.setEnabled(false);

			if (saveBox.getComponentCount() > 0)
				saveBox.removeAllItems();

			saveBox.setEnabled(false);
			break;
		case 2:
			launchButton.setEnabled(false);
			stopButton.setEnabled(true);
			rebootButton.setEnabled(true);
			reloadButton.setEnabled(false);

			if (saveBox.getComponentCount() > 0)
				saveBox.removeAllItems();

			saveBox.setEnabled(false);

			ArrayList<String> list = BackupManager.getList();

			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
					saveBox.addItem(list.get(i));

				reloadButton.setEnabled(true);
				saveBox.setEnabled(true);
			}

			break;
		default:
			launchButton.setEnabled(false);
			stopButton.setEnabled(false);
			rebootButton.setEnabled(false);
			reloadButton.setEnabled(false);

			if (saveBox.getComponentCount() > 0)
				saveBox.removeAllItems();

			saveBox.setEnabled(false);
			break;
		}
	}

	public void setStatus(int status)
	{
		Color panelColor = Color.white;
		Color color = Color.white;
		String text = "Server Status : Offline";
		ImageIcon icon = server_off;

		switch (status)
		{
		case 1:
			panelColor = References.emoOrange;
			color = References.emoOrange;
			text = "Server Status : Starting/Stopping";
			icon = server_on;
			break;
		case 2:
			panelColor = References.emoGreen;
			color = References.emoGreen;
			text = "Server Status : Online";
			icon = server_running;
			break;
		}

		MainServerManager.panel.setColor(panelColor);
		serverStatus.setForeground(color);
		serverStatus.setText(text);
		serverStatus.setIcon(icon);
	}

	public void setTime(String time)
	{
		serverStatus.setText("Server Status : Online " + time);
	}

	public void resetServer(boolean resetName)
	{
		if (resetName)
		{
			profileName.setText("");
			MainServerManager.panel.setTemplate(0);
		}
		jarPath.setText("");
		jarFile = null;
		PropertieManager.clearProperties();

		this.setTemplate(0);
		saveButton.setEnabled(false);
	}

	public void setJar(File file)
	{
		String name = file.getName();
		if (file != null && file.exists() && name.matches("^.+.jar$"))
		{
			jarFile = file;

			if (Paths.get(jarFile.getParent() + "\\server.properties").toFile().exists())
			{
				PropertieManager.setProperties(jarFile.getParent() + "/server.properties");
				saveButton.setEnabled(true);
			}
			else if (PropertieManager.hasProperties())
			{
				PropertieManager.clearProperties();
			}

			jarPath.setText(jarFile.getAbsolutePath());

			MainServerManager.panel.setTemplate(2);
			this.setTemplate(1);

			return;
		}
		else if (name.matches("^.+.jar$"))
			JOptionPane.showMessageDialog(this.getParent(), "Unable to find: " + file.getName() + ".", "Server Manager", JOptionPane.WARNING_MESSAGE);
		else
			JOptionPane.showMessageDialog(this.getParent(), "File: " + file.getName() + " is not a valid jar file.", "Server Manager", JOptionPane.WARNING_MESSAGE);

		this.resetServer(false);
	}

	public String readEula()
	{
		try (Reader reader = new FileReader(Paths.get(jarFile.getParent() + "\\eula.txt").toFile()); BufferedReader br = new BufferedReader(reader); LineNumberReader lnr = new LineNumberReader(br))
		{
			String str = "";

			while ((str = lnr.readLine()) != null)
			{
				if (str.matches("^eula=(true|false)$"))
					return str.split("=")[1];
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public void writeEula(boolean accept)
	{
		try (Writer writer = new FileWriter(Paths.get(jarFile.getParent() + "\\eula.txt").toFile()); BufferedWriter bw = new BufferedWriter(writer))
		{
			Date date = new Date();
			String line = "";

			line += "#By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).\n";
			line += "#" + date + "\n";
			line += "#Edited by Emotion's Server Manager.\n";
			line += "eula=" + String.valueOf(accept) + "\n";

			bw.write(line);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == fileChooser)
		{
			if (fileChooser.getSelectedFile() != null)
				setJar(fileChooser.getSelectedFile());
		}
		else if (e.getSource() instanceof JButton)
		{
			JButton button = (JButton) e.getSource();
			String name = button.getName();

			switch (name)
			{
			case "select":
				fileChooser.showOpenDialog(this.getParent());
				break;
			case "launch":
				if (!Paths.get(jarFile.getParent() + "\\eula.txt").toFile().exists() || readEula().equals("false"))
				{
					int option = JOptionPane.showConfirmDialog(this.getParent(), "Should I accept the EULA for you? (End User License Agreement)", "Server Manager", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

					if (option == JOptionPane.YES_OPTION)
						writeEula(true);
					else if (option == JOptionPane.NO_OPTION)
					{
						writeEula(false);
						break;
					}
				}
				
				if (Paths.get(jarFile.getParent() + "\\eula.txt").toFile().exists() && readEula().equals("true"))
				{
					OpManager.getOps(Paths.get(jarFile.getParent() + "/ops.json").toFile());
					ServerManager.setProcess(jarFile.getAbsolutePath(), jarFile.getParentFile());
				}
				break;
			case "stop":
				askDelay(RequestType.STOP);
				break;
			case "reboot":
				OpManager.getOps(Paths.get(jarFile.getParent() + "/ops.json").toFile());
				askDelay(RequestType.REBOOT);
				break;
			case "reload":
				OpManager.getOps(Paths.get(jarFile.getParent() + "/ops.json").toFile());
				askDelay(RequestType.RELOAD);
				break;
			case "save":
				PropertieManager.saveProperties(jarFile.getParent() + "\\server.properties");
				JOptionPane.showMessageDialog(this.getParent(), "Server properties has been saved.", "Server Manager", JOptionPane.INFORMATION_MESSAGE);
				break;
			}
		}
	}

	public void askDelay(RequestType type)
	{
		if (ServerManager.serverReady())
		{
			int option = JOptionPane.showConfirmDialog(this.getParent(), "Do you want to " + type.toString() + " with delay?", "Server Manager", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);

			if (option == JOptionPane.YES_OPTION)
			{
				try
				{
					int value = Integer.parseInt(JOptionPane.showInputDialog(this.getParent(), "Enter the time in second:", "Server Manager", JOptionPane.QUESTION_MESSAGE));
					ServerManager.resetServer(value, type);
				} catch (NumberFormatException e1)
				{
					JOptionPane.showMessageDialog(this.getParent(), "No time set, the server will " + type.toString() + " in 5 seconds.", "Server Manager", JOptionPane.ERROR_MESSAGE);
					ServerManager.resetServer(5, type);
				}
			}
			else if (option == JOptionPane.NO_OPTION)
				ServerManager.resetServer(0, type);
		}
		else
			JOptionPane.showMessageDialog(this.getParent(), "Server not ready yet, please wait.", "Server Manager", JOptionPane.WARNING_MESSAGE);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		g.setColor(new Color(0, 0, 0, 127));
		g.fillRect(5, 40, 340, 30);
		g.fillRect(5, 110, 340, 30);
	}
}
