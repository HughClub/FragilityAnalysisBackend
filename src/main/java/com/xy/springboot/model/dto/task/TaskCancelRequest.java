package com.xy.springboot.model.dto.task;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sc-xy
 * @time 2024/7/4
 */
@Data
public class TaskCancelRequest implements Serializable {
    /**
     * 任务id
     */
    private Long taskId;

    private static final long serialVersionUID = 1L;
}
