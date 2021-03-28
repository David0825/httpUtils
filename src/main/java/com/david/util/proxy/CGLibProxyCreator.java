package com.david.util.proxy;

import com.david.util.interfaces.ProxyCreator;
import org.springframework.cglib.proxy.CallbackHelper;
import org.springframework.cglib.proxy.Enhancer;

/**
 *
 * CGLib代理
 * @version 1.0.0
 *
 * @author wangwei910825@icloud.com
 *
 * @since 1.0.0
 *
 * @create 2021-03-22 22:16
 **/
public class CGLibProxyCreator implements ProxyCreator {

	private Class<?> proxyClass;
	private Enhancer enhancer;

	public CGLibProxyCreator(Class<?> superClass, CallbackHelper callbackHelper) {
		init(superClass,callbackHelper);
	}

	private void init(Class<?> superClass,CallbackHelper callbackHelper) {
		this.enhancer = new Enhancer();
		this.enhancer.setSuperclass(superClass);
		this.enhancer.setCallbackFilter(callbackHelper);
		this.enhancer.setCallbacks(callbackHelper.getCallbacks());
		this.proxyClass = this.enhancer.create().getClass();
	}

	@Override
	public Object newProxyInstance() throws Exception {
		return this.enhancer.create();
	}

	@Override
	public Class<?> getProxyClass() {
		return this.proxyClass;
	}
}
