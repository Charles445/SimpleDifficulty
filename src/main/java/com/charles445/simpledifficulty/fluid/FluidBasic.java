package com.charles445.simpledifficulty.fluid;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDFluids;

import git.jbredwards.fluidlogged_api.api.fluid.ICompatibleFluid;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "git.jbredwards.fluidlogged_api.api.fluid.ICompatibleFluid", modid = "fluidlogged_api")
public class FluidBasic extends Fluid implements ICompatibleFluid
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
	
	@Nonnull
	@Override
	@Optional.Method("fluidlogged_api")
    	public Fluid getParentFluid() { return FluidRegistry.WATER; }
}
