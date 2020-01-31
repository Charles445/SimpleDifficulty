package com.charles445.simpledifficulty.config.json;

import com.google.gson.annotations.SerializedName;

public class ArmorTemperature
{
	@SerializedName("temperature")
	public float temperature;
	
	public ArmorTemperature(float temperature)
	{
		this.temperature = temperature;
	}
}
