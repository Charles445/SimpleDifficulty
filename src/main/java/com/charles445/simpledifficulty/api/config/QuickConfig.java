package com.charles445.simpledifficulty.api.config;

public class QuickConfig
{
	//Alternative for commonly used config, should be convenient and have somewhat faster performance
	
	private static boolean temperatureEnabled;
	private static boolean thirstEnabled;
	
	public static boolean isTemperatureEnabled()
	{
		return temperatureEnabled;
	}
	
	public static boolean isThirstEnabled()
	{
		return thirstEnabled;
	}
	
	protected static void updateValues()
	{
		temperatureEnabled = ServerConfig.instance.getBoolean(ServerOptions.TEMPERATURE_ENABLED);
		thirstEnabled = ServerConfig.instance.getBoolean(ServerOptions.THIRST_ENABLED);
	}
}
