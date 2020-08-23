package com.charles445.simpledifficulty.client.render;

import com.charles445.simpledifficulty.tileentity.TileEntitySpit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;

public class RenderSpit extends TileEntitySpecialRenderer<TileEntitySpit>
{
	private final RenderItem itemRenderer;
	
	private boolean initialized = false;
	
	//Current padding is 0.3
	private final double itemBoundStart = 0.3;
	private final double itemBoundSize = 1.4142 - itemBoundStart - itemBoundStart;
	
	//Figuring out the math
	//itemBoundStart of 0.3 has best results with 0.28 between each on 3 items (0.86)
	//itemBoundStart of 0.2 has best results with 0.42 between each on 3 items (1.04)
	//itemBoundStart of 0.1 has best results with 0.565 between each on 3 items (1.23)
	
	
	public RenderSpit()
	{
		super();
		this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
	}
	
	//
	
	@Override
	public void render(TileEntitySpit te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		GlStateManager.pushMatrix();
		
		//GlStateManager.translate(x + ModConfig.client.xx, y + ModConfig.client.yy, z + ModConfig.client.zz);
		GlStateManager.translate(x, y, z);
		
		//GlStateManager.rotate((float)ModConfig.client.ra, (float)ModConfig.client.rx, (float)ModConfig.client.ry, (float)ModConfig.client.rz);
		GlStateManager.rotate(45.0f, 0.0f, 5.0f, 0.0f); //Sets the origin a bit above it, then rotates
		
		int slots = te.items.getSlots();
		
		//Computers are fast right?
		double separationAmt = itemBoundSize / (double)(slots - 1);
		
		for(int i=0; i < slots; i++)
		{
			ItemStack stack = te.items.getStackInSlot(i);
			if(!stack.isEmpty())
			{
				GlStateManager.pushMatrix();
				
				//GlStateManager.translate(i * ModConfig.client.px, i * ModConfig.client.py, i * ModConfig.client.pz);
				GlStateManager.translate(0.0d, 0.0d, (i * separationAmt) + itemBoundStart);
				
				//TODO could rotate this matrix and have it actually spin on the roast, that'd be silly
				
				itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
				
				GlStateManager.popMatrix();
			}
		}
		
		
		GlStateManager.popMatrix();
	}
}
