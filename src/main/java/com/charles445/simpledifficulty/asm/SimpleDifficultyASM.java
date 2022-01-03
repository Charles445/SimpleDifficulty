package com.charles445.simpledifficulty.asm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import com.charles445.simpledifficulty.asm.helper.ASMHelper;

import net.minecraft.launchwrapper.IClassTransformer;

public class SimpleDifficultyASM implements IClassTransformer
{
	//This is what happens when you're too stubborn to add hard dependencies
	
	Logger logger = LogManager.getLogger("SimpleDifficultyASM");
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		if(transformedName.startsWith("com.charles445.simpledifficulty.compat.mod."))
		{
			return redirectShadowed(basicClass);
		}
		
		return basicClass;
	}
	
	//TODO Add things as needed
	public byte[] redirectShadowed(byte[] basicClass)
	{
		ClassNode clazzNode = ASMHelper.readClassFromBytes(basicClass);
		
		List<AnnotationNode> annotations = clazzNode.visibleAnnotations;
		
		if(annotations == null)
			return basicClass;
		
		boolean hasAnnotation = false;
		
		for(AnnotationNode annotation : annotations)
		{
			if(annotation != null && "Lcom/charles445/simpledifficulty/compat/HasShadows;".equals(annotation.desc))
			{
				logger.info("Found class with shadows: "+clazzNode.name);
				
				if(clazzNode.interfaces != null)
				{
					List<String> readdedInterfaces = new ArrayList<>();
					Iterator<String> it = clazzNode.interfaces.iterator();
					while(it.hasNext())
					{
						readdedInterfaces.add(swapOwner(it.next()));
						it.remove();
					}
					
					for(String iface : readdedInterfaces)
					{
						clazzNode.interfaces.add(iface);
					}
				}
				
				if(clazzNode.fields != null)
				{
					for(FieldNode fNode : clazzNode.fields)
					{
						fNode.desc = swapDesc(fNode.desc);
					}
				}
				
				if(clazzNode.methods != null)
				{
					for(MethodNode mNode : clazzNode.methods)
					{
						mNode.desc = swapDesc(mNode.desc);
						if(mNode.localVariables != null)
						{
							for(LocalVariableNode lvn : mNode.localVariables)
							{
								lvn.desc = swapDesc(lvn.desc);
							}
						}
						
						if(mNode.instructions != null)
						{
							AbstractInsnNode anchor = mNode.instructions.getFirst();
							while(anchor != null)
							{
								shadowInsnNode(anchor);
								anchor = anchor.getNext();
							}
						}
					}
				}
				
				logger.info("Rewriting class with shadows: "+clazzNode.name);
				return ASMHelper.writeClassToBytes(clazzNode, ClassWriter.COMPUTE_MAXS);
			}
		}
		return basicClass;
	}

	//TODO Add things as needed
	public void shadowInsnNode(AbstractInsnNode anchor)
	{
		int type = anchor.getType();
		if(type == AbstractInsnNode.FIELD_INSN)
		{
			FieldInsnNode node = (FieldInsnNode)anchor;
			node.owner = swapOwner(node.owner);
			node.desc = swapDesc(node.desc);
		}
		else if(type == AbstractInsnNode.METHOD_INSN)
		{
			MethodInsnNode node = (MethodInsnNode)anchor;
			node.owner = swapOwner(node.owner);
			node.desc = swapDesc(node.desc);
		}
		else if(type == AbstractInsnNode.TYPE_INSN)
		{
			TypeInsnNode node = (TypeInsnNode)anchor;
			//TODO pretty sure this is not a desc but an owner, verify
			node.desc = swapOwner(node.desc);
		}
		else if(type == AbstractInsnNode.FRAME)
		{
			//Oh boy
			FrameNode frame = (FrameNode)anchor;
			
			if(frame.local != null)
			{
				List<Object> replaceList = new ArrayList<>();
				
				Iterator<Object> it = replaceList.iterator();
				
				while(it.hasNext())
				{
					for(int i=0; i<frame.local.size(); i++)
					{
						Object o = frame.local.get(i);
						
						if(o instanceof String)
						{
							replaceList.add(swapOwner((String)o));
						}
						else
						{
							replaceList.add(o);
						}
						
						it.remove();
					}
				}
				
				for(Object o : replaceList)
				{
					frame.local.add(o);
				}
			}
			
			if(frame.stack != null)
			{
				List<Object> replaceList = new ArrayList<>();
				
				Iterator<Object> it = replaceList.iterator();
				
				while(it.hasNext())
				{
					for(int i=0; i<frame.stack.size(); i++)
					{
						Object o = frame.stack.get(i);
						
						if(o instanceof String)
						{
							replaceList.add(swapOwner((String)o));
						}
						else
						{
							replaceList.add(o);
						}
						
						it.remove();
					}
				}
				
				for(Object o : replaceList)
				{
					frame.stack.add(o);
				}
			}
		}
	}
	
	@Nullable
	private String swapOwner(String owner)
	{
		if(owner == null)
			return null;
		
		String swap = ShadowMap.ownerMap.get(owner);
		if(swap != null)
			return swap;
		
		return owner;
	}

	@Nullable
	private String swapDesc(String desc)
	{
		if(desc == null)
			return null;
		
		String result = desc;
		for(Map.Entry<String, String> entry : ShadowMap.descMap.entrySet())
		{
			result = result.replace(entry.getKey(), entry.getValue());
		}
		
		return result;
	}
}
