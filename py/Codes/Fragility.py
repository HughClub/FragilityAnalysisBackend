# -*- coding: utf-8 -*
import numpy as np
import os
from scipy.stats import lognorm, binom
from scipy.optimize import minimize
from scipy.optimize import curve_fit
import matplotlib.pyplot as plt

def NoE(Dict_Sap, Dict_Cnd, Dict_Vln, Path_Out_Step_3, Path_Out_Step_4, Num_S):
    Path_opf = Path_Out_Step_3 + '/Output_opf'
    Path_ops = Path_Out_Step_3 + '/Output_ops'
    Path_NoE = Path_Out_Step_4 + '/NoE'
    if not os.path.isdir(Path_Out_Step_4):
        os.mkdir(Path_Out_Step_4)
    if not os.path.isdir(Path_NoE):
        os.mkdir(Path_NoE)
    # Hazard intensity
    WindSpd = np.loadtxt(Path_Out_Step_3 + '/Output_opf/Model_1/WindSpd.txt')

    # Damage state
    if Dict_Vln['Frgl_Bld'][0] == 1:  # Blade
        DS_B = np.array(Dict_Vln['DS_Bld'])
        NoE_B = np.zeros([len(WindSpd), len(DS_B)])

    if Dict_Vln['Frgl_Nac'][0] == 1:  # Nacelle
        DS_N = np.array(Dict_Vln['DS_Nac'])
        NoE_N = np.zeros([len(WindSpd), len(DS_N)])

    if Dict_Vln['Frgl_Twr'][0] == 1:  # Tower
        DS_T_Type = Dict_Vln['DS_Twr_Type'][0]
        DS_T = np.array(Dict_Vln['DS_Twr'])
        NoE_T = np.zeros([len(WindSpd), len(DS_T)])

    if Dict_Vln['Frgl_Sub'][0] == 1:  # Substructure/Monopile
        DS_S_Type = Dict_Vln['DS_Sub_Type'][0]
        DS_S = np.array(Dict_Vln['DS_Sub'])
        NoE_S = np.zeros([len(WindSpd), len(DS_S)])

    NoE_TS = np.zeros(len(WindSpd))
    Num_All = np.array([Num_S*Dict_Cnd['NumF'][0]])

    # Models for OpenSees
    Ops_Mdl = np.array(Dict_Cnd['IM_Model'])

    for ii in range(Num_S):
        BldL = 61.5
        TwrH = Dict_Sap['TwrTH'][0] + Dict_Sap['WatDep'][0]
        PltfH = Dict_Sap['SubTH'][0] + Dict_Sap['WatDep'][0]
        #  Response amplitudes for each model
        if Dict_Vln['Frgl_Res'][0] == 1:
            Type = 'X'
        elif Dict_Vln['Frgl_Res'][0] == 2:
            Type = 'Y'
        elif Dict_Vln['Frgl_Res'][0] == 3:
            Type = 'C'

        BldTipD = (np.loadtxt(Path_opf + '/Model_{}/BldTipD{}.txt'.format(ii+1, Type)))/BldL
        NclA = np.loadtxt(Path_opf + '/Model_{}/NclA{}.txt'.format(ii+1, Type))
        TwrTopD = (np.loadtxt(Path_opf + '/Model_{}/TwrTopD{}.txt'.format(ii+1, Type)))/TwrH
        PltfD = (np.loadtxt(Path_opf + '/Model_{}/PltfD{}.txt'.format(ii+1, Type)))/PltfH
        TwrBsS = np.loadtxt(Path_opf + '/Model_{}/TwrBsS{}.txt'.format(ii+1, Type))
        SubBsS = np.loadtxt(Path_opf + '/Model_{}/SubBsS{}.txt'.format(ii+1, Type))
        TowerStrike = np.loadtxt(Path_opf + '/Model_{}/TowerStrike.txt'.format(ii+1))

        # Substitute OpenSees results
        if Dict_Vln['Frgl_Op'] == 2:
            if ii+1 in Ops_Mdl:
                IM_Nth = Dict_Cnd['IM_Nth'][0]
                TwrTopD_ops = (np.loadtxt(Path_ops + '/Model_{}/TwrTopD{}.txt'.format(ii+1, Type)))/TwrH
                PltfD_ops = (np.loadtxt(Path_ops + '/Model_{}/PltfD{}.txt'.format(ii + 1, Type))) / PltfH
                TwrBsS_ops = np.loadtxt(Path_ops + '/Model_{}/TwrBsS{}.txt'.format(ii + 1, Type))
                SubBsS_ops = np.loadtxt(Path_ops + '/Model_{}/SubBsS{}.txt'.format(ii + 1, Type))

                TwrTopD[IM_Nth-1:, :] = TwrTopD_ops
                PltfD[IM_Nth-1:, :] = PltfD_ops
                TwrBsS[IM_Nth - 1:, :] = TwrBsS_ops
                SubBsS[IM_Nth - 1:, :] = SubBsS_ops

        # Calculate Number of exceedance for each wind speed and each damage state
        for jj in range(len(WindSpd)):
            if Dict_Vln['Frgl_Bld'][0] == 1:
                for kk in range(len(DS_B)):
                    NoE_B[jj, kk] += np.sum(BldTipD[jj, :] >= DS_B[kk])

            if Dict_Vln['Frgl_Nac'][0] == 1:
                for kk in range(len(DS_N)):
                    NoE_N[jj, kk] += np.sum(NclA[jj, :] >= DS_N[kk])

            if Dict_Vln['Frgl_Twr'][0] == 1:
                for kk in range(len(DS_T)):
                    if DS_T_Type == 1:
                        NoE_T[jj, kk] += np.sum(TwrTopD[jj, :] >= DS_T[kk])
                    elif DS_T_Type == 2:
                        NoE_T[jj, kk] += np.sum(TwrBsS[jj, :] >= DS_T[kk])

            if Dict_Vln['Frgl_Sub'][0] == 1:
                for kk in range(len(DS_S)):
                    if DS_S_Type == 1:
                        NoE_S[jj, kk] += np.sum(PltfD[jj, :] >= DS_S[kk])
                    elif DS_S_Type == 2:
                        NoE_S[jj, kk] += np.sum(SubBsS[jj, :] >= DS_S[kk])

            NoE_TS[jj] += np.sum(TowerStrike[jj, :] >= 0.5)


    np.savetxt(Path_NoE + '/WindSpd.txt', WindSpd, fmt="%.2f", delimiter=' ')
    np.savetxt(Path_NoE + '/NoE_TS.txt', NoE_TS, fmt="%d", delimiter=' ')
    np.savetxt(Path_NoE + '/Num_All.txt', Num_All, fmt="%d", delimiter=' ')
    if Dict_Vln['Frgl_Bld'][0] == 1:
        np.savetxt(Path_NoE + '/NoE_B.txt', NoE_B, fmt="%d", delimiter=' ')
    if Dict_Vln['Frgl_Nac'][0] == 1:
        np.savetxt(Path_NoE + '/NoE_N.txt', NoE_N, fmt="%d", delimiter=' ')
    if Dict_Vln['Frgl_Twr'][0] == 1:
        np.savetxt(Path_NoE + '/NoE_T.txt', NoE_T, fmt="%d", delimiter=' ')
    if Dict_Vln['Frgl_Sub'][0] == 1:
        np.savetxt(Path_NoE + '/NoE_S.txt', NoE_S, fmt="%d", delimiter=' ')



def Frgl_plt(Dict_Vln, Path_Out_Step_4):
    Path_NoE = Path_Out_Step_4 + '/NoE'
    Path_PoE = Path_Out_Step_4 + '/PoE'
    Frgl_Cmp(Path_Out_Step_4, Dict_Vln)
    Num_All = np.loadtxt(Path_NoE + '/Num_All.txt')
    WindSpd = np.loadtxt(Path_NoE + '/WindSpd.txt')
    IM = np.loadtxt(Path_PoE + '/IM.txt')
    Marker = ['v', '^', '<', '>', 'o', 's']

    if Dict_Vln['Frgl_Bld'][0] == 1:
        PoE_B = (np.loadtxt(Path_NoE + '/NoE_B.txt'))/Num_All
        Frgl_B = np.loadtxt(Path_PoE + '/Frgl_B.txt')
        Num_B = np.size(PoE_B, axis=1)

        plt.figure()
        [plt.scatter(WindSpd, PoE_B[:, i], marker=Marker[i], linewidth=1.5, label='DS-{}'.format(i+1)) for i in range(Num_B)]
        [plt.plot(IM, Frgl_B[:, i], linewidth=2, label='DS-{}'.format(i+1)) for i in range(Num_B)]
        plt.ylim((-0.05, 1.05))
        plt.xticks(fontsize=15)
        plt.yticks(np.arange(0, 1.1, 0.1), fontsize=15)
        plt.xlabel('Wind speed (m/s)', fontsize=15)
        plt.ylabel('Fragility', fontsize=15)
        plt.title('Blade', fontsize=15)
        plt.show()

    if Dict_Vln['Frgl_Nac'][0] == 1:
        PoE_N = (np.loadtxt(Path_NoE + '/NoE_N.txt')) / Num_All
        Frgl_N = np.loadtxt(Path_PoE + '/Frgl_N.txt')
        Num_N = np.size(PoE_N, axis=1)

        plt.figure()
        [plt.scatter(WindSpd, PoE_N[:, i], marker=Marker[i], linewidth=1.5, label='DS-{}'.format(i + 1)) for i in range(Num_N)]
        [plt.plot(IM, Frgl_N[:, i], linewidth=2, label='DS-{}'.format(i + 1)) for i in range(Num_N)]
        plt.ylim((-0.05, 1.05))
        plt.xticks(fontsize=15)
        plt.yticks(np.arange(0, 1.1, 0.1), fontsize=15)
        plt.xlabel('Wind speed (m/s)', fontsize=15)
        plt.ylabel('Fragility', fontsize=15)
        plt.title('Nacelle', fontsize=15)
        plt.show()

    if Dict_Vln['Frgl_Twr'][0] == 1:
        PoE_T = (np.loadtxt(Path_NoE + '/NoE_T.txt')) / Num_All
        Frgl_T = np.loadtxt(Path_PoE + '/Frgl_T.txt')
        Num_T = np.size(PoE_T, axis=1)

        plt.figure()
        [plt.scatter(WindSpd, PoE_T[:, i], marker=Marker[i], linewidth=1.5, label='DS-{}'.format(i + 1)) for i in range(Num_T)]
        [plt.plot(IM, Frgl_T[:, i], linewidth=2, label='DS-{}'.format(i + 1)) for i in range(Num_T)]
        plt.ylim((-0.05, 1.05))
        plt.xticks(fontsize=15)
        plt.yticks(np.arange(0, 1.1, 0.1), fontsize=15)
        plt.xlabel('Wind speed (m/s)', fontsize=15)
        plt.ylabel('Fragility', fontsize=15)
        plt.title('Tower', fontsize=15)
        plt.show()

    if Dict_Vln['Frgl_Sub'][0] == 1:
        PoE_S = (np.loadtxt(Path_NoE + '/NoE_S.txt')) / Num_All
        Frgl_S = np.loadtxt(Path_PoE + '/Frgl_S.txt')
        Num_S = np.size(PoE_S, axis=1)

        plt.figure()
        [plt.scatter(WindSpd, PoE_S[:, i], marker=Marker[i], linewidth=1.5, label='DS-{}'.format(i + 1)) for i in range(Num_S)]
        [plt.plot(IM, Frgl_S[:, i], linewidth=2, label='DS-{}'.format(i + 1)) for i in range(Num_S)]
        plt.ylim((-0.05, 1.05))
        plt.xticks(fontsize=15)
        plt.yticks(np.arange(0, 1.1, 0.1), fontsize=15)
        plt.xlabel('Wind speed (m/s)', fontsize=15)
        plt.ylabel('Fragility', fontsize=15)
        plt.title('Substructure', fontsize=15)
        plt.show()



def Frgl_Cmp(Path_Out_Step_4, Dict_Vln):
    Path_NoE = Path_Out_Step_4 + '/NoE'
    Path_PoE = Path_Out_Step_4 + '/PoE'
    if not os.path.isdir(Path_PoE):
        os.mkdir(Path_PoE)

    WindSpd = np.loadtxt(Path_NoE + '/WindSpd.txt')
    Num_All = np.loadtxt(Path_NoE + '/Num_All.txt')
    IM = np.arange(Dict_Vln['HIF'][0], Dict_Vln['HIF'][1] + Dict_Vln['dIM'][0], Dict_Vln['dIM'][0])
    np.savetxt(Path_PoE + '/IM.txt', IM, fmt="%.3f", delimiter=' ')

    NoE_TS = np.loadtxt(Path_NoE + '/NoE_TS.txt')
    NoE_TS = NoE_TS.reshape(len(NoE_TS), 1)
    Frgl_TS = fragility(WindSpd, Num_All, IM, NoE_TS, ['All'])
    np.savetxt(Path_PoE + '/Frgl_TS.txt', Frgl_TS, fmt="%.5f", delimiter=' ')

    if Dict_Vln['Frgl_Bld'][0] == 1:
        NoE_B = np.loadtxt(Path_NoE + '/NoE_B.txt')
        IMI_B = Dict_Vln['IMI_Bld']
        Frgl_B = fragility(WindSpd, Num_All, IM, NoE_B, IMI_B)
        np.savetxt(Path_PoE + '/Frgl_B.txt', Frgl_B, fmt="%.5f", delimiter=' ')

    if Dict_Vln['Frgl_Nac'][0] == 1:
        NoE_N = np.loadtxt(Path_NoE + '/NoE_N.txt')
        IMI_N = Dict_Vln['IMI_Nac']
        Frgl_N = fragility(WindSpd, Num_All, IM, NoE_N, IMI_N)
        np.savetxt(Path_PoE + '/Frgl_N.txt', Frgl_N, fmt="%.5f", delimiter=' ')

    if Dict_Vln['Frgl_Twr'][0] == 1:
        NoE_T = np.loadtxt(Path_NoE + '/NoE_T.txt')
        IMI_T = Dict_Vln['IMI_Twr']
        Frgl_T = fragility(WindSpd, Num_All, IM, NoE_T, IMI_T)
        np.savetxt(Path_PoE + '/Frgl_T.txt', Frgl_T, fmt="%.5f", delimiter=' ')

    if Dict_Vln['Frgl_Sub'][0] == 1:
        NoE_S = np.loadtxt(Path_NoE + '/NoE_S.txt')
        IMI_S = Dict_Vln['IMI_Sub']
        Frgl_S = fragility(WindSpd, Num_All, IM, NoE_S, IMI_S)
        np.savetxt(Path_PoE + '/Frgl_S.txt', Frgl_S, fmt="%.5f", delimiter=' ')


def fragility(WindSpd, Num_All, IM, NoE, IMI):
    Num_DS = np.size(NoE, axis=1)
    LogP = np.zeros([Num_DS, 2])
    Frgl = np.zeros([len(IM), Num_DS])
    for i in range(Num_DS):
        if IMI[i].upper() == 'All'.upper():
            # Temp_P = mleln(WindSpd, NoE[:, i], Num_All)
            # LogP[i, :] = Temp_P.x
            LogP[i, :] = fit_lognormal_cdf(WindSpd, NoE[:, i]/Num_All)
        else:
            Lower = IMI[i][0] - 1
            Upper = IMI[i][1] - 1
            LogP[i, :] = fit_lognormal_cdf(WindSpd[Lower:Upper], NoE[Lower:Upper, i] / Num_All)
            # Temp_P = mleln(WindSpd[Lower:Upper], NoE[Lower:Upper, i], Num_All)
            # LogP[i, :] = Temp_P.x
        Frgl[:, i] = lognorm.cdf(IM, s=LogP[i, 1], scale=np.exp(LogP[i, 0]))
    return Frgl


def lognormal_cdf(x, mu, sigma):
    return lognorm.cdf(x, sigma, scale=np.exp(mu))

# 拟合对数正态分布
def fit_lognormal_cdf(X, Y):
    params, _ = curve_fit(lognormal_cdf, X, Y, p0=[3, 0.06], maxfev=10000)
    LogP = np.array(params)
    return LogP


def mleln(WindSpd, NoE, Num_All):
    x0 = np.array([0.2, 2.5])
    LogP = minimize(mlefit, x0, args=(WindSpd.astype(int), NoE.astype(int), Num_All.astype(int)), bounds=([0, 1], [0, 5]))
    return LogP


def mlefit(x, WindSpd, NoE, Num_All):
    p = lognorm.cdf(WindSpd, s=x[0], scale=np.exp(x[1]))
    Likelihood = 0
    # Likelihood = -1
    for j in range(len(WindSpd)):
        Likelihood += -np.log(binom.pmf(NoE[j], Num_All, p[j]))
        # Likelihood = Likelihood * binom.pmf(NoE[j], Num_All, p[j])
    return Likelihood


