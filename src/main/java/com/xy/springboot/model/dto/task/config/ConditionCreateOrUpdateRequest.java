package com.xy.springboot.model.dto.task.config;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sc-xy
 * @time 2024/7/2
 */
@Data
public class ConditionCreateOrUpdateRequest implements Serializable {
    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 分析方式 IDA = 1, 随机模拟= 2
     */
    private String anaTy;

    /**
     * 每个风速强度需模拟的随机风场数量
     */
    private String numF;

    /**
     * 风速强度输入方法
     */
    private String IMImpM;

    /**
     * 轮毂风速强度/m/s
     */
    private String windSpd;

    /**
     * 波浪参数输入方法
     */
    private String WPInpM;

    /**
     * 波浪极值波高/m
     */
    private String waveHsF;

    /**
     * 波浪峰值周期/s
     */
    private String waveTpF;

    /**
     * 风机运行模式
     */
    private String OM;

    /**
     * 制动停机开始时间/s
     */
    private String shutT;

    /**
     * 每个工况模拟时间/s
     */
    private String simT;

    /**
     * OpenFAST分析步长/s
     */
    private String openFastDT;

    /**
     * 叶片模拟方法
     */
    private String bldSimM;

    /**
     * 湍流模型
     */
    private String turbModel;

    /**
     * IEC 湍流类型
     */
    private String IEC_WindType;

    /**
     * 风剖面类型
     */
    private String windProfileType;

    /**
     * 风剖面幂指数
     */
    private String PLExp;

    /**
     * 尾流模型
     */
    private String wakeMod;

    /**
     * OpenSees运行风速强度范围
     */
    private String IM_Model;

    /**
     * 从第N个强度开始OpenSees计算
     */
    private String IM_Nth;

    /**
     * 结构阻尼比
     */
    private String dampR;

    /**
     * OpenSees分析步长/s
     */
    private String openSeesDt;
    private static final long serialVersionUID = 1L;
}
