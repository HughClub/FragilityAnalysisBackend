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
     * 当前阶段
     */
    private int curStage;

    /**
     * 当前状态
     */
    private String curStatus;

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
