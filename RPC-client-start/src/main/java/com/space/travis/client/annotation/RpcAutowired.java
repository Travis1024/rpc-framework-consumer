package com.space.travis.client.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import java.lang.annotation.*;

/**
 * @ClassName RpcAutowired
 * @Description 定义RPC客户端注解
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/3/2
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Autowired
public @interface RpcAutowired {
    String version() default "1.0";
}
