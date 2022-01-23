package com.charles445.simpledifficulty.register;

import static com.charles445.simpledifficulty.api.SDFluids.*;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.block.BlockFluidBasic;
import com.charles445.simpledifficulty.fluid.FluidBasic;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class RegisterFluids
{
	//TODO block placing sound
	
	@Mod.EventBusSubscriber(modid = SimpleDifficulty.MODID)
	public static class Registrar
	{
		@SubscribeEvent
		public static void registerFluidsAndFluidBlocks(RegistryEvent.Register<Block> event)
		{
			final IForgeRegistry<Block> registry = event.getRegistry();
			
			
			//Create Fluids
			purifiedWater = new FluidBasic(
				"purifiedwater",
				"purifiedwater_still",
				"purifiedwater_flow",
				0xFF44AFF5
			);
			
			//Fluids register themselves
			
			//Create Fluid Blocks
			
			blockPurifiedWater = new BlockFluidBasic(purifiedWater, Material.WATER);
			
			for(String key : fluidBlocks.keySet())
			{
				registry.register(fluidBlocks.get(key));
			}
		}

		@SubscribeEvent
		public static void registerFluidItems(RegistryEvent.Register<Item> event)
		{
			final IForgeRegistry<Item> registry = event.getRegistry();
			
			for(String key : fluidBlocks.keySet())
			{
				registry.register(makeItemFromBlock(fluidBlocks.get(key)));
			}
		}
		
		private static ItemBlock makeItemFromBlock(Block block)
		{
			ItemBlock itemblock = new ItemBlock(block);
			itemblock.setRegistryName(block.getRegistryName());
			itemblock.setCreativeTab(ModCreativeTab.instance);
			return itemblock;
		}

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public static void registerBlockColourHandlers(final ColorHandlerEvent.Block event) {
			final BlockColors blockColors = event.getBlockColors();

			// Use the water colour of the biome or the default water colour
			final IBlockColor waterColourHandler = (state, blockAccess, pos, tintIndex) -> {
				if (blockAccess != null && pos != null) {
					return BiomeColorHelper.getWaterColorAtPos(blockAccess, pos);
				}

				return 0xFF44AFF5;
			};

			blockColors.registerBlockColorHandler(waterColourHandler, blockPurifiedWater);
		}

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public static void registerItemColourHandlers(final ColorHandlerEvent.Item event) {
			final BlockColors blockColors = event.getBlockColors();
			final ItemColors itemColors = event.getItemColors();

			// Use the Block's colour handler for an ItemBlock
			final IItemColor itemBlockColourHandler = (stack, tintIndex) -> {
				@SuppressWarnings("deprecation")
				final IBlockState state = ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
				return blockColors.colorMultiplier(state, null, null, tintIndex);
			};

			itemColors.registerItemColorHandler(itemBlockColourHandler, blockPurifiedWater);
		}
	}
}
