package com.charles445.simpledifficulty.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectUtil
{
	public static Method findMethod(Class clazz, String name) throws Exception
	{
		//Desc is not specified
		for(Method m : clazz.getDeclaredMethods())
		{
			if(m.getName().equals(name))
			{
				m.setAccessible(true);
				return m;
			}
		}
		
		throw new NoSuchMethodException(name);
	}
	
	public static Method findMethodAny(Class clazz, String nameA, String nameB, Class... params) throws Exception
	{
		try
		{
			return findMethod(clazz, nameA, params);
		}
		catch(Exception e)
		{
			return findMethod(clazz, nameB, params);
		}
	}
	
	public static Method findMethod(Class clazz, String name, Class... params) throws Exception
	{
		Method m = clazz.getDeclaredMethod(name, params);
		m.setAccessible(true);
		return m;
	}
	
	public static Field findField(Class clazz, String name) throws Exception
	{
		Field f = clazz.getDeclaredField(name);
		f.setAccessible(true);
		return f;
	}
	
	public static Field findFieldAny(Class clazz, String nameA, String nameB) throws Exception
	{
		try
		{
			return findField(clazz, nameA);
		}
		catch(Exception e)
		{
			return findField(clazz, nameB);
		}
	}
	
	public static Field[] findFields(Class clazz, String... names) throws Exception
	{
		Field[] fields = new Field[names.length];
		
		for(int i = 0; i < fields.length; i++)
		{
			fields[i] = findField(clazz,names[i]);
		}
		
		return fields;
	}
	
	public static Object[] createCastedArray(Class clazz, Object[] inputArray)
	{
		Class arrayClazz = Array.newInstance(clazz, 0).getClass();	
		return Arrays.copyOf(inputArray, inputArray.length, arrayClazz);
	}
}
