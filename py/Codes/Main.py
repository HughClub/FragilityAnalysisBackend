# -*- coding: utf-8 -*
#%%  OpenFAST path
Path_FAST = 'D:\Main\WorkSpace\java\ideaprojects\springboot-init\py\OpenFAST'  # OpenFAST文件夹所在位置，这个应该是在服务器的固定位置

## ---------------------------------------------##
Path_Inp = 'D:/Research/3-Fragility_extreme_condiitons/Software/Input'  # 输入文件所在位置
Path_Out = 'D:/Research/3-Fragility_extreme_condiitons/Software/Output'  # 输出文件夹所在位置
## ---------------------------------------------##

## Step-1
from LHS import LHS
def Step_1(Path_Inp, Path_Out):
    Path_Out_Step_1 = Path_Out + '/Step_1'
    LHS(Path_Inp, Path_Out_Step_1)
    return

## Step-2
from ReadInpFiles import ReadInpFile, ReadSampInp
from Opf_Run import OpfRun
from Ops_Run import OpsRun
def Step_2(Path_FAST, Path_Inp, Path_Out):
    Path_Out_Step_1 = Path_Out + '/Step_1'
    Path_Out_Step_2 = Path_Out + '/Step_2'

    Dict_Sap, Num_S = ReadSampInp(Path_Out_Step_1+'/LHS_Samples.xlsx')
    Dict_Cnd = ReadInpFile(Path_Inp + '/Condition.inp')

    OpfRun(Path_Out_Step_2, Path_FAST, Dict_Sap, Dict_Cnd, Num_S)

    OpsRun(Path_Out_Step_2, Dict_Sap, Dict_Cnd, Num_S)
    return

## Step-3
from ReadInpFiles import ReadInpFile, ReadSampInp
from Result_Amp import Result_Amp
def Step_3(Path_Inp, Path_Out):
    Path_Out_Step_2 = Path_Out + '/Step_2'
    Path_Out_Step_3 = Path_Out + '/Step_3'

    Dict_Sap, Num_S = ReadSampInp(Path_Out+'/Step_1/LHS_Samples.xlsx')
    Dict_Cnd = ReadInpFile(Path_Inp + '/Condition.inp')
    Dict_Vln = ReadInpFile(Path_Inp + '/Vulnerability.inp')

    Result_Amp(Dict_Sap, Dict_Cnd, Dict_Vln, Path_Out_Step_2, Path_Out_Step_3, Num_S)
    return

## Step-4
from ReadInpFiles import ReadInpFile, ReadSampInp
from Fragility import NoE, Frgl_plt
def Step_4(Path_Inp, Path_Out):
    Path_Out_Step_1 = Path_Out + '/Step_1'
    Path_Out_Step_3 = Path_Out + '/Step_3'
    Path_Out_Step_4 = Path_Out + '/Step_4'

    Dict_Sap, Num_S = ReadSampInp(Path_Out_Step_1+'/LHS_Samples.xlsx')
    Dict_Cnd = ReadInpFile(Path_Inp + '/Condition.inp')
    Dict_Vln = ReadInpFile(Path_Inp + '/Vulnerability.inp')

    NoE(Dict_Sap, Dict_Cnd, Dict_Vln, Path_Out_Step_3, Path_Out_Step_4, Num_S)
    Frgl_plt(Dict_Vln, Path_Out_Step_4)
    return

## Step-5
from ReadInpFiles import ReadInpFile, ReadSampInp
from Vulnerability import Vnbl
def Step_5(Path_Inp, Path_Out):
    Path_Out_Step_4 = Path_Out + '/Step_4'
    Path_Out_Step_5 = Path_Out + '/Step_5'

    Dict_Vln = ReadInpFile(Path_Inp + '/Vulnerability.inp')

    Vnbl(Dict_Vln, Path_Out_Step_4, Path_Out_Step_5)
    return


# 引入参数解析
import argparse


# ## Run steps
# Step_1(Path_Inp, Path_Out)
# Step_2(Path_FAST, Path_Inp, Path_Out)
# Step_3(Path_Inp, Path_Out)
# Step_4(Path_Inp, Path_Out)
# Step_5(Path_Inp, Path_Out)

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Run the OpenFAST')
    parser.add_argument('--Path_Inp', type=str, default=Path_Inp, help='The path of the input files')
    parser.add_argument('--Path_Out', type=str, default=Path_Out, help='The path of the output files')
    parser.add_argument('--Task_Stage', type=str, default=Path_FAST, help='The stage of the task')
    args = parser.parse_args()

    Path_Inp = args.Path_Inp
    Path_Out = args.Path_Out
    Task_Stage = args.Task_Stage

    if Task_Stage == 'Step_1':
        Step_1(Path_Inp, Path_Out)
    elif Task_Stage == 'Step_2':
        Step_2(Path_FAST, Path_Inp, Path_Out)
    elif Task_Stage == 'Step_3':
        Step_3(Path_Inp, Path_Out)
    elif Task_Stage == 'Step_4':
        Step_4(Path_Inp, Path_Out)
    elif Task_Stage == 'Step_5':
        Step_5(Path_Inp, Path_Out)
    else:
        # 抛异常
        raise ValueError('The Task_Stage is not correct')

