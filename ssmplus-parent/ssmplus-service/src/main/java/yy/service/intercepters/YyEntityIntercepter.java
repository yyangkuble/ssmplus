package yy.service.intercepters;

import java.util.List;
import java.util.Map;

import yy.entity.AppResult;
import yy.entity.PageResult;


public class YyEntityIntercepter<T> {
	/**
	 * 增加前运行
	 * @param entityParmeter 实体类,是前端传入的参数
	 * @param parameter map集合,是前端传入的参数
	 * @param result 这里是要返回的结果,如果不让程序运行,设置为false
	 */
	public void beforeSave(T entityParmeter, Map<String, Object> parameter,AppResult result){
		
	}
	public void endSave(T entityParmeter, Map<String, Object> parameter,AppResult result){
		
	}
	public void beforeUpdate(T entityParmeter, Map<String, Object> parameter,AppResult result){
		
	}
	public void endUpdate(T entityParmeter, Map<String, Object> parameter,AppResult result){
		
	}
	public void beforeDelete(T entityParmeter, Map<String, Object> parameter,AppResult result){
		
	}
	public void endDelete(T entityParmeter, Map<String, Object> parameter,AppResult result){
		
	}
	
	public void beforeFindList(T entityParmeter, Map<String, Object> parameter) {
		
	}
	/**
	 * 
	 * @param entityParmeter
	 * @param parameter
	 * @param result 如果是分页可以强制转换为PageResult对象,如果不是分页可以强制转换为List<T>
	 */
	public void endFindList(T entityParmeter, Map<String, Object> parameter,PageResult pageResult) {
		
	}
	public void endFindList(T entityParmeter, Map<String, Object> parameter,List<T> list) {
		
	}
	
	public void beforeFindOne(T entityParmeter, Map<String, Object> parameter) {
		
	}
	/**
	 * 
	 * @param entityParmeter
	 * @param parameter
	 * @param result 
	 */
	public void endFindOne(T entityParmeter, Map<String, Object> parameter,T result) {
		
	}
	
	public void beforeFindCount(T entityParmeter, Map<String, Object> parameter) {
		
	}
	/**
	 * 
	 * @param entityParmeter
	 * @param parameter
	 * @param result 
	 */
	public void endFindCount(T entityParmeter, Map<String, Object> parameter,Integer result) {
		
	}
}
