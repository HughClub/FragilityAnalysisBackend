package com.xy.springboot.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * Condition参数配置
 * @TableName condition
 */
@TableName(value ="`condition`")
@Data
public class Condition implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 分析方式 IDA = 1, 随机模拟= 2
     */
    @TableField(value = "AnaTy")
    private String anaTy;

    /**
     * 每个风速强度需模拟的随机风场数量
     */
    @TableField(value = "NumF")
    private String numF;

    /**
     * 风速强度输入方法
     */
    @TableField(value = "IMImpM")
    private String IMImpM;

    /**
     * 轮毂风速强度/m/s
     */
    @TableField(value = "WindSpd")
    private String windSpd;

    /**
     * 波浪参数输入方法
     */
    @TableField(value = "WPInpM")
    private String WPInpM;

    /**
     * 波浪极值波高/m
     */
    @TableField(value = "WaveHsF")
    private String waveHsF;

    /**
     * 波浪峰值周期/s
     */
    @TableField(value = "WaveTpF")
    private String waveTpF;

    /**
     * 风机运行模式
     */
    @TableField(value = "OM")
    private String OM;

    /**
     * 制动停机开始时间/s
     */
    @TableField(value = "ShutT")
    private String shutT;

    /**
     * 每个工况模拟时间/s
     */
    @TableField(value = "SimT")
    private String simT;

    /**
     * OpenFAST分析步长/s
     */
    @TableField(value = "OpenFastDT")
    private String openFastDT;

    /**
     * 叶片模拟方法
     */
    @TableField(value = "BldSimM")
    private String bldSimM;

    /**
     * 湍流模型
     */
    @TableField(value = "TurbModel")
    private String turbModel;

    /**
     * IEC 湍流类型
     */
    @TableField(value = "IEC_WindType")
    private String IEC_WindType;

    /**
     * 风剖面类型
     */
    @TableField(value = "WindProfileType")
    private String windProfileType;

    /**
     * 风剖面幂指数
     */
    @TableField(value = "PLExp")
    private String PLExp;

    /**
     * 尾流模型
     */
    @TableField(value = "WakeMod")
    private String wakeMod;

    /**
     * OpenSees运行风速强度范围
     */
    @TableField(value = "IM_Model")
    private String IM_Model;

    /**
     * 从第N个强度开始OpenSees计算
     */
    @TableField(value = "IM_Nth")
    private String IM_Nth;

    /**
     * 结构阻尼比
     */
    @TableField(value = "DampR")
    private String dampR;

    /**
     * OpenSees分析步长/s
     */
    @TableField(value = "OpenSeesDt")
    private String openSeesDt;

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
        Condition other = (Condition) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTaskId() == null ? other.getTaskId() == null : this.getTaskId().equals(other.getTaskId()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()))
            && (this.getAnaTy() == null ? other.getAnaTy() == null : this.getAnaTy().equals(other.getAnaTy()))
            && (this.getNumF() == null ? other.getNumF() == null : this.getNumF().equals(other.getNumF()))
            && (this.getIMImpM() == null ? other.getIMImpM() == null : this.getIMImpM().equals(other.getIMImpM()))
            && (this.getWindSpd() == null ? other.getWindSpd() == null : this.getWindSpd().equals(other.getWindSpd()))
            && (this.getWPInpM() == null ? other.getWPInpM() == null : this.getWPInpM().equals(other.getWPInpM()))
            && (this.getWaveHsF() == null ? other.getWaveHsF() == null : this.getWaveHsF().equals(other.getWaveHsF()))
            && (this.getWaveTpF() == null ? other.getWaveTpF() == null : this.getWaveTpF().equals(other.getWaveTpF()))
            && (this.getOM() == null ? other.getOM() == null : this.getOM().equals(other.getOM()))
            && (this.getShutT() == null ? other.getShutT() == null : this.getShutT().equals(other.getShutT()))
            && (this.getSimT() == null ? other.getSimT() == null : this.getSimT().equals(other.getSimT()))
            && (this.getOpenFastDT() == null ? other.getOpenFastDT() == null : this.getOpenFastDT().equals(other.getOpenFastDT()))
            && (this.getBldSimM() == null ? other.getBldSimM() == null : this.getBldSimM().equals(other.getBldSimM()))
            && (this.getTurbModel() == null ? other.getTurbModel() == null : this.getTurbModel().equals(other.getTurbModel()))
            && (this.getIEC_WindType() == null ? other.getIEC_WindType() == null : this.getIEC_WindType().equals(other.getIEC_WindType()))
            && (this.getWindProfileType() == null ? other.getWindProfileType() == null : this.getWindProfileType().equals(other.getWindProfileType()))
            && (this.getPLExp() == null ? other.getPLExp() == null : this.getPLExp().equals(other.getPLExp()))
            && (this.getWakeMod() == null ? other.getWakeMod() == null : this.getWakeMod().equals(other.getWakeMod()))
            && (this.getIM_Model() == null ? other.getIM_Model() == null : this.getIM_Model().equals(other.getIM_Model()))
            && (this.getIM_Nth() == null ? other.getIM_Nth() == null : this.getIM_Nth().equals(other.getIM_Nth()))
            && (this.getDampR() == null ? other.getDampR() == null : this.getDampR().equals(other.getDampR()))
            && (this.getOpenSeesDt() == null ? other.getOpenSeesDt() == null : this.getOpenSeesDt().equals(other.getOpenSeesDt()));
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
        result = prime * result + ((getAnaTy() == null) ? 0 : getAnaTy().hashCode());
        result = prime * result + ((getNumF() == null) ? 0 : getNumF().hashCode());
        result = prime * result + ((getIMImpM() == null) ? 0 : getIMImpM().hashCode());
        result = prime * result + ((getWindSpd() == null) ? 0 : getWindSpd().hashCode());
        result = prime * result + ((getWPInpM() == null) ? 0 : getWPInpM().hashCode());
        result = prime * result + ((getWaveHsF() == null) ? 0 : getWaveHsF().hashCode());
        result = prime * result + ((getWaveTpF() == null) ? 0 : getWaveTpF().hashCode());
        result = prime * result + ((getOM() == null) ? 0 : getOM().hashCode());
        result = prime * result + ((getShutT() == null) ? 0 : getShutT().hashCode());
        result = prime * result + ((getSimT() == null) ? 0 : getSimT().hashCode());
        result = prime * result + ((getOpenFastDT() == null) ? 0 : getOpenFastDT().hashCode());
        result = prime * result + ((getBldSimM() == null) ? 0 : getBldSimM().hashCode());
        result = prime * result + ((getTurbModel() == null) ? 0 : getTurbModel().hashCode());
        result = prime * result + ((getIEC_WindType() == null) ? 0 : getIEC_WindType().hashCode());
        result = prime * result + ((getWindProfileType() == null) ? 0 : getWindProfileType().hashCode());
        result = prime * result + ((getPLExp() == null) ? 0 : getPLExp().hashCode());
        result = prime * result + ((getWakeMod() == null) ? 0 : getWakeMod().hashCode());
        result = prime * result + ((getIM_Model() == null) ? 0 : getIM_Model().hashCode());
        result = prime * result + ((getIM_Nth() == null) ? 0 : getIM_Nth().hashCode());
        result = prime * result + ((getDampR() == null) ? 0 : getDampR().hashCode());
        result = prime * result + ((getOpenSeesDt() == null) ? 0 : getOpenSeesDt().hashCode());
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
        sb.append(", anaTy=").append(anaTy);
        sb.append(", numF=").append(numF);
        sb.append(", IMImpM=").append(IMImpM);
        sb.append(", windSpd=").append(windSpd);
        sb.append(", WPInpM=").append(WPInpM);
        sb.append(", waveHsF=").append(waveHsF);
        sb.append(", waveTpF=").append(waveTpF);
        sb.append(", OM=").append(OM);
        sb.append(", shutT=").append(shutT);
        sb.append(", simT=").append(simT);
        sb.append(", openFastDT=").append(openFastDT);
        sb.append(", bldSimM=").append(bldSimM);
        sb.append(", turbModel=").append(turbModel);
        sb.append(", IEC_WindType=").append(IEC_WindType);
        sb.append(", windProfileType=").append(windProfileType);
        sb.append(", PLExp=").append(PLExp);
        sb.append(", wakeMod=").append(wakeMod);
        sb.append(", IM_Model=").append(IM_Model);
        sb.append(", IM_Nth=").append(IM_Nth);
        sb.append(", dampR=").append(dampR);
        sb.append(", openSeesDt=").append(openSeesDt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}