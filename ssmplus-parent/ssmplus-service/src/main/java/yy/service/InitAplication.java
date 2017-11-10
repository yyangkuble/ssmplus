package yy.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.alibaba.fastjson.JSON;

import tk.mybatis.mapper.common.Mapper;
import yy.service.example.ExampleModel;
import yy.service.example.YyExample;
import yy.service.intercepters.YyEntityIntercepter;
import yy.service.intercepters.YySqlIdInterceptor;

public class InitAplication implements ApplicationListener<ContextRefreshedEvent> {
	//Map<pojo的简写名,pojo的class类型>
	public static Map<String, Class<?>> pojoMap = new HashMap<String, Class<?>>();
	public static Map<Class<?>, Mapper<?>> mapperMap = new HashMap<Class<?>, Mapper<?>>();
	public static Map<Class<?>, YyEntityIntercepter<?>> entityIntercepterMap = new HashMap<Class<?>, YyEntityIntercepter<?>>();
	public static Map<String, YySqlIdInterceptor> sqlIdIntercepterMap = new HashMap<String, YySqlIdInterceptor>();
	public static Map<String, Field> idFieldMap=new HashMap<String, Field>();
	public static Map<String, ExampleModel> exampleMethodMap=new HashMap<String, ExampleModel>();
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// TODO Auto-generated method stub
		System.out.println("以下是sqlId接口,欢迎使用");
		System.out.println("	根据sqlId增删改: exc/{sqlid}");
		System.out.println("	根据sqlId查询一行: findOne/{sqlid}");
		System.out.println("	根据sqlId查询列表: findList/{sqlid}");
		System.out.println("	/*selectList接口如果,传入page,size 参数,会自动完成分页功能,返回分页数据 */\n\n");
		ApplicationContext context = event.getApplicationContext();
		Map<String, Mapper> mapperBeans = context.getBeansOfType(Mapper.class);
		for (String key : mapperBeans.keySet()) {
			//在spring容器中,获取mybatis自动生成的实现类
			Mapper<?> mapper = mapperBeans.get(key);
			//获取我们自己的dao接口
			Class<?> myMapperInterface = mapper.getClass().getInterfaces()[0];
			//获取tk完成的通用Mapper接口
			Type tkMapperInterfaceType = myMapperInterface.getGenericInterfaces()[0];
			//获取tk完成的通用Mapper接口里面的pojo实体类
			Class<?> pojoClass = (Class<?>)((ParameterizedType)tkMapperInterfaceType).getActualTypeArguments()[0];
			Field field = getEntityId(pojoClass);
			if (field != null) {
				idFieldMap.put(pojoClass.getSimpleName(), field);
			}
			pojoMap.put(pojoClass.getSimpleName(), pojoClass);
			System.out.println(pojoClass.getSimpleName()+"自动生成以下接口,欢迎使用");
			System.out.println("	增加api: save/"+pojoClass.getSimpleName()+"               参数: 实体类"+pojoClass.getSimpleName()+"的所有字段");
			System.out.println("	增加api: save/"+pojoClass.getSimpleName()+"/withNull      参数: 实体类"+pojoClass.getSimpleName()+"的所有字段, 字段为空也受影响");
			System.out.println("	更新api: update/"+pojoClass.getSimpleName()+"             参数: 实体类"+pojoClass.getSimpleName()+"的所有字段,根据主键进行修改");
			System.out.println("	更新api: update/"+pojoClass.getSimpleName()+"/withNull    参数: 实体类"+pojoClass.getSimpleName()+"的所有字段,根据主键进行修改, 字段为空也受影响");
			System.out.println("	删除api: delete/"+pojoClass.getSimpleName()+"             参数: 实体类"+pojoClass.getSimpleName()+"的所有字段,会自动拼接where条件,进行删除,根据id删除只需传入实体类id即可");
			System.out.println("	删除api: deletebyid/"+pojoClass.getSimpleName()+"         参数: id固定不能缺少,例如id=1, 也支持多个id删除, 例如ids=1,2 中间用,号隔开");
			System.out.println("	查询api: find/"+pojoClass.getSimpleName()+"/one           参数: 实体类"+pojoClass.getSimpleName()+"的所有字段,会自动拼接where条件,进行查找,根据id查询只需传入实体类id即可,必须保证返回的是一行,不然会报错");
			System.out.println("	查询api: findbyid/"+pojoClass.getSimpleName()+"           参数: id固定不能缺少,例如id=1");
			System.out.println("	查询api: find/"+pojoClass.getSimpleName()+"/list          参数: 实体类"+pojoClass.getSimpleName()+"的所有字段,会自动拼接where条件,进行查找,如需分页,可传入page和size参数,会自动返回分页数据");
			System.out.println("	查询api: find/"+pojoClass.getSimpleName()+"/count         参数: 实体类"+pojoClass.getSimpleName()+"的所有字段,会自动拼接where条件,进行查找");
			System.out.println("	查询api: findByExample/"+pojoClass.getSimpleName()+"      参数: 实体类"+pojoClass.getSimpleName()+"的所有字段,继承YyExample接口,并创建void方法,手工编写example条件\n");
			mapperMap.put(pojoClass, mapper);
		}
		//添加entityInterceptor
		Map<String, YyEntityIntercepter> entityIntercipters = context.getBeansOfType(YyEntityIntercepter.class);
		for (YyEntityIntercepter entityIntercepter : entityIntercipters.values()) {
			Class<?> pojoClass = (Class<?>) ((ParameterizedType)entityIntercepter.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			entityIntercepterMap.put(pojoClass, entityIntercepter);
			System.out.println("拦截器:"+entityIntercepter.getClass()+"已生效");
		}
		
		//添加SqlExcInterceptor
		Map<String, YySqlIdInterceptor> sqlIdInterceptorForFinds = context.getBeansOfType(YySqlIdInterceptor.class);
		for (YySqlIdInterceptor sqlIdInterceptorForFind : sqlIdInterceptorForFinds.values()) {
			String sqlMapperId=null;
			YySqlId sqlId = sqlIdInterceptorForFind.getClass().getAnnotation(YySqlId.class);
			if (sqlId != null) {
				sqlMapperId = sqlId.value();
			}
			if (sqlId==null) {//如果没有sqlid注解,用类名全小写作为sqlid
				sqlMapperId=sqlIdInterceptorForFind.getClass().getSimpleName();
			}
			System.out.println("拦截器:"+sqlIdInterceptorForFind.getClass()+"已生效,sqlId: "+sqlMapperId);
			sqlIdIntercepterMap.put(sqlMapperId.toLowerCase(), sqlIdInterceptorForFind);
		}
		//注册example方法
		Map<String, YyExample> exampleService = context.getBeansOfType(YyExample.class);
		for (YyExample example : exampleService.values()) {
			Method[] methods = example.getClass().getMethods();
			for (Method method : methods) {
				Class<?>[] parameterTypes = method.getParameterTypes();
				if (parameterTypes.length>0) {//如果有参数
					String entityName="";
					boolean isExampleMethod = false;
					Class<?> exampleClass=null;
					String[] parameterTypesStr=new String[parameterTypes.length];
					for(int i=0;i<parameterTypes.length;i++){
						Class<?> class1 = parameterTypes[i];
						if (class1.getSimpleName().contains("Example")) {
							isExampleMethod=true;
							parameterTypesStr[i]="example";
							entityName=class1.getSimpleName().substring(0, class1.getSimpleName().length()-7);
							exampleClass=class1;
						}else if (class1.isAssignableFrom(Map.class)) {
							parameterTypesStr[i]="map";
						}else{
							parameterTypesStr[i]="entity";
							entityName=class1.getSimpleName();//覆盖截取的实体类名
						}
					}
					if (isExampleMethod) {//如果是一个实体类example方法
						ExampleModel exampleModel = new ExampleModel();
						exampleModel.setExampleService(example);
						exampleModel.setMethod(method);
						exampleModel.setParameterTypesStr(parameterTypesStr);
						exampleModel.setExampleClass(exampleClass);
						exampleMethodMap.put(entityName, exampleModel);
					}
				}
			}
		}
	}
	/**
	 * 获取pojo类的主键属性
	 * @param pojoClass
	 */
	public Field getEntityId(Class<?> pojoClass) {
		Field[] fields = pojoClass.getDeclaredFields();
		for (Field field : fields) {
			Annotation[] annotations = field.getDeclaredAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof Id) {
					return field;
				}
			}
		}
		return null;
	}
	
}
