package org.abigballofmud.flink.api.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/26 10:45
 * @since 1.0
 */
@Component
public class UrlJarLoader implements JarLoader {

    private final Map<String, File> files = new ConcurrentHashMap<>();

    @Override
    public File downLoad(String path, boolean cache) throws FileNotFoundException {
        if (path == null) {
            return null;
        }
        path = path.trim();
        if (cache) {
            File file = files.get(path);
            if (file == null) {
                file = toFile(path);
                files.put(path, file);
            }
            return file;
        }
        return toFile(path);
    }

    @Override
    public URL find(String path, boolean cache) throws FileNotFoundException, MalformedURLException {
        if (path == null) {
            return null;
        }
        if (isLocal(path) || isAvg(path)) {
            File file = downLoad(path, cache);
            if (file == null) {
                return null;
            }
            return file.toURI().toURL();
        } else {
            return new URL(path);
        }
    }

    private boolean isAvg(String pathString) {
        String path = pathString.trim();
        return !(path.startsWith("/")
                || path.startsWith("classpath:")
                || path.startsWith("http:")
                || path.startsWith("https:")
                || path.startsWith("file:")
                || path.startsWith("hdfs:"));
    }

    private boolean isLocal(String pathString) {
        return pathString.startsWith("/");
    }

    private File toFile(String path) throws FileNotFoundException {
        File file;
        if (path.startsWith("classpath:") || path.startsWith("file:")) {
            file = ResourceUtils.getFile(path);
        } else if (path.startsWith("hdfs:")) {
            throw new UnsupportedOperationException();
        } else if (path.startsWith("http:")) {
            throw new UnsupportedOperationException();
        } else if (path.startsWith("https:")) {
            throw new UnsupportedOperationException();
        } else {
            // default download from linux
            file = new File(path);
        }
        return file;
    }

}
