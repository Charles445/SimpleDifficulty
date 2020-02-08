package com.charles445.simpledifficulty.config.compat;

import net.minecraftforge.common.config.Config;

public class CFGServerCompatibility
{
	//Server Compatibility Settings
	
	@Config.Comment("Built-In Compatibility Toggles - Turn compatibility for mods on or off")
	@Config.Name("Built-In Compatibility Toggles")
	public final SDCFG_Toggles toggles = new SDCFG_Toggles();
	
	@Config.Comment("Armor Underwear Configurations")
	@Config.Name("Armor Underwear")
	public final SDCFGArmorUnderwear auw = new SDCFGArmorUnderwear();
	
	@Config.Comment("Pam's HarvestCraft Configurations")
	@Config.Name("Pam's HarvestCraft")
	public final SDCFGHarvestCraft harvestcraft = new SDCFGHarvestCraft();
	
	@Config.Comment("Harvest Festival Configurations")
	@Config.Name("Harvest Festival")
	public final SDCFGHarvestFestival harvestfestival = new SDCFGHarvestFestival();
	
	@Config.Comment("Serene Seasons Configurations")
	@Config.Name("Serene Seasons")
	public final SDCFGSereneSeasons sereneseasons = new SDCFGSereneSeasons();
	
	public static class SDCFG_Toggles
	{
		@Config.Comment("Enable Armor Underwear - Built-In Compatibility")
		@Config.Name("EnableArmorUnderwear")
		public boolean armorUnderwear = true;
		
		@Config.Comment("Enable Biomes O' Plenty - Built-In Compatibility")
		@Config.Name("EnableBiomesOPlenty")
		public boolean biomesOPlenty = true;
		
		@Config.Comment("Enable Harvest Craft - Built-In Compatibility")
		@Config.Name("EnableHarvestCraft")
		public boolean harvestCraft = true;
		
		@Config.Comment("Enable Harvest Festival - Built-In Compatibility")
		@Config.Name("EnableHarvestFestival")
		public boolean harvestFestival = true;
		
		@Config.Comment("Enable Lycanites Mobs - Built-In Compatibility")
		@Config.Name("EnableLycanitesMobs")
		public boolean lycanitesMobs = true;
		
		@Config.Comment("Enable OreExcavation - Built-In Compatibility")
		@Config.Name("EnableOreExcavation")
		public boolean oreExcavation = true;
		
		@Config.Comment("Enable Serene Seasons - Built-In Compatibility")
		@Config.Name("EnableSereneSeasons")
		public boolean sereneSeasons = true;
		
		@Config.Comment("Enable Simple Campfire - Built-In Compatibility")
		@Config.Name("EnableSimpleCampfire")
		public boolean simpleCampfire = true;

		@Config.Comment("Enable Tinkers' Construct - Built-In Compatibility")
		@Config.Name("EnableTinkersConstruct")
		public boolean tinkersconstruct = true;
		
	}
	
	public static class SDCFGArmorUnderwear
	{
		//Armor Underwear
		
		@Config.Comment("Goopak Temperature Modifier - Effect of a Goopak on temperature (won't change tooltip)")
		@Config.Name("GoopakTemperatureModifier")
		@Config.RangeDouble(min=0.0)
		public double goopakTemperatureModifier = 2.0d;
		
		@Config.Comment("Goopak Maximum Active - How many Goopaks can stack their effects at once")
		@Config.Name("GoopakMaximumActive")
		@Config.RangeInt(min=1)
		public int goopakMaximumActive = 5;
		
		@Config.Comment("Ozzy Base Range - The base temperature range of an Ozzy Liner")
		@Config.Name("OzzyBaseRange")
		@Config.RangeDouble(min = 0.0)
		public double ozzyBaseRange = 3.0d;
		
		@Config.Comment("Ozzy Extra Range - The added temperature range when upgrading an Ozzy Liner")
		@Config.Name("OzzyExtraRange")
		@Config.RangeDouble(min = 0.0)
		public double ozzyExtraRange = 3.0d;
		
		@Config.Comment("Liner Multiplier - Multiplier for the effect of normal liners")
		@Config.Name("LinerMultiplier")
		@Config.RangeDouble(min = 0.0)
		public double linerMultiplier = 1.0d;
	}
	
	public static class SDCFGHarvestCraft
	{
		//Harvestcraft
		
		@Config.Comment("Juice Thirst")
		@Config.Name("JuiceThirst")
		public int juiceThirst = 6;
		
		@Config.Comment("Juice Saturation")
		@Config.Name("JuiceSaturation")
		public double juiceSaturation = 5.0d;
		
		@Config.Comment("Juice Thirsty Chance")
		@Config.Name("JuiceThirstyChance")
		public double juiceThirstyChance = 0.0d;
		
		@Config.Comment("Smoothie Thirst")
		@Config.Name("SmoothieThirst")
		public int smoothieThirst = 9;
		
		@Config.Comment("Smoothie Saturation")
		@Config.Name("SmoothieSaturation")
		public double smoothieSaturation = 7.0d;
		
		@Config.Comment("Smoothie Thirsty Chance")
		@Config.Name("SmoothieThirstyChance")
		public double smoothieThirstyChance = 0.0d;
		
		@Config.Comment("Soda Thirst")
		@Config.Name("SodaThirst")
		public int sodaThirst = 9;
		
		@Config.Comment("Soda Saturation")
		@Config.Name("SodaSaturation")
		public double sodaSaturation = 7.0d;
		
		@Config.Comment("Soda Thirsty Chance")
		@Config.Name("SodaThirstyChance")
		public double sodaThirstyChance = 0.0d;
	}
	
	public static class SDCFGHarvestFestival
	{
		//Harvest Festival
		
		@Config.Comment("Season Winter - Temperature change during the Harvest Festival season")
		@Config.Name("SeasonWinter")
		public int seasonWinter = -10;
		
		@Config.Comment("Season Spring - Temperature change during the Harvest Festival season")
		@Config.Name("SeasonSpring")
		public int seasonSpring = 0;
		
		@Config.Comment("Season Summer - Temperature change during the Harvest Festival season")
		@Config.Name("SeasonSummer")
		public int seasonSummer = 4;
		
		@Config.Comment("Season Autumn - Temperature change during the Harvest Festival season")
		@Config.Name("SeasonAutumn")
		public int seasonAutumn = 0;

	}
	
	public static class SDCFGSereneSeasons
	{
		//Serene Seasons
		
		@Config.Comment("Season Early Winter - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonEarlyWinter")
		@Config.RangeInt
		public int seasonEarlyWinter = -7;
		
		@Config.Comment("Season Mid Winter - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonMidWinter")
		@Config.RangeInt
		public int seasonMidWinter = -14;
		
		@Config.Comment("Season Late Winter - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonLateWinter")
		@Config.RangeInt
		public int seasonLateWinter = -7;
		
		@Config.Comment("Season Early Spring - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonEarlySpring")
		@Config.RangeInt
		public int seasonEarlySpring = -3;
		
		@Config.Comment("Season Mid Spring - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonMidSpring")
		@Config.RangeInt
		public int seasonMidSpring = 0;
		
		@Config.Comment("Season Late Spring - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonLateSpring")
		@Config.RangeInt
		public int seasonLateSpring = 2;
		
		@Config.Comment("Season Early Summer - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonEarlySummer")
		@Config.RangeInt
		public int seasonEarlySummer = 3;
		
		@Config.Comment("Season Mid Summer - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonMidSummer")
		@Config.RangeInt
		public int seasonMidSummer = 5;
		
		@Config.Comment("Season Late Summer - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonLateSummer")
		@Config.RangeInt
		public int seasonLateSummer = 3;
		
		@Config.Comment("Season Early Autumn - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonEarlyAutumn")
		@Config.RangeInt
		public int seasonEarlyAutumn = 2;
		
		@Config.Comment("Season Mid Autumn - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonMidAutumn")
		@Config.RangeInt
		public int seasonMidAutumn = 0;
		
		@Config.Comment("Season Late Autumn - Temperature change during the Serene Seasons season")
		@Config.Name("SeasonLateAutumn")
		@Config.RangeInt
		public int seasonLateAutumn = -3;
	}
}
