package org.abigballofmud.flink.api.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/26 10:40
 * @since 1.0
 */
public interface JarLoader {

    /**
     * 根据path获取jar
     *
     * @param path jar path
     * @return File
     */
    default File downLoad(String path) throws FileNotFoundException {
        return downLoad(path, false);
    }

    /**
     * 根据path获取jar
     *
     * @param path  jar path
     * @param cache 是否将下载下来的jar缓存
     * @return File
     */
    File downLoad(String path, boolean cache) throws FileNotFoundException;

    /**
     * 根据path寻找jar
     *
     * @param path jar path
     * @return URL
     */
    default URL find(String path) throws FileNotFoundException, MalformedURLException {
        return find(path, false);
    }

    /**
     * 根据path寻找jar
     *
     * @param path jar path
     * @param cache 是否缓存jar
     * @return URL
     */
    URL find(String path, boolean cache) throws FileNotFoundException, MalformedURLException;

}
