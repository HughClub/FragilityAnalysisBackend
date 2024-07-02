package com.xy.springboot.model.dto.task;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 任务查询请求
 */
@Data
public class TaskQueryRequest implements Serializable {

    /**
     * 任务状态
     */
    private String taskStatus;
    /**
     * 任务名称
     */
    private String taskName;

    private static final long serialVersionUID = 1L;
}
