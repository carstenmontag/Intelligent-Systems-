from copy import deepcopy
from turtle import color, pos
import seaborn as sns
import pandas as pd
import matplotlib.pyplot as plt
import os 
from copy import deepcopy 
from pathlib import Path

from sympy import E



class Harry_Plotter(): 
    def __init__(self , file : str):
        self.col_names = ["Strategies","Winrate","Average Placement","Average Turns per Game", "Turns to Finish","Average Blocks Created","Average Kicks",
                          "Average got Kicked","Game most Kicks","Game most Blocks","Game most got Kicked"]
        self.path_plot_dir = Path(os.path.dirname(os.path.abspath(__file__)))
        self.strategies = ["First","Last","Prefer_Beat", "Prefer_Block"]
        self.observer_sim_dic = dict()
        self.data = self.load_csv(file)
        for strat in self.strategies:
            filtered = self.filtered_by_observer(strat)
            self.observer_sim_dic.update({strat: filtered})
        self.create_plots()
        

    def load_csv(self,file : str)-> pd.DataFrame :
        try : 
            return pd.read_csv(self.path_plot_dir.joinpath(file), sep = ";")
        except Exception as e : print(e)

    def filtered_by_observer(self, observer : str)-> pd.DataFrame:
        copy = deepcopy(self.data)
        mask = copy.apply(lambda x : self.__is_observer__(x['Strategies'],observer),axis = 1 )
        df_observed = copy[mask]      
        return df_observed

    def create_plots(self,):
        for obs in self.observer_sim_dic.keys(): 
            df_by_obs = self.observer_sim_dic.get(obs)
            self.plot_winrate(df_by_obs,obs)
    def plot_winrate(self,data : pd.DataFrame,observer : str):
        subdata = data[["Strategies", "Winrate"]]
        enemy_list = deepcopy(self.strategies)
        enemy_list.remove(observer)
        positions = [0,1.25,2.5]
        vs3 = []
        vs2 = []
        for enemy in enemy_list: 
            vs3.append([strat for strat in subdata["Strategies"].tolist() if strat.count(enemy)==3][0])   
            vs2.append([strat for strat in subdata["Strategies"].tolist() if strat.count(enemy)==2][0])
        v3_winrates = [subdata.loc[subdata['Strategies'] == strat].reset_index().at[0,"Winrate"] for strat in vs3]
        v2_winrates = [subdata.loc[subdata['Strategies'] == strat].reset_index().at[0,"Winrate"] for strat in vs2]
        fig = plt.figure(figsize = (5,6))
        plt.bar(positions,v3_winrates,width = 0.4, color = 'b')
        plt.bar([position+0.5 for position in positions],v2_winrates,width=0.4,color = 'r')
        ticks_positions = [position+0.25 for position in positions]
        plt.xticks(ticks_positions,enemy_list)
        plt.ylim((0,0.75))
        plt.title(f"Winrates by {observer}")
        colors = ['blue','red']
        handles = [plt.Rectangle((0,0),1,1, color=color) for color in colors]
        plt.legend(handles,["vs3", "vs2"],loc = 'upper right')
            #v2_winrate =
        plt.show()

    def count_strats(self,strat_comp,to_count):
        
        print(to_count)
        print(strat_comp.count(to_count))



        #plt.bar(positions, vs3)
        #print(subdata)


    def __is_observer__(self,strat_comp : str, observer : str)-> bool:
        row_obs =  strat_comp.split(sep = ",")[0]
        return row_obs == observer
    def __get_enemys__(self, strat_comp : str)-> list[str]:
        return strat_comp.split(sep = ",")[1:]
    



    

if __name__ == "__main__" :    
    file = "results_2000_runs.csv"
    plotter = Harry_Plotter(file)
    # TODO sinnvolle Plots #
    # plot winrate vs 3/2x Strat a  --> 4x3x2 Balken DONE
    # kicks& blocks / round statt avg turns per game 
    col_names = ["Strategies","Winrate","Average" ,"Placement","Average Turns per Game", "Turns to Finish","Average Blocks Created","Average Kicks",
                          "Average got Kicked","Game most Kicks","Game most Blocks","Game most got Kicked"]