package org.abigballofmud.flink.sqlsubmit.loader.demo.demo2;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.abigballofmud.flink.sqlsubmit.loader.exceptions.ClassLoaderException;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/24 20:17
 * @since 1.0
 */
public class JavaCompileClassLoader extends ClassLoader {

    /**
     * 自定义FileManager
     */
    private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    private final FileManager manager = new FileManager(this.compiler);

    public Class<?> compileClass(String name, String code) {
        List<Source> list = new ArrayList<>();
        // 输入
        list.add(new Source(name, JavaFileObject.Kind.SOURCE, code));
        StringWriter stringWriter = new StringWriter();
        this.compiler.getTask(stringWriter, this.manager, null, null, null, list).call();
        // 输出
        Output mc = this.manager.getOutput(name);
        if (mc != null) {
            // 转换成byte[]
            byte[] array = mc.toByteArray();
            // 需要继承ClassLoader
            return defineClass(name, array, 0, array.length);
        }
        throw new ClassLoaderException("java compile error");
    }
}
