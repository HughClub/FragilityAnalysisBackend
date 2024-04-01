import argparse
from ReadInpFiles import ReadInpFile, ReadSampInp
from Opf_Run import OpfRun
from Ops_Run import OpsRun
from Result_Amp import Result_Amp
from Fragility import NoE, Frgl_plt
from Vulnerability import Vnbl
from LHS import LHS

# 创建解析器
parser = argparse.ArgumentParser(description='Process some paths.')
parser.add_argument('--Path_Inp', type=str, required=True, help='The path to the input folder')
parser.add_argument('--Path_FAST', type=str, required=True, help='The path to the OpenFAST folder')
parser.add_argument('--Path_Out', type=str, required=True, help='The path to the output folder')

# 解析参数
args = parser.parse_args()

# 使用参数
Path_Inp = args.Path_Inp
Path_FAST = args.Path_FAST
Path_Out = args.Path_Out

# 以下代码保持不变
LHS(Path_Inp)
Dict_Sap, Num_S = ReadSampInp(Path_Inp+'/LHS_Samples.xlsx')
Dict_Cnd = ReadInpFile(Path_Inp + '/Condition.inp')
Dict_Vln = ReadInpFile(Path_Inp + '/Vulnerability.inp')
OpfRun(Path_Out + '/Output_opf', Path_FAST, Dict_Sap, Dict_Cnd, Num_S)
OpsRun(Path_Out + '/Output_ops', Path_Out + '/Output_opf', Dict_Sap, Dict_Cnd, Num_S)
Result_Amp(Dict_Sap, Dict_Cnd, Dict_Vln, Path_Out + '/Output_opf', Path_Out + '/Output_ops', Num_S)
NoE(Dict_Sap, Dict_Cnd, Dict_Vln, Path_Out, Num_S)
# 本来是要绘制脆弱性曲线的，但是由于只需要结果，所以将函数中的绘制部分注释掉了
Frgl_plt(Dict_Vln, Path_Out)
Vnbl(Dict_Vln, Path_Out)