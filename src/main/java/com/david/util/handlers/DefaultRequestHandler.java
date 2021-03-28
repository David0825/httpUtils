package com.david.util.handlers;

import com.david.util.beans.RequestInfo;
import com.david.util.enums.HttpMethod;
import com.david.util.interfaces.IRequestHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 默认网络请求代理，可以采用restTemplate
 * @version 1.0.0
 *
 * @author wangwei910825@icloud.com
 *
 * @since 1.0.0
 *
 * @create 2021-03-21 22:17
 **/
@Component
public class DefaultRequestHandler implements IRequestHandle {

	@Autowired
	private RestTemplate restTemplate;
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRequestHandler.class);

	@Override
	public <T> T handler(RequestInfo requestInfo) {
		HttpMethod httpMethod = requestInfo.getHttpMethod();
		switch (httpMethod) {

			case POST:
				break;
			case GET:
			default:
				return httpGet(requestInfo);
		}
		return null;
	}


	private <T> T httpGet(RequestInfo requestInfo) {
		Map<String,String> params = requestInfo.getParams();
		StringBuffer sbUrl = new StringBuffer(requestInfo.getUrl());
		if (params != null && !params.isEmpty()){
			final boolean[] isFirst = {true};
			params.forEach((k,v)->{
				if (isFirst[0]){
					sbUrl.append("?" + k + "=" + v);
					isFirst[0] = false;
					return;
				}
				sbUrl.append("&" + k + "=" + v);
			});
		}
		ResponseEntity responseEntity = restTemplate.getForEntity(sbUrl.toString(),requestInfo.getReturnType());
		if (!responseEntity.getStatusCode().equals(HttpStatus.OK)){
			LOGGER.info("远程调用失败：url：【{}】,status：【{}】",sbUrl.toString(),responseEntity.getStatusCode());
			return null;
		}
		return (T)responseEntity.getBody();
	}
}
