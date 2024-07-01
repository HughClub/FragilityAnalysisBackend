package com.xy.springboot.model.dto.task;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sc-xy
 * @time 2024/7/1
 */
@Data
public class TaskCreateRequest implements Serializable {
    /**
     * 任务名称
     */
    private String taskName;

    private static final long serialVersionUID = 1L;
}
