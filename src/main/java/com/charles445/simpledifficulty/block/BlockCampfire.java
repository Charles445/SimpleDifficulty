package com.charles445.simpledifficulty.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.OreDictUtil;
import com.charles445.simpledifficulty.util.SoundUtil;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCampfire extends Block implements IBlockStateIgnore
{
	private static final int AGE_MIN = 0;
	private static final int AGE_MAX = 7;
	private static final int LOG_REFUEL = 3;
	private static final int TICK_RATE = 10;
	
	public static final PropertyInteger AGE = PropertyInteger.create("age", AGE_MIN, AGE_MAX);
	public static final PropertyBool BURNING = PropertyBool.create("burning");

	private static final IProperty[] ignoredProperties = new IProperty[]{BURNING};
	private static final AxisAlignedBB HITBOX = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
	
	public BlockCampfire()
	{
		super(Material.CIRCUITS, MapColor.WOOD);
		setDefaultState(blockState.getBaseState().withProperty(AGE, AGE_MIN).withProperty(BURNING, false));
		setHardness(0.5f);
		setSoundType(SoundType.WOOD);
		setTickRandomly(true);
	}
	
	//INTERACT
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		final ItemStack heldItemStack = player.getHeldItem(hand);
		
		//Return true on open hand
		if(heldItemStack.isEmpty())
			return true;
		
		final Item heldItem = heldItemStack.getItem();
		
		//Allow direct placement of a spit
		if(Block.getBlockFromItem(heldItem).equals(SDBlocks.spit))
			return false;
		
		//Always return true from this point onwards
		
		if(world.isRemote)
		{
			//Client Sound Handler
			if(heldItem==Items.FLINT_AND_STEEL)
			{
				int age = state.getValue(AGE);
				boolean burning = state.getValue(BURNING);
				if(!burning && age < AGE_MAX && !world.isRainingAt(pos.up()))
				{
					world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
				}
			}
			
			return true;
		}
		
		//SERVER SIDE HANDLING
		
		int age = state.getValue(AGE);
		boolean burning = state.getValue(BURNING);
		
		if(OreDictUtil.isOre(OreDictUtil.logWood,heldItemStack))
		{
			if(age > AGE_MIN)
			{
				//Do logs
				heldItemStack.shrink(1);
				//Give it a little bit of a bump if it's all gone...
				int refuelAmount = (LOG_REFUEL + (age == AGE_MAX? 1 : 0));
				world.setBlockState(pos, state.withProperty(AGE, Math.max(AGE_MIN, age - refuelAmount)), 2);
				//TODO feedback on log fueling?
			}
			
			return true;
		}
		else if(!burning && age < AGE_MAX && !world.isRainingAt(pos.up()))
		{
			if(OreDictUtil.isOre(OreDictUtil.stick, heldItemStack) || heldItem == Items.STICK)
			{
				//Do stick
				heldItemStack.shrink(1);
				if(world.rand.nextInt(ModConfig.server.miscellaneous.campfireStickIgniteChance)==0)
				{
					world.setBlockState(pos, state.withProperty(BURNING, true), 2);
				}
				
				return true;
			}
			else if(heldItem==Items.FLINT_AND_STEEL)
			{
				//Do flint and steel
				world.setBlockState(pos, state.withProperty(BURNING, true), 2);
				
				//Sound would normally go here but it's handled clientside instead
				
				heldItemStack.damageItem(1, player);
				return true;
			}
		}
		
		//Prevent block placement unless shift is held
		return true;
	}
	
	//UPDATE
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if(world.isRemote)
			return;
		
		int age = state.getValue(AGE);
		boolean burning = state.getValue(BURNING);
		
		if(burning)
		{
			if(world.isRainingAt(pos.up()))
			{
				//Extinguish, don't waste the logs though
				world.setBlockState(pos, state.withProperty(BURNING, false), 2);
				effectExtinguish(world,pos);
			}
			else if(rand.nextInt(ModConfig.server.miscellaneous.campfireDecayChance)==0)
			{
				age++;
				if(age>=AGE_MAX)
				{
					world.setBlockState(pos, state.withProperty(AGE, AGE_MAX).withProperty(BURNING, false), 2);
					effectExtinguish(world,pos);
				}
				else
				{
					world.setBlockState(pos, state.withProperty(AGE, age), 2);
				}
			}
		}
	}
	
	@Override
	public int tickRate(World world)
	{
		return TICK_RATE;
	}
	
	// STATE
	
	//Bit setup
	//A A A A A A A B
	
	//So annoying that there's only 8 bits to work with
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		boolean burning = (meta & 1) != 0;
		int age = meta >> 1;
		return this.getDefaultState().withProperty(AGE, age).withProperty(BURNING, burning);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return (state.getValue(AGE) << 1) | (state.getValue(BURNING)? 1 : 0);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {AGE, BURNING});
	}	
	
	//COLLISION
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return HITBOX;
	}
	
	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos)
	{
		return Block.NULL_AABB;
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
	{
		if(!world.isRemote && state.getValue(BURNING) && entity instanceof EntityLivingBase)
		{
			entity.setFire(1);
		}
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face)
	{
		return BlockFaceShape.UNDEFINED;
	}
	
	//LIGHT
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		if(state.getValue(BURNING))
		{
			//return (15 - state.getValue(AGE));
			return 15;
		}
		else
		{
			return 0;
		}
	}
	
	//DROPS
	
	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random)
	{ 
		return 0;
	}
	
	//RENDER
	
	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
	{
		super.randomDisplayTick(state, world, pos, rand);
		
		//So this function is called very strangely? Enchanting tables use this for the bookcase effect I think
		
		if(state.getValue(BURNING))
		{
			int age = state.getValue(AGE);
			float strength = 1.0f - ((float)age / (float)(AGE_MAX-AGE_MIN));
			
			if(rand.nextFloat() < strength)
			{
				int loop = rand.nextInt(6) + 1;
				for(int i=0;i<loop;i++)
				{
					createFlameParticle(world,pos,rand);
				}
			}
			
			if(rand.nextInt(30)==0)
			{
				world.playSound(0.5d + pos.getX(),0.5d + pos.getY(),0.5d + pos.getZ(), SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 0.5f, 1.0f, false);
			}
		}
	}
	
	private void effectExtinguish(World world, BlockPos pos)
	{
		SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH);
		world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, 0.5d +  pos.getX(), 0.25d + pos.getY(), 0.5d + pos.getZ(), 0.0d, 0.1d, 0.0d);
		
		world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, 0.75d +  pos.getX(), 0.25d + pos.getY(), 0.75d + pos.getZ(), 0.0d, 0.05d, 0.0d);
		world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, 0.75d +  pos.getX(), 0.25d + pos.getY(), 0.25d + pos.getZ(), 0.0d, 0.05d, 0.0d);
		world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, 0.25d +  pos.getX(), 0.25d + pos.getY(), 0.75d + pos.getZ(), 0.0d, 0.05d, 0.0d);
		world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, 0.25d +  pos.getX(), 0.25d + pos.getY(), 0.25d + pos.getZ(), 0.0d, 0.05d, 0.0d);
	}
	
	private void createFlameParticle(World world, BlockPos pos, Random rand)
	{
		/*
		//Box shape
		double xOffset = rand.nextDouble() * 0.8d + 0.1d;
		double zOffset = rand.nextDouble() * 0.8d + 0.1d;
		double yOffset = rand.nextDouble() * 0.2d + 0.35d;
		*/
		
		//Pyramid shape
		double yOffset = rand.nextDouble() * 0.35d + 0.35d;
		double offAdj = (0.7d - yOffset) * 2.28571428d;
		
		double xOffset = (rand.nextDouble() - 0.5d) * offAdj + 0.5d;
		double zOffset = (rand.nextDouble() - 0.5d) * offAdj + 0.5d;

		
		
		world.spawnParticle(EnumParticleTypes.FLAME, xOffset + pos.getX(), yOffset + pos.getY(), zOffset + pos.getZ(), 0.0d, rand.nextDouble() * 0.015d, 0.0d);
	}
	
	@Override
	public boolean isFullCube(IBlockState state) { return false; }
	@Override
	public boolean isOpaqueCube(IBlockState state){ return false; }
	
	@SideOnly(Side.CLIENT)
	@Override
  	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public IProperty[] getIgnoredProperties()
	{
		return ignoredProperties;
	}
}
