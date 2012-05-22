package com.quickwebframework.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MapperObject {
	protected Object orginObject;
	private Class<?> orginObjectClass;
	// 名称与方法列表映射
	private Map<String, List<Method>> nameMethodListMap;

	public MapperObject(Object orginObject) {
		this.orginObject = orginObject;
		orginObjectClass = orginObject.getClass();

		nameMethodListMap = new HashMap<String, List<Method>>();
		Method[] methods = orginObjectClass.getMethods();
		for (Method method : methods) {
			String methodName = method.getName();

			List<Method> methodList = null;
			if (nameMethodListMap.containsKey(methodName)) {
				methodList = nameMethodListMap.get(methodName);
			} else {
				methodList = new ArrayList<Method>();
				nameMethodListMap.put(methodName, methodList);
			}
			methodList.add(method);
		}
	}

	public class NullObjectClass {
	}

	public Object invokeOrginObjectMethod(Object... args) {

		StackTraceElement stackTraceElement = Thread.currentThread()
				.getStackTrace()[2];

		String methodName = stackTraceElement.getMethodName();

		if (!nameMethodListMap.containsKey(methodName)) {
			throw new RuntimeException("在[" + orginObjectClass + "]类中未找到方法:"
					+ methodName);
		}

		// 初始化传入参数类型列表
		List<Class<?>> parameterTypeList = new ArrayList<Class<?>>();
		for (Object arg : args) {
			if (arg == null) {
				parameterTypeList.add(NullObjectClass.class);
				continue;
			}
			Class<?> argClass = arg.getClass();
			if (argClass == Byte.class)
				argClass = byte.class;
			else if (argClass == Character.class)
				argClass = char.class;
			else if (argClass == Double.class)
				argClass = double.class;
			else if (argClass == Float.class)
				argClass = float.class;
			else if (argClass == Integer.class)
				argClass = int.class;
			else if (argClass == Long.class)
				argClass = long.class;
			else if (argClass == Short.class)
				argClass = short.class;
			else if (argClass == Boolean.class)
				argClass = boolean.class;
			parameterTypeList.add(argClass);
		}

		Method theMethod = null;

		// 方法名为[methodName]的方法列表
		List<Method> methodList = nameMethodListMap.get(methodName);
		for (Method method : methodList) {
			Class<?>[] typeParameters = method.getParameterTypes();
			// 如果参数数量不匹配
			if (parameterTypeList.size() != typeParameters.length) {
				continue;
			}
			boolean isTheMethod = true;
			for (int i = 0; i <= parameterTypeList.size() - 1; i++) {
				// 调用的方法参数类型
				Class<?> iType = parameterTypeList.get(i);
				// 原类型方法参数类型
				Class<?> oType = typeParameters[i];

				// 类型不同且无派生关系，且不是NullObjectClass类
				if (iType != oType && !oType.isAssignableFrom(iType)
						&& iType != NullObjectClass.class) {
					isTheMethod = false;
					continue;
				}
			}
			if (isTheMethod) {
				theMethod = method;
				break;
			}
		}

		try {
			if (theMethod == null)
				throw new Exception("未找到方法:" + methodName);
			return theMethod.invoke(orginObject, args);
		} catch (Exception ex) {
			System.out.println("Class:" + orginObjectClass.getName()
					+ "     method:" + methodName + "    parameterTypeList:"
					+ parameterTypeList);
			throw new RuntimeException(ex);
		}
	}
}
