package com.charles445.simpledifficulty.fluid;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDFluids;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class FluidBasic extends Fluid
{
	public FluidBasic(String fluidName, String still, String flowing, int ARGB)
	{
		super(fluidName, 
			new ResourceLocation(SimpleDifficulty.MODID, "fluids/"+still),
			new ResourceLocation(SimpleDifficulty.MODID, "fluids/"+flowing),
			ARGB
		);
		
		//Add to generic fluid map
		SDFluids.fluids.put(fluidName, this);
		
		//Register and add bucket for self
		FluidRegistry.registerFluid(SDFluids.fluids.get(fluidName));
		FluidRegistry.addBucketForFluid(SDFluids.fluids.get(fluidName));
	}
}
