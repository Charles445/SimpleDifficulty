package com.charles445.simpledifficulty.api.config;

import java.util.HashMap;
import java.util.Map;

public abstract class ConfigBase
{
	public final Map<String,String> values = new HashMap<String, String>();
	
	public boolean getBoolean(IConfigOption option)
	{
		return Boolean.valueOf(values.get(option.getName()));
	}
	
	public int getInteger(IConfigOption option)
	{
		return Integer.valueOf(values.get(option.getName()));
	}
	
	public void put(IConfigOption option, Object obj)
	{
		values.put(option.getName(), ""+obj);
		QuickConfig.updateValues();
	}
}
