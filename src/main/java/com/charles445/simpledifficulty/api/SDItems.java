package com.charles445.simpledifficulty.api;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public class SDItems
{
	public static final Map<String, ArmorMaterial> armorMaterials = new LinkedHashMap<String, ArmorMaterial>();
	public static final Map<String, Item> items = new LinkedHashMap<String, Item>();
	
	public static ArmorMaterial woolArmorMaterial;
	public static ArmorMaterial iceArmorMaterial;
	
	public static Item canteen;
	public static Item ironCanteen;
	public static Item charcoalFilter;
	public static Item juice;
	public static Item purifiedWaterBottle;
	
	public static Item ice_chunk;
	public static Item magma_chunk;
	
	public static Item thermometer;
	
	public static Item wool_helmet;
	public static Item wool_chestplate;
	public static Item wool_leggings;
	public static Item wool_boots;
	
	public static Item ice_helmet;
	public static Item ice_chestplate;
	public static Item ice_leggings;
	public static Item ice_boots;
}
