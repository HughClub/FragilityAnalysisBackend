package com.xy.springboot.model.dto.task.config;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sc-xy
 * @time 2024/7/2
 */
@Data
public class UncertaintyCreateOrUpdateRequest implements Serializable {
    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 样本（模型）数量
     */
    private String num_S;

    /**
     * 塔筒顶部高度/m
     */
    private String twrTH;

    /**
     * 塔筒底部外径/m
     */
    private String twrBD;

    /**
     * 塔筒底部厚度/m
     */
    private String twrBT;

    /**
     * 塔筒顶部外径/m
     */
    private String twrTD;

    /**
     * 塔筒顶部厚度/m
     */
    private String twrTT;

    /**
     * 下部结构顶部（转换平台）高度/m
     */
    private String subTH;

    /**
     * 水深/m
     */
    private String watDep;

    /**
     * 下部结构一致外径/m
     */
    private String subUD;

    /**
     * 下部结构一致厚度/m
     */
    private String subUT;

    /**
     * 材料屈服强度/Pa的正态分布均值
     */
    private String matStrMean;

    /**
     * 材料屈服强度/Pa的正态分布方差
     */
    private String matStrVariance;

    /**
     * 材料弹性模量/Pa
     */
    private String matMod;

    /**
     * 材料密度/kg/m^3
     */
    private String matDen;

    /**
     * 材料泊松比
     */
    private String matPoR;

    /**
     * 轮毂质量/kg
     */
    private String hubMass;

    /**
     * 机舱质量/kg
     */
    private String nacMass;

    /**
     * 轮毂高度/m
     */
    private String hubH;

    /**
     * 叶片阻尼比/%
     */
    private String blDR;

    /**
     * 结构阻尼比/%
     */
    private String stDR;

    /**
     * 平均风速/m/s
     */
    private String windSpd;

    /**
     * 风向/deg最小值
     */
    private String windDirMin;

    /**
     * 风向/deg最大值
     */
    private String windDirMax;

    /**
     * 湍流强度
     */
    private String turbC;

    /**
     * 地面粗糙长度/m
     */
    private String z0;

    /**
     * 波浪极值波高/m
     */
    private String waveHs;

    /**
     * 波浪峰值周期/s
     */
    private String waveTp;

    /**
     * 波浪入流方向/deg
     */
    private String waveDir;

    /**
     * 地震PGA/g
     */
    private String seisPGA;

    /**
     * 地震入射方向/deg
     */
    private String seisDir;

    private static final long serialVersionUID = 1L;

}
