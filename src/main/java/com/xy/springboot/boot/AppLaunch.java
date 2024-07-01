//package com.xy.springboot.boot;
//
//import com.xy.springboot.model.entity.Task;
//import com.xy.springboot.service.TaskService;
//import com.xy.springboot.utils.TaskUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.io.IOException;
//import java.util.List;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//@Component
//@Slf4j
//public class AppLaunch implements Launch {
//    @Resource
//    private TaskService taskService;
//    // 有一个线程池，负责实行任务
//    private ThreadPoolExecutor threadPoolExecutor;
//
//    public AppLaunch() {
//    }
//
//    public AppLaunch(ThreadPoolExecutor threadPoolExecutor) {
//        this.threadPoolExecutor = threadPoolExecutor;
//    }
//
//    @Override
//    public int start() {
//        this.threadPoolExecutor = new ThreadPoolExecutor(5, 5, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<>(100));
//        // 拉取任务
//        for (;;) {
//            // 从任务池中拉取任务
//            if (100 - threadPoolExecutor.getQueue().size() >= 10) {
//                List<Task> tasks = taskService.listUnfinishedTask();
//                for(Task task : tasks) {
//                    // 任务执行逻辑
//                    threadPoolExecutor.execute(new AnalysisTask(task.getId(), taskService));
//                }
//            } else {
//                log.info("Task pool is full, waiting for 5 minutes");
//            }
//            try {
//                Thread.sleep(1000 * 60 * 5);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    @Override
//    public int destroy() {
//        return 0;
//    }
//
//    @Override
//    public int init() {
//        return 0;
//    }
//
//    public class AnalysisTask implements Runnable {
//        // python程序地址
//        private static final String PYTHON_SCRIPT = "C:\\Users\\XY\\Desktop\\springboot-init-master\\py\\Codes\\Main.py";
//        // OpenFast地址
//        private static final String OPENFST = "C:\\Users\\XY\\Desktop\\springboot-init-master\\py\\OpenFAST";
//        private final Long taskId;
//        private final TaskService taskService;
//
//        public AnalysisTask(Long taskId, TaskService taskService) {
//            this.taskId = taskId;
//            this.taskService = taskService;
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
//                    "--Path_Inp", TaskUtils.getInputDir(taskId), "--Path_FAST", OPENFST,
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
//            // 等待进程执行结束或超时
//            while (true) {
//                // 检查进程是否已经结束
//                try {
//                    int exitCode = process.exitValue();
//                    // 进程已经结束
//                    if (exitCode != 0) {
//                        // 报错处理
//                        log.info("Task {} failed with exit code {}", taskId, exitCode);
//                        taskService.taskUpdate(taskId, "failed");
//                    } else {
//                        log.info("Task {} finished successfully", taskId);
//                        taskService.taskUpdate(taskId, "done");
//                    }
//                    break;
//                } catch (IllegalThreadStateException e) {
//                    // 进程还在运行
//                    if (System.currentTimeMillis() - startTime > timeout) {
//                        // 超时，终止进程
//                        process.destroy();
//                        log.info("Task {} timeout", taskId);
//                        taskService.taskUpdate(taskId, "failed");
//                        break;
//                    }
//                    // 等待一段时间后再次检查
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException ex) {
//                        throw new RuntimeException(ex);
//                    }
//                }
//            }
//        }
//    }
//}
//
