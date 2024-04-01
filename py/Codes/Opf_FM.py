# -*- coding: utf-8 -*

def FM_fst(OpfVar_fst, Path_FAST, Path_File):
    File = []
    InputFile = open(Path_FAST + '/Main.fst', encoding='UTF-8')
    for line in InputFile:
        File.append(line.rstrip())
    InputFile.close()
    File[5] = str(OpfVar_fst['TMax']) + '     TMax'
    File[6] = str(OpfVar_fst['DT']) + '     DT'
    File[12] = str(OpfVar_fst['CompElast']) + '     CompElast'
    File[29] = str(OpfVar_fst['WtrDpth']) + '     WtrDpth'
    File[32] = str(OpfVar_fst['EDFile']) + '     EDFile'
    File[33] = str(OpfVar_fst['BDBldFile']) + '     BDBldFile(1)'
    File[34] = str(OpfVar_fst['BDBldFile']) + '     BDBldFile(2)'
    File[35] = str(OpfVar_fst['BDBldFile']) + '     BDBldFile(3)'
    File[36] = str(OpfVar_fst['InflowFile']) + '     InflowFile'
    File[37] = str(OpfVar_fst['AeroFile']) + '     AeroFile'
    File[38] = str(OpfVar_fst['ServoFile']) + '     ServoFile'
    File[39] = str(OpfVar_fst['HydroFile']) + '     HydroFile'
    File[40] = str(OpfVar_fst['SubFile']) + '     SubFile'
    with open(Path_File, 'w') as file:
        file.write('\n'.join(File))
    file.close()

def FM_ED(OpfVar_ED, Path_FAST, Path_File):
    File = []
    InputFile = open(Path_FAST + '/ElastoDyn.dat', encoding='UTF-8')
    for line in InputFile:
        File.append(line.rstrip())
    InputFile.close()
    File[27] = str(OpfVar_ED['BlPitch']) + '     BlPitch(1)'
    File[28] = str(OpfVar_ED['BlPitch']) + '     BlPitch(2)'
    File[29] = str(OpfVar_ED['BlPitch']) + '     BlPitch(3)'
    File[32] = str(OpfVar_ED['RotSpeed']) + '     RotSpeed'
    File[62] = str(OpfVar_ED['Twr2Shft']) + '     Twr2Shft'
    File[63] = str(OpfVar_ED['TowerHt']) + '     TowerHt'
    File[64] = str(OpfVar_ED['TowerBsHt']) + '     TowerBsHt'
    File[67] = str(OpfVar_ED['PtfmCMzt']) + '     PtfmCMzt'
    File[68] = str(OpfVar_ED['PtfmRefzt']) + '     PtfmRefzt'
    File[73] = str(OpfVar_ED['HubMass']) + '     HubMass'
    File[76] = str(OpfVar_ED['NacMass']) + '     NacMass'
    File[85] = str(OpfVar_ED['BldFile']) + '     BldFile(1)'
    File[86] = str(OpfVar_ED['BldFile']) + '     BldFile(2)'
    File[87] = str(OpfVar_ED['BldFile']) + '     BldFile(3)'
    File[107] = str(OpfVar_ED['TwrFile']) + '     TwrFile'
    with open(Path_File, 'w') as file:
        file.write('\n'.join(File))
    file.close()

def FM_EDT(OpfVar_EDT, Path_FAST, Path_File):
    File = []
    InputFile = open(Path_FAST + '/ElastoDyn_Tower.dat', encoding='UTF-8')
    for line in InputFile:
        File.append(line.rstrip())
    InputFile.close()

    for i in range(28):
        File[19+i] = ' '.join(map(str, OpfVar_EDT['TwrProp'][i]))
    for i in range(5):
        File[48+i] = str(OpfVar_EDT['TwrShape'][i, 0]) + '     TwFAM1Sh({})'.format(i+2)
    for i in range(5):
        File[53+i] = str(OpfVar_EDT['TwrShape'][i, 1]) + '     TwFAM2Sh({})'.format(i+2)
    for i in range(5):
        File[59+i] = str(OpfVar_EDT['TwrShape'][i, 2]) + '     TwSSM1Sh({})'.format(i+2)
    for i in range(5):
        File[64+i] = str(OpfVar_EDT['TwrShape'][i, 3]) + '     TwSSM2Sh({})'.format(i+2)
    with open(Path_File, 'w') as file:
        file.write('\n'.join(File))
    file.close()

def FM_EDB(OpfVar_EDB, Path_FAST, Path_File):
    File = []
    InputFile = open(Path_FAST + '/ElastoDyn_Blade.dat', encoding='UTF-8')
    for line in InputFile:
        File.append(line.rstrip())
    InputFile.close()
    File[4] = str(OpfVar_EDB['BldFlDmp']) + '     BldFlDmp(1)'
    File[5] = str(OpfVar_EDB['BldFlDmp']) + '     BldFlDmp(2)'
    File[6] = str(OpfVar_EDB['BldFlDmp']) + '     BldEdDmp(1)'
    with open(Path_File, 'w') as file:
        file.write('\n'.join(File))
    file.close()

def FM_SubD(OpfVar_SubD, Path_FAST, Path_File):
    File = []
    InputFile = open(Path_FAST + '/SubDyn.dat', encoding='UTF-8')
    for line in InputFile:
        File.append(line.rstrip())
    InputFile.close()

    for i in range(11):
        File[27+i] = '      '.join(['{:d}'.format(int(x)) if x.is_integer() else '{:.2f}'.format(x) for x in OpfVar_SubD['Joints'][i]])
    for i in range(10):
        File[52+i] = '      '.join(['{:d}'.format(int(x)) if x.is_integer() else '{:.2f}'.format(x) for x in OpfVar_SubD['Members'][i]])
    File[66] = OpfVar_SubD['PropSet']
    with open(Path_File, 'w') as file:
        file.write('\n'.join(File))
    file.close()

def FM_SerD(OpfVar_SerD, Path_FAST, Path_File):
    File = []
    InputFile = open(Path_FAST + '/ServoDyn.dat', encoding='UTF-8')
    for line in InputFile:
        File.append(line.rstrip())
    InputFile.close()
    File[7] = str(OpfVar_SerD['TPCOn']) + '     TPCOn'
    File[8] = str(OpfVar_SerD['TPitManS']) + '     TPitManS(1)'
    File[9] = str(OpfVar_SerD['TPitManS']) + '     TPitManS(2)'
    File[10] = str(OpfVar_SerD['TPitManS']) + '     TPitManS(3)'
    File[11] = str(OpfVar_SerD['PitManRat']) + '     PitManRat(1)'
    File[12] = str(OpfVar_SerD['PitManRat']) + '     PitManRat(2)'
    File[13] = str(OpfVar_SerD['PitManRat']) + '     PitManRat(3)'
    File[14] = str(OpfVar_SerD['BlPitchF']) + '     BlPitchF(1)'
    File[15] = str(OpfVar_SerD['BlPitchF']) + '     BlPitchF(2)'
    File[16] = str(OpfVar_SerD['BlPitchF']) + '     BlPitchF(3)'
    File[24] = str(OpfVar_SerD['TimGenOn']) + '     TimGenOn'
    File[25] = str(OpfVar_SerD['TimGenOf']) + '     TimGenOf'
    File[76] = str(OpfVar_SerD['DLL_FileName']) + '     DLL_FileName'
    File[77] = str(OpfVar_SerD['DLL_InFile']) + '     DLL_InFile'
    with open(Path_File, 'w') as file:
        file.write('\n'.join(File))
    file.close()

def FM_TS(OpfVar_TS, Path_FAST, Path_File):
    File = []
    InputFile = open(Path_FAST + '/TurbSim.inp', encoding='UTF-8')
    for line in InputFile:
        File.append(line.rstrip())
    InputFile.close()
    File[4] = str(OpfVar_TS['RandSeed1']) + '     RandSeed1'
    File[5] = str(OpfVar_TS['RandSeed2']) + '     RandSeed2'
    File[21] = str(OpfVar_TS['AnalysisTime']) + '     AnalysisTime'
    File[23] = str(OpfVar_TS['HubHt']) + '     HubHt'
    File[24] = str(OpfVar_TS['GridHeight']) + '     GridHeight'
    File[25] = str(OpfVar_TS['GridWidth']) + '     GridWidth'
    File[27] = str(OpfVar_TS['HFlowAng']) + '     HFlowAng'
    File[30] = str(OpfVar_TS['TurbModel']) + '     TurbModel'
    File[33] = str(OpfVar_TS['IECturbc']) + '     IECturbc'
    File[34] = str(OpfVar_TS['IEC_WindType']) + '     IEC_WindType'
    File[36] = str(OpfVar_TS['WindProfileType']) + '     WindProfileType'
    File[38] = str(OpfVar_TS['RefHt']) + '     RefHt'
    File[39] = str(OpfVar_TS['URef']) + '     URef'
    File[41] = str(OpfVar_TS['PLExp']) + '     PLExp'
    File[42] = str(OpfVar_TS['Z0']) + '     Z0'
    with open(Path_File, 'w') as file:
        file.write('\n'.join(File))
    file.close()

def FM_IW(OpfVar_IW, Path_FAST, Path_File):
    File = []
    InputFile = open(Path_FAST + '/InflowWind.dat', encoding='UTF-8')
    for line in InputFile:
        File.append(line.rstrip())
    InputFile.close()
    File[10] = str(OpfVar_IW['WindVziList']) + '     WindVziList'
    File[20] = str(OpfVar_IW['FileName_BTS']) + '     FileName_BTS'
    with open(Path_File, 'w') as file:
        file.write('\n'.join(File))
    file.close()

def FM_AD(OpfVar_AD, Path_FAST, Path_File):
    File = []
    InputFile = open(Path_FAST + '/AeroDyn15.dat', encoding='UTF-8')
    for line in InputFile:
        File.append(line.rstrip())
    InputFile.close()
    File[5] = str(OpfVar_AD['WakeMod']) + '     WakeMod'
    for i in range(8):
        File[46+i] = OpfVar_AD['AFNames'][i]
    for i in range(3):
        File[56+i] = OpfVar_AD['ADBlFile'] + '     ADBlFile({})'.format(i+1)
    for i in range(9):
        File[63+i] = '  '.join(map(str, OpfVar_AD['TwrAD'][i]))
    with open(Path_File, 'w') as file:
        file.write('\n'.join(File))
    file.close()

def FM_HD(OpfVar_HD, Path_FAST, Path_File):
    File = []
    InputFile = open(Path_FAST + '/HydroDyn.dat', encoding='UTF-8')
    for line in InputFile:
        File.append(line.rstrip())
    InputFile.close()
    File[8] = str(OpfVar_HD['WaveMod']) + '     WaveMod'
    File[10] = str(OpfVar_HD['WaveTMax']) + '     WaveTMax'
    File[12] = str(OpfVar_HD['WaveHs']) + '     WaveHs'
    File[13] = str(OpfVar_HD['WaveTp']) + '     WaveTp'
    File[17] = str(OpfVar_HD['WaveDir']) + '     WaveDir'
    File[22] = str(OpfVar_HD['WaveSeed(1)']) + '     WaveSeed(1)'
    File[23] = str(OpfVar_HD['WaveSeed(2)']) + '     WaveSeed(2)'
    for i in range(2):
        File[101+i] = '    '.join(['{:d}'.format(int(x)) if x.is_integer() else '{:.2f}'.format(x) for x in OpfVar_HD['Jointzi'][i]])
    File[107] = '    '.join(['{:d}'.format(int(x)) if x.is_integer() else '{:.2f}'.format(x) for x in OpfVar_HD['PropMem']])
    with open(Path_File, 'w') as file:
        file.write('\n'.join(File))
    file.close()
