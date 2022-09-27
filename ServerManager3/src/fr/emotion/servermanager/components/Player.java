package fr.emotion.servermanager.components;

public class Player
{
	private String name;
	private long time;
	private boolean reconnect;
	private boolean admin;

	public Player(String name, long time, boolean reconnect)
	{
		this(name, time, reconnect, false);
	}
	
	public Player(String name, long time, boolean reconnect, boolean admin)
	{
		this.name = name;
		this.time = time;
		this.reconnect = reconnect;
		this.admin = admin;
	}
	
	public void setName(String name){ this.name = name; }
	public void setTime(long time){ this.time = time; }
	public void setAdmin(boolean admin){ this.admin = admin; }
	public void setReco(boolean reco){ this.reconnect = reco; }

	public String getName(){ return this.name; }
	public long getTime(){ return this.time; }
	public boolean isAdmin(){ return this.admin; }
	public boolean isReconnecting(){ return this.reconnect; }
}
