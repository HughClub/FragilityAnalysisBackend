package com.xy.springboot.utils;

import com.xy.springboot.model.entity.AnalyzeConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.xy.springboot.utils.TaskUtils.getInputDir;

public class ConfigUtils {
    /**
     * 生成Uncertainty.inp文件，在对应的{taskId}/input文件夹下
     *
     * @param taskId
     * @param config
     */
    public static void generateConfigFile(Long taskId, AnalyzeConfig config) throws IOException {
        File inputDir = new File(getInputDir(taskId));
        if (!inputDir.exists()) {
            inputDir.mkdirs();
        }
        File uncertaintyFile = new File(getInputDir(taskId) + "/Uncertainty.inp");
        File conditionFile = new File(getInputDir(taskId) + "/Condition.inp");
        File vulnerabilityFile = new File(getInputDir(taskId) + "/Vulnerability.inp");
        try (BufferedWriter writer1 = new BufferedWriter(new FileWriter(uncertaintyFile));
             BufferedWriter writer2 = new BufferedWriter(new FileWriter(conditionFile));
             BufferedWriter writer3 = new BufferedWriter(new FileWriter(vulnerabilityFile))) {
            // 生成文件内容
            String uncertaintyContent = generateUncertaintyFileContent(config);
            String conditionContent = generateConditionFileContent();
            String vulnerabilityContent = generateVulnerabilityFileContent();
            // 写入文件
            writer1.write(uncertaintyContent);
            writer2.write(conditionContent);
            writer3.write(vulnerabilityContent);
        } catch (IOException e) {
            // 处理可能发生的异常
            throw new IOException("Failed to generate Uncertainty.inp file", e);
        }
    }

    public static void main(String[] args) {
        AnalyzeConfig config = new AnalyzeConfig();
        config.setNum_S("100");
        config.setTwrTH("0");
        config.setTwrBD("0");
        config.setTwrBT("0");
        config.setTwrTD("0");
        config.setTwrTT("0");
        config.setSubTH("0");
        config.setWatDep("0");
        config.setSubUD("0");
        config.setSubUT("0");
        config.setMatStr(new AnalyzeConfig.NormalDistribution("3", "25"));
        config.setMatMod("0");
        config.setMatDen("0");
        config.setMatPoR("0");
        config.setHubMass("0");
        config.setNacMass("0");
        config.setHubH("0");
        config.setBlDR("0");
        config.setStDR("0");
        config.setWindSpd("0");
        config.setWindDir(new AnalyzeConfig.UniformDistribution("1", "2"));
        config.setTurbC("0");
        config.setZ0("0");
        config.setWaveHs("0");
        config.setWaveTp("0");
        config.setWaveDir("0");
        config.setSeisPGA("0");
        config.setSeisDir("0");

        try {
            new ConfigUtils().generateConfigFile(1L, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateUncertaintyFileContent(AnalyzeConfig config) {
        StringBuilder content = new StringBuilder();

        // 添加文件头部
        content.append("---------- LHS ----------\n")
                .append("[" + config.getNum_S() + "] -- Num_S -- Number of random samples\n");

        // 添加塔架几何参数
        content.append("---------- Tower Geometry ----------\n")
                .append("[0, " + config.getTwrTH() + "] -- TwrTH -- Tower-top height (m)\n")
                .append("[0, " + config.getTwrBD() + "] -- TwrBD -- Tower-base diameter (m)\n")
                .append("[0, " + config.getTwrBT() + "] -- TwrBT -- Tower-base thickness (m)\n")
                .append("[0, " + config.getTwrTD() + "] -- TwrTD -- Tower-top diameter (m)\n")
                .append("[0, " + config.getTwrTT() + "] -- TwrTT -- Tower-top thickness (m)\n");

        // 添加单桩几何参数
        content.append("---------- Monopile Geometry ----------\n")
                .append("[0, " + config.getSubTH() + "] -- SubTH -- Substructure-top height (m)\n")
                .append("[0, " + config.getWatDep() + "] -- WatDep -- Water depth (m)\n")
                .append("[0, " + config.getSubUD() + "] -- SubUD -- Substructure uniform diameter (m)\n")
                .append("[0, " + config.getSubUT() + "] -- SubUT -- Substructure uniform thickness (m)\n");

        // 添加材料参数
        content.append("---------- Material ----------\n")
                .append("[3, " + config.getMatStr().getMean() + ", " + config.getMatStr().getVariance() + "] -- MatStr -- Substructure material yielding strength (Pa)\n")
                .append("[0, " + config.getMatMod() + "] -- MatMod -- Substructure material elastic modulus (Pa)\n")
                .append("[0, " + config.getMatDen() + "] -- MatDen -- Substructure material mass density (kg/m^3)\n")
                .append("[0, " + config.getMatPoR() + "] -- MatPoR -- Substructure material poisson ratio\n");

        // 添加轮毂-机舱组件参数
        content.append("---------- Rotor-nacelle Assembly ----------\n")
                .append("[0, " + config.getHubMass() + "] -- HubMass -- Hub Mass (kg)\n")
                .append("[0, " + config.getNacMass() + "] -- NacMass -- Nacelle Mass (kg)\n")
                .append("[0, " + config.getHubH() + "] -- HubH -- Hub height (m)\n");

        // 添加阻尼参数
        content.append("---------- Damping ----------\n")
                .append("[0, " + config.getBlDR() + "] -- BlDR -- Blade's damping ratio (%)\n")
                .append("[0, " + config.getStDR() + "] -- StDR -- structural-system damping ratio (%)\n");

        // 添加风况参数
        content.append("---------- Wind Condition ----------\n")
                .append("[0, " + config.getWindSpd() + "] -- WindSpd -- Wind speed (m/s)\n")
                .append("[1, " + config.getWindDir().getMin() + ", " + config.getWindDir().getMax() + "] -- WindDir -- Wind incident direction (degree)\n")
                .append("[0, " + config.getTurbC() + "] -- TurbC -- Turbulence characteristic\n")
                .append("[0, " + config.getZ0() + "] -- Z0 -- Surface roughness length (m)\n");

        // 添加波浪条件参数
        content.append("---------- Wave Condition ----------\n")
                .append("[0, " + config.getWaveHs() + "] -- WaveHs -- Input file for significant wave height (m)\n")
                .append("[0, " + config.getWaveTp() + "] -- WaveTp -- Input file for wave peak period (s)\n")
                .append("[0, " + config.getWaveDir() + "] -- WaveDir -- Wave incident direction (degree)\n");

        // 添加地震条件参数（当前未使用）
        content.append("---------- Seismic Condition (Unused currently) ----------\n")
                .append("[0, " + config.getSeisPGA() + "] -- SeisPGA -- Seismic PGA (g)\n")
                .append("[0, " + config.getSeisDir() + "] -- SeisDir -- Seismic incident direction (degree)\n");

        // 添加文件尾部
        content.append("---------- END ----------\n")
                .append("Distribution model {0='None', 1='Uniform', 2='Normal', 3='Lognormal', 4='Weibull' distribution}\n")
                .append("'None' means this parameter is deterministic : Parameter = [0, Value]\n")
                .append("'Uniform' : Parameter = [1, Lower bound, Upper bound]\n")
                .append("'Normal' : Parameter = [2, Mean, CoV]\n")
                .append("'LogNormal' : Parameter = [3, Mean, CoV]\n")
                .append("Weibull' : Parameter = [4, Shape, Scale]\n");

        return content.toString();
    }

    public static String generateVulnerabilityFileContent() {
        return "---------- Fragility ----------\n" +
                "[1] -- Frgl_Op -- Using results from openfast or opensees {Switch: 1=openfast, 2=openfast+opensees}\n" +
                "[3] -- IniTI -- Initial time ingored for response amplitudes processing (s)\n" +
                "[1] -- Frgl_Res -- Structural response used to establish fragility {Switch: 1=response in X direction, 2=response in Y direction, 3=response in space}\n" +
                "[1] -- Frgl_Bld -- Fragility of blades {Switch: 1=true, 2=false}\n" +
                "[0.01, 0.02, 0.04] -- DS_Bld -- Damage states of blades (Blade-tip deflection m) [used only when Frgl_Bld=1]\n" +
                "[\"All\", \"All\", \"All\"] -- IMI_Bld -- IM intensity for distribution models estimation for blades {\"All\" or [ith IM, jth IM]} [used only when Frgl_Bld=1]\n" +
                "[0] -- Frgl_Nac -- Fragility of nacelle {Switch: 1=true, 2=false}\n" +
                "[0.2, 0.4, 0.6] -- DS_Nac -- Damage states of nacelle (Nacelle acceleration m/s2) [used only when Frgl_Nac=1]\n" +
                "[\"All\", \"All\", \"All\"] -- IMI_Nac -- IM intensity for distribution models estimation for nacelle {\"All\" or [ith IM, jth IM]} [used only when Frgl_Nac=1]\n" +
                "[1] -- Frgl_Twr -- Fragility of tower {Switch: 1=true, 2=false}\n" +
                "[1] -- DS_Twr_Type -- Damage states type of tower {Switch: 1=tower-top drift, 2=tower-base stress} [used only when Frgl_Twr=1]\n" +
                "[0.001, 0.002, 0.003] -- DS_Twr -- Damage states of tower [used only when Frgl_Twr=1]\n" +
                "[\"All\", \"All\", \"All\"] -- IMI_Twr -- IM intensity for distribution models estimation for tower {\"All\" or [ith IM, jth IM]} [used only when Frgl_Twr=1]\n" +
                "[1] -- Frgl_Sub -- Fragility of monopile {Switch: 1=true, 2=false}\n" +
                "[1] -- DS_Sub_Type -- Damage states type of monopile {Switch: 1=platform drift, 2=base stress} [used only when Frgl_Mop=1]\n" +
                "[0.0003, 0.0006, 0.0009] -- DS_Sub -- Damage states of monopile [used only when Frgl_Mop=1]\n" +
                "[\"All\", \"All\", \"All\"] -- IMI_Sub -- IM intensity for distribution models estimation for monopile {\"All\" or [ith IM, jth IM]} [used only when Frgl_Mop=1]\n" +
                "[3, 25] -- HIF -- Hazard interval for fragility output\n" +
                "[0.01] -- dIM -- Step of hazard level for fragility output\n" +
                "---------- Vulnerability ----------\n" +
                "[100] -- Num_RS -- Number of random simulation for each intensity \n" +
                "[0, 0.4, 0.8, 1.0] -- LossR_Bld -- Loss ratio definition for blades [used only when Frgl_Bld=1]\n" +
                "[0, 0.4, 0.8, 1.0] -- LossR_Nac -- Loss ratio definition for nacelle [used only when Frgl_Nac=1]\n" +
                "[0, 0.4, 0.8, 1.0] -- LossR_Twr -- Loss ratio definition for tower [used only when Frgl_Twr=1]\n" +
                "[0, 0.4, 0.8, 1.0] -- LossR_Mop -- Loss ratio definition for monopile [used only when Frgl_Mop=1]\n" +
                "---------- Dependency ----------\n" +
                "[1] -- Dep_Bld -- Damage states of blade and tower will be altered to severe damage if 'tower strike' occurred {Switch: 1=true, 2=false}\n" +
                "[1] -- Dep_Nac -- Damage states of blade will be altered to severe damage if severe damage occurred for nacelle {Switch: 1=true, 2=false}\n" +
                "[1] -- Dep_Twr -- Damage states of both blade and nacelle will be altered to severe damage if severe damage occurred for tower {Switch: 1=true, 2=false}\n" +
                "[1] -- Dep_Mop -- Damage states of blade, nacelle, and tower will be altered to severe damage if severe damage occurred for monopile {Switch: 1=true, 2=false}\n" +
                "---------- END ----------\n";
    }

    public static String generateConditionFileContent() {
        return "---------- Analyze type ----------\n" +
                "[1] -- AnaTy -- Analyze type {1=IDA, 2=Uncertian}\n" +
                "[10] -- NumF -- Number of wind-wave fields for each hazrad intensity\n" +
                "---------- IDA Setting ---------- [Used only when AnaTy=1]\n" +
                "[1] -- IMImpM -- Intensity measure input method {1=arithmetic series, 2=user define}\n" +
                "[10, 15, 1] -- WindSpd -- Wind speed at hub height (m/s), Parameter = [Cut-in ,Cut-out, Step] when IMImp=1, and input file when IMImp=2\n" +
                "[2] -- WPInpM -- Wave parameters input method {1=still water, 2=using wind-wave correlation, 3=user-defined file}\n" +
                "[] -- WaveHsF -- Input file for significant wave height (m) [used only when WPInpM=3]\n" +
                "[] -- WaveTpF -- Input file for wave peak period (s) [used only when WPInp=3]\n" +
                "---------- Oprational Condition ----------\n" +
                "[2] -- OM -- Operational mode {1='Normal operating', 2='Parked', 3='Start up', 4='Idling', 5'='Normal shutdown', 6='Emergency shutdown'}\n" +
                "[] -- ShutT -- Time to shutdown [used only when OM = 5 and 6]\n" +
                "---------- Simulation Setting ----------\n" +
                "[10] -- SimT -- Simulation time (s)\n" +
                "[0.005] -- DT -- Time step for simulation in OpenFAST (s)\n" +
                "[1] -- BldSimM -- Blade simulation method {1=ElastoDyn, 2=BeamDyn}\n" +
                "---------- Wind Condition ----------\n" +
                "['IECKAI'] -- TurbModel -- Turbulence model {'IECKAI','IECVKM','GP_LLJ','NWTCUP','SMOOTH','WF_UPW','WF_07D','WF_14D','TIDAL','API','USRINP','TIMESR'}\n" +
                "['NTM'] -- IEC_WindType -- IEC turbulence type {'NTM'=normal, 'xETM'=extreme turbulence x = 1, 2, 3}\n" +
                "['PL'] -- WindProfileType -- Velocity profile type {'LOG','PL'=power law,'JET','H2L'=Log law for TIDAL model,'API','USR','TS'}\n" +
                "['default'] -- PLExp -- Power law exponent [-] (or \"default\")\n" +
                "[1] -- WakeMod -- Type of wake/induction model (switch) {0=none, 1=BEMT, 2=DBEMT}\n" +
                "---------- OpenSees Seeting ----------\n" +
                "[\"ALL\"] -- IM_Model -- Model selected to perform Analyses in OpenSees {[1,2,3...] or [\"ALL\"] or [\"None\"]}\n" +
                "[1] -- IM_Nth -- Nth number of IM starting to perform IDA in OpenSees [Used only when AnaTy=1]\n" +
                "[0.01] -- DampR -- Damping ratio of structure\n" +
                "[0.05] -- dT -- Time step for simulation in OpenSees (s)\n" +
                "---------- END ----------\n";
    }
}
