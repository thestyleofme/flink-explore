package org.abigballofmud.flink.sqlsubmit.loader.demo.demo2;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/24 20:13
 * @since 1.0
 */
public class Output extends SimpleJavaFileObject {

    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    public Output(String name, Kind kind) {
        //URI
        super(URI.create("dynamic:///" + name.replace('.', '/') + kind.extension), kind);
    }

    public byte[] toByteArray() {
        // 外部调用，生成Class
        return this.byteArrayOutputStream.toByteArray();
    }

    @Override
    public ByteArrayOutputStream openOutputStream() {
        return this.byteArrayOutputStream;
    }
}
