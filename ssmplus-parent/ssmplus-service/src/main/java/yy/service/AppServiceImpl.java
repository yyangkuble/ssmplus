package yy.service;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageRowBounds;
import tk.mybatis.mapper.common.Mapper;
import yy.entity.AppResult;
import yy.entity.PageResult;
import yy.service.example.ExampleInvoke;
import yy.service.intercepters.YyEntityIntercepter;
import yy.service.intercepters.YySqlIdInterceptor;
import yy.service.interfaces.AppService;

@Service
public class AppServiceImpl implements AppService {
	@Resource
	SqlSessionFactory sessionFactory ;
	
	
	public Object findList(String sqlId,Map<String, Object> parameter) {
		SqlSession session = sessionFactory.openSession();
		YySqlIdInterceptor yySqlIdInterceptor = InitAplication.sqlIdIntercepterMap.get(sqlId.toLowerCase());
		if (yySqlIdInterceptor==null) {
			yySqlIdInterceptor = InitAplication.sqlIdIntercepterMap.get(sqlId.substring(sqlId.lastIndexOf(".")+1).toLowerCase());
		}
		if (yySqlIdInterceptor != null) {
			yySqlIdInterceptor.beforeFind(parameter);
		}
		Long page=null;
		Integer size=null;
		Object pageObj = parameter.get("page");
		Object sizeObj = parameter.get("size");
		if (pageObj != null && !pageObj.equals("")) {
			page = Long.valueOf(String.valueOf(pageObj));
			if (sizeObj != null && !sizeObj.equals("")) {
				size = Integer.valueOf(String.valueOf(sizeObj));
			}else{
				size=10;
			}
			PageRowBounds rowBounds = new PageRowBounds((int) ((page-1)*size), size);
			List<Map<String, Object>> list = session.selectList(sqlId, parameter, rowBounds);
			PageResult pageResult = new PageResult();
			pageResult.setData(list);
			pageResult.setCurrentPage(page);
			pageResult.setSize(size);
			pageResult.setTotal(rowBounds.getTotal());
			pageResult.setPageCount(pageResult.getTotal()%size==0?pageResult.getTotal()/size:pageResult.getTotal()/size+1);
			if (yySqlIdInterceptor != null) {
				yySqlIdInterceptor.endFindList(parameter, pageResult);
			}
			return pageResult;
		}else{
			List<Map<String, Object>> list = session.selectList(sqlId, parameter);
			if (yySqlIdInterceptor != null) {
				yySqlIdInterceptor.endFindList(parameter, list);
			}
			return list;
		}
	}
	
	public Map<String, Object> findOne(String sqlId,Map<String, Object> parameter) {
		SqlSession session = sessionFactory.openSession();
		YySqlIdInterceptor yySqlIdInterceptor = InitAplication.sqlIdIntercepterMap.get(sqlId.toLowerCase());
		if (yySqlIdInterceptor==null) {
			yySqlIdInterceptor = InitAplication.sqlIdIntercepterMap.get(sqlId.substring(sqlId.lastIndexOf(".")+1).toLowerCase());
		}
		if (yySqlIdInterceptor != null) {
			yySqlIdInterceptor.beforeFind(parameter);
		}
		Map<String, Object> resultMap = session.selectOne(sqlId, parameter);
		if (yySqlIdInterceptor != null) {
			yySqlIdInterceptor.endFindOne(parameter, resultMap);
		}
		return resultMap;
	}
	
	
	public AppResult exc(String sqlId,Map<String, Object> parameter) {
		AppResult result = new AppResult();
		
		YySqlIdInterceptor yySqlIdInterceptor = InitAplication.sqlIdIntercepterMap.get(sqlId.toLowerCase());
		if (yySqlIdInterceptor==null) {
			yySqlIdInterceptor = InitAplication.sqlIdIntercepterMap.get(sqlId.substring(sqlId.lastIndexOf(".")+1).toLowerCase());
		}
		if (yySqlIdInterceptor != null) {
			yySqlIdInterceptor.beforeExc(result, parameter);
		}
		try {
			if (result.getStatus()) {
				SqlSession session = sessionFactory.openSession();
				int updateCount = session.update(sqlId, parameter);
				result.setData(updateCount);
				if (yySqlIdInterceptor != null) {
					yySqlIdInterceptor.endExc(result, parameter);
				}
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.setStatus(false);
		result.setData("操作失败");
		return result;
	}
	
	public AppResult insert(String entityName,Map<String, Object> parameter) {
		AppResult result = new AppResult();
		Object entity = EntityManager.getEntityByMap(entityName, parameter);
		YyEntityIntercepter<Object> entityIntercepter = (YyEntityIntercepter<Object>) InitAplication.entityIntercepterMap.get(entity.getClass());
		if (entityIntercepter != null) {//存储之前调用拦截器
			entityIntercepter.beforeSave(entity, parameter, result);
		}
		try {
			if (result.getStatus()) {
				Mapper mapper = InitAplication.mapperMap.get(entity.getClass());
				mapper.insert(entity);
				result.setData(entity);
				if (entityIntercepter != null) {//存储后调用拦截器
					entityIntercepter.endSave(entity, parameter, result);
				}
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.setStatus(false);
		result.setData("操作失败");
		return result;
	}
	
	public AppResult insertSelective(String entityName,Map<String, Object> parameter) {
		AppResult result = new AppResult();
		Object entity = EntityManager.getEntityByMap(entityName, parameter);
		
		YyEntityIntercepter<Object> entityIntercepter = (YyEntityIntercepter<Object>) InitAplication.entityIntercepterMap.get(entity.getClass());
		if (entityIntercepter != null) {//存储之前调用拦截器
			entityIntercepter.beforeSave(entity, parameter, result);
		}
		try {
			if (result.getStatus()) {//如果拦截器继续运行
				Mapper mapper =  InitAplication.mapperMap.get(entity.getClass());
				int insertCount = mapper.insertSelective(entity);
				result.setData(entity);
				if (entityIntercepter != null) {//存储后调用拦截器
					entityIntercepter.endSave(entity, parameter, result);
				}
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.setStatus(false);
		result.setData("操作失败");
		return result;
	}
	
	public AppResult updateByPrimaryKey(String entityName,Map<String, Object> parameter) {
		AppResult result = new AppResult();
		Object entity = EntityManager.getEntityByMap(entityName, parameter);
		
		YyEntityIntercepter<Object> entityIntercepter = (YyEntityIntercepter<Object>) InitAplication.entityIntercepterMap.get(entity.getClass());
		if (entityIntercepter != null) {//存储之前调用拦截器
			entityIntercepter.beforeUpdate(entity, parameter, result);
		}
		try {
			if (result.getStatus()) {
				Mapper mapper = InitAplication.mapperMap.get(entity.getClass());
				int updateCount = mapper.updateByPrimaryKey(entity);
				result.setData(updateCount);
				if (entityIntercepter != null) {//存储后调用拦截器
					entityIntercepter.endUpdate(entity, parameter, result);
				}
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.setStatus(false);
		result.setData("操作失败");
		return result;
	}
	
	
	public AppResult updateByPrimaryKeySelective(String entityName,Map<String, Object> parameter) {
		AppResult result = new AppResult();
		Object entity = EntityManager.getEntityByMap(entityName, parameter);
		
		YyEntityIntercepter<Object> entityIntercepter = (YyEntityIntercepter<Object>) InitAplication.entityIntercepterMap.get(entity.getClass());
		if (entityIntercepter != null) {//存储之前调用拦截器
			entityIntercepter.beforeUpdate(entity, parameter, result);
		}
		try {
			if (result.getStatus()) {
				Mapper mapper = InitAplication.mapperMap.get(entity.getClass());
				int updateCount = mapper.updateByPrimaryKeySelective(entity);
				result.setData(updateCount);
				if (entityIntercepter != null) {//存储后调用拦截器
					entityIntercepter.endUpdate(entity, parameter, result);
				}
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.setStatus(false);
		result.setData("操作失败");
		return result;
	}
	
	public AppResult delete(String entityName,Map<String, Object> parameter) {
		AppResult result = new AppResult();
		Object entity = EntityManager.getEntityByMap(entityName, parameter);
		
		YyEntityIntercepter<Object> entityIntercepter = (YyEntityIntercepter<Object>) InitAplication.entityIntercepterMap.get(entity.getClass());
		if (entityIntercepter != null) {//存储之前调用拦截器
			entityIntercepter.beforeDelete(entity, parameter, result);
		}
		try {
			if (result.getStatus()) {
				Mapper mapper = InitAplication.mapperMap.get(entity.getClass());
				int deleteCount = mapper.delete(entity);
				result.setData(deleteCount);
				if (entityIntercepter != null) {//存储后调用拦截器
					entityIntercepter.endDelete(entity, parameter, result);
				}
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.setStatus(false);
		result.setData("操作失败");
		return result;
	}
	
	public <T> Object select(String entityName,Map<String, Object> parameter) {
		T entity = (T) EntityManager.getEntityByMap(entityName, parameter);
		Mapper<T> mapper = (Mapper<T>) InitAplication.mapperMap.get(entity.getClass());
		
		YyEntityIntercepter<Object> entityIntercepter = (YyEntityIntercepter<Object>) InitAplication.entityIntercepterMap.get(entity.getClass());
		if (entityIntercepter != null) {//存储之前调用拦截器
			entityIntercepter.beforeFindList(entity, parameter);
		}
		Long page=null;
		Integer size=null;
		Object pageObj = parameter.get("page");
		Object sizeObj = parameter.get("size");
		if (pageObj != null && !pageObj.equals("")) {
			page = Long.valueOf(String.valueOf(pageObj));
			if (sizeObj != null && !sizeObj.equals("")) {
				size = Integer.valueOf(String.valueOf(sizeObj));
			}else{
				size=10;
			}
			PageRowBounds rowBounds = new PageRowBounds((int) ((page-1)*size), size);
			List<T> list = mapper.selectByRowBounds(entity, rowBounds);
			PageResult pageResult = new PageResult();
			pageResult.setData(list);
			pageResult.setCurrentPage(page);
			pageResult.setSize(size);
			pageResult.setTotal(rowBounds.getTotal());
			pageResult.setPageCount(pageResult.getTotal()%size==0?pageResult.getTotal()/size:pageResult.getTotal()/size+1);
			if (entityIntercepter != null) {//存储后调用拦截器
				entityIntercepter.endFindList(entity, parameter, pageResult);
			}
			return pageResult;
		}else {
			List list = mapper.select(entity);
			if (entityIntercepter != null) {//存储后调用拦截器
				entityIntercepter.endFindList(entity, parameter, list);
			}
			return list;
		}
	}
	
	
	public <T> Integer selectCount(String entityName,Map<String, Object> parameter) {
		T entity = (T) EntityManager.getEntityByMap(entityName, parameter);
		Mapper<T> mapper = (Mapper<T>) InitAplication.mapperMap.get(entity.getClass());
		
		YyEntityIntercepter<Object> entityIntercepter = (YyEntityIntercepter<Object>) InitAplication.entityIntercepterMap.get(entity.getClass());
		if (entityIntercepter != null) {//存储之前调用拦截器
			entityIntercepter.beforeFindCount(entity, parameter);
		}
		int selectCount = mapper.selectCount(entity);
		if (entityIntercepter != null) {//存储后调用拦截器
			entityIntercepter.endFindCount(entity, parameter, selectCount);
		}
		return selectCount;
	}
	
	public <T> T selectOne(String entityName,Map<String, Object> parameter) {
		T entity = (T) EntityManager.getEntityByMap(entityName, parameter);
		Mapper<T> mapper = (Mapper<T>) InitAplication.mapperMap.get(entity.getClass());
		
		YyEntityIntercepter<Object> entityIntercepter = (YyEntityIntercepter<Object>) InitAplication.entityIntercepterMap.get(entity.getClass());
		if (entityIntercepter != null) {//存储之前调用拦截器
			entityIntercepter.beforeFindOne(entity, parameter);
		}
		T resultEntity = mapper.selectOne(entity);
		if (entityIntercepter != null) {//存储后调用拦截器
			entityIntercepter.endFindOne(entity, parameter, resultEntity);
		}
		return resultEntity;
	}

	public AppResult deletebyid(String entityName, Map<String, Object> parameter) {
		AppResult result = new AppResult();
		Class<?> pojoClass = InitAplication.pojoMap.get(entityName);
		Field idField = InitAplication.idFieldMap.get(entityName);
		Mapper mapper = InitAplication.mapperMap.get(pojoClass);
		String id = parameter.get("id").toString();
		YyEntityIntercepter<Object> entityIntercepter = (YyEntityIntercepter<Object>) InitAplication.entityIntercepterMap.get(pojoClass);
		if (entityIntercepter != null) {//存储之前调用拦截器
			entityIntercepter.beforeDelete(null, parameter, result);
		}
		int deleteCount=0;
		try {
			if (result.getStatus()) {
				if (id.contains(",")) {//如果删除多个
					String[] ids = id.split(",");
					for (String theId : ids) {
						deleteCount+=mapper.deleteByPrimaryKey(EntityManager.caseType(idField.getType(), theId));
					}
				}else{//如果删除一个
					deleteCount=mapper.deleteByPrimaryKey(EntityManager.caseType(idField.getType(), id));
				}
				result.setData(deleteCount);
				if (entityIntercepter != null) {//存储后调用拦截器
					entityIntercepter.endDelete(null, parameter, result);
				}
				return result;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		result.setStatus(false);
		result.setData("操作失败");
		return result;
	}

	@Override
	public <T> T selectById(String entityName, Map<String, Object> parameter) {
		Class<?> pojoClass = InitAplication.pojoMap.get(entityName);
		Field idField = InitAplication.idFieldMap.get(entityName);
		Mapper mapper = InitAplication.mapperMap.get(pojoClass);
		String id = parameter.get("id").toString();
		YyEntityIntercepter<Object> entityIntercepter = (YyEntityIntercepter<Object>) InitAplication.entityIntercepterMap.get(pojoClass);
		if (entityIntercepter != null) {//存储之前调用拦截器
			entityIntercepter.beforeFindOne(null, parameter);
		}
		Object entity=null;
		try {
			entity = mapper.selectByPrimaryKey(EntityManager.caseType(idField.getType(), id));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (entityIntercepter != null) {
			entityIntercepter.endFindOne(null, parameter, entity);
		}
		return (T) entity;
	}
	
	public <T> Object  selectByExample(String entityName, Map<String, Object> parameter) {
		T entity = (T) EntityManager.getEntityByMap(entityName, parameter);
		Mapper<T> mapper = (Mapper<T>) InitAplication.mapperMap.get(entity.getClass());
		
		YyEntityIntercepter<Object> entityIntercepter = (YyEntityIntercepter<Object>) InitAplication.entityIntercepterMap.get(entity.getClass());
		if (entityIntercepter != null) {//存储之前调用拦截器
			entityIntercepter.beforeFindList(entity, parameter);
		}
		Long page=null;
		Integer size=null;
		Object pageObj = parameter.get("page");
		Object sizeObj = parameter.get("size");
		if (pageObj != null && !pageObj.equals("")) {
			page = Long.valueOf(String.valueOf(pageObj));
			if (sizeObj != null && !sizeObj.equals("")) {
				size = Integer.valueOf(String.valueOf(sizeObj));
			}else{
				size=10;
			}
			PageRowBounds rowBounds = new PageRowBounds((int) ((page-1)*size), size);
			List<T> list = mapper.selectByExampleAndRowBounds(ExampleInvoke.invoke(parameter, entity), rowBounds);
			PageResult pageResult = new PageResult();
			pageResult.setData(list);
			pageResult.setCurrentPage(page);
			pageResult.setSize(size);
			pageResult.setTotal(rowBounds.getTotal());
			pageResult.setPageCount(pageResult.getTotal()%size==0?pageResult.getTotal()/size:pageResult.getTotal()/size+1);
			if (entityIntercepter != null) {//存储后调用拦截器
				entityIntercepter.endFindList(entity, parameter, pageResult);
			}
			return pageResult;
		}else {
			List list = mapper.selectByExample(ExampleInvoke.invoke(parameter, entity));
			if (entityIntercepter != null) {//存储后调用拦截器
				entityIntercepter.endFindList(entity, parameter, list);
			}
			return list;
		}
	}
	
}
