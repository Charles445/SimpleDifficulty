package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.config.ModConfig;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public abstract class ModifierBase implements ITemperatureModifier
{	
	//Trying to mentally organize the modifiers to determine which ones should be unique and which ones should override others
	
	/* --
	 * Unique World Modifiers (Ambience, Intangible Environment, Natural)
	 * 
	 * Altitude
	 * Biome
	 * Default
	 * Season
	 * Snow
	 * Time
	 * Wet
	 * --
	 * Proximity World Modifiers (Blocks, Tile Entities, Unnatural, Radiates Heat)
	 * 
	 * Proximity (Blocks, Tile Entities)
	 * --
	 * Unique Player Modifiers (Armor, Items, Effects, State)
	 * 
	 * Armor
	 * Sprint
	 * Temporary
	 * --
	 */
	
	
	
	private final String name;
	protected final float defaultTemperature;
	
	protected ModifierBase(String name)
	{
		this.name=name;
		this.defaultTemperature = (TemperatureEnum.NORMAL.getUpperBound() + TemperatureEnum.COLD.getUpperBound()) / 2;
	}
	
	@Override
	public float getPlayerInfluence(EntityPlayer player)
	{
		return 0.0f;
	}

	@Override
	public float getWorldInfluence(World world, BlockPos pos)
	{
		return 0.0f;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	//Convenient shared functions
	
	protected float applyUndergroundEffect(float temperature, World world, BlockPos pos)
	{
		//TODO more accurate 
		//TODO this gets called by multiple modifiers... there might be a way to cache this first and then simply read the result
		//TODO Run before any modifiers...
		//TODO That'd be pretty good
		
		//Y 64 - 256 is always unchanged temperature
		if(pos.getY() >= 64)
			return temperature;
		
		//Y [-inf, 64)
		
		if(!ModConfig.server.temperature.undergroundEffect || !world.provider.isSurfaceWorld())
			return temperature;
		
		//Check that the player actually is underground
		//Tough As Nails checks a 10x10 area with canSeeSky first, then checks how underground the player is
		//Then it does that many more times to get a smooth blending of the effect
		//This is cool, but it looks like it takes a lot of processing, and only really matters with surface exposed things under Y 64 (which should be few...)
		//
		//I'm going to keep this simple for now, just checking the player's body blocks and if one of them can see the sky
		
		//TODO don't do it like this
		if(world.canSeeSky(pos) || world.canSeeSky(pos.up()))
			return temperature;
		
		
		//The player is underground, so actually do the math
		int cutoff = ModConfig.server.temperature.undergroundEffectCutoff;
		
		//If Y is past cutoff, or if cutoff is literally 64, apply the effect fully
		if(pos.getY() <= cutoff || cutoff == 64)
			return 0.0f;

		return temperature * (float)(pos.getY() - cutoff) / (64.0f - cutoff);
		
		
	}
	
	protected float getTempForBiome(Biome biome)
	{
		//Take a biome's temperature, cut it off at 1.35f, and turn it into a value between 0 and 1
		return MathHelper.clamp(biome.getDefaultTemperature(),0.0f,1.35f)/1.35f;
	}
	
	protected float normalizeToPlusMinus(float value)
	{
		//Assume input is between 0 and 1
		//Convert range to -1 and 1 instead
		return (value * 2.0f) - 1.0f;
	}
}