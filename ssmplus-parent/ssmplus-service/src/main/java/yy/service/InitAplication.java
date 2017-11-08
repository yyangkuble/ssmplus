package yy.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.alibaba.fastjson.JSON;

import tk.mybatis.mapper.common.Mapper;
import yy.service.intercepters.YyEntityIntercepter;
import yy.service.intercepters.YySqlIdInterceptor;

public class InitAplication implements ApplicationListener<ContextRefreshedEvent> {
	//Map<pojo的简写名,pojo的class类型>
	public static Map<String, Class<?>> pojoMap = new HashMap<String, Class<?>>();
	public static Map<Class<?>, Mapper<?>> mapperMap = new HashMap<Class<?>, Mapper<?>>();
	public static Map<Class<?>, YyEntityIntercepter<?>> entityIntercepterMap = new HashMap<Class<?>, YyEntityIntercepter<?>>();
	public static Map<String, YySqlIdInterceptor> sqlIdIntercepterMap = new HashMap<String, YySqlIdInterceptor>();
	
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
			pojoMap.put(pojoClass.getSimpleName(), pojoClass);
			System.out.println(pojoClass.getSimpleName()+"自动生成以下接口,欢迎使用");
			System.out.println("	增加api: save/"+pojoClass.getSimpleName()+"               参数: 实体类"+pojoClass.getSimpleName()+"的所有字段");
			System.out.println("	增加api: save/"+pojoClass.getSimpleName()+"/withNull      参数: 实体类"+pojoClass.getSimpleName()+"的所有字段, 字段为空也受影响");
			System.out.println("	更新api: update/"+pojoClass.getSimpleName()+"             参数: 实体类"+pojoClass.getSimpleName()+"的所有字段,根据主键进行修改");
			System.out.println("	更新api: update/"+pojoClass.getSimpleName()+"/withNull    参数: 实体类"+pojoClass.getSimpleName()+"的所有字段,根据主键进行修改, 字段为空也受影响");
			System.out.println("	删除api: delete/"+pojoClass.getSimpleName()+"             参数: 实体类"+pojoClass.getSimpleName()+"的所有字段,会自动拼接where条件,进行删除,根据id删除只需传入实体类id即可");
			System.out.println("	查询api: find/"+pojoClass.getSimpleName()+"/one           参数: 实体类"+pojoClass.getSimpleName()+"的所有字段,会自动拼接where条件,进行查找,根据id查询只需传入实体类id即可,必须保证返回的是一行,不然会报错");
			System.out.println("	查询api: find/"+pojoClass.getSimpleName()+"/list          参数: 实体类"+pojoClass.getSimpleName()+"的所有字段,会自动拼接where条件,进行查找,如需分页,可传入page和size参数,会自动返回分页数据");
			System.out.println("	查询api: find/"+pojoClass.getSimpleName()+"/count         参数: 实体类"+pojoClass.getSimpleName()+"的所有字段,会自动拼接where条件,进行查找,返回{'count':100}格式.\n");
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
	}
	 
	
}
