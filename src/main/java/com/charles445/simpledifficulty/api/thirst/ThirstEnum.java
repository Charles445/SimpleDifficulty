package com.charles445.simpledifficulty.api.thirst;

public enum ThirstEnum
{
	NORMAL	("normal",		3,	0.3f, 	0.75f),
	RAIN	("rain",		1,	0.05f,	0.0f),
	POTION	("potion",		2,	0.2f,	0.0f),
	PURIFIED("purified",	6,	3.0f,	0.0f);
	
	private String name;
	private int thirst;
	private float saturation;
	private float dirty;
	
	private ThirstEnum(String name, int thirst, float saturation, float dirty)
	{
		this.name = name;
		this.thirst=thirst;
		this.saturation=saturation;
		this.dirty=dirty;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public int getThirst()
	{
		return thirst;
	}
	
	public float getSaturation()
	{
		return saturation;
	}
	
	public float getThirstyChance()
	{
		return dirty;
	}
	
	public String toString()
	{
		return this.getName();
	}
	
}
