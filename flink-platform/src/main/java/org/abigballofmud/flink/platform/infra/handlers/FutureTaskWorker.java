package org.abigballofmud.flink.platform.infra.handlers;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/04/02 21:54
 * @since 1.0
 */
@Data
@AllArgsConstructor
public class FutureTaskWorker<T, R> {

    /**
     * 需要异步执行的任务
     */
    private List<T> taskList;

    /**
     * 需要执行的方法
     */
    private Function<T, CompletableFuture<R>> workFunction;

    public CompletableFuture<Void> getAllCompletableFuture(){
        return CompletableFuture.allOf(taskList.stream().map(workFunction).toArray(CompletableFuture[]::new));
    }
}
