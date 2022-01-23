import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import os 


col_names = ["Strategies","Winrate","Average" ,"Placement","Average Turns per Game", "Turns to Finish",
            "Average Blocks Created","Average Kicks",
            "Average got Kicked","Game most Kicks","Game most Blocks",
            "Game most got Kicked"]
# current dir
csv_path = f"{os.getcwd()}/results_100_runs.csv"
# load the data 
base_frame = pd.read_csv(csv_path, sep = ";")
#base_frame.sort_values(by = "Winrate",ascending = False,inplace = True)

block_view = base_frame[["Strategies", "Average Blocks Created", "Game most Blocks"]]
print(block_view)