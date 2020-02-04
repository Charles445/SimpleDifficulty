package com.charles445.simpledifficulty.api.temperature;

import java.util.LinkedHashMap;

public class TemperatureRegistry
{
	/**
	 * Map of modifiers that the temperature system uses.
	 * <br>
	 * It is recommended to add modifiers with {@link #registerModifier(ITemperatureModifier) registerModifier}
	 * <br>
	 */
	public static LinkedHashMap<String, ITemperatureModifier> modifiers = new LinkedHashMap<String, ITemperatureModifier>();
	
	/**
	 * Registers a modifier to be used with the temperature system
	 * <br>
	 * Custom modifiers need to extend ITemperatureModifier and be registered here
	 * @param modifier
	 */
	public static void registerModifier(ITemperatureModifier modifier)
	{
		modifiers.put(modifier.getName(), modifier);
	}
}
