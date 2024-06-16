package com.xy.springboot.model.entity;


import lombok.Data;

@Data
public class AnalyzeConfig {
    private String Num_S; // 样本（模型）数量
    private String TwrTH; // 塔筒顶部高度/m
    private String TwrBD; // 塔筒底部外径/m
    private String TwrBT; // 塔筒底部厚度/m
    private String TwrTD; // 塔筒顶部外径/m
    private String TwrTT; // 塔筒顶部厚度/m
    private String SubTH; // 下部结构顶部（转换平台）高度/m
    private String WatDep; // 水深/m
    private String SubUD; // 下部结构一致外径/m
    private String SubUT; // 下部结构一致厚度/m
    private NormalDistribution MatStr; // 材料屈服强度/Pa的正态分布
    private String MatMod; // 材料弹性模量/Pa
    private String MatDen; // 材料密度/kg/m^3
    private String MatPoR; // 材料泊松比
    private String HubMass; // 轮毂质量/kg
    private String NacMass; // 机舱质量/kg
    private String HubH; // 轮毂高度/m
    private String BlDR; // 叶片阻尼比/%
    private String StDR; // 结构阻尼比/%
    private String WindSpd; // 平均风速/m/s
    private UniformDistribution WindDir; // 风向/deg
    private String TurbC; // 湍流强度
    private String Z0; // 地面粗糙长度/m
    private String WaveHs; // 波浪极值波高/m
    private String WaveTp; // 波浪峰值周期/s
    private String WaveDir; // 波浪入流方向/deg
    private String SeisPGA; // 地震PGA/g
    private String SeisDir; // 地震入射方向/deg

    // 正态分布内部类
    @Data
    public static class NormalDistribution {
        private String mean;      // 正态分布的均值
        private String variance;  // 正态分布的方差

        public NormalDistribution(String mean, String variance) {
            this.mean = mean;
            this.variance = variance;
        }
    }

    // 均匀分布内部类
    @Data
    public static class UniformDistribution {
        private String min; // 均匀分布的最小值
        private String max; // 均匀分布的最大值

        public UniformDistribution(String min, String max) {
            this.min = min;
            this.max = max;
        }
    }

}
