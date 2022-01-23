package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.api.SDFluids;

import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Loader;

import java.util.Map;

public class BlockFluidBasic extends BlockFluidClassic
{
	public BlockFluidBasic(Fluid fluid, Material material)
	{
		super(fluid, material);
		setRegistryName(fluid.getName());
		setUnlocalizedName(this.getRegistryName().toString());
		SDFluids.fluidBlocks.put(fluid.getName(), this);

		if (Loader.isModLoaded("backportedflora")) {
			displacements.putAll(customDisplacements);
		}
	}

	protected final static Map<Block, Boolean> customDisplacements = Maps.newHashMap();
	static
	{
		customDisplacements.put(REGISTRY.getObject(new ResourceLocation("backportedflora", "rivergrass")), false);
		customDisplacements.put(REGISTRY.getObject(new ResourceLocation("backportedflora", "seagrass")), false);
		customDisplacements.put(REGISTRY.getObject(new ResourceLocation("backportedflora", "kelp")), false);
		customDisplacements.put(Blocks.WATER, false);
	}

	@Override
	@SuppressWarnings("deprecation")
	public int getLightOpacity(IBlockState state) {
		if (ServerConfig.instance.getBoolean(ServerOptions.PURIFIED_WATER_OPACITY)) {
			return 1;
		} else {
			return 3;
		}
	}
}
