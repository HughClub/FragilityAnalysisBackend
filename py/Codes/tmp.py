# -*- coding: utf-8 -*
from LHS import LHS
from ReadInpFiles import ReadInpFile, ReadSampInp
from Opf_Run import OpfRun
from Ops_Run import OpsRun
from Result_Amp import Result_Amp
from Fragility import NoE, Frgl_plt
from Vulnerability import Vnbl

#%%  Main paths
Path_Main = 'C:/Users/XY/Desktop/ysxfx'
Path_Inp = Path_Main + '/Input'
Path_FAST = Path_Main + '/OpenFAST'
Path_Out = Path_Main + '/Output'
#%%  LHS Samples for Uncertainty
LHS(Path_Inp)
#%%  Variables for analysis
Dict_Sap, Num_S = ReadSampInp(Path_Inp+'/LHS_Samples.xlsx')
Dict_Cnd = ReadInpFile(Path_Inp + '/Condition.inp')
Dict_Vln = ReadInpFile(Path_Inp + '/Vulnerability.inp')
#%%  Run OpenFAST for each simulation
OpfRun(Path_Out + '/Output_opf', Path_FAST, Dict_Sap, Dict_Cnd, Num_S)
#%%  Run OpenSees for each simulation
OpsRun(Path_Out + '/Output_ops', Path_Out + '/Output_opf', Dict_Sap, Dict_Cnd, Num_S)
#%%  Amplitudes of response histories
Result_Amp(Dict_Sap, Dict_Cnd, Dict_Vln, Path_Out + '/Output_opf', Path_Out + '/Output_ops', Num_S)
NoE(Dict_Sap, Dict_Cnd, Dict_Vln, Path_Out, Num_S)
#%%  Plot fragility curves
# Dict_Vln = ReadInpFile(Path_Inp + '/Vulnerability.inp')
# NoE(Dict_Sap, Dict_Cnd, Dict_Vln, Path_Out, Num_S)
Frgl_plt(Dict_Vln, Path_Out)
#%%  Plot vulnerability curves
Vnbl(Dict_Vln, Path_Out)

