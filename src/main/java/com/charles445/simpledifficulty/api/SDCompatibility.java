package com.charles445.simpledifficulty.api;

import java.util.ArrayList;
import java.util.List;

public class SDCompatibility
{
	/**
	 * Whether the default thirst display should be rendered. 
	 * <br>
	 * Mods that replace the thirst display should set this to false!
	 * <br>
	 */
	public static boolean defaultThirstDisplay = true;
	
	/**
	 * Don't change this directly, use {@link #disableBuiltInModJsonConfiguration(String) disableBuiltInModJsonConfiguration}
	 * <br>
	 */
	public static List<String> disabledDefaultJson = new ArrayList<String>();
	
	/**
	 * Don't change this directly, use {@link #disableBuiltInModCompatibility(String) disableBuiltInModCompatibility}
	 * <br>
	 */
	public static List<String> disabledCompletely = new ArrayList<String>();
	
	/**
	 * Call this in preInit or init with your mod's modid.<br>
	 * SimpleDifficulty won't automatically make settings for your mod.<br>
	 * This is useful if you want to make defaults yourself.
	 * @param modid
	 */
	public static void disableBuiltInModJsonConfiguration(String modid)
	{
		disabledDefaultJson.add(modid);
	}
	
	/**
	 * Call this in preInit or init with your mod's modid<br>
	 * SimpleDifficulty will stop automatically being compatible with your mod.<br>
	 * This is useful if you want to make the mod compatibility in your own way!<br>
	 * NOTE that this will disable any built in mod json configuration as well.
	 */
	public static void disableBuiltInModCompatibility(String modid)
	{
		disabledDefaultJson.add(modid);
		disabledCompletely.add(modid);
	}
}
