package com.charles445.simpledifficulty.register;

import static com.charles445.simpledifficulty.api.SDFluids.*;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.block.BlockFluidBasic;
import com.charles445.simpledifficulty.fluid.FluidBasic;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
					0xFFFFFFFF);
			
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
	}
}
