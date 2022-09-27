package fr.emotion.servermanager.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import fr.emotion.servermanager.References;
import fr.emotion.servermanager.utils.Propertie;
import fr.emotion.servermanager.utils.PropertieManager;

public class PropertieLabel extends JLabel implements MouseListener
{
	private static final long serialVersionUID = 1L;

	private static final String[] gamemodeList = { "survival", "creative", "adventure", "spectator" };
	private static final String[] difficultyList = { "peaceful", "easy", "normal", "hard" };
	private static final String[] typeList = { "normal", "flat", "large_biomes", "amplified", "single_biome_surface" };

	private Propertie propertie;
	private JToggleButton toggle = new JToggleButton();
	private JTextField field = new JTextField();
	private JComboBox<String> comboBox = new JComboBox<>();

	private ImageIcon enabled;
	private ImageIcon disabled;
	private ImageIcon pressed;

	public PropertieLabel(Propertie propertie)
	{
		try
		{
			enabled = new ImageIcon(getClass().getResource("/resources/images/switch_on.png"));
			disabled = new ImageIcon(getClass().getResource("/resources/images/switch_off.png"));
			pressed = new ImageIcon(getClass().getResource("/resources/images/switch_pressed.png"));
		} catch (NullPointerException e)
		{
			e.printStackTrace();
		}

		this.propertie = propertie;

		this.setPreferredSize(new Dimension(350 - 20, 40));
		this.setLayout(null);
		this.setFont(new Font("Open Sans", Font.PLAIN, 16));
		this.setHorizontalAlignment(JLabel.LEFT);
		this.setVerticalAlignment(JLabel.CENTER);
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.setForeground(Color.white);
		this.setText(propertie.getKey());
		this.setToolTipText("Default Value : " + propertie.getDefaultValue());
		this.setOpaque(false);
		this.addMouseListener(this);

		toggle.setBounds(350 - (enabled.getIconWidth() + 38) - 5, (40 - enabled.getIconHeight()) / 2, enabled.getIconWidth(), enabled.getIconHeight());
		toggle.setBorderPainted(false);
		toggle.setSelectedIcon(enabled);
		toggle.setDisabledIcon(disabled);
		toggle.setPressedIcon(pressed);
		toggle.setIcon(disabled);
		toggle.setToolTipText(propertie.getType());

		field.setBounds(350 - (80 + 38) - 5, 0, 80, 40);
		field.setFont(new Font("Open Sans", Font.PLAIN, 16));
		field.setHorizontalAlignment(JTextField.CENTER);
		field.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
		field.setBackground(Color.white);
		field.setToolTipText(propertie.getType());
		field.setText(propertie.getValue());

		comboBox.setBounds(350 - (160 + 38) - 5, 0, 160, 40);
		comboBox.setFont(new Font("Open Sans", Font.PLAIN, 16));
		comboBox.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
		comboBox.setEditable(true);

		setComponent();
	}

	public void setComponent()
	{
		switch (propertie.getType())
		{
		case "boolean":
			toggle.setSelected(Boolean.parseBoolean(propertie.getValue()));
			this.add(toggle);
			break;
		case "integer":
			this.add(field);
			break;
		case "gamemodePicker":
			for (String gamemode : gamemodeList)
				comboBox.addItem(gamemode);

			comboBox.setSelectedItem(propertie.getValue());
			this.add(comboBox);
			break;
		case "levelPicker":
			for (String world : PropertieManager.getWorlds())
				comboBox.addItem(world);

			comboBox.setSelectedItem(propertie.getValue());
			this.add(comboBox);
			break;
		case "difficultyPicker":
			for (String difficulty : difficultyList)
				comboBox.addItem(difficulty);

			comboBox.setSelectedItem(propertie.getValue());
			this.add(comboBox);
			break;
		case "typePicker":
			for (String type : typeList)
				comboBox.addItem(type);

			comboBox.setSelectedItem(propertie.getValue());
			this.add(comboBox);
			break;
		case "string":
			field.setBounds(350 - (160 + 38) - 5, 0, 160, 40);
			this.add(field);
			break;
		}
	}

	public String getValue()
	{
		if (propertie.getType() == "boolean")
			return String.valueOf(toggle.isSelected());
		else if (propertie.getType().contains("Picker"))
			return (String) comboBox.getEditor().getItem();
		else
			return field.getText();
	}

	@Override
	public String getName()
	{
		return propertie.getKey();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		g.setColor(new Color(0, 0, 0, 100));
		g.fillRect(0, 5, this.getWidth() - 18, this.getHeight() - 10);
		super.paintComponent(g);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON3)
		{
			String html = "<html><body style=\"" + References.style + "\"><strong>\"" + this.propertie.getKey() + "\"</strong>"
					+ (!this.propertie.getDefaultValue().isEmpty() ? "<br><br><b>Default Value : </b>" + this.propertie.getDefaultValue() : "")
					+ (!this.propertie.getDescription().isEmpty() ? "<br><br><b>Description : </b>" + this.propertie.getDescription() : "")
					+ (!this.propertie.getRange().isEmpty() ? "<br><br><b>Range : </b>" + this.propertie.getRange() : "") + "</body></html>";
			
			JEditorPane ep = new JEditorPane("text/html", html);
			ep.setOpaque(false);
			ep.setEditable(false);
			
			JOptionPane.showMessageDialog(this.getRootPane(), ep, "Server Manager", 1);
		}
	}

	@Override
	public void mousePressed(MouseEvent e)
	{}

	@Override
	public void mouseReleased(MouseEvent e)
	{}

	@Override
	public void mouseEntered(MouseEvent e)
	{}

	@Override
	public void mouseExited(MouseEvent e)
	{}
}
