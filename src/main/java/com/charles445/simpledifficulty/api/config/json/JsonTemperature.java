package com.charles445.simpledifficulty.api.config.json;

import com.google.gson.annotations.SerializedName;

public class JsonTemperature
{
	@SerializedName("temperature")
	public float temperature;
	
	public JsonTemperature(float temperature)
	{
		this.temperature = temperature;
	}
}