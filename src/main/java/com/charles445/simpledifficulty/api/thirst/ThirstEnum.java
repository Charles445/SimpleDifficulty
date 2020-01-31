package com.charles445.simpledifficulty.api.thirst;

public enum ThirstEnum
{
	NORMAL	(	3,	0.1f, 	0.75f),	//0.3
	RAIN	(	1,	0.05f,	0.0f),	//0.05
	POTION	(	2,	0.1f,	0.0f),	//0.2
	PURIFIED(	6,	0.5f,	0.0f);	//3
	
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
	
	public float getDirtyChance()
	{
		return dirty;
	}
	
	public String toString()
	{
		return this.name().toLowerCase();
	}
	
}
