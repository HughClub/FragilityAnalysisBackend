package com.xy.springboot.utils;

import com.xy.springboot.model.entity.Condition;
import com.xy.springboot.model.entity.Uncertainty;
import com.xy.springboot.model.entity.Vulnerability;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

import static com.xy.springboot.utils.TaskUtils.getInputDir;

public class ConfigUtils {
    /**
     * 生成Uncertainty.inp文件，在对应的{taskId}/input文件夹下
     *
     * @param taskId
     * @param config
     */
//    public static void generateConfigFile(Long taskId, Uncertainty config) throws IOException {
//        File inputDir = new File(getInputDir(taskId));
//        if (!inputDir.exists()) {
//            inputDir.mkdirs();
//        }
//        File uncertaintyFile = new File(getInputDir(taskId) + "/Uncertainty.inp");
//        File conditionFile = new File(getInputDir(taskId) + "/Condition.inp");
//        File vulnerabilityFile = new File(getInputDir(taskId) + "/Vulnerability.inp");
//        try (BufferedWriter writer1 = new BufferedWriter(new FileWriter(uncertaintyFile));
//             BufferedWriter writer2 = new BufferedWriter(new FileWriter(conditionFile));
//             BufferedWriter writer3 = new BufferedWriter(new FileWriter(vulnerabilityFile))) {
//            // 生成文件内容
//            String uncertaintyContent = generateUncertaintyFileContent(config);
//            String conditionContent = generateConditionFileContent();
//            String vulnerabilityContent = generateVulnerabilityFileContent();
//            // 写入文件
//            writer1.write(uncertaintyContent);
//            writer2.write(conditionContent);
//            writer3.write(vulnerabilityContent);
//        } catch (IOException e) {
//            // 处理可能发生的异常
//            throw new IOException("Failed to generate Uncertainty.inp file", e);
//        }
//    }

    public static void generateUncertaintyFile(Long taskId, Uncertainty config) throws IOException {
        File inputDir = new File(getInputDir(taskId));
        if (!inputDir.exists()) {
            inputDir.mkdirs();
        }
        File uncertaintyFile = new File(getInputDir(taskId) + "/Uncertainty.inp");
        try (BufferedWriter writer1 = new BufferedWriter(new FileWriter(uncertaintyFile))) {
            // 生成文件内容
            String uncertaintyContent = generateUncertaintyFileContent(config);
            // 写入文件
            writer1.write(uncertaintyContent);
        } catch (IOException e) {
            // 处理可能发生的异常
            throw new IOException("Failed to generate Uncertainty.inp file", e);
        }
    }

    public static void generateConditionFile(Long taskId, Condition condition) throws IOException {
        File inputDir = new File(getInputDir(taskId));
        if (!inputDir.exists()) {
            inputDir.mkdirs();
        }
        File conditionFile = new File(getInputDir(taskId) + "/Condition.inp");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(conditionFile))) {
            // 生成文件内容
            String conditionContent = generateConditionFileContent(taskId, condition);
            // 写入文件
            writer.write(conditionContent);
        } catch (IOException e) {
            // 处理可能发生的异常
            throw new IOException("Failed to generate Condition.inp file", e);
        }
    }

    public static void generateVulnerabilityFile(Long taskId, Vulnerability vulnerability) throws IOException {
        File inputDir = new File(getInputDir(taskId));
        if (!inputDir.exists()) {
            inputDir.mkdirs();
        }
        File vulnerabilityFile = new File(getInputDir(taskId) + "/Vulnerability.inp");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(vulnerabilityFile))) {
            // 生成文件内容
            String vulnerabilityContent = generateVulnerabilityFileContent(vulnerability);
            // 写入文件
            writer.write(vulnerabilityContent);
        } catch (IOException e) {
            // 处理可能发生的异常
            throw new IOException("Failed to generate Vulnerability.inp file", e);
        }
    }

    public static void main(String[] args) {
        Uncertainty config = new Uncertainty();
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
        config.setMatStrMean("3");
        config.setMatStrVariance("25");
        config.setMatMod("0");
        config.setMatDen("0");
        config.setMatPoR("0");
        config.setHubMass("0");
        config.setNacMass("0");
        config.setHubH("0");
        config.setBlDR("0");
        config.setStDR("0");
        config.setWindSpd("0");
        config.setWindDirMin("1");
        config.setWindDirMax("1");
        config.setTurbC("0");
        config.setZ0("0");
        config.setWaveHs("0");
        config.setWaveTp("0");
        config.setWaveDir("0");
        config.setSeisPGA("0");
        config.setSeisDir("0");

//        try {
//            new ConfigUtils().generateConfigFile(1L, config);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static String generateUncertaintyFileContent(Uncertainty config) {
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
                .append("[3, " + config.getMatStrMean() + ", " + config.getMatStrVariance() + "] -- MatStr -- Substructure material yielding strength (Pa)\n")
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
                .append("[1, " + config.getWindDirMin() + ", " + config.getWindDirMax() + "] -- WindDir -- Wind incident direction (degree)\n")
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

    public static String generateVulnerabilityFileContent(Vulnerability vulnerability) {
        return "---------- Fragility ----------\n" +
                "[" + vulnerability.getFrgl_Op() + "] -- Frgl_Op -- Using results from openfast or opensees {Switch: 1=openfast, 2=openfast+opensees}\n" +
                "[" + vulnerability.getIniTI() + "] -- IniTI -- Initial time ingored for response amplitudes processing (s)\n" +
                "[" + vulnerability.getFrgl_Res() + "] -- Frgl_Res -- Structural response used to establish fragility {Switch: 1=response in X direction, 2=response in Y direction, 3=response in space}\n" +
                "[" + vulnerability.getFrgl_Bld() + "] -- Frgl_Bld -- Fragility of blades {Switch: 1=true, 2=false}\n" +
                "[" + vulnerability.getDS_Bld() + "] -- DS_Bld -- Damage states of blades (Blade-tip deflection m) [used only when Frgl_Bld=1]\n" +
                "[" + vulnerability.getIMI_Bld() + "] -- IMI_Bld -- IM intensity for distribution models estimation for blades {\"All\" or [ith IM, jth IM]} [used only when Frgl_Bld=1]\n" +
                "[" + vulnerability.getFrgl_Nac() + "] -- Frgl_Nac -- Fragility of nacelle {Switch: 1=true, 2=false}\n" +
                "[" + vulnerability.getDS_Nac() + "] -- DS_Nac -- Damage states of nacelle (Nacelle acceleration m/s2) [used only when Frgl_Nac=1]\n" +
                "[" + vulnerability.getIMI_Nac() + "] -- IMI_Nac -- IM intensity for distribution models estimation for nacelle {\"All\" or [ith IM, jth IM]} [used only when Frgl_Nac=1]\n" +
                "[" + vulnerability.getFrgl_Twr() + "] -- Frgl_Twr -- Fragility of tower {Switch: 1=true, 2=false}\n" +
                "[" + vulnerability.getDS_Twr_Type() + "] -- DS_Twr_Type -- Damage states type of tower {Switch: 1=tower-top drift, 2=tower-base stress} [used only when Frgl_Twr=1]\n" +
                "[" + vulnerability.getDS_Twr() + "] -- DS_Twr -- Damage states of tower [used only when Frgl_Twr=1]\n" +
                "[" + vulnerability.getIMI_Twr() + "] -- IMI_Twr -- IM intensity for distribution models estimation for tower {\"All\" or [ith IM, jth IM]} [used only when Frgl_Twr=1]\n" +
                "[" + vulnerability.getFrgl_Sub() + "] -- Frgl_Sub -- Fragility of monopile {Switch: 1=true, 2=false}\n" +
                "[" + vulnerability.getDS_Sub_Type() + "] -- DS_Sub_Type -- Damage states type of monopile {Switch: 1=platform drift, 2=base stress} [used only when Frgl_Mop=1]\n" +
                "[" + vulnerability.getDS_Sub() + "] -- DS_Sub -- Damage states of monopile [used only when Frgl_Mop=1]\n" +
                "[" + vulnerability.getIMI_Sub() + "] -- IMI_Sub -- IM intensity for distribution models estimation for monopile {\"All\" or [ith IM, jth IM]} [used only when Frgl_Mop=1]\n" +
                "[" + vulnerability.getHIF() + "] -- HIF -- Hazard interval for fragility output\n" +
                "[" + vulnerability.getDIM() + "] -- dIM -- Step of hazard level for fragility output\n" +
                "---------- Vulnerability ----------\n" +
                "[" + vulnerability.getNum_RS() + "] -- Num_RS -- Number of random simulation for each intensity \n" +
                "[" + vulnerability.getLossR_Bld() + "] -- LossR_Bld -- Loss ratio definition for blades [used only when Frgl_Bld=1]\n" +
                "[" + vulnerability.getLossR_Nac() + "] -- LossR_Nac -- Loss ratio definition for nacelle [used only when Frgl_Nac=1]\n" +
                "[" + vulnerability.getLossR_Twr() + "] -- LossR_Twr -- Loss ratio definition for tower [used only when Frgl_Twr=1]\n" +
                "[" + vulnerability.getLossR_Mop() + "] -- LossR_Mop -- Loss ratio definition for monopile [used only when Frgl_Mop=1]\n" +
                "---------- Dependency ----------\n" +
                "[" + vulnerability.getDep_Bld() + "] -- Dep_Bld -- Damage states of blade and tower will be altered to severe damage if 'tower strike' occurred {Switch: 1=true, 2=false}\n" +
                "[" + vulnerability.getDep_Nac() + "] -- Dep_Nac -- Damage states of blade will be altered to severe damage if severe damage occurred for nacelle {Switch: 1=true, 2=false}\n" +
                "[" + vulnerability.getDep_Twr() + "] -- Dep_Twr -- Damage states of both blade and nacelle will be altered to severe damage if severe damage occurred for tower {Switch: 1=true, 2=false}\n" +
                "[" + vulnerability.getDep_Mop() + "] -- Dep_Mop -- Damage states of blade, nacelle, and tower will be altered to severe damage if severe damage occurred for monopile {Switch: 1=true, 2=false}\n" +
                "---------- END ----------\n";
    }

    public static String generateConditionFileContent(Long taskId, Condition condition) {
        File tmp1 = new File(getInputDir(taskId) + "/tmp1.inp");
        File tmp2 = new File(getInputDir(taskId) + "/tmp2.inp");
        File tmp3 = new File(getInputDir(taskId) + "/tmp3.inp");
        try (BufferedWriter writer1 = new BufferedWriter(new FileWriter(tmp1));
             BufferedWriter writer2 = new BufferedWriter(new FileWriter(tmp2));
             BufferedWriter writer3 = new BufferedWriter(new FileWriter(tmp3))) {
            writer1.write(condition.getWindSpd());
            writer2.write(condition.getWaveHsF());
            writer3.write(condition.getWaveTpF());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "---------- Analyze type ----------\n" +
                "[" + condition.getAnaTy() + "] -- AnaTy -- Analyze type {1=IDA, 2=Uncertian}\n" +
                "[" + condition.getNumF() + "] -- NumF -- Number of wind-wave fields for each hazrad intensity\n" +
                "---------- IDA Setting ---------- [Used only when AnaTy=1]\n" +
                "[" + condition.getIMImpM() + "] -- IMImpM -- Intensity measure input method {1=arithmetic series, 2=user define}\n" +
                "[" + ("1".equals(condition.getIMImpM()) ? condition.getWindSpd() : (TaskUtils.getInputDir(taskId) + "tmp1.inp")) + "] -- WindSpd -- Wind speed at hub height (m/s), Parameter = [Cut-in ,Cut-out, Step] when IMImp=1, and input file when IMImp=2\n" +
                "[" + condition.getWPInpM() + "] -- WPInpM -- Wave parameters input method {1=still water, 2=using wind-wave correlation, 3=user-defined file}\n" +
                "[" + ("3".equals(condition.getWPInpM()) ? "" : (TaskUtils.getInputDir(taskId) + "tmp2.inp")) + "] -- WaveHsF -- Input file for significant wave height (m) [used only when WPInpM=3]\n" +
                "[" + ("3".equals(condition.getWPInpM()) ? "" : (TaskUtils.getInputDir(taskId) + "tmp3.inp")) + "] -- WaveTpF -- Input file for wave peak period (s) [used only when WPInp=3]\n" +
                "---------- Oprational Condition ----------\n" +
                "[" + condition.getOM() + "] -- OM -- Operational mode {1='Normal operating', 2='Parked', 3='Start up', 4='Idling', 5'='Normal shutdown', 6='Emergency shutdown'}\n" +
                "[" + condition.getShutT() + "] -- ShutT -- Time to shutdown [used only when OM = 5 and 6]\n" +
                "---------- Simulation Setting ----------\n" +
                "[" + condition.getSimT() + "] -- SimT -- Simulation time (s)\n" +
                "[" + condition.getOpenFastDT() + "] -- DT -- Time step for simulation in OpenFAST (s)\n" +
                "[" + condition.getBldSimM() + "] -- BldSimM -- Blade simulation method {1=ElastoDyn, 2=BeamDyn}\n" +
                "---------- Wind Condition ----------\n" +
                "['" + condition.getTurbModel() + "'] -- TurbModel -- Turbulence model {'IECKAI','IECVKM','GP_LLJ','NWTCUP','SMOOTH','WF_UPW','WF_07D','WF_14D','TIDAL','API','USRINP','TIMESR'}\n" +
                "['" + condition.getIEC_WindType() + "'] -- IEC_WindType -- IEC turbulence type {'NTM'=normal, 'xETM'=extreme turbulence x = 1, 2, 3}\n" +
                "['" + condition.getWindProfileType() + "'] -- WindProfileType -- Velocity profile type {'LOG','PL'=power law,'JET','H2L'=Log law for TIDAL model,'API','USR','TS'}\n" +
                "[" + condition.getPLExp() + "] -- PLExp -- Power law exponent [-] (or \"default\")\n" +
                "[" + condition.getWakeMod() + "] -- WakeMod -- Type of wake/induction model (switch) {0=none, 1=BEMT, 2=DBEMT}\n" +
                "---------- OpenSees Seeting ----------\n" +
                "[" + condition.getIM_Model() + "] -- IM_Model -- Model selected to perform Analyses in OpenSees {[1,2,3...] or [\"ALL\"] or [\"None\"]}\n" +
                "[" + condition.getIM_Nth() + "] -- IM_Nth -- Nth number of IM starting to perform IDA in OpenSees [Used only when AnaTy=1]\n" +
                "[" + condition.getDampR() + "] -- DampR -- Damping ratio of structure\n" +
                "[" + condition.getOpenSeesDt() + "] -- dT -- Time step for simulation in OpenSees (s)\n" +
                "---------- END ----------\n";
    }
}
