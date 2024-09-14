# -*- coding: utf-8 -*
import time
import os.path as osp

cur_dir = osp.dirname(__file__)
#%%  OpenFAST path
Path_FAST = osp.join(cur_dir, "..", "OpenFAST")
## ---------------------------------------------##
Path_Inp = 'D:/upload/0/input'  # 输入文件所在位置
Path_Out = 'D:/upload/0/output'  # 输出文件夹所在位置
## ---------------------------------------------##

def get_step_path(path:str, step:int) -> str:
    return osp.join(path, f"Step_{step}")


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
def Step_2(Path_Inp, Path_Out, Path_FAST=Path_FAST):
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
from Fragility import NoE, Frgl_Cmp
def Step_4(Path_Inp, Path_Out):
    Path_Out_Step_1 = Path_Out + '/Step_1'
    Path_Out_Step_3 = Path_Out + '/Step_3'
    Path_Out_Step_4 = Path_Out + '/Step_4'

    Dict_Sap, Num_S = ReadSampInp(Path_Out_Step_1+'/LHS_Samples.xlsx')
    Dict_Cnd = ReadInpFile(Path_Inp + '/Condition.inp')
    Dict_Vln = ReadInpFile(Path_Inp + '/Vulnerability.inp')

    NoE(Dict_Sap, Dict_Cnd, Dict_Vln, Path_Out_Step_3, Path_Out_Step_4, Num_S)
    Frgl_Cmp(Path_Out_Step_4, Dict_Vln)
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


def Steps(pathInput, pathOutput):
    outputs = [osp.join(pathOutput, f"Step_{i+1}") for i in range(5)]

    # Step_1
    step1_start = time.time()
    LHS(pathInput, outputs[0])
    yield time.time() - step1_start

    # Step_2
    step2_start = time.time()
    dictSample, numSample = ReadSampInp(osp.join(outputs[0], "LHS_Samples.xlsx"))
    condDict = ReadInpFile(osp.join(pathInput, "Condition.inp"))
    OpfRun(outputs[1], Path_FAST, dictSample, condDict, numSample)
    OpsRun(outputs[1], dictSample, condDict, numSample)
    yield time.time() - step2_start

    # Step_3
    step3_start = time.time()
    vulDict = ReadInpFile(osp.join(pathInput, "Vulnerability.inp"))
    Result_Amp(dictSample, condDict, vulDict, outputs[1], outputs[2], numSample)
    yield time.time() - step3_start

    # Step_4
    step4_start = time.time()
    NoE(dictSample, condDict, vulDict, outputs[2], outputs[3], numSample)
    Frgl_Cmp(outputs[3], vulDict)
    yield time.time() - step4_start

    # Step_5
    step5_start = time.time()
    Vnbl(vulDict, outputs[3], outputs[4])
    yield time.time() - step5_start

def main():
    parser = argparse.ArgumentParser(description='Run the OpenFAST')
    parser.add_argument('--Path_Inp', type=str, default=Path_Inp, help='The path of the input files')
    parser.add_argument('--Path_Out', type=str, default=Path_Out, help='The path of the output files')
    parser.add_argument('--Path_FAST', type=str, default=Path_FAST, help='The path of the OpenFAST')
    parser.add_argument('--Task_Stage', type=str, default=Path_FAST, help='The stage of the task')
    parser.add_argument("--aio", action="store_true", help="Use asyncio to run the steps")
    args = parser.parse_args()
    print(args)

    if args.aio:
        times = [val for val in Steps(args.Path_Inp, args.Path_Out)]
        for idx, val in enumerate(times):
            print(f"Step {idx+1} costs: {val:.2f}s")
        return

    if args.Task_Stage == 'Step_1':
        Step_1(args.Path_Inp, args.Path_Out)
    elif args.Task_Stage == 'Step_2':
        Step_2(args.Path_Inp, args.Path_Out, Path_FAST)
    elif args.Task_Stage == 'Step_3':
        Step_3(args.Path_Inp, args.Path_Out)
    elif args.Task_Stage == 'Step_4':
        Step_4(args.Path_Inp, args.Path_Out)
    elif args.Task_Stage == 'Step_5':
        Step_5(args.Path_Inp, args.Path_Out)
    else:
        # 抛异常
        raise ValueError('The Task_Stage is not correct')

if __name__ == '__main__':
    main()