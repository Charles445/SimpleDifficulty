package com.charles445.simpledifficulty.api.thirst;

public enum ThirstEnum
{
	NORMAL	(	3,	0.3f, 	0.75f),
	RAIN	(	1,	0.05f,	0.0f),
	POTION	(	2,	0.2f,	0.0f),
	PURIFIED(	6,	3.0f,	0.0f);
	
	private int thirst;
	private float saturation;
	private float dirty;
	
	private ThirstEnum(int thirst, float saturation, float dirty)
	{
		this.thirst=thirst;
		this.saturation=saturation;
		this.dirty=dirty;
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
		return this.name().toLowerCase();
	}
	
}
