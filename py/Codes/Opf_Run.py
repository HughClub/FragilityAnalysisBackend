# -*- coding: utf-8 -*
import numpy as np
import subprocess
import os
import shutil
import Opf_FM
from BModes import BModes


def OpfRun(Path_Out_Step_2, Path_FAST, Dict_Sap, Dict_Cnd, Num_S):
    # %%  make directories
    if not os.path.isdir(Path_Out_Step_2):
        os.mkdir(Path_Out_Step_2)
    Path_Out = Path_Out_Step_2 + '/Output_opf'
    if not os.path.isdir(Path_Out):
        os.mkdir(Path_Out)
    Path_Temp = Path_Out + '/Temp'
    if not os.path.isdir(Path_Temp):
        os.mkdir(Path_Temp)
    for i in range(Num_S):
        Path_Model = Path_Out + '/Model_' + str(i + 1)
        Path_Temp = Path_Model + '/Temp'
        if not os.path.isdir(Path_Model):
            os.mkdir(Path_Model)
        if not os.path.isdir(Path_Temp):
            os.mkdir(Path_Temp)
    #%% Bmodes for openfast
    BModes(Path_FAST, Path_Out, Dict_Sap, Num_S)
    # %%  OpenFAST variables
    OpfVar = OpenFAST_Vars()
    # %%  Operational Condition
    OpfVar['ED']['BlPitch'], OpfVar['ED']['RotSpeed'], OpfVar['SerD']['TPCOn'], OpfVar['SerD']['TPitManS'], \
    OpfVar['SerD']['PitManRat'], OpfVar['SerD']['BlPitchF'], OpfVar['SerD']['TimGenOn'], \
    OpfVar['SerD']['TimGenOf'] = Operational_Cnd(Dict_Cnd)
    OpfVar['SerD']['DLL_FileName'] = '"' + Path_FAST + '/ServoData/DISCON.dll' + '"'
    OpfVar['SerD']['DLL_InFile'] = '"' + Path_FAST + '/ServoData/DISCON.IN' + '"'
    OpfVar['fst']['ServoFile'] = '"' + Path_Out + '/Temp/ServoDyn.dat' + '"'
    # %%  Simulation settings
    OpfVar['fst']['TMax'] = Dict_Cnd['SimT'][0]
    OpfVar['fst']['DT'] = Dict_Cnd['DT'][0]
    OpfVar['fst']['CompElast'] = Dict_Cnd['BldSimM'][0]
    OpfVar['TS']['AnalysisTime'] = OpfVar['fst']['TMax']
    OpfVar['HD']['WaveTMax'] = OpfVar['fst']['TMax']
    OpfVar['fst']['BDBldFile'] = '"' + Path_FAST + '/BeamDyn.dat' + '"'
    # %%  Wind conditions for wind farm simulation
    OpfVar['TS']['TurbModel'] = '"' + Dict_Cnd['TurbModel'][0] + '"'
    OpfVar['TS']['IEC_WindType'] = '"' + Dict_Cnd['IEC_WindType'][0] + '"'
    OpfVar['TS']['WindProfileType'] = '"' + Dict_Cnd['WindProfileType'][0] + '"'
    OpfVar['TS']['PLExp'] = Dict_Cnd['PLExp'][0]
    OpfVar['AD']['WakeMod'] = Dict_Cnd['WakeMod'][0]
    OpfVar['AD']['AFNames'] = ['"' + Path_FAST + '/Airfoils' + '/Cylinder1.dat' + '"',
                               '"' + Path_FAST + '/Airfoils' + '/Cylinder2.dat' + '"',
                               '"' + Path_FAST + '/Airfoils' + '/DU40_A17.dat' + '"',
                               '"' + Path_FAST + '/Airfoils' + '/DU35_A17.dat' + '"',
                               '"' + Path_FAST + '/Airfoils' + '/DU30_A17.dat' + '"',
                               '"' + Path_FAST + '/Airfoils' + '/DU25_A17.dat' + '"',
                               '"' + Path_FAST + '/Airfoils' + '/DU21_A17.dat' + '"',
                               '"' + Path_FAST + '/Airfoils' + '/NACA64_A17.dat' + '"']
    OpfVar['AD']['ADBlFile'] = '"' + Path_FAST + '/AeroDyn_blade.dat' + '"'
    # %%  Running mode and simulation
    Path_SerD = Path_Out + '/Temp/ServoDyn.dat'
    Opf_FM.FM_SerD(OpfVar['SerD'], Path_FAST, Path_SerD)  # ServoDyn.dat modification

    for ii in range(Num_S):  # Variables related to each model
        Path_Model = Path_Out + '/Model_' + str(ii+1)
        dict_Sap = {key: value[ii] for key, value in Dict_Sap.items()}
        OpfVar['fst']['WtrDpth'] = dict_Sap['WatDep']
        OpfVar['fst']['EDFile'] = '"' + Path_Model + '/Temp/ElastoDyn.dat' + '"'
        OpfVar['fst']['AeroFile'] = '"' + Path_Model + '/Temp/AeroDyn15.dat' + '"'
        OpfVar['fst']['SubFile'] = '"' + Path_Model + '/Temp/SubDyn.dat' + '"'
        OpfVar['ED']['Twr2Shft'] = dict_Sap['HubH'] - dict_Sap['TwrTH'] - 0.43744
        OpfVar['ED']['TowerHt'] = dict_Sap['TwrTH']
        OpfVar['ED']['TowerBsHt'] = dict_Sap['SubTH']
        OpfVar['ED']['PtfmCMzt'] = dict_Sap['SubTH']
        OpfVar['ED']['PtfmRefzt'] = dict_Sap['SubTH']
        OpfVar['ED']['HubMass'] = dict_Sap['HubMass']
        OpfVar['ED']['NacMass'] = dict_Sap['NacMass']
        OpfVar['ED']['TwrFile'] = '"' + Path_Model + '/Temp/ElastoDyn_Tower.dat' + '"'
        OpfVar['ED']['BldFile'] = '"' + Path_Model + '/Temp/ElastoDyn_Blade.dat' + '"'
        OpfVar['EDT']['TwrFADmp'] = dict_Sap['StDR']
        OpfVar['EDT']['TwrProp'], OpfVar['AD']['TwrAD'] = TwrProp(dict_Sap)
        OpfVar['EDT']['TwrShape'] = np.loadtxt(Path_Model + '/Temp/Twr_Shape_Coefs.txt')
        OpfVar['EDB']['BldFlDmp'] = dict_Sap['BlDR']
        OpfVar['SubD']['Joints'], OpfVar['SubD']['Members'], OpfVar['SubD']['PropSet'], OpfVar['HD']['Jointzi'], OpfVar['HD']['PropMem'] = SubSProp(dict_Sap)
        OpfVar['TS']['HubHt'] = dict_Sap['HubH']
        OpfVar['TS']['GridHeight'] = 2 * dict_Sap['HubH'] - 5
        OpfVar['TS']['GridWidth'] = 2 * dict_Sap['HubH'] - 5
        OpfVar['TS']['HFlowAng'] = dict_Sap['WindDir']
        OpfVar['TS']['IECturbc'] = dict_Sap['TurbC']
        OpfVar['TS']['RefHt'] = dict_Sap['HubH']
        OpfVar['TS']['Z0'] = dict_Sap['Z0']
        OpfVar['IW']['WindVziList'] = dict_Sap['HubH']
        OpfVar['HD']['WaveDir'] = dict_Sap['WaveDir']

        Path_SubD = Path_Model + '/Temp/SubDyn.dat'
        Opf_FM.FM_SubD(OpfVar['SubD'], Path_FAST, Path_SubD)  # SubDyn.dat modification
        Path_ED = Path_Model + '/Temp/ElastoDyn.dat'
        Opf_FM.FM_ED(OpfVar['ED'], Path_FAST, Path_ED)  # ElastoDyn.dat modification
        Path_EDT = Path_Model + '/Temp/ElastoDyn_Tower.dat'
        Opf_FM.FM_EDT(OpfVar['EDT'], Path_FAST, Path_EDT)  # ElastoDyn_Tower.dat modification
        Path_EDB = Path_Model + '/Temp/ElastoDyn_Blade.dat'
        Opf_FM.FM_EDB(OpfVar['EDB'], Path_FAST, Path_EDB)  # ElastoDyn_Blade.dat modification
        Path_AD = Path_Model + '/Temp/AeroDyn15.dat'
        Opf_FM.FM_AD(OpfVar['AD'], Path_FAST, Path_AD)  # AeroDyn15.dat modification

    #%% Mode == 1: IDA Analysis
        if Dict_Cnd['AnaTy'][0] == 1:  # Analyze Type -- IDA
            WindSpd, OpfVar['HD']['WaveMod'], WaveHs, WaveTp = IDA_Set(Dict_Cnd)  # Wind-wave farm information for IDA

            for jj in range(len(WindSpd)):  # Variables related to wind speed (i.e. hazard intensity)
                Path_Wind = Path_Model + '/' + str(WindSpd[jj]) + 'mps'
                Path_Temp = Path_Wind + '/Temp'
                if not os.path.isdir(Path_Wind):
                    os.mkdir(Path_Wind)
                if not os.path.isdir(Path_Temp):
                    os.mkdir(Path_Temp)
                OpfVar['TS']['URef'] = WindSpd[jj]
                OpfVar['HD']['WaveHs'] = np.float32(WaveHs[jj])
                OpfVar['HD']['WaveTp'] = np.float32(WaveTp[jj])

                Num_Sim = Dict_Cnd['NumF'][0]

                for kk in range(int(Num_Sim)):  # Variables related to random seeds
                    RandSeeds = np.random.randint(-2147483648, 2147483647, 4)
                    OpfVar['TS']['RandSeed1'] = RandSeeds[0]
                    OpfVar['TS']['RandSeed2'] = RandSeeds[1]
                    OpfVar['IW']['FileName_BTS'] = '"' + Path_Wind + '/Temp/TurbSim_' + str(kk+1) + '.bts' + '"'
                    OpfVar['HD']['WaveSeed(1)'] = RandSeeds[2]
                    OpfVar['HD']['WaveSeed(2)'] = RandSeeds[3]
                    OpfVar['fst']['InflowFile'] = '"' + Path_Wind + '/Temp/InflowWind_' + str(kk+1) + '.dat' + '"'
                    OpfVar['fst']['HydroFile'] = '"' + Path_Wind + '/Temp/HydroDyn_' + str(kk+1) + '.dat' + '"'

                    Path_TS = Path_Wind + '/Temp/TurbSim_{}.inp'.format(kk+1)
                    Opf_FM.FM_TS(OpfVar['TS'], Path_FAST, Path_TS)  # TurbSim.inp modification
                    Path_IW = Path_Wind + '/Temp/InflowWind_{}.dat'.format(kk + 1)
                    Opf_FM.FM_IW(OpfVar['IW'], Path_FAST, Path_IW)  # InflowWind.dat modification
                    Path_HD = Path_Wind + '/Temp/HydroDyn_{}.dat'.format(kk + 1)
                    Opf_FM.FM_HD(OpfVar['HD'], Path_FAST, Path_HD)  # HydroDyn.dat modification
                    Path_fst = Path_Wind + '/Temp/Main_{}.fst'.format(kk + 1)
                    Opf_FM.FM_fst(OpfVar['fst'], Path_FAST, Path_fst)  # Main.fst modification

                    RunFAST(kk+1, Path_FAST, Path_Wind)

    #%% Mode == 2: Random analysis
        if Dict_Cnd['AnaTy'][0] == 2:  # Analyze Type -- Random
            OpfVar['HD']['WaveMod'] = 2
            OpfVar['TS']['URef'] = Dict_Sap['WindSpd']
            OpfVar['HD']['WaveHs'] = Dict_Sap['WaveHs']
            OpfVar['HD']['WaveTp'] = Dict_Sap['WaveTp']

            for kk in range(np.int(Dict_Cnd['NumF'][0])):  # Variables related to random seeds
                RandSeeds = np.random.randint(-2147483648, 2147483647, 4)
                OpfVar['TS']['RandSeed1'] = RandSeeds[0]
                OpfVar['TS']['RandSeed2'] = RandSeeds[1]
                OpfVar['IW']['FileName_BTS'] = '"' + Path_Model + '/Temp/TurbSim_' + str(kk + 1) + '.bts' + '"'
                OpfVar['HD']['WaveSeed(1)'] = RandSeeds[2]
                OpfVar['HD']['WaveSeed(2)'] = RandSeeds[3]
                OpfVar['fst']['InflowFile'] = '"' + Path_Model + '/Temp/InflowWind_' + str(kk + 1) + '.dat' + '"'
                OpfVar['fst']['HydroFile'] = '"' + Path_Model + '/Temp/HydroDyn_' + str(kk + 1) + '.dat' + '"'

                Path_TS = Path_Model + '/Temp/TurbSim_{}.inp'.format(kk + 1)
                Opf_FM.FM_TS(OpfVar['TS'], Path_FAST, Path_TS)  # TurbSim.inp modification
                Path_IW = Path_Model + '/Temp/InflowWind_{}.dat'.format(kk + 1)
                Opf_FM.FM_IW(OpfVar['IW'], Path_FAST, Path_IW)  # InflowWind.dat modification
                Path_HD = Path_Model + '/Temp/HydroDyn_{}.dat'.format(kk + 1)
                Opf_FM.FM_HD(OpfVar['HD'], Path_FAST, Path_HD)  # HydroDyn.dat modification
                Path_fst = Path_Model + '/Temp/Main_{}.fst'.format(kk + 1)
                Opf_FM.FM_fst(OpfVar['fst'], Path_FAST, Path_fst)  # Main.fst modification

                RunFAST(kk+1, Path_FAST, Path_Model)


#%% Functions
def OpenFAST_Vars():
    OpfVar = {'fst': [], 'AD': [], 'ED': [], 'EDT': [], 'EDB': [], 'IW': [], 'SubD': [], 'SerD': [], 'HD': [], 'TS': []}
    OpfVar['fst'] = {'TMax': [], 'DT': [], 'CompElast': [], 'BDBldFile': [], 'WtrDpth': [], 'EDFile': [],
                     'AeroFile': [], 'SubFile': [], 'InflowFile': [], 'ServoFile': [], 'HydroFile': []}
    OpfVar['AD'] = {'WakeMod': [], 'AFNames': [], 'ADBlFile': [], 'TwrAD': []}
    OpfVar['ED'] = {'BlPitch': [], 'RotSpeed': [], 'Twr2Shft': [], 'TowerHt': [], 'TowerBsHt': [], 'PtfmCMzt': [],
                    'PtfmRefzt': [], 'HubMass': [], 'NacMass': [], 'BldFile': [], 'TwrFile': []}
    OpfVar['EDT'] = {'TwrFADmp': [], 'TwrProp': [], 'TwrShape': []}
    OpfVar['EDB'] = {'BldFlDmp': []}
    OpfVar['IW'] = {'WindVziList': [], 'FileName_BTS': []}
    OpfVar['SubD'] = {'Joints': [], 'Members': [], 'PropSet': []}
    OpfVar['SerD'] = {'TPCOn': [], 'TPitManS': [], 'PitManRat': [], 'BlPitchF': [], 'TimGenOn': [], 'TimGenOf': [],
                      'DLL_FileName': [], 'DLL_InFile': []}
    OpfVar['HD'] = {'WaveMod': [], 'WaveTMax': [], 'WaveHs': [], 'WaveTp': [], 'WaveSeed(1)': [], 'WaveSeed(2)': [],
                    'WaveDir': [], 'Jointzi': [], 'PropMem': []}
    OpfVar['TS'] = {'RandSeed1': [], 'RandSeed2': [], 'AnalysisTime': [], 'HubHt': [], 'GridHeight': [],
                    'GridWidth': [], 'HFlowAng': [], 'TurbModel': [], 'IECturbc': [], 'IEC_WindType': [],
                    'WindProfileType': [], 'RefHt': [], 'URef': [], 'PLExp': [], 'Z0': []}
    return OpfVar


def IDA_Set(Dict_Cnd):
    #  Wind Speed for IDA (Hazard intensity)
    if Dict_Cnd['IMImpM'][0] == 1:  # Wind input method
        WindSpd = np.arange(Dict_Cnd['WindSpd'][0], Dict_Cnd['WindSpd'][1]+Dict_Cnd['WindSpd'][2], Dict_Cnd['WindSpd'][2])
    elif Dict_Cnd['IMImpM'][0] == 2:
        WindSpd = np.loadtxt(Dict_Cnd['WindSpd'][0], dtype=np.float)
    #  Wave parameters for IDA
    if Dict_Cnd['WPInpM'][0] == 1:  # Wave input method
        WaveMod = 0
        WaveHs = np.zeros([len(WindSpd), 1])
        WaveTp = np.zeros([len(WindSpd), 1])
    elif Dict_Cnd['WPInpM'][0] == 2:
        WaveMod = 2
        WaveHs = 0.0002491*WindSpd**3-0.009742*WindSpd**2+0.3361*WindSpd-1.092+0.17
        WaveTp = 0.0003416*WindSpd**3-0.02636*WindSpd**2+0.9053*WindSpd-0.3805
    elif Dict_Cnd['WPInpM'][0] == 3:
        WaveMod = 2
        WaveHs = np.loadtxt(Dict_Cnd['WaveHsF'][0], dtype=np.float)
        WaveTp = np.loadtxt(Dict_Cnd['WaveTpF'][0], dtype=np.float)
    return WindSpd, WaveMod, WaveHs, WaveTp


def Operational_Cnd(Dict_Cnd):
    # 1='Normal operating', 2='Parked', 3='Start up', 4='Idling', '5'='Normal shutdown', 6='Emergency shutdown'
    OM = np.int32(Dict_Cnd['OM'][0])
    ShutT = 9999.9
    if OM == 6:
        ShutT = Dict_Cnd['ShutT'][0]
    BlPitch_All = np.array([12, 0, 0, 0, 23.47, 23.47])
    RotSpeed_All = np.array([12.1, 0, 0, 12.1, 12.1, 12.1])
    TPCOn_All = np.array([0, 9999.9, 0, 9999.9, 0, 0])
    TPitManS_All = np.array([9999.9, 0, 9999.9, 9999.9, ShutT, ShutT])
    PitManRat_All = np.array([2, 2, 2, 2, 2, 8])
    BlPitchF_All = np.array([0, 90, 0, 0, 90, 90])
    TimGenOn_All = np.array([0, 9999.9, 0, 0, 0, 0])
    TimGenOf_All = np.array([9999.9, 0, 9999.9, 9999.9, ShutT, ShutT])
    BlPitch = BlPitch_All[OM - 1]
    RotSpeed = RotSpeed_All[OM - 1]
    TPCOn = TPCOn_All[OM - 1]
    TPitManS = TPitManS_All[OM - 1]
    PitManRat = PitManRat_All[OM - 1]
    BlPitchF = BlPitchF_All[OM - 1]
    TimGenOn = TimGenOn_All[OM - 1]
    TimGenOf = TimGenOf_All[OM - 1]
    return BlPitch, RotSpeed, TPCOn, TPitManS, PitManRat, BlPitchF, TimGenOn, TimGenOf


def TwrProp(dict_Sap):
    Num_TwrS = 28
    Sec_Prop = np.zeros([Num_TwrS, 4])
    Sec_Prop[:, 0] = np.linspace(0, 1, num=Num_TwrS)
    Sec_D = np.linspace(dict_Sap['TwrBD'], dict_Sap['TwrTD'], num=Num_TwrS)
    Sec_T = np.linspace(dict_Sap['TwrBT'], dict_Sap['TwrTT'], num=Num_TwrS)
    Sec_d = Sec_D - 2*Sec_T
    MatMod = dict_Sap['MatMod']
    MatDen = dict_Sap['MatDen']
    Sec_Prop[:, 1] = np.pi/4*(Sec_D**2-Sec_d**2)*MatDen
    Sec_Prop[:, 2] = np.pi/64*(Sec_D**4-Sec_d**4)*MatMod
    Sec_Prop[:, 3] = Sec_Prop[:, 2]

    TwrAD = np.zeros([9, 4])
    TwrAD[:, 0] = [(np.linspace(dict_Sap['SubTH'], dict_Sap['TwrTH'], num=Num_TwrS))[3*(i+1)] for i in np.arange(9)]
    TwrAD[:, 1] = [Sec_D[3*(i+1)] for i in np.arange(9)]
    TwrAD[:, 2] = np.ones(9)
    TwrAD[:, 3] = np.ones(9)*0.1
    return Sec_Prop, TwrAD


def SubSProp(dict_Sap):
    Num_J = 11
    SubBH = dict_Sap['WatDep']
    SubTH = dict_Sap['SubTH']
    Joint = np.zeros([Num_J, 9])
    Joint[:, 0] = np.arange(1, Num_J+1, dtype=int)
    Joint[:, 3] = np.linspace(0-SubBH, SubTH, num=Num_J)
    Joint[:, 4] = np.ones(Num_J, dtype=int)
    Member = np.zeros([Num_J-1, 6])
    Member[:, 0] = np.arange(1, Num_J, dtype=int)
    Member[:, 1] = np.arange(1, Num_J, dtype=int)
    Member[:, 2] = np.arange(2, Num_J+1, dtype=int)
    Member[:, 3:6] = np.ones([Num_J-1, 3], dtype=int)
    YoungE = dict_Sap['MatMod']
    ShearG = YoungE/2/(1+dict_Sap['MatPoR'])
    MatDens = dict_Sap['MatDen']
    XsecD = dict_Sap['SubUD']
    XsecT = dict_Sap['SubUT']
    PropSet = '1' + ' '*6 + str(YoungE) + ' '*8 + str(ShearG) + ' '*8 + str(MatDens) + ' '*8 + str(XsecD) + ' '*8 + str(XsecT)

    Jointzi = np.array([[1, 0, 0, 0-SubBH, 1, 0], [2, 0, 0, SubTH, 1, 0]])
    PropMem = np.array([1, XsecD, XsecT])
    return Joint, Member, PropSet, Jointzi, PropMem


def RunFAST(num, Path_FAST, Path_out):
    with open(Path_out + '/{}_out.txt'.format(num), 'a+', encoding='UTF-8') as out:
        subprocess.run([Path_FAST + "/TurbSim_x64.exe", Path_out + '/Temp/TurbSim_{}.inp'.format(num)], stdout=subprocess.PIPE)
        subprocess.run([Path_FAST + '/openfast_x64.exe', Path_out + '/Temp/Main_{}.fst'.format(num)], stdout=out)
    out.close()

    if num == 1:
        shutil.copyfile(Path_out + '/Temp/Main_{}.sum'.format(num), Path_out + '/Sum.txt'.format(num))
    shutil.copyfile(Path_out + '/Temp/Main_{}.out'.format(num), Path_out + '/{}_data.txt'.format(num))

    os.remove(Path_out + '/Temp/Main_{}.sum'.format(num))
    os.remove(Path_out + '/Temp/Main_{}.out'.format(num))
    os.remove(Path_out + '/Temp/TurbSim_{}.inp'.format(num))
    os.remove(Path_out + '/Temp/TurbSim_{}.sum'.format(num))
    os.remove(Path_out + '/Temp/TurbSim_{}.bts'.format(num))
    os.remove(Path_out + '/Temp/InflowWind_{}.dat'.format(num))
    os.remove(Path_out + '/Temp/HydroDyn_{}.dat'.format(num))
    os.remove(Path_out + '/Temp/Main_{}.fst'.format(num))







