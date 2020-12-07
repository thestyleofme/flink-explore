package com.github.thestyleofme.flink.practice.sqlsubmit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * <p>
 * 往kafka里灌数据
 * java -cp /data/flink/flink-app-1.0-SNAPSHOT-jar-with-dependencies.jar \
 *  com.github.thestyleofme.flink.practice.sqlsubmit.KafkaSenderFromLog 1000 \
 *  | /usr/hdp/3.1.0.0-78/kafka/bin/kafka-console-producer.sh \
 *  --broker-list hdsp001:6667,hdsp002:6667,hdsp003:6667 \
 *  --topic user_behavior
 * </p>
 *
 * @author isacc 2020/03/10 19:07
 * @since 1.0
 */
public class KafkaSenderFromLog {

    /**
     * 每秒1000条
     */
    private static final long SPEED = 1000;

    public static void main(String[] args) {
        long speed = SPEED;
        if (args.length > 0) {
            speed = Long.parseLong(args[0]);
        }
        // 每条耗时多少毫秒
        long delay = 1000_000 / speed;
        try (InputStream inputStream = KafkaSenderFromLog.class.getClassLoader().getResourceAsStream("user_behavior.log")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
            long start = System.nanoTime();
            while (reader.ready()) {
                String line = reader.readLine();
                System.out.println(line);

                long end = System.nanoTime();
                long diff = end - start;
                while (diff < (delay * SPEED)) {
                    Thread.sleep(1);
                    end = System.nanoTime();
                    diff = end - start;
                }
                start = end;
            }
            reader.close();
        } catch (InterruptedException | IOException e) {
            throw new IllegalStateException();
        }
    }
}
