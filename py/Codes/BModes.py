# -*- coding: utf-8 -*
import numpy as np
from math import *
import subprocess
from sklearn.linear_model import LinearRegression
import os
# import matplotlib.pyplot as plt

def BModes(Path_FAST, Path_Out, Dict_Sap, Num_S):
    for ii in range(Num_S):
        Path_Temp = Path_Out + '/Model_' + str(ii + 1) + '/Temp'
        #  Write Twr_Prop file for BModes
        Key = ['TwrTH', 'TwrTD', 'TwrTT', 'TwrBD', 'TwrBT', 'SubTH', 'WatDep', 'SubUD', 'SubUT', 'MatMod', 'MatDen', 'MatPoR', 'HubMass', 'NacMass', 'HubH']
        Value = np.zeros(0)
        for j in range(len(Key)):
            Value = np.append(Value, Dict_Sap[Key[j]][ii])
        File_Inp, FE_loc = WriteInput(Value, Path_FAST, Path_Temp)

        # Run BModes to obtain tower modes
        subprocess.run([(Path_FAST+'/BModes_Alpha.exe'), File_Inp])

        # Get coefficients for modes
        File_out = (Path_Temp+'\\BModes_Inp.out')
        Coefs = GetShapeCoefs(File_out, FE_loc)

        # Save coefficients for ElastoDyn input
        np.savetxt((Path_Out+'/Model_' + str(ii+1) + '/Temp/Twr_Shape_Coefs.txt'), Coefs, fmt='%.6f', delimiter=' ')

def WriteInput(Value, Path_FAST, Path_Des):
    # Write sectional parameters
    # global Num_SubS, Num_TwrS
    Num_SubS = 11
    Num_TwrS = 28
    Num_Sec = Num_SubS + Num_TwrS
    Ratio_Sub = round((Value[5] + Value[6]) / (Value[0] + Value[6]), 5)
    Frac_Sub = np.linspace(0, Ratio_Sub, num=Num_SubS)
    Frac_Twr = np.linspace(Ratio_Sub + 0.00001, 1, num=Num_TwrS)
    Frac = np.append(Frac_Sub, Frac_Twr)

    Sec_D = np.append(np.array([Value[7]] * Num_SubS), np.linspace(Value[3], Value[1], num=Num_TwrS))
    Sec_T = np.append(np.array([Value[8]] * Num_SubS), np.linspace(Value[4], Value[2], num=Num_TwrS))
    Sec_d = np.zeros(0)
    for j in range(Num_Sec):
        Sec_d = np.append(Sec_d, (Sec_D[j] - 2 * Sec_T[j]))

    G = Value[9] / 2 / (1 + Value[11])
    InpVal = np.zeros([Num_Sec, 13])
    InpVal[:, 0] = Frac
    for j in range(Num_Sec):
        InpVal[j, 3] = (1/4 * pi * (pow(Sec_D[j], 2) - pow(Sec_d[j], 2)) * Value[10])
        InpVal[j, 4:5] = (1/64 * pi * (pow(Sec_D[j], 4) - pow(Sec_d[j], 4)) * Value[10])
        InpVal[j, 6:7] = (1/64 * pi * (pow(Sec_D[j], 4) - pow(Sec_d[j], 4)) * Value[9])
        InpVal[j, 8] = (1/32 * pi * (pow(Sec_D[j], 4) - pow(Sec_d[j], 4)) * G)
        InpVal[j, 9] = (1/4 * pi * (pow(Sec_D[j], 2) - pow(Sec_d[j], 2)) * Value[9])
    InpVal = np.round(InpVal, decimals=6)

    with open((Path_FAST+'/BModes_Twr.dat'), 'r') as file:
        lines = file.readlines()
        for j in range(Num_Sec):
            array_str = ' '.join(map(str, InpVal[j]))
            lines[5+j] = lines[5+j].rstrip('\n') + ' ' + array_str.lstrip() + '\n'
    File_Sec = (Path_Des+'/BModes_Twr.dat')
    with open(File_Sec, 'w') as f:
        f.writelines(lines)

    #  Write input file for BModes
    FE_loc = np.append(np.linspace(0, Ratio_Sub, 15), np.linspace(Ratio_Sub+0.01, 1, 46))
    FE_loc = np.round(FE_loc, decimals=6)

    tip_mass = np.round((53220 + Value[12] + Value[13]), 8)
    cm_axial = 0.8196*(Value[14]-Value[0])
    with open((Path_FAST + '/BModes_Inp.bmi'), 'r') as file:
        lines = file.readlines()
        lines[8] = str(Value[0] + Value[6]) + ' ' * 5 + 'radius:' + ' ' * 5 + '\n'
        lines[18] = str(tip_mass) + ' ' * 5 + 'tip_mass' + ' ' * 5 + '\n'
        lines[20] = str(cm_axial) + ' ' * 5 + 'cm_axial' + ' ' * 5 + '\n'
        lines[30] = "'" + File_Sec + "'" + ' ' * 5 + ': sec_props_file' + ' ' * 5 + '\n'
        lines[47] = ' '.join(map(str, FE_loc)) + '\n'
    File_Inp = (Path_Des + '/BModes_Inp.bmi')
    with open(File_Inp, 'w') as f:
        f.writelines(lines)

    return File_Inp, FE_loc


def GetShapeCoefs(File_out, FE_loc):
    span_loc = FE_loc[15:]
    with open(File_out, 'r') as file:
        lines = file.readlines()
        count_X = -1
        count_Y = 1
        Twr_Shape = np.zeros([46, 4])  # Fore-aft 1st and 2nd， Side-side 1st and 2nd
        for k in range(6):
            s = []
            [s.append(lines[j]) for j in range(12+k*67, 73+k*67, 1)]
            Shape = [list(map(float, item.split())) for item in s]
            Shape = np.array(Shape)
            Trans = abs(Shape[:, [2, 4, 5]])
            max_idx = np.unravel_index(np.argmax(Trans), Trans.shape)
            if max_idx[1] == 1:
                count_X += 1
                Twr_Shape[:, count_X] = Shape[15:, 3]
            elif max_idx[1] == 0:
                count_Y += 1
                Twr_Shape[:, count_Y] = Shape[15:, 1]
            if count_X == 1 and count_Y == 3:
                break

    Coefs = CoefsCal(span_loc, Twr_Shape)
    return Coefs


def CoefsCal(span_loc, Twr_Shape):
    Coefs = np.zeros([5, 4])
    for j in range(4):
        x = span_loc-span_loc[0]
        y = Twr_Shape[:, j]
        f2 = (y[1]-y[0])/(x[1]-x[0])
        y_direct = y-y[0]-x*f2
        Norm_x = x/(max(x))
        x_ipdr_s2 = Norm_x**2
        x_ipdr_s3 = Norm_x**3
        x_ipdr_s4 = Norm_x**4
        x_ipdr_s5 = Norm_x**5
        x_ipdr_s6 = Norm_x**6
        X = np.column_stack([x_ipdr_s2, x_ipdr_s3, x_ipdr_s4, x_ipdr_s5, x_ipdr_s6])
        Y = y_direct
        model = LinearRegression()
        model.fit(X, Y)
        b = model.coef_
        b = b/sum(b)
        b[4] = 1-sum(b[0:4])
        Coefs[:, j] = b

        # f1 = 1e-4*abs((x[1]-x[0])/y[1]-y[0])
        # factor_y = y*f1
        # project_x1 = x*cos(atan(f1*f2))+(factor_y-factor_y[0])*(sin(atan(f1*f2)))
        # project_y1 = -x*sin(atan(f1*f2))+(factor_y-factor_y[0])*(cos(atan(f1*f2)))
        # Norm_x1 = project_x1/(max(project_x1))
        # Norm_y1 = project_y1/(project_y1[-1])
        # Y_polyfit = b[0]*x_ipdr_s2+b[1]*x_ipdr_s3+b[2]*x_ipdr_s4+b[3]*x_ipdr_s5+b[4]*x_ipdr_s6

        # plt.scatter(Norm_x1, Norm_y1)
        # plt.plot(Norm_x1, Y_polyfit)
        # plt.xticks(fontsize=18, fontname='Times New Roman')
        # plt.yticks(fontsize=18, fontname='Times New Roman')
        # plt.show()
    return Coefs
