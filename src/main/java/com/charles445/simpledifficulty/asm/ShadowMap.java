package com.charles445.simpledifficulty.asm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShadowMap
{
	public static Map<String, String> ownerMap;
	public static Map<String, String> descMap;
	
	public static void addClassShadow(String shadow, String destination)
	{
		ownerMap.put(shadow, destination);
		descMap.put("L"+shadow+";", "L"+destination+";");
	}
	
	static
	{
		ownerMap = new ConcurrentHashMap<>();
		descMap = new ConcurrentHashMap<>();
		
		//Inspirations
		addClassShadow("com/charles445/simpledifficulty/compat/shadow/InspirationsShadow$ICauldronRecipe", "knightminer/inspirations/library/recipe/cauldron/ICauldronRecipe");
		addClassShadow("com/charles445/simpledifficulty/compat/shadow/InspirationsShadow$ICauldronRecipe$CauldronState", "knightminer/inspirations/library/recipe/cauldron/ICauldronRecipe$CauldronState");
	}
}
