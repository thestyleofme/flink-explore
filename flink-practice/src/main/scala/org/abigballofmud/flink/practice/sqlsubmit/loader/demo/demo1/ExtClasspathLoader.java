package org.abigballofmud.flink.practice.sqlsubmit.loader.demo.demo1;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.flink.practice.sqlsubmit.loader.exceptions.ClassLoaderException;

/**
 * <p>
 * 根据properties中配置的路径把jar和配置文件加载到classpath中
 * 此工具类加载类时使用的是SystemClassLoader，如有需要对加载类进行校验，请另外实现自己的加载器
 * <p>
 * java如何使用jar命令打包成jar包
 * https://www.cnblogs.com/jayworld/p/9765474.html
 * </p>
 *
 * @author isacc 2020/03/24 10:58
 * @since 1.0
 */
@Slf4j
public class ExtClasspathLoader {

    private ExtClasspathLoader() {
        // private
    }

    private static final String JAR_SUFFIX = ".jar";
    private static final String ZIP_SUFFIX = ".zip";

    /**
     * URLClassLoader的addURL方法
     */
    private static Method addURL = initAddMethod();

    /**
     * Application Classloader
     */
    private static URLClassLoader classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();

    /**
     * 初始化addUrl 方法.
     *
     * @return 可访问addUrl方法的Method对象
     */
    private static Method initAddMethod() {
        try {
            Method add = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            add.setAccessible(true);
            return add;
        } catch (Exception e) {
            throw new ClassLoaderException("classloader error", e);
        }
    }

    /**
     * 通过filepath加载文件到classpath。
     *
     * @param file 文件路径
     */
    private static void addUrl(File file) throws MalformedURLException, InvocationTargetException, IllegalAccessException {
        addURL.invoke(classloader, file.toURI().toURL());
    }

    /**
     * load Resource by dir
     *
     * @param file dir
     */
    public static void loadResource(File file) throws Exception {
        // 资源文件只加载路径
        log.info("load Resource of dir : {}", file.getAbsolutePath());
        if (file.isDirectory()) {
            addUrl(file);
            File[] subFiles = file.listFiles();
            if (subFiles != null) {
                for (File tmp : subFiles) {
                    loadResource(tmp);
                }
            }
        }
    }

    /**
     * load Classpath by dir
     *
     * @param file dir
     */
    public static void loadClasspath(File file) throws Exception {
        log.info("load Classpath of dir : {}", file.getAbsolutePath());
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            if (subFiles != null) {
                for (File subFile : subFiles) {
                    loadClasspath(subFile);
                }
            }
        } else {
            if (file.getAbsolutePath().endsWith(JAR_SUFFIX) || file.getAbsolutePath().endsWith(ZIP_SUFFIX)) {
                addUrl(file);
            }
        }
    }
}
