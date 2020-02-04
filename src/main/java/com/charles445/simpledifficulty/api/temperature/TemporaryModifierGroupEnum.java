package com.charles445.simpledifficulty.api.temperature;

public enum TemporaryModifierGroupEnum
{
	FOOD("food"),
	DRINK("drink");
	
	private String group;
	
	private TemporaryModifierGroupEnum(String group)
	{
		this.group=group;
	}
	
	public String group()
	{
		return group;
	}
}
