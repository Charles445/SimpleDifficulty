package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.api.SDFluids;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidBasic extends BlockFluidClassic
{
	public BlockFluidBasic(Fluid fluid, Material material)
	{
		super(fluid, material);
		setRegistryName(fluid.getName());
		setUnlocalizedName(this.getRegistryName().toString());
		fluid.setBlock(this);
		SDFluids.fluidBlocks.put(fluid.getName(), this);
	}
}
