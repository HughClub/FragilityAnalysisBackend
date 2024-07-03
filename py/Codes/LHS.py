# -*- coding: utf-8 -*
# This file is to specify the uncertainties and generate samples using Latin Hypercube Sampling (LHS) method
# Sensitivity analyses are not included in this version
import numpy as np
import pandas as pd
from pyDOE import lhs
import os
from scipy.stats import norm, lognorm, weibull_min
from ReadInpFiles import ReadInpFile


def LHS(Path_Inp, Path_Out):
    Dict_Unc = ReadInpFile(Path_Inp + '/Uncertainty.inp')
    # ---------- LHS Sampling ----------
    Num_S = Dict_Unc['Num_S'][0]
    LHS_Samples = np.zeros([Num_S, len(Dict_Unc)-1])
    for i in range(len(Dict_Unc)-1):
        LHS_Samples[:, i] = Sampling(list(Dict_Unc.values())[i+1], Num_S)
    DF_Samp = pd.DataFrame(LHS_Samples, columns=list(Dict_Unc.keys())[1:])

    # ---------- Write to Excel ----------
    if not os.path.exists(Path_Out):
        os.makedirs(Path_Out)
    with pd.ExcelWriter((Path_Out + '/LHS_Samples.xlsx')) as writer:
        DF_Samp.to_excel(writer, sheet_name='Models', index=False)


def Sampling(Parameter, Num_S):
    lhs_samples = lhs(1, samples=Num_S, criterion='maximin')

    if Parameter[0] == 0:
        Samples = np.ones(Num_S)*Parameter[1]
    elif Parameter[0] == 1:
        Samples = Parameter[1] + lhs_samples*(Parameter[2]-Parameter[1])
    elif Parameter[0] == 2:
        Samples = norm.ppf(lhs_samples, loc=Parameter[1], scale=Parameter[2]*Parameter[1])
    elif Parameter[0] == 3:
        mu = np.log(Parameter[1])
        sigma = np.sqrt(np.log(1 + Parameter[2]**2))
        Samples = lognorm.ppf(lhs_samples, s=sigma, scale=np.exp(mu))
    elif Parameter[0] == 4:
        Samples = weibull_min.ppf(lhs_samples, Parameter[1], Parameter[2])

    Samples = Samples.flatten()
    Samples = np.vectorize(lambda x: '{:.8g}'.format(x))(Samples)
    return(Samples)

