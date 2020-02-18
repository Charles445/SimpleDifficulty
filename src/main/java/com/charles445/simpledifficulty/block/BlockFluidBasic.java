package com.charles445.simpledifficulty.block;

import javax.annotation.Nonnull;

import com.charles445.simpledifficulty.api.SDFluids;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidBasic extends BlockFluidClassic
{
	public BlockFluidBasic(Fluid fluid, Material material)
	{
		super(fluid, material);
		setRegistryName(fluid.getName());
		setUnlocalizedName(this.getRegistryName().toString());
		SDFluids.fluidBlocks.put(fluid.getName(), this);
	}
	
	@Override
	public int getLightValue(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos)
	{
		return 1;
	}
	
	@Override
	public int getLightValue(IBlockState state)
	{
		return 1;
	}
}
