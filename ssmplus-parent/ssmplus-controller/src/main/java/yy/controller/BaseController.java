package yy.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;

import yy.entity.AppResult;
import yy.service.interfaces.AppService;

@RestController
public class BaseController {
	
	@Reference(timeout = 1000000)
	AppService appService;
	
	@RequestMapping("/findList/{sqlId}")
	public Object findList(@PathVariable String sqlId,HttpServletRequest request ) {
		Map<String, Object> map = requestToMap(request);
		return appService.findList(sqlId, map);
	}
	
	@RequestMapping("/findOne/{sqlId}")
	public Map<String, Object> findOne(@PathVariable String sqlId,HttpServletRequest request) {
		Map<String, Object> map = requestToMap(request);
		return appService.findOne(sqlId, map);
	}
	
	@RequestMapping("/exc/{sqlId}")
	public AppResult exc(@PathVariable String sqlId,HttpServletRequest request) {
		Map<String, Object> map = requestToMap(request);
		return appService.exc(sqlId, map);
	}
	
	@RequestMapping("/save/{entityName}")
	public AppResult save(@PathVariable String entityName,HttpServletRequest request) {
		Map<String, Object> map = requestToMap(request);
		return appService.insertSelective(entityName, map);
	}
	
	@RequestMapping("/save/{entityName}/withNull")
	public AppResult save_withNull(@PathVariable String entityName,HttpServletRequest request) {
		Map<String, Object> map = requestToMap(request);
		return appService.insert(entityName, map);
	}
	
	@RequestMapping("/update/{entityName}")
	public AppResult update(@PathVariable String entityName,HttpServletRequest request) {
		Map<String, Object> map = requestToMap(request);
		return appService.updateByPrimaryKeySelective(entityName, map);
	}
	
	@RequestMapping("/update/{entityName}/withNull")
	public AppResult update_withNull(@PathVariable String entityName,HttpServletRequest request) {
		Map<String, Object> map = requestToMap(request);
		return appService.updateByPrimaryKey(entityName, map);
	}
	
	@RequestMapping("/delete/{entityName}")
	public AppResult delete(@PathVariable String entityName,HttpServletRequest request) {
		Map<String, Object> map = requestToMap(request);
		return appService.delete(entityName, map);
	}
	
	@RequestMapping("/find/{entityName}/list")
	public Object selectEntityList(@PathVariable String entityName,HttpServletRequest request) {
		Map<String, Object> map = requestToMap(request);
		return appService.select(entityName, map);
	}
	
	@RequestMapping("/find/{entityName}/one")
	public Object selectEntityOne(@PathVariable String entityName,HttpServletRequest request) {
		Map<String, Object> map = requestToMap(request);
		return appService.selectOne(entityName, map);
	}
	
	@RequestMapping("/find/{entityName}/count")
	public Integer selectEntityCount(@PathVariable String entityName,HttpServletRequest request) {
		Map<String, Object> map = requestToMap(request);
		return appService.selectCount(entityName, map);
	}
	
	/**
	 * 从Request中获取参数转换层map对象,支持json数据和普通表单提交数据
	 * @param request
	 * @return
	 */
	public Map<String, Object> requestToMap(HttpServletRequest request) {
		Map<String, Object> map=null;
		//如果请求是一个json
		if (request.getContentType() != null && request.getContentType().contains("json")) {
			try {
				//获取request的写出流
				ServletInputStream inputStream = request.getInputStream();
				//创建输出流,用于接收request里面的数据
				ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
				byte[] bytes=new byte[1024];//创建缓冲区
				int flag=0;
				while ((flag = inputStream.read(bytes)) > 0){ 
			      byteArrayOutputStream.write(bytes,0,flag); 
			    } 
				//获取json文本字符串
				String jsonText = new String(byteArrayOutputStream.toByteArray(),"UTF-8");
				//fastjson将文本字符串转换成Map
				map = JSON.parseObject(jsonText, Map.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//处理非json提交的数据
		if (map == null) {
			map = new HashMap<String, Object>();
		}
		Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String name = (String) parameterNames.nextElement();
			map.put(name, request.getParameter(name));
		}
		return map;
	}
}
