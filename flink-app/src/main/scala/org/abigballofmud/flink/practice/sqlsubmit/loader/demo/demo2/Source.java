package org.abigballofmud.flink.practice.sqlsubmit.loader.demo.demo2;

import java.net.URI;
import javax.tools.SimpleJavaFileObject;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/24 20:15
 * @since 1.0
 */
public class Source extends SimpleJavaFileObject {

    private final String content;

    public Source(String name, Kind kind, String content) {
        super(URI.create("dynamic:///" + name.replace('.', '/') + kind.extension), kind);
        this.content = content;
    }

    @Override
    public CharSequence getCharContent(boolean ignore) {
        return this.content;
    }
}
