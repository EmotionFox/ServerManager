package fr.emotion.servermanager.utils;

import java.util.ArrayList;

import fr.emotion.servermanager.References;

public class Propertie
{
	private String key = "";
	private String type = "";
	private String defaultValue = "";
	private String description = "";
	private String value = "";
	private String range = "";

	// Default Constructor
	public Propertie(String key, String value)
	{
		this.key = key;
		this.value = value;
	}

	// Wiki Constructor
	public Propertie(String key, String type, String defaultValue, String description)
	{
		this.key = key;

		// Override type
		if (this.key.equals("gamemode"))
			this.type = "gamemodePicker";
		else if (this.key.equals("level-name"))
			this.type = "levelPicker";
		else if (this.key.equals("difficulty"))
			this.type = "difficultyPicker";
		else if (this.key.equals("level-type"))
			this.type = "typePicker";
		else
			this.type = type;

		this.defaultValue = defaultValue;
		this.value = "";
		this.description = description;
	}

	public String getKey()
	{
		return this.key;
	}

	public String getType()
	{
		return this.type;
	}

	public String getDefaultValue()
	{
		return this.defaultValue;
	}

	public String getDescription()
	{
		return this.description;
	}

	public String getValue()
	{
		return this.value;
	}

	public String getRange()
	{
		return this.range;
	}

	public void setInfo(String type)
	{
		this.type = type;
		this.description = type;
	}

	public void lookOverWiki(ArrayList<Propertie> list)
	{
		for (Propertie propertie : list)
		{
			if (this.key.equals(propertie.getKey()))
			{
				if (!this.type.contains("Picker") && !this.type.equals(propertie.getType()) && !propertie.getType().isEmpty())
				{
					if (propertie.getType().matches(References.rangeRegex))
					{
						this.type = "integer";
						this.range = propertie.getType().split("integer ")[1];
					}
					else
						this.type = propertie.getType();
				}
				if (!propertie.getDescription().isEmpty())
					this.description = propertie.description;
				if (!propertie.getDefaultValue().isEmpty())
					this.defaultValue = propertie.defaultValue;

				return;
			}
		}
	}

	@Override
	public String toString()
	{
		return this.key;
	}
}
