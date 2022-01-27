from copy import deepcopy
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import os 
from copy import deepcopy 
from pathlib import Path



class Harry_Plotter(): 
    def __init__(self , file : str):
        self.col_names = ["Strategies","Winrate","Average" ,"Placement","Average Turns per Game", "Turns to Finish","Average Blocks Created","Average Kicks",
                          "Average got Kicked","Game most Kicks","Game most Blocks","Game most got Kicked"]
        self.path_plot_dir = Path(os.path.dirname(os.path.abspath(__file__)))
        self.strategies = ["First","Last","Prefer_Beat", "Prefer_Block"]
        self.observer_sim_dic = dict()
        self.data = self.load_csv(file)
        for strat in self.strategies:
            filtered = self.filtered_by_observer(strat)
            self.observer_sim_dic.update({strat : filtered})
        for key in self.observer_sim_dic.keys():
            print(key, self.observer_sim_dic.get(key))
    def load_csv(self,file : str)-> pd.DataFrame :
        try : 
            return pd.read_csv(self.path_plot_dir.joinpath(file), sep = ";")
        except Exception as e : print(e)

    def filtered_by_observer(self, observer : str):
        copy = deepcopy(self.data)
        mask = copy.apply(lambda x : self.is_observer(x['Strategies'],observer),axis = 1 )

        df_observed = copy[mask]
        return df_observed

    def is_observer(self,strat_comp : str, observer : str)-> bool:
        row_obs =  strat_comp.split(sep = ",")[0]
        print(f"row_obs = {row_obs}, {len(row_obs)}  observer  = {observer}, {len(observer)}")
        return row_obs == observer

    

if __name__ == "__main__" :    
    file = "results_2000_runs.csv"
    plotter = Harry_Plotter(file)
    # TODO #