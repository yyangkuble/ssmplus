package yy.service;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class EntityManager {
	
	public static Object getEntityByMap(String entityName,Map<String, Object> parameter) {
		try {
			Class<?> entityClass =  InitAplication.pojoMap.get(entityName);
			Object entity = entityClass.newInstance();
			Field[] fields = entityClass.getDeclaredFields();
			for (Field field : fields) {
				String fieldName = field.getName();
				Object mapValue = parameter.get(fieldName);
				if (mapValue != null) {//如果有值
					field.setAccessible(true);
					field.set(entity, caseType(field.getType(), mapValue));
				}
			}
			return  entity;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	private static <T> T caseType(Class<T> t,Object object) throws ParseException {
		if (object==null) {
			return null;
		}else {
			if (t.isAssignableFrom(String.class)) {
				return t.cast(String.valueOf(object)) ;
			}else if(Integer.class.getName().equals(t.getName()) && object != null && !object.equals("")){
				return t.cast(Integer.valueOf(String.valueOf(object)));
			}else if(Double.class.getName().equals(t.getName())  && object != null && !object.equals("")){
				return t.cast(Double.parseDouble(String.valueOf(object)));
			}else if(Float.class.getName().equals(t.getName()) && object != null && !object.equals("")){
				return t.cast(Float.parseFloat(String.valueOf(object)));
			}else if(Long.class.getName().equals(t.getName())  && object != null && !object.equals("")){
				return t.cast(Long.parseLong(String.valueOf(object)));
			}else if(Date.class.getName().equals(t.getName())  && object != null && !object.equals("")){
				return t.cast(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(object)));
			}else if(Timestamp.class.getName().equals(t.getName()) && object != null && !object.equals("")){
				return t.cast(new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH-mm:ss").parse(String.valueOf(object)).getTime()));
			}else {
				System.out.println(t.getName()+"转换失败");
				return null;
			}
		}
	}
}
