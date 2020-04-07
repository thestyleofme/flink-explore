package org.abigballofmud.flink.practice.sqlsubmit.loader.demo;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.flink.practice.sqlsubmit.loader.demo.demo1.DiskClassLoader;
import org.abigballofmud.flink.practice.sqlsubmit.loader.demo.demo1.ExtClasspathLoader;
import org.abigballofmud.flink.practice.sqlsubmit.loader.demo.demo2.JavaCompileClassLoader;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/24 10:33
 * @since 1.0
 */
@Slf4j
public class ClassLoaderTest {

    public static void main(String[] args) {
        log.info("testDiskClassLoader");
        testDiskClassLoader();
        log.info("testExtClasspathLoader");
        testExtClasspathLoader();
        log.info("testJavaCompileClassLoader");
        testJavaCompileClassLoader();
    }

    @SneakyThrows
    private static void testJavaCompileClassLoader() {
        JavaCompileClassLoader javaCompileClassLoader = new JavaCompileClassLoader();
        String code = "package org.demo;\n" +
                "\n" +
                "/**\n" +
                " * <p>\n" +
                " * test classloader\n" +
                " * </p>\n" +
                " *\n" +
                " * @author isacc 2020/03/24 10:18\n" +
                " * @since 1.0\n" +
                " */\n" +
                "public class Test {\n" +
                "\n" +
                "    public void say() {\n" +
                "        System.out.println(\"Hello World by java compile\");\n" +
                "    }\n" +
                "\n" +
                "}\n";
        Class<?> clazz = javaCompileClassLoader.compileClass("org.demo.Test", code);
        Object obj = clazz.newInstance();
        System.out.println("testJavaCompileClassLoader: " + obj);
        Method method = clazz.getDeclaredMethod("say");
        method.invoke(obj);
    }

    @SneakyThrows
    private static void testExtClasspathLoader() {
        ExtClasspathLoader.loadClasspath(new File("C:\\Users\\isacc\\Desktop\\test"));
        Class<?> clazz = Class.forName("org.demo.Test");
        Object obj = clazz.newInstance();
        System.out.println("testExtClasspathLoader: " + obj);
        Method method = clazz.getDeclaredMethod("say");
        method.invoke(obj);
    }

    private static void testDiskClassLoader() {
        // 创建自定义classloader对象
        DiskClassLoader diskLoader = new DiskClassLoader("C:\\Users\\isacc\\Desktop\\test\\org\\demo");
        try {
            // 加载class文件
            Class<?> c = diskLoader.loadClass("org.demo.Test");
            if (c != null) {
                Object obj = c.newInstance();
                System.out.println("testDiskClassLoader: " + obj);
                Method method = c.getDeclaredMethod("say");
                // 通过反射调用Test类的say方法
                method.invoke(obj);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
            log.error("classloader error", e);
        }
    }

}
