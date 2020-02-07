package com.charles445.simpledifficulty.api.config;

public enum ClientOptions implements IConfigOption
{
	/*Boolean*/	DEBUG		("debug"),
	/*Boolean*/ DRAW_THIRST_SATURATION ("drawThirstSaturation"),
	/*Boolean*/ ENABLE_THERMOMETER ("enableThermometer"),
	/*Boolean*/ ALTERNATE_TEMP ("alternateTemp"),
	/*Boolean*/ HUD_THERMOMETER ("hudThermometer"),
	/*Integer*/ HUD_THERMOMETERX ("hudThermometerX"),
	/*Integer*/ HUD_THERMOMETERY ("hudThermometerY");
	
	String name;
	
	private ClientOptions(String name)
	{
		this.name=name;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	public String toString()
	{
		return this.getName();
	}
}
