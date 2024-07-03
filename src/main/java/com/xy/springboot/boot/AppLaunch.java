package com.xy.springboot.boot;

import com.xy.springboot.model.entity.Task;
import com.xy.springboot.service.TaskService;
import com.xy.springboot.utils.TaskUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AppLaunch implements Launch {
    @Resource
    private TaskService taskService;

    // 有一个线程池，负责实行任务
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 5, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<>(100));
    private final Map<Long, Process> processMap = new ConcurrentHashMap<>();
    private final long timeoutMillis = 3 * 60 * 60 * 1000;

    @Override
    public void terminatePythonTask(Long taskId) {
        Process process = processMap.get(taskId);
        if (Objects.nonNull(process) && process.isAlive()) {
            process.destroy();
            processMap.remove(taskId);
            taskService.taskUpdate(taskId, "failed");
        }
    }

    @Override
    public void executePythonTask(Long taskId, Integer taskStage) {
        threadPoolExecutor.submit(() -> {
            Process process = null;
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("python", TaskUtils.getPythonScript(),
                        "--Path_Inp", TaskUtils.getInputDir(taskId), "--Task_Stage", "Step_" + taskStage,
                        "--Path_Out", TaskUtils.getOutputDir(taskId));
                log.info("python " + TaskUtils.getPythonScript() + " --Path_Inp " + TaskUtils.getInputDir(taskId) + " --Task_Stage Step_" + taskId + " --Path_Out " + TaskUtils.getOutputDir(taskId));
                log.info("Task {} started {} stage", taskId, taskStage);
                taskService.taskUpdate(taskId, "running");
                process = processBuilder.start();
                processMap.put(taskId, process);

                boolean isFinished = process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS);

                if (isFinished) {
                    int exitValue = process.exitValue();
                    if (exitValue == 0) {
                        Task task = taskService.getById(taskId);
                        taskService.taskUpdate(taskId, "done");

                    } else {
                        taskService.taskUpdate(taskId, "failed");
                    }
                } else {
                    process.destroy();
                    taskService.taskUpdate(taskId, "failed");
                }
            } catch (IOException | InterruptedException e) {
                log.error("Task {} stage {} failed to run", taskId, taskStage, e);
                taskService.taskUpdate(taskId, "failed");
                throw new RuntimeException(e);
            } finally {
                if (Objects.nonNull(process)) {
                    processMap.remove(taskId);
                }
            }
        });
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

