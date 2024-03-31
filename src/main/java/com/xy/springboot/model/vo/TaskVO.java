package com.xy.springboot.model.vo;

import java.util.Date;

/**
 * 任务视图对象
 */

public class TaskVO {
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

}
