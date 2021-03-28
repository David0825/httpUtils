package com.david.util.http;

import com.david.util.annotation.HttpUtil;
import com.david.util.invocation.HttpInvocationHandler;
import com.david.util.proxy.CGLibProxyCreator;
import com.david.util.proxy.JDKProxyCreator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cglib.proxy.CallbackHelper;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.Set;

/**
 *
 *
 * @version 1.0.0
 *
 * @author wangwei910825@icloud.com
 *
 * @since 1.0.0
 *
 * @create 2021-03-21 21:03
 **/
public class HttpRequestRegistrar
		implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware,
		BeanFactoryAware {

	private ClassLoader classLoader;
	private ResourceLoader resourceLoader;
	private Environment environment;
	private BeanFactory beanFactory;

	@Override
	public void registerBeanDefinitions(AnnotationMetadata annotationMetadata,
			BeanDefinitionRegistry beanDefinitionRegistry) {
		ClassPathScanningCandidateComponentProvider scanner = getClassScanner();
		scanner.setResourceLoader(resourceLoader);
		AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(HttpUtil.class);
		scanner.addIncludeFilter(annotationTypeFilter);
		String packageName = ClassUtils.getPackageName(annotationMetadata.getClassName());
		Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(packageName);
		HttpInvocationHandler handler = getHttpInvocationHandler();
		candidateComponents.forEach(beanDefinition -> {
			if (beanDefinition instanceof AnnotatedBeanDefinition) {
				try {
					Class<?> clazz = Class.forName(beanDefinition.getBeanClassName(), false, classLoader);
					BeanDefinition proxyBeanDefinition = getProxyBeanDefinition(clazz,handler);
					registerBeans(clazz.getSimpleName(),proxyBeanDefinition);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("failed to load" + beanDefinition.getBeanClassName());
				}
			}
		});
	}

	private void registerBeans(String className,BeanDefinition proxyBeanDefinition){
		((DefaultListableBeanFactory)this.beanFactory).registerBeanDefinition(className,proxyBeanDefinition);
	}

	/**
	 * 构造class扫描器，设置只扫描顶级接口，不扫描内部类
	 * @param
	 * @version 1.0.0
	 * @return org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
	 * @author wangwei910825@icloud.com
	 * @date 2021/3/21 9:27 下午
	 * @since 1.0.0
	 */
	private ClassPathScanningCandidateComponentProvider getClassScanner() {
		return new ClassPathScanningCandidateComponentProvider(Boolean.FALSE, this.environment) {
			@Override
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
				if (beanDefinition.getMetadata().isInterface()) {
					try {
						Class<?> target = ClassUtils.forName(beanDefinition.getMetadata().getClassName(), classLoader);
						return !target.isAnnotation();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				return false;
			}
		};
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	private HttpInvocationHandler getHttpInvocationHandler() {
		return new HttpInvocationHandler((DefaultListableBeanFactory)beanFactory);
	}


	private BeanDefinition getProxyBeanDefinition(Class<?> clazz, HttpInvocationHandler handler)
			throws NoSuchMethodException {
		boolean isProxyClass = isProxyClass(clazz);
		if (isProxyClass) {
			Class<?> cls = getCGLibProxyClass(clazz, handler);
			return getCGLibBeanDefinition(cls);
		}
		Class<?> cls = getJDKDynamicProxyClass(clazz,handler);
		return getJDKBeanDefinition(cls,handler);


	}

	/**
	 * 判断是不是需要代理类（相对于代理接口，如果是代理类则使用cglib）
	 * 不是interface活着proxyClass为true，则返回true
	 * @param clazz
	 * @version 1.0.0
	 * @return boolean
	 * @author wangwei910825@icloud.com
	 * @date 2021/3/28 2:59 下午
	 * @since 1.0.0
	 */
	private boolean isProxyClass(Class<?> clazz) {
		return !clazz.isInterface() || clazz.getAnnotation(HttpUtil.class).proxyClass();
	}

	/**
	 * 通过CGLib获取代理对象实例
	 * @param clazz
	 * @param handler
	 * @version 1.0.0
	 * @return java.lang.Class<?>
	 * @author wangwei910825@icloud.com
	 * @date 2021/3/28 3:07 下午
	 * @since 1.0.0
	 */
	private Class<?> getCGLibProxyClass(Class<?> clazz, HttpInvocationHandler handler) {
		CallbackHelper callbackHelper = new CallbackHelper(clazz, new Class[]{}) {
			@Override
			protected Object getCallback(Method method) {
				return handler;
			}
		};
		CGLibProxyCreator cgLibProxyCreator = new CGLibProxyCreator(clazz, callbackHelper);
		return cgLibProxyCreator.getProxyClass();
	}

	/**
	 * 通过jdk代理获取代理对象实例
	 * @param clazz
	 * @param handler
	 * @version 1.0.0
	 * @return java.lang.Class<?>
	 * @author wangwei910825@icloud.com
	 * @date 2021/3/28 3:11 下午
	 * @since 1.0.0
	 */
	private Class<?> getJDKDynamicProxyClass(Class<?> clazz, HttpInvocationHandler handler)
			throws NoSuchMethodException {
		String newClassName = clazz.getCanonicalName() + "proxy";
		JDKProxyCreator jdkProxyCreator = new JDKProxyCreator(newClassName, new Class<?>[]{clazz}, handler);
		return jdkProxyCreator.getProxyClass();
	}

	/**
	 * 获取jdk代理的beanDefinition
	 * @param clazz
	 * @param handler
	 * @version 1.0.0
	 * @return org.springframework.beans.factory.config.BeanDefinition
	 * @author wangwei910825@icloud.com
	 * @date 2021/3/28 3:13 下午
	 * @since 1.0.0
	 */
	private BeanDefinition getJDKBeanDefinition(Class<?> clazz, HttpInvocationHandler handler) {
		BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(clazz)
				.addConstructorArgValue(handler).getRawBeanDefinition();
		beanDefinition.setAutowireCandidate(true);
		return beanDefinition;
	}

	/**
	 * 获取CGLib代理的beanDefinition
	 * @param clazz
	 * @version 1.0.0
	 * @return org.springframework.beans.factory.config.BeanDefinition
	 * @author wangwei910825@icloud.com
	 * @date 2021/3/28 3:14 下午
	 * @since 1.0.0
	 */
	private BeanDefinition getCGLibBeanDefinition(Class<?> clazz){
		BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition().getRawBeanDefinition();
		beanDefinition.setAutowireCandidate(true);
		return beanDefinition;
	}
}
