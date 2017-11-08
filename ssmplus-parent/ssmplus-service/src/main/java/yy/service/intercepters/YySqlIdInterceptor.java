package yy.service.intercepters;

import java.util.List;
import java.util.Map;

import yy.entity.AppResult;
import yy.entity.PageResult;

public abstract class YySqlIdInterceptor {
	
	public void beforeExc(AppResult result , Map<String, Object> parameter){};
	public void beforeFind(Map<String, Object> parameter){};
	public void endFindList(Map<String, Object> parameter,PageResult pageResult){}
	public void endFindList(Map<String, Object> parameter,List<Map<String, Object>> list){}
	public void endFindOne(Map<String, Object> parameter,Map<String, Object> mapResult){}
	public void endExc(AppResult result , Map<String, Object> parameter){};
}
