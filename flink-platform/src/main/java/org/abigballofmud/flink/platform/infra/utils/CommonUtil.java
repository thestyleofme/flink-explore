package org.abigballofmud.flink.platform.infra.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import org.abigballofmud.flink.platform.infra.execeptions.ClassLoaderException;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/04/02 10:55
 * @since 1.0
 */
public class CommonUtil {

    private CommonUtil() {
        throw new IllegalStateException("util class");
    }

    public static File multiPartFileToFile(MultipartFile multipartFile) {
        Assert.notNull(multipartFile, "multipartFile must not be null");
        try {
            File toFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            byte[] bytes = FileCopyUtils.copyToByteArray(multipartFile.getInputStream());
            FileCopyUtils.copy(bytes, toFile);
            return toFile;
        } catch (IOException e) {
            throw new ClassLoaderException("multiPartFileToFile error", e);
        }
    }
}
