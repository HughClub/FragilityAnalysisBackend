# -*- coding: utf-8 -*
import numpy as np
import pandas as pd
import os
import math

def Ops_input(Dict_Sap, Path_Temp, num):
    Key = ['TwrTH', 'TwrTD', 'TwrTT', 'TwrBD', 'TwrBT', 'SubTH', 'WatDep', 'SubUD', 'SubUT', 'MatMod', 'MatDen',
           'MatPoR', 'HubMass', 'NacMass', 'HubH']
    Num_SubS = 11  # Number of sections for substructure
    Num_TwrS = 28  # Number of sections for tower
    Num_Sec = Num_SubS + Num_TwrS


    Value = np.zeros(0)
    for key in Key:
        Value = np.append(Value, Dict_Sap[key][num - 1])

    Node_Str = ['Node', 'X', 'Y', 'Z', 'Mass']
    Node_Prop = np.zeros([Num_Sec+1, len(Node_Str)])
    Node_Prop[:, 0] = np.arange(1, Num_Sec+2)

    Ratio_Sub = round((Value[5] + Value[6]) / (Value[0] + Value[6]), 5)
    Frac_Sub = np.linspace(0, Ratio_Sub, num=Num_SubS)
    Frac_Twr = np.linspace(Ratio_Sub + 0.00001, 1, num=Num_TwrS)
    Frac = np.append(Frac_Sub, Frac_Twr)
    Node_Prop[:-1, 3] = Frac*(Value[0]+Value[6]) - Value[6]
    Node_Prop[-1, 3] = Value[-1]

    Ele_Str = ['Element', 'Node1', 'Node2', 'Section']
    Ele_Prop = np.zeros([Num_Sec-1, 4])
    Ele_Prop[:, 0] = np.arange(1, Num_Sec)
    Ele_Prop[:, 1] = np.arange(1, Num_Sec)
    Ele_Prop[:, 2] = np.arange(2, Num_Sec+1)
    Ele_Prop[:, 3] = np.arange(1, Num_Sec)

    Sec_Str = ['Section', 'R_in', 'R_out', 'GJ']
    Sec_Prop = np.zeros([Num_Sec, 4])
    Sec_Prop[:, 0] = np.arange(1, Num_Sec+1)
    Sec_D = np.append(np.array([Value[7]] * Num_SubS), np.linspace(Value[3], Value[1], num=Num_TwrS))
    Sec_T = np.append(np.array([Value[8]] * Num_SubS), np.linspace(Value[4], Value[2], num=Num_TwrS))
    Sec_d = Sec_D-2*Sec_T
    Sec_Prop[:, 2] = Sec_D/2
    Sec_Prop[:, 1] = (Sec_D-2*Sec_T)/2
    G = Value[9] / 2 / (1 + Value[11])
    Sec_Prop[:, 3] = (1/32*math.pi*(pow(Sec_D, 4)-pow(Sec_d, 4))*G)

    AD_Str = ['Node', 'H', 'Ht', 'D']
    AD_Prop = np.zeros([9, 4])
    AD_Prop[:, 0] = np.arange(1, 10)
    for j in range(9):
        AD_Prop[j, 0] = Node_Prop[-2-3*j, 0]
        AD_Prop[j, 1] = Node_Prop[-2-3*j, 3]
        AD_Prop[j, 3] = Sec_D[-1-3*j]
    AD_Prop[:, 2] = np.ones(9)*(AD_Prop[0, 1]-AD_Prop[1, 1])

    Node_Prop[:, 4] = Node_Mass(Node_Prop, Ele_Prop, Sec_Prop, Value, Num_Sec)

    # Write file .xlsx
    DF_Node = pd.DataFrame(Node_Prop, columns=Node_Str)
    DF_Ele = pd.DataFrame(Ele_Prop, columns=Ele_Str)
    DF_Sec = pd.DataFrame(Sec_Prop, columns=Sec_Str)
    DF_AD = pd.DataFrame(AD_Prop, columns=AD_Str)

    DF_Node['Node'] = DF_Node['Node'].astype(int)
    DF_Ele = DF_Ele.astype(int)
    DF_Sec['Section'] = DF_Sec['Section'].astype(int)
    DF_AD['Node'] = DF_AD['Node'].astype(int)

    with pd.ExcelWriter((Path_Temp + '/OpsInp.xlsx')) as writer:
        DF_Node.to_excel(writer, sheet_name='Node', index=False)
        DF_Ele.to_excel(writer, sheet_name='Element', index=False)
        DF_Sec.to_excel(writer, sheet_name='Section', index=False)
        DF_AD.to_excel(writer, sheet_name='AeroDyn', index=False)


def Node_Mass(Node_Prop, Ele_Prop, Sec_Prop, Value, Num_Sec):
    Rho = Value[10]
    Sec_Mass = math.pi*(pow(Sec_Prop[:, 2], 2)-pow(Sec_Prop[:, 1], 2))*Rho
    Nodes_Mass = np.zeros(Num_Sec+1)

    for i in range(Num_Sec-1):
        Node_1 = int(Ele_Prop[i][1])-1
        Node_2 = int(Ele_Prop[i][2])-1
        Node_1_x = Node_Prop[Node_1][1]
        Node_2_x = Node_Prop[Node_2][1]
        Node_1_y = Node_Prop[Node_1][2]
        Node_2_y = Node_Prop[Node_2][2]
        Node_1_z = Node_Prop[Node_1][3]
        Node_2_z = Node_Prop[Node_2][3]
        Ele_Len = math.sqrt((Node_1_x-Node_2_x)**2+(Node_1_y-Node_2_y)**2+(Node_1_z-Node_2_z)**2)
        Ele_Mass = Sec_Mass[int(Ele_Prop[i][3])-1]*Ele_Len
        Nodes_Mass[Node_1] += Ele_Mass/2
        Nodes_Mass[Node_2] += Ele_Mass/2
    Nodes_Mass[-1] = 53220 + Value[12] + Value[13]
    return(Nodes_Mass)
