package yy.service;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageRowBounds;
import tk.mybatis.mapper.common.Mapper;
import yy.entity.AppResult;
import yy.entity.PageResult;
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
			entityIntercepter.beforeSaveWithNull(entity, parameter, result);
		}
		try {
			if (result.getStatus()) {
				Mapper mapper = InitAplication.mapperMap.get(entity.getClass());
				int insertCount = mapper.insert(entity);
				result.setData(entity);
				if (entityIntercepter != null) {//存储后调用拦截器
					entityIntercepter.endSaveWithNull(entity, parameter, result);
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
			entityIntercepter.beforeUpdateWithNull(entity, parameter, result);
		}
		try {
			if (result.getStatus()) {
				Mapper mapper = InitAplication.mapperMap.get(entity.getClass());
				int updateCount = mapper.updateByPrimaryKey(entity);
				result.setData(updateCount);
				if (entityIntercepter != null) {//存储后调用拦截器
					entityIntercepter.endUpdateWithNull(entity, parameter, result);
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
			entityIntercepter.beforeFindEntityList(entity, parameter);
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
				entityIntercepter.endFindEntityList(entity, parameter, pageResult);
			}
			return pageResult;
		}else {
			List list = mapper.select(entity);
			if (entityIntercepter != null) {//存储后调用拦截器
				entityIntercepter.endFindEntityList(entity, parameter, list);
			}
			return list;
		}
	}
	
	
	public <T> Integer selectCount(String entityName,Map<String, Object> parameter) {
		T entity = (T) EntityManager.getEntityByMap(entityName, parameter);
		Mapper<T> mapper = (Mapper<T>) InitAplication.mapperMap.get(entity.getClass());
		
		YyEntityIntercepter<Object> entityIntercepter = (YyEntityIntercepter<Object>) InitAplication.entityIntercepterMap.get(entity.getClass());
		if (entityIntercepter != null) {//存储之前调用拦截器
			entityIntercepter.beforeFindEntityCount(entity, parameter);
		}
		int selectCount = mapper.selectCount(entity);
		if (entityIntercepter != null) {//存储后调用拦截器
			entityIntercepter.endFindEntityCount(entity, parameter, selectCount);
		}
		return selectCount;
	}
	
	public <T> T selectOne(String entityName,Map<String, Object> parameter) {
		T entity = (T) EntityManager.getEntityByMap(entityName, parameter);
		Mapper<T> mapper = (Mapper<T>) InitAplication.mapperMap.get(entity.getClass());
		
		YyEntityIntercepter<Object> entityIntercepter = (YyEntityIntercepter<Object>) InitAplication.entityIntercepterMap.get(entity.getClass());
		if (entityIntercepter != null) {//存储之前调用拦截器
			entityIntercepter.beforeFindEntityOne(entity, parameter);
		}
		T resultEntity = mapper.selectOne(entity);
		if (entityIntercepter != null) {//存储后调用拦截器
			entityIntercepter.endFindEntityOne(entity, parameter, resultEntity);
		}
		return resultEntity;
	}

}
