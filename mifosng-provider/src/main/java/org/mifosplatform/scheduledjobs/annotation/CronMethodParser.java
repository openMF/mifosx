package org.mifosplatform.scheduledjobs.annotation;

import java.lang.reflect.Method;

/**
 * Parser to find method which is marked with CronTargetMethod annotation
 * 
 */
public class CronMethodParser {

    public static String findTargetMethodName(Class<?> beanClass) {
        Method[] methods = beanClass.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(CronTargetMethod.class)) { return method.getName(); }
        }
        return null;
    }
}
