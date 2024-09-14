# -*- coding: utf-8 -*
import numpy as np
import os
from Ops_Inp import Ops_input
from Ops_Mdl import OpenSees_Model

def OpsRun(Path_Out_Step_2, Dict_Sap, Dict_Cnd, Num_S):
    if not os.path.isdir(Path_Out_Step_2):
        os.mkdir(Path_Out_Step_2)
    Path_Out = Path_Out_Step_2 + '/Output_ops'
    if not os.path.isdir(Path_Out):
        os.mkdir(Path_Out)
    Path_opf = Path_Out_Step_2 + '/Output_opf'
    #%% Analysis type of OpenSees
    if Dict_Cnd['IM_Model'][0].upper() == 'None'.upper():
        os._exit(0)
    elif Dict_Cnd['IM_Model'][0].upper() == 'All'.upper():
        Ops_M = np.arange(1, Num_S+1)
    else:
        assert max(Dict_Cnd['IM_Model']) <= Num_S, 'IM_Model does not match the number of samples'
        Ops_M = np.array(Dict_Cnd['IM_Model'])

    #%% Generate input file for opensees
    ksi = Dict_Cnd['DampR'][0]
    dT = Dict_Cnd['dT'][0]

    if not os.path.isdir(Path_Out):
        os.mkdir(Path_Out)
    for ii in range(len(Ops_M)):
        Path_Model = Path_Out + '/Model_' + str(Ops_M[ii])
        Path_Temp = Path_Model + '/Temp'
        if not os.path.isdir(Path_Model):
            os.mkdir(Path_Model)
        if not os.path.isdir(Path_Temp):
            os.mkdir(Path_Temp)

        Ops_input(Dict_Sap, Path_Temp, Ops_M[ii])
        Path_File = Path_Model + '/Temp/OpsInp.xlsx'
        MatStren = Dict_Sap['MatStr'][ii]
        MatMod = Dict_Sap['MatMod'][ii]

        #%% AnaTy
        if Dict_Cnd['AnaTy'][0] == 1:
            if Dict_Cnd['IMImpM'][0] == 1:  # Wind input method
                WindSpd = np.arange(Dict_Cnd['WindSpd'][0], Dict_Cnd['WindSpd'][1] + 1, Dict_Cnd['WindSpd'][2])
            elif Dict_Cnd['IMImpM'][0] == 2:
                WindSpd = np.loadtxt(Dict_Cnd['WindSpd'][0], dtype=np.float)
            WindSpd = WindSpd[Dict_Cnd['IM_Nth'][0]-1:]

            for jj in range(len(WindSpd)):
                Path_Wind = Path_Model + '/{}mps'.format(WindSpd[jj])
                if not os.path.isdir(Path_Wind):
                    os.mkdir(Path_Wind)

                for kk in range(Dict_Cnd['NumF'][0]):
                    Path_Load = Path_opf + '/Model_' + str(Ops_M[ii]) + '/{}mps'.format(WindSpd[jj])
                    OpenSees_Model(Path_Load, Path_File, Path_Wind, ksi, dT, MatStren, MatMod, kk + 1)

        if Dict_Cnd['AnaTy'][0] == 2:
            for kk in range(Dict_Cnd['NumF'][0]):
                Path_Load = Path_opf + '/Model_' + str(Ops_M[ii]) + '/{}_data.txt'.format(kk + 1)
                OpenSees_Model(Path_Load, Path_File, Path_Model, ksi, dT, MatStren, MatMod, kk + 1)

