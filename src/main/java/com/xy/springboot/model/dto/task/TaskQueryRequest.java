package com.xy.springboot.model.dto.task;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务查询请求
 */
@Data
public class TaskQueryRequest {

    /**
     * 任务状态
     */
    private String taskStatus;
}
