package com.xy.springboot.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Uncertainty参数配置
 * @TableName uncertainty
 */
@TableName(value ="uncertainty")
@Data
public class Uncertainty implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务id
     */
    @TableField(value = "taskId")
    private Long taskId;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableField(value = "isDelete")
    private Integer isDelete;

    /**
     * 样本（模型）数量
     */
    @TableField(value = "Num_S")
    private String num_S;

    /**
     * 塔筒顶部高度/m
     */
    @TableField(value = "TwrTH")
    private String twrTH;

    /**
     * 塔筒底部外径/m
     */
    @TableField(value = "TwrBD")
    private String twrBD;

    /**
     * 塔筒底部厚度/m
     */
    @TableField(value = "TwrBT")
    private String twrBT;

    /**
     * 塔筒顶部外径/m
     */
    @TableField(value = "TwrTD")
    private String twrTD;

    /**
     * 塔筒顶部厚度/m
     */
    @TableField(value = "TwrTT")
    private String twrTT;

    /**
     * 下部结构顶部（转换平台）高度/m
     */
    @TableField(value = "SubTH")
    private String subTH;

    /**
     * 水深/m
     */
    @TableField(value = "WatDep")
    private String watDep;

    /**
     * 下部结构一致外径/m
     */
    @TableField(value = "SubUD")
    private String subUD;

    /**
     * 下部结构一致厚度/m
     */
    @TableField(value = "SubUT")
    private String subUT;

    /**
     * 材料屈服强度/Pa的正态分布均值
     */
    @TableField(value = "MatStrMean")
    private String matStrMean;

    /**
     * 材料屈服强度/Pa的正态分布方差
     */
    @TableField(value = "MatStrVariance")
    private String matStrVariance;

    /**
     * 材料弹性模量/Pa
     */
    @TableField(value = "MatMod")
    private String matMod;

    /**
     * 材料密度/kg/m^3
     */
    @TableField(value = "MatDen")
    private String matDen;

    /**
     * 材料泊松比
     */
    @TableField(value = "MatPoR")
    private String matPoR;

    /**
     * 轮毂质量/kg
     */
    @TableField(value = "HubMass")
    private String hubMass;

    /**
     * 机舱质量/kg
     */
    @TableField(value = "NacMass")
    private String nacMass;

    /**
     * 轮毂高度/m
     */
    @TableField(value = "HubH")
    private String hubH;

    /**
     * 叶片阻尼比/%
     */
    @TableField(value = "BlDR")
    private String blDR;

    /**
     * 结构阻尼比/%
     */
    @TableField(value = "StDR")
    private String stDR;

    /**
     * 平均风速/m/s
     */
    @TableField(value = "WindSpd")
    private String windSpd;

    /**
     * 风向/deg最小值
     */
    @TableField(value = "WindDirMin")
    private String windDirMin;

    /**
     * 风向/deg最大值
     */
    @TableField(value = "WindDirMax")
    private String windDirMax;

    /**
     * 湍流强度
     */
    @TableField(value = "TurbC")
    private String turbC;

    /**
     * 地面粗糙长度/m
     */
    @TableField(value = "Z0")
    private String z0;

    /**
     * 波浪极值波高/m
     */
    @TableField(value = "WaveHs")
    private String waveHs;

    /**
     * 波浪峰值周期/s
     */
    @TableField(value = "WaveTp")
    private String waveTp;

    /**
     * 波浪入流方向/deg
     */
    @TableField(value = "WaveDir")
    private String waveDir;

    /**
     * 地震PGA/g
     */
    @TableField(value = "SeisPGA")
    private String seisPGA;

    /**
     * 地震入射方向/deg
     */
    @TableField(value = "SeisDir")
    private String seisDir;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Uncertainty other = (Uncertainty) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getTaskId() == null ? other.getTaskId() == null : this.getTaskId().equals(other.getTaskId()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
                && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()))
                && (this.getNum_S() == null ? other.getNum_S() == null : this.getNum_S().equals(other.getNum_S()))
                && (this.getTwrTH() == null ? other.getTwrTH() == null : this.getTwrTH().equals(other.getTwrTH()))
                && (this.getTwrBD() == null ? other.getTwrBD() == null : this.getTwrBD().equals(other.getTwrBD()))
                && (this.getTwrBT() == null ? other.getTwrBT() == null : this.getTwrBT().equals(other.getTwrBT()))
                && (this.getTwrTD() == null ? other.getTwrTD() == null : this.getTwrTD().equals(other.getTwrTD()))
                && (this.getTwrTT() == null ? other.getTwrTT() == null : this.getTwrTT().equals(other.getTwrTT()))
                && (this.getSubTH() == null ? other.getSubTH() == null : this.getSubTH().equals(other.getSubTH()))
                && (this.getWatDep() == null ? other.getWatDep() == null : this.getWatDep().equals(other.getWatDep()))
                && (this.getSubUD() == null ? other.getSubUD() == null : this.getSubUD().equals(other.getSubUD()))
                && (this.getSubUT() == null ? other.getSubUT() == null : this.getSubUT().equals(other.getSubUT()))
                && (this.getMatStrMean() == null ? other.getMatStrMean() == null : this.getMatStrMean().equals(other.getMatStrMean()))
                && (this.getMatStrVariance() == null ? other.getMatStrVariance() == null : this.getMatStrVariance().equals(other.getMatStrVariance()))
                && (this.getMatMod() == null ? other.getMatMod() == null : this.getMatMod().equals(other.getMatMod()))
                && (this.getMatDen() == null ? other.getMatDen() == null : this.getMatDen().equals(other.getMatDen()))
                && (this.getMatPoR() == null ? other.getMatPoR() == null : this.getMatPoR().equals(other.getMatPoR()))
                && (this.getHubMass() == null ? other.getHubMass() == null : this.getHubMass().equals(other.getHubMass()))
                && (this.getNacMass() == null ? other.getNacMass() == null : this.getNacMass().equals(other.getNacMass()))
                && (this.getHubH() == null ? other.getHubH() == null : this.getHubH().equals(other.getHubH()))
                && (this.getBlDR() == null ? other.getBlDR() == null : this.getBlDR().equals(other.getBlDR()))
                && (this.getStDR() == null ? other.getStDR() == null : this.getStDR().equals(other.getStDR()))
                && (this.getWindSpd() == null ? other.getWindSpd() == null : this.getWindSpd().equals(other.getWindSpd()))
                && (this.getWindDirMin() == null ? other.getWindDirMin() == null : this.getWindDirMin().equals(other.getWindDirMin()))
                && (this.getWindDirMax() == null ? other.getWindDirMax() == null : this.getWindDirMax().equals(other.getWindDirMax()))
                && (this.getTurbC() == null ? other.getTurbC() == null : this.getTurbC().equals(other.getTurbC()))
                && (this.getZ0() == null ? other.getZ0() == null : this.getZ0().equals(other.getZ0()))
                && (this.getWaveHs() == null ? other.getWaveHs() == null : this.getWaveHs().equals(other.getWaveHs()))
                && (this.getWaveTp() == null ? other.getWaveTp() == null : this.getWaveTp().equals(other.getWaveTp()))
                && (this.getWaveDir() == null ? other.getWaveDir() == null : this.getWaveDir().equals(other.getWaveDir()))
                && (this.getSeisPGA() == null ? other.getSeisPGA() == null : this.getSeisPGA().equals(other.getSeisPGA()))
                && (this.getSeisDir() == null ? other.getSeisDir() == null : this.getSeisDir().equals(other.getSeisDir()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTaskId() == null) ? 0 : getTaskId().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        result = prime * result + ((getNum_S() == null) ? 0 : getNum_S().hashCode());
        result = prime * result + ((getTwrTH() == null) ? 0 : getTwrTH().hashCode());
        result = prime * result + ((getTwrBD() == null) ? 0 : getTwrBD().hashCode());
        result = prime * result + ((getTwrBT() == null) ? 0 : getTwrBT().hashCode());
        result = prime * result + ((getTwrTD() == null) ? 0 : getTwrTD().hashCode());
        result = prime * result + ((getTwrTT() == null) ? 0 : getTwrTT().hashCode());
        result = prime * result + ((getSubTH() == null) ? 0 : getSubTH().hashCode());
        result = prime * result + ((getWatDep() == null) ? 0 : getWatDep().hashCode());
        result = prime * result + ((getSubUD() == null) ? 0 : getSubUD().hashCode());
        result = prime * result + ((getSubUT() == null) ? 0 : getSubUT().hashCode());
        result = prime * result + ((getMatStrMean() == null) ? 0 : getMatStrMean().hashCode());
        result = prime * result + ((getMatStrVariance() == null) ? 0 : getMatStrVariance().hashCode());
        result = prime * result + ((getMatMod() == null) ? 0 : getMatMod().hashCode());
        result = prime * result + ((getMatDen() == null) ? 0 : getMatDen().hashCode());
        result = prime * result + ((getMatPoR() == null) ? 0 : getMatPoR().hashCode());
        result = prime * result + ((getHubMass() == null) ? 0 : getHubMass().hashCode());
        result = prime * result + ((getNacMass() == null) ? 0 : getNacMass().hashCode());
        result = prime * result + ((getHubH() == null) ? 0 : getHubH().hashCode());
        result = prime * result + ((getBlDR() == null) ? 0 : getBlDR().hashCode());
        result = prime * result + ((getStDR() == null) ? 0 : getStDR().hashCode());
        result = prime * result + ((getWindSpd() == null) ? 0 : getWindSpd().hashCode());
        result = prime * result + ((getWindDirMin() == null) ? 0 : getWindDirMin().hashCode());
        result = prime * result + ((getWindDirMax() == null) ? 0 : getWindDirMax().hashCode());
        result = prime * result + ((getTurbC() == null) ? 0 : getTurbC().hashCode());
        result = prime * result + ((getZ0() == null) ? 0 : getZ0().hashCode());
        result = prime * result + ((getWaveHs() == null) ? 0 : getWaveHs().hashCode());
        result = prime * result + ((getWaveTp() == null) ? 0 : getWaveTp().hashCode());
        result = prime * result + ((getWaveDir() == null) ? 0 : getWaveDir().hashCode());
        result = prime * result + ((getSeisPGA() == null) ? 0 : getSeisPGA().hashCode());
        result = prime * result + ((getSeisDir() == null) ? 0 : getSeisDir().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", taskId=").append(taskId);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", num_S=").append(num_S);
        sb.append(", twrTH=").append(twrTH);
        sb.append(", twrBD=").append(twrBD);
        sb.append(", twrBT=").append(twrBT);
        sb.append(", twrTD=").append(twrTD);
        sb.append(", twrTT=").append(twrTT);
        sb.append(", subTH=").append(subTH);
        sb.append(", watDep=").append(watDep);
        sb.append(", subUD=").append(subUD);
        sb.append(", subUT=").append(subUT);
        sb.append(", matStrMean=").append(matStrMean);
        sb.append(", matStrVariance=").append(matStrVariance);
        sb.append(", matMod=").append(matMod);
        sb.append(", matDen=").append(matDen);
        sb.append(", matPoR=").append(matPoR);
        sb.append(", hubMass=").append(hubMass);
        sb.append(", nacMass=").append(nacMass);
        sb.append(", hubH=").append(hubH);
        sb.append(", blDR=").append(blDR);
        sb.append(", stDR=").append(stDR);
        sb.append(", windSpd=").append(windSpd);
        sb.append(", windDirMin=").append(windDirMin);
        sb.append(", windDirMax=").append(windDirMax);
        sb.append(", turbC=").append(turbC);
        sb.append(", z0=").append(z0);
        sb.append(", waveHs=").append(waveHs);
        sb.append(", waveTp=").append(waveTp);
        sb.append(", waveDir=").append(waveDir);
        sb.append(", seisPGA=").append(seisPGA);
        sb.append(", seisDir=").append(seisDir);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}