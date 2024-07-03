package com.xy.springboot.model.dto.task;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sc-xy
 * @time 2024/7/3
 */
@Data
public class TaskRunRequest implements Serializable {
    /**
     * 任务id
     */
    private Long taskId;
    /**
     * 任务阶段
     */
    private Integer taskStage;

    private static final long serialVersionUID = 1L;
}
