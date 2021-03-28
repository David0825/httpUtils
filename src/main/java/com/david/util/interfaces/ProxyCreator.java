package com.david.util.interfaces;

/**
 *
 * 创建代理对象的class和Instance的接口
 * @version 1.0.0
 *
 * @author wangwei910825@icloud.com
 *
 * @since 1.0.0
 *
 * @create 2021-03-21 22:19
 **/
public interface ProxyCreator {

	/**
	 * 创建代理对象实例
	 * @param
	 * @version 1.0.0
	 * @return java.lang.Object
	 * @author wangwei910825@icloud.com
	 * @date 2021/3/21 10:20 下午
	 * @since 1.0.0
	 * @throws Exception
	 */
	Object newProxyInstance() throws Exception;


	/**
	 * 获取代理对象的class实例
	 * @param
	 * @version 1.0.0
	 * @return java.lang.Class<?>
	 * @author wangwei910825@icloud.com
	 * @date 2021/3/21 10:21 下午
	 * @since 1.0.0
	 */
	Class<?> getProxyClass();
}

