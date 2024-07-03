package com.xy.springboot.boot;

public interface Launch {

    void terminatePythonTask(Long taskId);

    void executePythonTask(Long taskId, Integer taskStage);
}
