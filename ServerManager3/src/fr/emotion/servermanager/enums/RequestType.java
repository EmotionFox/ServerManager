package fr.emotion.servermanager.enums;

public enum RequestType
{
	STOP("stop"), REBOOT("reboot"), RELOAD("reboot and reload");

	private final String name;

	private RequestType(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
