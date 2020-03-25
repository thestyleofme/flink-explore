package org.abigballofmud.flink.sqlsubmit.loader.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import groovy.lang.GroovyClassLoader;
import lombok.SneakyThrows;
import org.abigballofmud.flink.sqlsubmit.loader.exceptions.GroovyCompileException;
import org.codehaus.groovy.control.CompilerConfiguration;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/24 15:22
 * @since 1.0
 */
public class GroovyCompiler {

    private GroovyCompiler() {
        throw new IllegalStateException("util class");
    }

    static GroovyClassLoader getGroovyClassLoader() {
        CompilerConfiguration config = new CompilerConfiguration();
        config.setSourceEncoding("UTF-8");
        return new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);
    }

    /**
     * Compiles Groovy code and returns the Class of the compiles code.
     */
    public static Class<?> compile(String sCode, String sName) {
        GroovyClassLoader loader = getGroovyClassLoader();
        return loader.parseClass(sCode, sName);
    }

    /**
     * Compiles groovy class from a file
     */
    public static Class<?> compile(File file) throws IOException {
        GroovyClassLoader loader = getGroovyClassLoader();
        return loader.parseClass(file);
    }

    @SneakyThrows
    public static void main(String[] args) {
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
                "        System.out.println(\"Hello World\");\n" +
                "    }\n" +
                "\n" +
                "}\n";
        Class<?> clazz = GroovyCompiler.compile(code, "test");
        Class<?> aClass = getGroovyClassLoader().loadClass("org.demo.Test");
        Object obj = clazz.newInstance();
        System.out.println("after compile: " + obj);
        Method method = clazz.getDeclaredMethod("say");
        method.invoke(obj);
    }

}
