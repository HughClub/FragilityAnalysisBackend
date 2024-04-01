# -*- coding: utf-8 -*
import openseespy.opensees as op
import numpy as np
import math
import os
import pandas as pd

def OpenSees_Model(Path_Load, Path_File, Path_Out, ksi, dT, MatStren, MatMod, num):
    Load_File = Path_Load + '/{}_data.txt'.format(num)

    Nodes = pd.read_excel(Path_File, sheet_name='Node', header=0)
    Elements = pd.read_excel(Path_File, sheet_name='Element', header=0)
    Sections = pd.read_excel(Path_File, sheet_name='Section', header=0)
    Aero_Twr = pd.read_excel(Path_File, sheet_name='AeroDyn', header=0)

    op.wipe()
    op.model('basic', '-ndm', 3, '-ndf', 6)
    [op.node(i + 1, float(Nodes.X[i]), float(Nodes.Y[i]), float(Nodes.Z[i])) for i in range(len(Nodes))]
    op.fix(1, 1, 1, 1, 1, 1, 1)
    op.uniaxialMaterial('Steel02', 1, MatStren, MatMod, 0.01, 18.0, 0.925, 0.15)
    for i in range(len(Sections)):
        op.section('Fiber', i + 1, '-GJ', Sections.GJ[i])
        op.patch('circ', 1, 18, 3, 0.0, 0.0, Sections.R_in[i], Sections.R_out[i], 0.0, 360.0)
    op.geomTransf('Linear', 1, 1, 1, 1)
    for i in range(len(Elements)):
        op.beamIntegration('Legendre', i + 1, int(Elements.Section[i]), 5)
        op.element('forceBeamColumn', i + 1, int(Elements.Node1[i]), int(Elements.Node2[i]), 1, i + 1)
    op.rigidLink('beam', 39, 40)
    [op.mass(i + 1, Nodes.Mass[i], Nodes.Mass[i], Nodes.Mass[i], 0.0, 0.0, 0.0) for i in range(len(Nodes))]
    Eigen = op.eigen('genBandArpack', 4)
    Omega = [math.pow(Eigen[i], 0.5) for i in range(len(Eigen))]
    a0 = 2 * ksi * (Omega[0] * Omega[2]) / (Omega[0] + Omega[2])
    a1 = 2 * ksi / (Omega[0] + Omega[2])
    op.rayleigh(a0, a1, 0.0, 0.0)
    Nodes_Rot = 40
    Nodes_Twr = Aero_Twr.Node
    Nodes_Sub = 11
    DOF_Load_x = [1, 0, 0, 0, 0, 0]
    DOF_Load_y = [0, 1, 0, 0, 0, 0]
    Time, Dic_Rot, Dic_Twr, Dic_Fod, Col_Rot, Col_Twr, Col_Sub = Loading(Path_Load, Load_File, Aero_Twr)
    [op.timeSeries('Path', 101 + i, '-dt', dT, '-values', *Dic_Rot[i], '-time', *Time, 'startTime', 0.0) for i in
     range(len(Col_Rot))]  # Rotor
    [op.timeSeries('Path', 201 + i, '-dt', dT, '-values', *Dic_Twr[i], '-time', *Time, 'startTime', 0.0) for i in
     range(len(Col_Twr))]  # Tower
    [op.timeSeries('Path', 301 + i, '-dt', dT, '-values', *Dic_Fod[i], '-time', *Time, 'startTime', 0.0) for i in
     range(len(Col_Sub))]  # Substructure
    op.pattern('Plain', 101, 101)  # Rotor
    op.load(int(Nodes_Rot), *DOF_Load_x)
    op.pattern('Plain', 102, 102)  # Rotor
    op.load(int(Nodes_Rot), *DOF_Load_y)
    for i in range(9):  # Tower
        op.pattern('Plain', 201 + 2*i, 201 + 2*i)
        op.load(int(Nodes_Twr[i]), *DOF_Load_x)
        op.pattern('Plain', 202 + 2*i, 202 + 2*i)
        op.load(int(Nodes_Twr[i]), *DOF_Load_y)
    op.pattern('Plain', 301, 301)  # Substructure
    op.load(int(Nodes_Sub), *DOF_Load_x)
    op.pattern('Plain', 302, 302)  # Substructure
    op.load(int(Nodes_Sub), *DOF_Load_y)

    op.recorder('Node', '-file', (Path_Out + '/TopDisp_{}.txt'.format(num)), '-time', '-dT', dT, '-node', 40, '-dof', *[1, 2], 'disp')
    op.recorder('Node', '-file', (Path_Out + '/TopAccel_{}.txt'.format(num)), '-time', '-dT', dT, '-node', 40, '-dof', *[1, 2], 'accel')
    op.recorder('Node', '-file', (Path_Out + '/PltfDisp_{}.txt'.format(num)), '-time', '-dT', dT, '-node', 11, '-dof', *[1, 2], 'disp')

    Ori = np.arange(0, 360, 10)
    for i in range(len(Ori)):
        Theta = Ori[i]/180*np.pi
        op.recorder('EnvelopeElement', '-file', (Path_Out + '/TwrBaseS_{}_{}.txt'.format(num, i+1)), '-ele', 11, 'section', 1, 'fiber', *[100*np.cos(Theta), 100*np.sin(Theta)], 'stress')
        op.recorder('EnvelopeElement', '-file', (Path_Out + '/BaseS_{}_{}.txt'.format(num, i+1)), '-ele', 1, 'section', 1, 'fiber', *[100*np.cos(Theta), 100*np.sin(Theta)], 'stress')

    op.wipeAnalysis()
    op.constraints('Transformation')
    op.numberer('Plain')
    op.system('Umfpack')
    op.test('NormDispIncr', 1.0e-6, 1000, 0)
    op.algorithm('BFGS')
    op.integrator('Newmark', 0.5, 0.25)
    op.analysis('Transient')
    numIncr = len(Time)

    ok = op.analyze(numIncr, dT)
    tCurrent = op.getTime()
    test = {1: 'NormDispIncr', 2: 'RelativeEnergyIncr', 4: 'RelativeNormUnbalance', 5: 'RelativeNormDispIncr',
            6: 'NormUnbalance'}
    algorithm = {1: 'KrylovNewton', 2: 'SecantNewton', 4: 'RaphsonNewton', 5: 'PeriodicNewton', 6: 'BFGS', 7: 'Broyden',
                 8: 'NewtonLineSearch'}

    for i in test:
        for j in algorithm:
            if ok != 0:
                if j < 4:
                    op.algorithm(algorithm[j], '-initial')
                else:
                    op.algorithm(algorithm[j])

                op.test(test[i], 1e-6, 1000)
                ok = op.analyze(numIncr, dT)

                if ok == 0:
                    File = []
                    File[0] = 'test = ' + '"' + test[i] + '"'
                    File[1] = 'algorithm = ' + '"' + algorithm[j] + '"'
                    with open(Path_Out + '/{}_Completed.txt'.format(num), 'w') as file:
                        file.write('\n'.join(File))
                    file.close()
                    break
            else:
                continue

    if ok != 0:
        op.test('NormDispIncr', 1.0e-6, 1000, 0)
        op.algorithm('BFGS')
        op.analyze(numIncr, dT)
        File = []
        File[0] = 'test = "BFGS"'
        File[1] = 'algorithm = "Newmark"'
        with open(Path_Out + '/{}_Failed.txt'.format(num), 'w') as file:
            file.write('\n'.join(File))
        file.close()

    op.remove('recorders')

    #%% Deal with Stress output
    TwrBsS = np.zeros([3, 1])
    SubBsS = np.zeros([3, 1])
    Temp_TS = np.zeros(len(Ori))
    Temp_SS = np.zeros(len(Ori))
    for i in range(len(Ori)):
        Data_T = np.loadtxt(Path_Out + '/TwrBaseS_{}_{}.txt'.format(num, i+1))
        Data_S = np.loadtxt(Path_Out + '/BaseS_{}_{}.txt'.format(num, i+1))
        Temp_TS[i] = Data_T[2]
        Temp_SS[i] = Data_S[2]
        os.remove(Path_Out + '/TwrBaseS_{}_{}.txt'.format(num, i+1))
        os.remove(Path_Out + '/BaseS_{}_{}.txt'.format(num, i + 1))

    TwrBsS[0] = np.max([Temp_TS[0], Temp_TS[18]])
    TwrBsS[1] = np.max([Temp_TS[9], Temp_TS[27]])
    TwrBsS[2] = np.max(Temp_TS)
    np.savetxt(Path_Out + '/TwrBsS_{}.txt'.format(num), TwrBsS, delimiter=' ')
    SubBsS[0] = np.max([Temp_SS[0], Temp_SS[18]])
    SubBsS[1] = np.max([Temp_SS[9], Temp_SS[27]])
    SubBsS[2] = np.max(Temp_SS)
    np.savetxt(Path_Out + '/SubBsS_{}.txt'.format(num), SubBsS, delimiter=' ')


def Loading(Path_Load, Load_File, Aero_Twr):
    Data = []
    with open(Load_File, 'r', encoding='UTF-8') as File:
        for line in File:
            Data.append(line.rstrip())
    File.close()
    Data = Data[8:]

    Col_Rot, Col_Twr, Col_Sub = Load_Col(Path_Load)

    Time = []
    Dic_Twr = {}
    Dic_Rot = {}
    Dic_Sub = {}
    Ht = Aero_Twr.Ht
    for i in range(len(Col_Rot)):
        Dic_Rot[i] = []
    for i in range(len(Col_Twr)):
        Dic_Twr[i] = []
    for i in range(len(Col_Sub)):
        Dic_Sub[i] = []

    for row in Data:
        lines = row.split('\t')
        templine = []
        for linedata in lines:
            rd = float(linedata)
            templine.append(rd)
        Time.append(templine[0])
        for j in range(len(Col_Rot)):
            Dic_Rot[j].append(templine[Col_Rot[j]])
        for j in range(9):
            Dic_Twr[j].append(templine[Col_Twr[j]]*Ht[j])
        for j in range(9):
            Dic_Twr[j+9].append(templine[Col_Twr[j+9]]*Ht[j])
        for j in range(len(Col_Sub)):
            Dic_Sub[j].append(templine[Col_Sub[j]])

    return Time, Dic_Rot, Dic_Twr, Dic_Sub, Col_Rot, Col_Twr, Col_Sub

def Load_Col(Path_Load):
    Opf_Rot = ['RtAeroFxh', 'RtAeroFyh']
    Opf_Sub = ['HydroFxi', 'HydroFyi']
    Opf_Twr = []
    for i in range(9):
        Opf_Twr.append('TwN{}Fdx'.format(i+1))
        Opf_Twr.append('TwN{}Fdy'.format(i + 1))

    with open(Path_Load + '/Sum.txt') as File:
        Data = File.readlines()
    File.close()
    Col_Rot = []
    Col_Sub = []
    Col_Twr = []
    for i in range(len(Opf_Rot)):
        for line in Data:
            if Opf_Rot[i] in line:
                line_ = line.strip().split()
                Col_Rot.append(int(line_[0]) - 1)
    for i in range(len(Opf_Sub)):
        for line in Data:
            if Opf_Sub[i] in line:
                line_ = line.strip().split()
                Col_Sub.append(int(line_[0]) - 1)
    for i in range(len(Opf_Twr)):
        for line in Data:
            if Opf_Twr[i] in line:
                line_ = line.strip().split()
                Col_Twr.append(int(line_[0]) - 1)
    return Col_Rot, Col_Twr, Col_Sub

