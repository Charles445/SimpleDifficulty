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
	 * Map of dynamic modifiers that the temperature system uses.
	 * <br>
	 * It is recommended to avoid using these if possible.
	 * <br>
	 * It is recommended to add modifiers with {@link #registerDynamicModifier(ITemperatureDynamicModifier) registerDynamicModifier}
	 * <br>
	 */
	public static LinkedHashMap<String, ITemperatureDynamicModifier> dynamicModifiers = new LinkedHashMap<String, ITemperatureDynamicModifier>();
	
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
	
	/**
	 * Registers a dynamic modifier to be used with the temperature system
	 * <br>
	 * It is recommended to avoid using these if possible.
	 * <br>
	 * Custom dynamic modifiers need to extend ITemperatureDynamicModifier and be registered here
	 * @param modifier
	 */
	public static void registerDynamicModifier(ITemperatureDynamicModifier modifier)
	{
		dynamicModifiers.put(modifier.getName(), modifier);
	}
}
