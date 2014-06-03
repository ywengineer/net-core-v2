/*******************************************************************
 *
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * ClassUtils.java
 *
 * 2013 2013-5-17 下午4:07:57
 *
 *******************************************************************/
package com.handee.utils;

import com.github.jmkgreen.morphia.utils.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Class和实例相关工具类。
 *
 * @author Mark
 */
public class ClassUtils {
    private static final Logger logger = Logger.getLogger(ClassUtils.class);

    /**
     * 获取某Class的所有Field映射类。包括其父类继承的Field.
     * <p/>
     * 不包括final和static的Field.
     *
     * @param cls Class描述符
     * @return Field映射
     */
    public static Map<String, Field> getClassFieldsMap(Class<?> cls) {
        Field[] fields = ReflectionUtils.getDeclaredAndInheritedFields(cls, false);
        Map<String, Field> map = new HashMap<>();
        for (Field field : fields) {
            map.put(field.getName(), field);
        }
        return map;
    }

    /**
     * 从一个包中查找出所有的类，在jar包中不能查找.
     *
     * @param packageName 包名
     * @return 该包下所有类，包括子包下的所有类。
     */
    public static Set<Class<?>> getClasses(String packageName) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return getClasses(loader, packageName);
    }

    /**
     * 获取指定包名下的所有class文件
     *
     * @param loader      class加载器
     * @param packageName 包名
     * @return class文件列表
     */
    public static Set<Class<?>> getClasses(final ClassLoader loader, final String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        String path = packageName.replace('.', '/');
        try {
            Enumeration<URL> resources = loader.getResources(path);
            if (resources != null) {
                while (resources.hasMoreElements()) {
                    String filePath = resources.nextElement().getFile();
                    // WINDOWS HACK
                    if (filePath.indexOf("%20") > 0)
                        filePath = filePath.replaceAll("%20", " ");
                    // # in the jar name
                    if (filePath.indexOf("%23") > 0)
                        filePath = filePath.replaceAll("%23", "#");

                    if (filePath != null) {
                        if ((filePath.indexOf("!") > 0) & (filePath.indexOf(".jar") > 0)) {
                            String jarPath = filePath.substring(0, filePath.indexOf("!")).substring(filePath.indexOf(":") + 1);
                            // WINDOWS HACK
                            if (jarPath.contains(":")) {
                                jarPath = jarPath.substring(1);
                            }
                            classes.addAll(getFromJARFile(jarPath, path));
                        } else {
                            classes.addAll(getFromDirectory(new File(filePath), packageName));
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    /**
     * 获取某jar包中的所有class文件
     *
     * @param jar         jar包
     * @param packageName 指定包名
     * @return class文件列表
     * @throws IOException            IO异常
     * @throws ClassNotFoundException Class未找到
     */
    public static Set<Class<?>> getFromJARFile(final String jar, final String packageName) throws IOException, ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        JarInputStream jarFile = new JarInputStream(new FileInputStream(jar));
        JarEntry jarEntry;
        do {
            jarEntry = jarFile.getNextJarEntry();
            if (jarEntry != null) {
                String className = jarEntry.getName();
                if (className.endsWith(".class")) {
                    className = stripFilenameExtension(className);
                    if (className.startsWith(packageName)) {
                        classes.add(Class.forName(className.replace('/', '.')));
                    }
                }
            }
        } while (jarEntry != null);

        jarFile.close();
        return classes;
    }

    /**
     * 提取文件名称。
     * <p/>
     * 如:A.class,调用此方法返回A
     *
     * @param filename 文件全名，包括suffix
     * @return 文件名称，不包括后缀
     */
    public static String stripFilenameExtension(final String filename) {
        if (filename.indexOf('.') != -1) {
            return filename.substring(0, filename.lastIndexOf('.'));
        } else {
            return filename;
        }
    }

    /**
     * 获取类描述
     *
     * @param className 类名描述符，包括包句
     * @return Class描述
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClass(String className) {
        try {
            if (StringUtils.isEmpty(className)) {
                return null;
            }
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            logger.error("ClassUtils.getClass [className = " + className + "] :: ", e);// e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取类实例
     *
     * @param cls           Class描述
     * @param initialParams 构造函数参数
     * @return Class的Instance
     */
    public static <T> T getClassInstance(Class<T> cls, Object... initialParams) {
        if (cls == null) {
            return null;
        }
        try {
            if (initialParams != null && initialParams.length > 0) {
                Class<?>[] parameterTypes = new Class<?>[initialParams.length];
                for (int index = 0; index < initialParams.length; index++) {
                    parameterTypes[index] = initialParams[index].getClass();
                }
                Constructor<T> constructor = cls.getConstructor(parameterTypes);
                if (constructor != null) {
                    return constructor.newInstance(initialParams);
                }
            } else {
                return cls.newInstance();
            }
        } catch (Exception e) {
            logger.error("getClassInstance Error :: ", e);// e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getClassInstance(String className, Object... initargs) {
        return (T) getClassInstance(getClass(className), initargs);
    }

    /**
     * 获取某目录下所有class文件。
     * <p/>
     * 包括其子目录
     *
     * @param directory   目录
     * @param packageName 指定包名
     * @return 所有class文件列表
     */
    public static Set<Class<?>> getFromDirectory(final File directory, final String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null)
                for (File file : files) {
                    if (file.isDirectory()) {
                        assert !file.getName().contains(".");
                        classes.addAll(getFromDirectory(file, packageName + "." + file.getName()));
                    } else if (file.getName().endsWith(".class")) {
                        String clsName = packageName + '.' + stripFilenameExtension(file.getName());
                        clsName = clsName.startsWith(".") ? StringUtils.substring(clsName, 1) : clsName;
                        try {
                            classes.add(Class.forName(clsName));
                        } catch (ClassNotFoundException e) {
                            // e.printStackTrace();
                            logger.error("Class.forName[" + clsName + "]", e);
                        }
                    }
                }
        }
        return classes;
    }

    /**
     * 为某Field赋值。
     *
     * @param host  属性宿 主
     * @param field 属性描述
     * @param value 值
     */
    public static void setValue(Object host, Field field, Object value) {
        try {
            if (field == null) {
                return;
            }
            boolean originalAccess = field.isAccessible();
            if (!originalAccess) {
                field.setAccessible(true);
            }
            field.set(host, to(value, field.getType()));
            field.setAccessible(originalAccess);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            // e.printStackTrace();
            logger.error("Set value to instance error :: ", e);
        }
    }

    private static Object to(Object value, Class<?> cls) {
        if (value == null || cls == value.getClass()) {
            return value;
        } else if (int.class == cls || Integer.class == cls) {
            return MathUtils.getInteger(value);
        } else if (String.class == cls) {
            return MathUtils.getString(value);
        } else if (boolean.class == cls || Boolean.class == cls) {
            return MathUtils.getBoolean(value);
        } else if (byte.class == cls || Byte.class == cls) {
            return MathUtils.getByte(value);
        } else if (char.class == cls || Character.class == cls) {
            return MathUtils.getChar(value);
        } else if (double.class == cls || Double.class == cls) {
            return MathUtils.getDouble(value);
        } else if (float.class == cls || Float.class == cls) {
            return MathUtils.getFloat(value);
        } else if (long.class == cls || Long.class == cls) {
            return MathUtils.getLong(value);
        } else if (short.class == cls || Short.class == cls) {
            return MathUtils.getShort(value);
        } else if (Class.class == cls) {
            return ClassUtils.getClass(MathUtils.getString(value));
        } else {
            return null;
        }
    }
}
