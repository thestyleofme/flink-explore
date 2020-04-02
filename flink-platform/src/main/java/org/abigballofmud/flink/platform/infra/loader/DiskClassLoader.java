package org.abigballofmud.flink.platform.infra.loader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/24 10:21
 * @since 1.0
 */
@Slf4j
public class DiskClassLoader extends ClassLoader {

    private String libPath;

    public DiskClassLoader(String path) {
        libPath = path;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String fileName = getFileName(name);
        File file = new File(libPath, fileName);
        byte[] data;
        try (FileInputStream is = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            int len;
            while ((len = is.read()) != -1) {
                bos.write(len);
            }
            data = bos.toByteArray();
            return defineClass(name, data, 0, data.length);
        } catch (IOException e) {
            log.error("load .class file error", e);
        }
        return super.findClass(name);
    }

    /**
     * 获取要加载 的class文件名
     */
    private String getFileName(String name) {
        int index = name.lastIndexOf('.');
        if (index == -1) {
            return name + ".class";
        } else {
            return name.substring(index + 1) + ".class";
        }
    }

}

