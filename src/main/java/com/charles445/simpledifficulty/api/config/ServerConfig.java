package com.charles445.simpledifficulty.api.config;

import net.minecraft.nbt.NBTTagCompound;

public class ServerConfig extends ConfigBase
{
	
	/*
	 *	Server Configuration
	 *
	 *	Example Usage: 
	 *	
	 *	boolean serverDebug = ServerConfig.instance.getBoolean(ServerOptions.DEBUG);
	 */
	
	public static ServerConfig instance = new ServerConfig();
	
	/**
	 * (Don't use this!) <br>
	 * Updates the ServerConfig instance with the values from the compound
	 */
	public void updateValues(NBTTagCompound compound)
	{
		for(String key : compound.getKeySet())
		{
			if(values.containsKey(key))
			{
				String newValue = compound.getString(key);
				values.put(key, newValue);
			}
		}
		
		QuickConfig.updateValues();
	}
}
