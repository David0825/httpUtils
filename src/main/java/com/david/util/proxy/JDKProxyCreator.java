package com.david.util.proxy;

import com.david.util.interfaces.ProxyCreator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 *
 * jdk动态代理
 * @version 1.0.0
 *
 * @author wangwei910825@icloud.com
 *
 * @since 1.0.0
 *
 * @create 2021-03-22 22:22
 **/
public class JDKProxyCreator implements ProxyCreator {

	private Class<?>[] interfaces;
	private Class<?> proxyClass;
	private String proxyClassName;
	private Constructor<?> proxyConstructor;
	private InvocationHandler invocationHandler;

	public JDKProxyCreator(String proxyClassName,Class<?>[] interfaces,InvocationHandler invocationHandler)
			throws NoSuchMethodException {
		this.proxyClassName = proxyClassName;
		this.interfaces = interfaces;
		this.invocationHandler = invocationHandler;
		this.proxyClass = Proxy.getProxyClass(JDKProxyCreator.class.getClassLoader(),this.interfaces);
		this.proxyConstructor = this.proxyClass.getConstructor(InvocationHandler.class);
	}

	@Override
	public Object newProxyInstance() throws Exception {
		return this.proxyConstructor.newInstance(this.invocationHandler);
	}

	@Override
	public Class<?> getProxyClass() {
		return this.proxyClass;
	}
}
