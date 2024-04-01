package com.xy.springboot.utils;

import org.springframework.beans.factory.annotation.Value;

/**
 * 任务工具类
 */
public class TaskUtils {

    public static String getInputDir(Long taskId) {
        return "D:/upload/" + taskId + "/input";
    }

    public static String getOutputDir(Long taskId) {
        return "D:/upload/" + taskId + "/output";
    }
}
