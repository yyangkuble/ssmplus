package yy.service.example;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import yy.service.InitAplication;

public class ExampleInvoke {
	
	public static Object invoke(Map<String, Object> parameter,Object entity) {
		try {
			ExampleModel exampleModel = InitAplication.exampleMethodMap.get(entity.getClass().getSimpleName());
			Method method = exampleModel.getMethod();
			String[] parameterTypesStr = exampleModel.getParameterTypesStr();
			Object[] objects = new Object[parameterTypesStr.length];
			Object exampleObj=exampleModel.getExampleClass().newInstance();
			for(int i=0;i<parameterTypesStr.length;i++){
				String type = parameterTypesStr[i];
				if (type.equals("example")) {
					objects[i]=exampleObj;
				}else if (type.equals("entity")) {
					objects[i]=entity;
				}else if (type.equals("map")) {
					objects[i]=parameter;
				}
			}
			method.setAccessible(true);
			method.invoke(exampleModel.getExampleService(), objects);
			return exampleObj;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
