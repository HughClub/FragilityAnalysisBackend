# -*- coding: utf-8 -*
import pandas as pd
def ReadInpFile(InpFile):
    Title_line = []
    with open(InpFile, 'r') as File:
        content = File.readlines()
        for line_num, line in enumerate(content):
            if '--------' in line:
                Title_line.append(line_num)
            if 'END' in line:
                END = line_num
    File.close()
    content = content[0:END]
    for index in sorted(Title_line[:-1], reverse=True):
        del content[index]

    Value = []
    Key = []
    for sublist in content:
        item = sublist.split('--')
        Value.append(item[0].strip())
        Key.append(item[1].strip())
    Value = [eval(item) for item in Value]
    Pairs = zip(Key, Value)
    Dict_Var = {key: value for key, value in Pairs}
    return Dict_Var


def ReadSampInp(Path_LHS):
    Samples = pd.read_excel(Path_LHS)
    Dict_Sap = Samples.to_dict(orient='list')
    Num_S = len(list(Dict_Sap.values())[0])
    return Dict_Sap, Num_S
