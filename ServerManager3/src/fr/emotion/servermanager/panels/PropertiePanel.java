package fr.emotion.servermanager.panels;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;

public class PropertiePanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	public PropertiePanel(Dimension dim)
	{
		this.setPreferredSize(dim);
		this.setOpaque(false);
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
	}
}
