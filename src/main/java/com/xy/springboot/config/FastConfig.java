package com.xy.springboot.config;


/**
 * global FastAPI local config singleton
 */
public final class FastConfig {
    public static String ip = "127.0.0.1";
    public static int port = 9999;
    public static String protocol = "http";
    public static String getTaskUrl(Long taskId, int stepId) {
        return String.format("%s://%s:%d/step/%d/%d", protocol, ip, port, taskId, stepId);
    }
}