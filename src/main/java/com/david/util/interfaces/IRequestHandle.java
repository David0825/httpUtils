package com.david.util.interfaces;

import com.david.util.beans.RequestInfo;

/**
 *
 * 处理网络请求接口
 * @version 1.0.0
 *
 * @author wangwei910825@icloud.com
 *
 * @since 1.0.0
 *
 * @create 2021-03-21 22:11
 **/
public interface IRequestHandle {


	/**
	 * 网络请求方法
	 * @param requestInfo
	 * @version 1.0.0
	 * @return java.lang.Object
	 * @author wangwei910825@icloud.com
	 * @date 2021/3/21 10:16 下午
	 * @since 1.0.0
	 */
	<T> T handler(RequestInfo requestInfo);
}
