package yy.service.interfaces;

import java.util.List;
import java.util.Map;

import yy.entity.AppResult;

public interface AppService {

	Object findList(String sqlId, Map<String, Object> map);

	Map<String, Object> findOne(String sqlId, Map<String, Object> map);

	AppResult exc(String sqlId, Map<String, Object> map);

	AppResult insert(String entityName, Map<String, Object> parameter);

	AppResult insertSelective(String entityName, Map<String, Object> parameter);
	AppResult updateByPrimaryKey(String entityName, Map<String, Object> parameter);
	AppResult updateByPrimaryKeySelective(String entityName, Map<String, Object> parameter);

	AppResult delete(String entityName, Map<String, Object> parameter);
	<T> Object select(String entityName, Map<String, Object> parameter);
	<T> Integer selectCount(String entityName, Map<String, Object> parameter);
	<T> T selectOne(String entityName, Map<String, Object> parameter);

	
}
