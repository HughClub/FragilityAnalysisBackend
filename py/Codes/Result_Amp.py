# -*- coding: utf-8 -*
import numpy as np
import os

def Result_Amp(Dict_Sap, Dict_Cnd, Dict_Vln, Path_Sim, Path_Out, Num_S):
    Path_opf = Path_Sim + '/Output_opf'
    Path_ops = Path_Sim + '/Output_ops'
    #  Wind Speed for IDA (Hazard intensity)
    if Dict_Cnd['IMImpM'][0] == 1:  # Wind input method
        WindSpd = np.arange(Dict_Cnd['WindSpd'][0], Dict_Cnd['WindSpd'][1] + 1, Dict_Cnd['WindSpd'][2])
    elif Dict_Cnd['IMImpM'][0] == 2:
        WindSpd = np.loadtxt(Dict_Cnd['WindSpd'][0], dtype=np.float)

    Opf_Amp(Dict_Sap, Dict_Cnd, Dict_Vln, Path_opf, Path_Out, WindSpd, Num_S)
    if Dict_Cnd['IM_Model'][0].upper() != 'None'.upper():
        Ops_Amp(Dict_Cnd, Dict_Vln, Path_ops, Path_Out, WindSpd, Num_S)

    if Dict_Vln['Frgl_Op'][0] == 2:
        Ops_Amp(Dict_Cnd, Dict_Vln, Path_ops, Path_Out, WindSpd, Num_S)


def Opf_Amp(Dict_Sap, Dict_Cnd, Dict_Vln, Path_opf, Path_Out, WindSpd, Num_S):
    Out_Opf = ['OoPDefl1', 'IPDefl1', 'YawBrTAxp', 'YawBrTAyp', 'TwrTpTDxi', 'TwrTpTDyi', 'PtfmSurge', 'PtfmSway',
               'TwrBsFxt', 'TwrBsFyt', 'TwrBsFzt', 'TwrBsMxt', 'TwrBsMyt', 'TwrBsMzt',
               '-ReactFXss', '-ReactFYss', '-ReactFZss', '-ReactMXss', '-ReactMYss', '-ReactMZss']

    with open(Path_opf + '/Model_1' + '/{}mps'.format(WindSpd[0]) + '/Sum.txt') as File:
        Data = File.readlines()
    File.close()
    Col_Opf = []
    for i in range(len(Out_Opf)):
        for line in Data:
            if Out_Opf[i] in line:
                line_ = line.strip().split()
                Col_Opf.append(int(line_[0]) - 1)

    Out_Amp = ['BldTipDX', 'BldTipDY', 'BldTipDC', 'NclAX', 'NclAY', 'NclAC', 'TwrTopDX', 'TwrTopDY', 'TwrTopDC',
               'PltfDX', 'PltfDY', 'PltfDC', 'TwrBsSX', 'TwrBsSY', 'TwrBsSC', 'SubBsSX', 'SubBsSY', 'SubBsSC', 'TowerStrike']
    Temp_Amp = np.zeros(len(Out_Amp))
    Dict_Amp = {}
    Dict_Amp['WindSpd'] = WindSpd

    #%% IDA result for each model
    for ii in range(Num_S):

        #  Structural property of each model
        Twr_D = Dict_Sap['TwrBD'][ii]
        Twr_t = Dict_Sap['TwrBT'][ii]
        Twr_A = np.pi/4*(Twr_D**2-(Twr_D-2*Twr_t)**2)
        Twr_W = np.pi/32*Twr_D**3*(1-((Twr_D-2*Twr_t)/Twr_D)**4)
        Sub_D = Dict_Sap['SubUD'][ii]
        Sub_t = Dict_Sap['SubUT'][ii]
        Sub_A = np.pi/4*(Sub_D**2-(Sub_D-2*Sub_t)**2)
        Sub_W = np.pi/32*Sub_D**3*(1-((Sub_D-2*Sub_t)/Sub_D)**4)

        #%%  IDA result of each model
        if Dict_Cnd['AnaTy'][0] == 1:  # Analyze Type -- IDA
            for key in Out_Amp:
                Dict_Amp[key] = np.zeros([len(WindSpd), Dict_Cnd['NumF'][0]])

            for jj in range(len(WindSpd)):  # for each wind speed
                Path_Data = Path_opf + '/Model_{}'.format(ii + 1) + '/{}mps'.format(WindSpd[jj])

                for kk in range(Dict_Cnd['NumF'][0]):  # for each wind-wave farm
                    Amp = Data_Amp_Opf(Path_Data, kk+1, Dict_Vln['IniTI'][0], Col_Opf, Temp_Amp, Twr_D, Twr_t, Twr_A, Twr_W, Sub_D, Sub_t, Sub_A, Sub_W)

                    for i in range(len(Out_Amp)):
                        Dict_Amp[Out_Amp[i]][jj, kk] = Amp[i]


        #%%  Random-simulation result of each model
        if Dict_Cnd['AnaTy'][0] == 2:  # Analyze Type -- IDA
            for key in Out_Amp:
                Dict_Amp[key] = np.zeros(Dict_Cnd['NumF'][0])

            Path_Data = Path_opf + '/Model_{}'.format(ii + 1)
            for kk in range(Dict_Cnd['NumF'][0]):  # for each wind-wave farm
                Amp = Data_Amp_Opf(Path_Data, kk+1, Dict_Vln['IniTI'][0], Col_Opf, Temp_Amp, Twr_D, Twr_t, Twr_A, Twr_W, Sub_D, Sub_t, Sub_A, Sub_W)

                for i in range(len(Out_Amp)):
                    Dict_Amp[Out_Amp[i]][kk] = Amp[i]

        # Save IDA result
        if not os.path.exists(Path_Out):
            os.makedirs(Path_Out)
        Folder = Path_Out + '/Output_opf'
        if not os.path.exists(Folder):
            os.makedirs(Folder)
        Folder = Path_Out + '/Output_opf/Model_{}'.format(ii + 1)
        if not os.path.isdir(Folder):
            os.mkdir(Folder)
        for key, value in Dict_Amp.items():
            np.savetxt(Folder + '/{}.txt'.format(key), value, delimiter=' ')

def Ops_Amp(Dict_Cnd, Dict_Vln, Path_ops, Path_Out, WindSpd, Num_S):
    # Analysis type of OpenSees
    if Dict_Cnd['IM_Model'][0].upper() == 'None'.upper():
        os._exit(0)
    elif Dict_Cnd['IM_Model'][0].upper() == 'All'.upper():
        Ops_M = np.arange(1, Num_S + 1)
    else:
        Ops_M = np.array(Dict_Cnd['IM_Model'])

    Out_Amp = ['NclAX', 'NclAY', 'NclAC', 'TwrTopDX', 'TwrTopDY', 'TwrTopDC', 'PltfDX', 'PltfDY', 'PltfDC', 'TwrBsSX',
               'TwrBsSY', 'TwrBsSC', 'SubBsSX', 'SubBsSY', 'SubBsSC']
    Temp_Amp = np.zeros(len(Out_Amp))
    Dict_Amp = {}
    Dict_Amp['WindSpd'] = WindSpd

    for ii in range(len(Ops_M)):

        if Dict_Cnd['AnaTy'][0] == 1:  # Analyze Type -- IDA
            WindSpd = WindSpd[Dict_Cnd['IM_Nth'][0]-1:]
            for key in Out_Amp:
                Dict_Amp[key] = np.zeros([len(WindSpd), Dict_Cnd['NumF'][0]])

            for jj in range(len(WindSpd)):  # for each wind speed
                Path_Data = Path_ops + '/Model_{}'.format(Ops_M[ii]) + '/{}mps'.format(WindSpd[jj])

                for kk in range(Dict_Cnd['NumF'][0]):  # for each wind-wave farm
                    Amp = Data_Amp_Ops(Path_Data, Dict_Vln['IniTI'][0], Dict_Cnd['dT'][0], Temp_Amp, kk+1)

                    for i in range(len(Out_Amp)):
                        Dict_Amp[Out_Amp[i]][jj, kk] = Amp[i]

        if Dict_Cnd['AnaTy'][0] == 2:  # Analyze Type -- IDA
            for key in Out_Amp:
                Dict_Amp[key] = np.zeros(Dict_Cnd['NumF'][0])

            Path_Data = Path_ops + '/Model_{}'.format(Ops_M[ii])
            for kk in range(Dict_Cnd['NumF'][0]):
                Amp = Data_Amp_Ops(Path_Data, Dict_Vln['IniTI'][0], Dict_Cnd['dT'][0], Temp_Amp, kk + 1)

                for i in range(len(Out_Amp)):
                    Dict_Amp[Out_Amp[i]][kk] = Amp[i]

        # Save IDA result
        if not os.path.exists(Path_Out):
            os.makedirs(Path_Out)
        Folder = Path_Out + '/Output_opf'
        if not os.path.exists(Folder):
            os.makedirs(Folder)
        Folder = Path_Out + '/Output_opf/Model_{}'.format(ii + 1)
        if not os.path.isdir(Folder):
            os.mkdir(Folder)
        for key, value in Dict_Amp.items():
            np.savetxt(Folder + '/{}.txt'.format(key), value, delimiter=' ')

def Data_Amp_Opf(Path_Data, num, IniTI, Col_Opf, Temp_Amp, Twr_D, Twr_t, Twr_A, Twr_W, Sub_D, Sub_t, Sub_A, Sub_W):
    # Tower strike
    Count = 0
    with open(Path_Data + '/{}_out.txt'.format(num), 'r', encoding='UTF-8') as File:
        Data = File.readlines()
        File.close()
        for line in Data:
            if 'strike' in line:
                Count += 1
    if Count != 0:
        Temp_Amp[-1] = 1

    # Amp results
    Data = []
    with open(Path_Data + '/{}_data.txt'.format(num), 'r', encoding='UTF-8') as File:
        for line in File:
            Data.append(line.rstrip())
    File.close()
    Data = Data[int(IniTI / 0.05) + 8:]

    Response = np.zeros([len(Data), len(Col_Opf)])
    ii = 0
    for row in Data:
        lines = row.split('\t')
        templine = np.zeros(0)
        for linedata in lines:
            templine = np.append(templine, np.float64(linedata))
        Response[ii, :] = templine[Col_Opf]
        ii += 1

    Temp_Amp[0] = np.max(np.abs(Response[:, 0]))  # BldTipDX
    Temp_Amp[1] = np.max(np.abs(Response[:, 1]))  # BldTipDY
    Temp_Amp[2] = np.max(np.sqrt(Response[:, 0] ** 2 + Response[:, 1] ** 2))  # Combined response BldTipDC
    Temp_Amp[3] = np.max(np.abs(Response[:, 2]))  # NclAX
    Temp_Amp[4] = np.max(np.abs(Response[:, 3]))  # NclAY
    Temp_Amp[5] = np.max(np.sqrt(Response[:, 2] ** 2 + Response[:, 3] ** 2))  # NclAC
    Temp_Amp[6] = np.max(np.abs(Response[:, 4]))  # TwrTopDX
    Temp_Amp[7] = np.max(np.abs(Response[:, 5]))  # TwrTopDY
    Temp_Amp[8] = np.max(np.sqrt(Response[:, 4] ** 2 + Response[:, 5] ** 2))  # TwrTopDC
    Temp_Amp[9] = np.max(np.abs(Response[:, 6]))  # PltfDX
    Temp_Amp[10] = np.max(np.abs(Response[:, 7]))  # PltfDY
    Temp_Amp[11] = np.max(np.sqrt(Response[:, 6] ** 2 + Response[:, 7] ** 2))  # PltfDCc

    Temp_TS, Temp_SS = Opf_S(Response, Twr_D, Twr_t, Twr_A, Twr_W, Sub_D, Sub_t, Sub_A, Sub_W)
    Temp_Amp[12] = np.max([Temp_TS[0], Temp_TS[18]])  # TwrBsSX
    Temp_Amp[13] = np.max([Temp_TS[9], Temp_TS[27]])  # TwrBsSY
    Temp_Amp[14] = np.max(Temp_TS)  # TwrBsSC
    Temp_Amp[15] = np.max([Temp_SS[0], Temp_SS[18]])  # SubBsSX
    Temp_Amp[16] = np.max([Temp_SS[9], Temp_SS[27]])  # SubBsSY
    Temp_Amp[17] = np.max(Temp_SS)  # SubBsSC
    return Temp_Amp


def Opf_S(Response, Twr_D, Twr_t, Twr_A, Twr_W, Sub_D, Sub_t, Sub_A, Sub_W):
    Ori = np.arange(0, 360, 10)
    Temp_TS = np.zeros(len(Ori))
    Temp_SS = np.zeros(len(Ori))

    for i in range(len(Ori)):
        Theta = Ori[i] / 180 * np.pi
        T = np.array([[np.cos(Theta), np.sin(Theta)], [-np.sin(Theta), np.cos(Theta)]])
        sigma_T = np.zeros(len(Response[:, 0]))
        sigma_S = np.zeros(len(Response[:, 0]))
        for j in range(len(Response[:, 0])):
            FT_XY = np.array([[Response[j, 8]], [Response[j, 9]]])
            FT_Z = Response[j, 10]
            MT_XY = np.array([[Response[j, 11]], [Response[j, 12]]])
            MT_Z = Response[j, 13]
            sigma_T[j] = Cir_S(FT_XY, FT_Z, MT_XY, MT_Z, T, Twr_D / 2, Twr_t, Twr_A, Twr_W) / 1e3

            FS_XY = np.array([[Response[j, 14]], [Response[j, 15]]])
            FS_Z = Response[j, 16]
            MS_XY = np.array([[Response[j, 17]], [Response[j, 18]]])
            MS_Z = Response[j, 19]
            sigma_S[j] = Cir_S(FS_XY, FS_Z, MS_XY, MS_Z, T, Sub_D / 2, Sub_t, Sub_A, Sub_W) / 1e6

        Temp_TS[i] = np.max(sigma_T)
        Temp_SS[i] = np.max(sigma_S)
    return Temp_TS, Temp_SS


def Cir_S(F_XY, F_Z, M_XY, M_Z, T, R, t, A, W):
    F_XY_bar = T @ F_XY
    M_XY_bar = T @ M_XY
    F_Z_bar = F_Z
    M_Z_bar = M_Z
    phi = np.arctan(F_XY_bar[1]/F_XY_bar[0])
    tau_shear = np.sqrt(np.sum(F_XY_bar**2))*np.sin(phi)/np.pi/R/t
    tau_torsion = M_Z_bar/2/np.pi/t/R/R
    tau = tau_shear + tau_torsion
    sigma = F_Z_bar/A + M_XY_bar[1]/W
    sigma_eq = np.sqrt(sigma**2 + 3*tau**2)
    return sigma_eq


def Data_Amp_Ops(Path_Data, IniTI, dT, Temp_Amp, num):
    Data = np.loadtxt(Path_Data + '/TopAccel_{}.txt'.format(num))
    Data = Data[int(IniTI/dT):, [1, 2]]
    Temp_Amp[0] = np.max(np.abs(Data[:, 0]))
    Temp_Amp[1] = np.max(np.abs(Data[:, 1]))
    Temp_Amp[2] = np.max(np.sqrt(Data[:, 0] ** 2 + Data[:, 1] ** 2))

    Data = np.loadtxt(Path_Data + '/TopDisp_{}.txt'.format(num))
    Data = Data[int(IniTI / dT):, [1, 2]]
    Temp_Amp[3] = np.max(np.abs(Data[:, 0]))
    Temp_Amp[4] = np.max(np.abs(Data[:, 1]))
    Temp_Amp[5] = np.max(np.sqrt(Data[:, 0] ** 2 + Data[:, 1] ** 2))

    Data = np.loadtxt(Path_Data + '/PltfDisp_{}.txt'.format(num))
    Data = Data[int(IniTI / dT):, [1, 2]]
    Temp_Amp[6] = np.max(np.abs(Data[:, 0]))
    Temp_Amp[7] = np.max(np.abs(Data[:, 1]))
    Temp_Amp[8] = np.max(np.sqrt(Data[:, 0] ** 2 + Data[:, 1] ** 2))

    Data = np.loadtxt(Path_Data + '/TwrBsS_{}.txt'.format(num))
    Temp_Amp[9] = Data[0]
    Temp_Amp[10] = Data[1]
    Temp_Amp[11] = Data[2]

    Data = np.loadtxt(Path_Data + '/SubBsS_{}.txt'.format(num))
    Temp_Amp[12] = Data[0]
    Temp_Amp[13] = Data[1]
    Temp_Amp[14] = Data[2]

    return Temp_Amp

