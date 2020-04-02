package org.abigballofmud.flink.platform.infra.loader;

import java.util.HashMap;
import java.util.Map;
import javax.tools.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/24 20:12
 * @since 1.0
 */
public class FileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    protected final Map<String, Output> map = new HashMap<>();

    public FileManager(JavaCompiler compiler) {
        // 取得文件管理器
        super(compiler.getStandardFileManager(null, null, null));
    }

    @Override
    public Output getJavaFileForOutput(Location location,
                                       String name,
                                       JavaFileObject.Kind kind,
                                       FileObject source) {
        // 与文件连接
        Output mc = new Output(name, kind);
        this.map.put(name, mc);
        return mc;
    }

    public Output getOutput(String name) {
        return map.get(name);
    }

}