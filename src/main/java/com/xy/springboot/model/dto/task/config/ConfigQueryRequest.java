package com.xy.springboot.model.dto.task.config;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sc-xy
 * @time 2024/7/12
 */
@Data
public class ConfigQueryRequest implements Serializable {
    /**
     * 任务id
     */
    private Long taskId;
    /**
     * 配置类型
     */
    private String configType;
    private static final long serialVersionUID = 1L;
}
