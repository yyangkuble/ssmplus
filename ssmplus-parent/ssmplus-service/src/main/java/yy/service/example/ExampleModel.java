package yy.service.example;

import java.lang.reflect.Method;

public class ExampleModel {
	Method method;
	String[] parameterTypesStr;
	Object exampleService;
	Class<?> exampleClass;
	
	public Class<?> getExampleClass() {
		return exampleClass;
	}
	public void setExampleClass(Class<?> exampleClass) {
		this.exampleClass = exampleClass;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public String[] getParameterTypesStr() {
		return parameterTypesStr;
	}
	public void setParameterTypesStr(String[] parameterTypesStr) {
		this.parameterTypesStr = parameterTypesStr;
	}
	public Object getExampleService() {
		return exampleService;
	}
	public void setExampleService(Object exampleService) {
		this.exampleService = exampleService;
	}
	
	
}
