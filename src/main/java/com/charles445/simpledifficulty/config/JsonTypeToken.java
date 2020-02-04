package com.charles445.simpledifficulty.config;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.charles445.simpledifficulty.api.config.json.JsonConsumableTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableThirst;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonTemperature;
import com.charles445.simpledifficulty.config.json.MaterialTemperature;
import com.google.gson.reflect.TypeToken;

public class JsonTypeToken
{
	public static Type get(JsonFileName jcfn)
	{
		switch(jcfn)
		{
			case armorTemperatures: 	return new TypeToken<Map<String, JsonTemperature>>(){}.getType();
			case blockTemperatures: 	return new TypeToken<Map<String, List<JsonPropertyTemperature>>>(){}.getType();
			case consumableTemperature: return new TypeToken<Map<String, List<JsonConsumableTemperature>>>(){}.getType();
			case consumableThirst: 		return new TypeToken<Map<String, List<JsonConsumableThirst>>>(){}.getType();
			case fluidTemperatures: 	return new TypeToken<Map<String, JsonTemperature>>(){}.getType();
			case materialTemperature: 	return new TypeToken<MaterialTemperature>(){}.getType();
			default: return null;
		}
	}
}
