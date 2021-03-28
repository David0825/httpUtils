package com.david.util.annotation;

import com.david.util.enums.HttpMethod;

import java.lang.annotation.*;

/**
 *
 *
 * @version 1.0.0
 *
 * @author wangwei910825@icloud.com
 *
 * @since 1.0.0
 *
 * @create 2021-03-21 20:47
 **/
@Documented
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpRequest {

	HttpMethod httpMethod() default HttpMethod.GET;
	String url();
	Class<?> returnType() default String.class;
}
