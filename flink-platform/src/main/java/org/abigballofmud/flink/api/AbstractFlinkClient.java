package org.abigballofmud.flink.api;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.flink.api.loader.JarLoader;
import org.abigballofmud.flink.api.request.JarSubmitFlinkRequest;
import org.abigballofmud.flink.api.response.SubmitFlinkResponse;
import org.abigballofmud.flink.execeptions.CommonException;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/25 17:34
 * @since 1.0
 */
@Slf4j
public abstract class AbstractFlinkClient implements FlinkClient {

    private JarLoader jarLoader;

    public AbstractFlinkClient(JarLoader jarLoader) {
        this.jarLoader = jarLoader;
    }

    public void submitJar(ClusterClient<?> clusterClient,
                          JarSubmitFlinkRequest request,
                          Consumer<SubmitFlinkResponse> consumer) {
        log.trace("start submit jar request, entryClass: {}", request.getEntryClass());
        try {
            // File file = jarLoader.downLoad(request.getJarPath(), request.isCache());
            // List<String> programArgs = JarArgUtil.tokenizeArguments(request.getProgramArgs());
            // String[] programArgsArray = new String[programArgs.size()];
            // PackagedProgram packagedProgram = PackagedProgram.newBuilder()
            //         .setJarFile(file)
            //         .setEntryPointClassName(request.getEntryClass())
            //         .setArguments(programArgs.toArray(programArgsArray))
            //         .build();
            // final ClassLoader classLoader = packagedProgram.getUserCodeClassLoader();
            // Optimizer optimizer = new Optimizer(new DataStatistics(), new DefaultCostEstimator(), new Configuration());
            // FlinkPlan plan = ClusterClient.getOptimizedPlan(optimizer, packagedProgram, request.getParallelism());
            // Savepoint restore settings
            final SavepointRestoreSettings savepointSettings;
            String savepointPath = request.getSavepointPath();
            if (StringUtils.isNotEmpty(savepointPath)) {
                Boolean allowNonRestoredOpt = request.getAllowNonRestoredState();
                boolean allowNonRestoredState = allowNonRestoredOpt != null && allowNonRestoredOpt;
                savepointSettings = SavepointRestoreSettings.forPath(savepointPath, allowNonRestoredState);
            } else {
                savepointSettings = SavepointRestoreSettings.none();
            }
            JobGraph jobGraph = new JobGraph(request.getJobName());
            jobGraph.setSavepointRestoreSettings(savepointSettings);
            CompletableFuture<JobID> completableFuture = clusterClient.submitJob(jobGraph);
            JobID jobId = completableFuture.get();
            log.debug("jobId: {}", jobId);
        } catch (Throwable e) {
            log.error("submit jar request fail");
            throw new CommonException("submit jar request fail", e);
        }
    }
}
