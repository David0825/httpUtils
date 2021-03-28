package com.david.util.invocation;

import com.david.util.annotation.HttpRequest;
import com.david.util.annotation.Param;
import com.david.util.beans.RequestInfo;
import com.david.util.enums.HttpMethod;
import com.david.util.interfaces.IRequestHandle;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * 代理类，jdk和cglib都可以调用的代理类，实现jdk和cglib两个接口
 * @version 1.0.0
 *
 * @author wangwei910825@icloud.com
 *
 * @since 1.0.0
 *
 * @create 2021-03-22 20:56
 **/
public class HttpInvocationHandler implements InvocationHandler, org.springframework.cglib.proxy.InvocationHandler {

	/**
	 * spring的beanFactory
	 * 为了在允许时得到实际的网络请求处理类的bean
	 * */
	private final BeanFactory beanFactory;

	private IRequestHandle requestHandle;

	public HttpInvocationHandler(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	private IRequestHandle getRequestHandler(){
		if (this.requestHandle == null) {
			this.requestHandle = this.beanFactory.getBean(IRequestHandle.class);
		}
		if (this.requestHandle == null) {
			throw  new NullPointerException("IOC容器中没有IRequestHandle的实现类");
		}
		return requestHandle;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		RequestInfo requestInfo = extractRequestInfo(method,args);
		return getRequestHandler().handler(requestInfo);
	}


	/**
	 * 解析方法上的注解
	 * @param method
	 * @param args
	 * @version 1.0.0
	 * @return com.david.util.beans.RequestInfo
	 * @author wangwei910825@icloud.com
	 * @date 2021/3/22 9:44 下午
	 * @since 1.0.0
	 */
	private RequestInfo extractRequestInfo(Method method,Object[] args) {
		HttpRequest annotation = method.getAnnotation(HttpRequest.class);
		if (annotation == null) {
			throw new NullPointerException("当前被代理方法" + method.getName() + "没有定义注解信息");
		}
		RequestInfo requestInfo = new RequestInfo();
		String url = annotation.url();
		if (StringUtils.isEmpty(url)) {
			throw  new NullPointerException("当前注解信息没有配置url属性" + method.getName());
		}
		Class returnType = annotation.returnType();
		HttpMethod httpMethod = annotation.httpMethod();
		requestInfo.setUrl(url);
		requestInfo.setReturnType(returnType);
		requestInfo.setHttpMethod(httpMethod);

		Map<String,String> params = extractParams(method,args);
		if (params != null) {
			requestInfo.setParams(params);
		}
		return requestInfo;
	}


	/**
	 * 解析请求参数
	 * @param method
	 * @param args
	 * @version 1.0.0
	 * @return java.util.LinkedHashMap<java.lang.String,java.lang.String>
	 * @author wangwei910825@icloud.com
	 * @date 2021/3/22 9:53 下午
	 * @since 1.0.0
	 */
	private HashMap<String,String> extractParams(Method method,Object[]args) {
		Parameter[] parameters = method.getParameters();
		if (parameters.length == 0) {
			return null;
		}
		HashMap<String,String> params = new HashMap<>();
		for (int i = 0; i < parameters.length; i++) {
			Param param = parameters[i].getAnnotation(Param.class);
			if (param != null) {
				if (args[i] instanceof Map) {
					for (Map.Entry<String,String> entry : ((Map<String, String>) args[i]).entrySet()){
						params.put(entry.getKey(),entry.getValue());
					}
					continue;
				}
				params.put(param.value(),String.valueOf(args[i]));
			}
		}

		return params;
	}
}
