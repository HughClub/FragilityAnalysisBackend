package com.xy.springboot.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 任务视图对象
 */
@Data
public class TaskVO implements Serializable {
    /**
     * 任务 id
     */
    private Long taskId;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 任务状态
     */
    private String taskStatus;

    /**
     * 任务创建时间
     */
    private Date createTime;

    /**
     * 任务更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

}
