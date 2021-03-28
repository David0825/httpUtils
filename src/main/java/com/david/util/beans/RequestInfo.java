package com.david.util.beans;

import com.david.util.enums.HttpMethod;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 网络请求bean
 * @version 1.0.0
 *
 * @author wangwei910825@icloud.com
 *
 * @since 1.0.0
 *
 * @create 2021-03-21 22:13
 **/
public class RequestInfo {

	/**
	 * 请求url
	 * */
	private String url;

	/**
	 * 请求响应类型
	 * */
	private Class<?> returnType;

	/**
	 * 请求参数
	 * */
	private Map<String,String> params;

	/**
	 * 请求类型方法
	 * */
	private HttpMethod httpMethod;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}
}
