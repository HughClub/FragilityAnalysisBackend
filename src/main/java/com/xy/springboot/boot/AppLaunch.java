package com.xy.springboot.boot;

import com.xy.springboot.common.FastResponse;
import com.xy.springboot.config.FastConfig;
import com.xy.springboot.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

@Component
@Slf4j
public class AppLaunch implements Launch {
    @Resource
    private TaskService taskService;

    // 有一个线程池，负责实行任务
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 5, 35, TimeUnit.MINUTES, new LinkedBlockingQueue<>(100));
    private final Map<Long, Process> processMap = new ConcurrentHashMap<>();
    private final Map<Long, Future<String>> futureMap = new ConcurrentHashMap<>();
    private final long timeoutMillis = 3 * 60 * 60 * 1000;

    @Override
    public void terminatePythonTask(Long taskId) {
        Future<String> future = futureMap.get(taskId);
        if (Objects.nonNull(future) &&!future.isDone()) {
            future.cancel(true);
            futureMap.remove(taskId);
            taskService.taskUpdate(taskId, "failed");
        }
    }

    @Override
    public void executePythonTask(Long taskId, Integer taskStage) {
        Future<String> future = threadPoolExecutor.submit(() -> {
            // GET http://localhost:{port}}/step/{taskId}/{taskStage}
            RestTemplate restTemplate = new RestTemplate();
            String url = FastConfig.getTaskUrl(taskId, taskStage);
            String result = null;
            log.warn("api call: {}", url);
            try {
                FastResponse response = restTemplate.getForObject(url, FastResponse.class);
                log.warn("api response: {}", response);
                result = response != null ? response.toString() : "no response";
                taskService.taskUpdate(taskId, "done");

            } catch (Exception e) {
                log.error("Task {} stage {} failed to run", taskId, taskStage, e);
                taskService.taskUpdate(taskId, "failed");
                throw new RuntimeException(e);
            } finally {
                futureMap.remove(taskId);
            }
            return result;

            // region old code with process launch
//            Process process = null;
//            try {
//                ProcessBuilder processBuilder = new ProcessBuilder("python", TaskUtils.getPythonScript(),
//                        "--Path_Inp", TaskUtils.getInputDir(taskId), "--Task_Stage", "Step_" + taskStage,
//                        "--Path_Out", TaskUtils.getOutputDir(taskId));
//                log.info("python " + TaskUtils.getPythonScript() + " --Path_Inp " + TaskUtils.getInputDir(taskId) + " --Task_Stage Step_" + taskId + " --Path_Out " + TaskUtils.getOutputDir(taskId));
//                // python Main.py --Path_Inp D:/upload/{taskId}/input/ --Task_Stage Step_1 --Path_Out D:/upload/{taskId}/output
//                log.info("Task {} started {} stage", taskId, taskStage);
//                process = processBuilder.start();
//                processMap.put(taskId, process);
//
//                boolean isFinished = process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS);
//
//                if (isFinished) {
//                    int exitValue = process.exitValue();
//                    if (exitValue == 0) {
//                        Task task = taskService.getById(taskId);
//                        taskService.taskUpdate(taskId, "done");
//
//                    } else {
//                        taskService.taskUpdate(taskId, "failed");
//                    }
//                } else {
//                    process.destroy();
//                    taskService.taskUpdate(taskId, "failed");
//                }
//            } catch (IOException | InterruptedException e) {
//                log.error("Task {} stage {} failed to run", taskId, taskStage, e);
//                taskService.taskUpdate(taskId, "failed");
//                throw new RuntimeException(e);
//            } finally {
//                if (Objects.nonNull(process)) {
//                    processMap.remove(taskId);
//                }
//            }
            // endregion
        });
        futureMap.put(taskId, future);
    }

//    public class AnalysisTask implements Runnable {
//        // python程序地址
//        private static final String PYTHON_SCRIPT = "C:\\Users\\XY\\Desktop\\springboot-init-master\\py\\Codes\\Main.py";
//        private final Long taskId;
//        private final TaskService taskService;
//
//        private final Integer taskStage;
//
//        public AnalysisTask(Long taskId, TaskService taskService, Integer taskStage) {
//            this.taskId = taskId;
//            this.taskService = taskService;
//            this.taskStage = taskStage;
//        }
//
//        @Override
//        public void run() {
//            // 任务执行逻辑
//            // 占据任务
//            taskService.taskUpdate(taskId, "running");
//            // 执行Python程序
////            python C:\Users\XY\Desktop\ysxfx\Codes\Main.py --Path_Inp C:/Users/XY/Desktop/ysxfx/Input
////            --Path_FAST C:/Users/XY/Desktop/ysxfx/OpenFAST --Paht_Out C:\Users\XY\Desktop\output1
//            ProcessBuilder processBuilder = new ProcessBuilder("python", PYTHON_SCRIPT,
//                    "--Path_Inp", TaskUtils.getInputDir(taskId), "--Task_Stage", "Step_" + taskId,
//                    "--Path_Out", TaskUtils.getOutputDir(taskId));
//            log.info("Task {} started", taskId);
//            Process process = null;
//            try {
//                process = processBuilder.start();
//            } catch (IOException e) {
//                log.info("Task {} failed to start", taskId);
//                taskService.taskUpdate(taskId, "failed");
//                throw new RuntimeException(e);
//            }
//
//            // 设置超时时间
//            long timeout = 3 * 60 * 60 * 1000; // 3小时
//            long startTime = System.currentTimeMillis();
//
//
//        }
//    }
}

