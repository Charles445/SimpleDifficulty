package com.charles445.simpledifficulty.util;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class RenderUtil
{
	//
	// Essentially a static version of net.minecraft.client.gui.Gui.drawTexturedModalRect
	//
	
	public static void drawTexturedModalRect(float x, float y, int texX, int texY, int width, int height)
	{
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		double z = 0.0D;
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x, y + height, z)
			.tex((texX * f), (texY + height) * f1).endVertex();
		bufferbuilder.pos((x + width), y + height, z)
			.tex((texX + width) * f, (texY + height) * f1).endVertex();
		bufferbuilder.pos((x + width), y, z)
			.tex((texX + width) * f,(texY * f1)).endVertex();
		bufferbuilder.pos(x, y, z)
			.tex((texX * f), (texY * f1)).endVertex();
		tessellator.draw();
	}
}
