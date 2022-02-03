from copy import deepcopy
from turtle import color, pos
import seaborn as sns
import pandas as pd
import matplotlib.pyplot as plt
import os 
from copy import deepcopy 
from pathlib import Path
import numpy as np




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
        self.plot_winrate()
        self.plot_blocks_by_avg_turns()
        self.plot_beats_by_avg_turns()

    def plot_winrate(self):
        fig,ax = plt.subplots(nrows= 2 ,ncols = 2,figsize = (6,7.5))
        for i,observer in enumerate(self.observer_sim_dic.keys()):
            x_axis = 0
            if i>1 : x_axis = 1
            y_axis =i%2
            c_axis = ax[x_axis][y_axis]
            subdata = self.observer_sim_dic.get(observer)
            enemy_list = deepcopy(self.strategies)
            enemy_list.remove(observer)
            positions = [0,1.25,2.5]
            v3_winrates,v2_winrates = self.extract_data_vs3_vs2(observer, subdata, enemy_list,"Winrate")
            c_axis.bar(positions,v3_winrates,width = 0.4, color = 'royalblue')
            c_axis.bar([position+0.5 for position in positions],v2_winrates,width=0.4,color = 'lightskyblue')
            ticks_positions = [position+0.25 for position in positions]
            c_axis.set_xticks(ticks_positions)
            c_axis.set_xticklabels(enemy_list,fontsize = 7.5)
            c_axis.set_ylim((0,0.75))
            c_axis.set_title(f"Winrates by {observer}",fontsize = 10)
            colors = ['royalblue','lightskyblue']
            handles = [plt.Rectangle((0,0),1,1, color=color) for color in colors]
            c_axis.legend(handles,["vs3", "vs2"],loc = 'upper right')
            c_axis.axhline(y = 0.25,color = '0.75')
            plt.suptitle("Winrates measured in %")
            plt.savefig(self.path_plot_dir.joinpath("plots/winrate_plot.png"),dpi = fig.dpi)

    def plot_blocks_by_avg_turns(self):
        fig,ax = plt.subplots(nrows = 2, ncols = 2, figsize = (6,7.5))
        for i,observer in enumerate(self.observer_sim_dic.keys()):   
            x_axis = 0
            if i>1 : x_axis = 1
            y_axis =i%2
            c_axis = ax[x_axis][y_axis]
            subdata = self.observer_sim_dic.get(observer)
            enemy_list = deepcopy(self.strategies)
            enemy_list.remove(observer)
            #data prep
            positions = [0,1.25,2.5]
            avg_turns_by_obs3,avg_turns_by_obs2 = self.extract_data_vs3_vs2(observer,subdata,enemy_list,"Turns to Finish")
            avg_blocks3,avg_blocks2 = self.extract_data_vs3_vs2(observer,subdata,enemy_list,"Average Blocks Created")
            #most_blocks3, most_blocks2= self.extract_data_vs3_vs2(observer,subdata,enemy_list,"Game most Blocks")
            weighted_avg_blocks3 = [avg_blocks3[i]/avg_turns_by_obs3[i]for i in range(len(avg_blocks3))]
            weighted_avg_blocks2 = [avg_blocks2[i]/avg_turns_by_obs2[i]for i in range(len(avg_blocks2))]
            #weighted_most_blocks3 = [most_blocks3[i]/avg_turns_by_obs3[i] for i in range(len(most_blocks3))]
            #weighted_most_blocks2 = [most_blocks2[i]/avg_turns_by_obs2[i] for i in range(len(most_blocks2))] 

            c_axis.bar(positions,weighted_avg_blocks3,width = 0.4,color = 'royalblue')
            c_axis.bar([position+0.5 for position in positions],weighted_avg_blocks2,width = 0.4, color = 'lightskyblue')
            x_ticks = [0.34,1.88,3.42]
            c_axis.set_ylim((0,0.15))
            c_axis.set_xticks(x_ticks)
            c_axis.set_xticklabels(enemy_list,fontsize = 7.5)
            c_axis.set_title(f"{observer}",fontsize = 10)
            colors = ['royalblue','lightskyblue']
            handles = [plt.Rectangle((0,0),1,1, color=color) for color in colors]
            c_axis.legend(handles,["vs3", "vs2"],loc = 'upper right')
        plt.suptitle("Turns creating blocks in %")
        plt.savefig(self.path_plot_dir.joinpath("plots/block_by_turns.png"),dpi = fig.dpi)


    def plot_beats_by_avg_turns(self):
        
        fig,ax = plt.subplots(nrows = 2, ncols = 2, figsize = (6,7.5))
        for i,observer in enumerate(self.observer_sim_dic.keys()):   
            x_axis = 0
            if i>1 : x_axis = 1
            y_axis =i%2
            c_axis = ax[x_axis][y_axis]
            subdata = self.observer_sim_dic.get(observer)
            enemy_list = deepcopy(self.strategies)
            enemy_list.remove(observer)
            #data prep
            positions = [0,1.25,2.5]
            avg_turns_by_obs3,avg_turns_by_obs2 = self.extract_data_vs3_vs2(observer,subdata,enemy_list,"Turns to Finish")
            avg_beats3,avg_beats2 = self.extract_data_vs3_vs2(observer,subdata,enemy_list,"Average Kicks")
            weighted_avg_beats3 = [avg_beats3[i]/avg_turns_by_obs3[i]for i in range(len(avg_beats3))]
            weighted_avg_beats2 = [avg_beats2[i]/avg_turns_by_obs2[i]for i in range(len(avg_beats2))]
            c_axis.bar(positions,weighted_avg_beats3,width = 0.4,color = 'royalblue')
            c_axis.bar([position+0.5 for position in positions],weighted_avg_beats2,width = 0.4, color = 'lightskyblue')
            x_ticks = [0.34,1.88,3.42]
            c_axis.set_ylim((0,0.275))
            c_axis.set_xticks(x_ticks)
            c_axis.set_xticklabels(enemy_list,fontsize = 7.5)
            c_axis.set_title(f"{observer}",fontsize = 10)
            
            colors = ['royalblue','lightskyblue']
            handles = [plt.Rectangle((0,0),1,1, color=color) for color in colors]
            c_axis.legend(handles,["vs3", "vs2"],loc = 'upper right')
        plt.suptitle("Beating turns in %")
        plt.savefig(self.path_plot_dir.joinpath("plots/beats_by_turns.png"),dpi = fig.dpi)
        

    def extract_data_vs3_vs2(self,observer: str,subdata: pd.DataFrame, enemy_list : list, column : str)-> tuple:
        # function extracts data for every opponent strategy for a given observer. extracts the given column in the dataframe for the given data.
        # extracts a tuple of all records in which any strategy occurs with 2 and 3 players employing it.
        # example : observer : first -> extracts  : first, last, last, last  , first, last, last ,any
        #                                           first, pref_bl, pref_bl, pref_bl .......
        vs3 = []
        vs2 = []
        for enemy in enemy_list:
            vs3.append([strat for strat in subdata["Strategies"].tolist() if strat.count(enemy)==3][0])   
            vs2.append([strat for strat in subdata["Strategies"].tolist() if strat.count(enemy)==2][0])
        v3_extracted_data = [subdata.loc[subdata['Strategies'] == strat].reset_index().at[0,column] for strat in vs3]
        v2_extracted_data = [subdata.loc[subdata['Strategies'] == strat].reset_index().at[0,column] for strat in vs2]

        return v3_extracted_data,v2_extracted_data
        

    def __is_observer__(self,strat_comp : str, observer : str)-> bool:
        row_obs =  strat_comp.split(sep = ",")[0]
        return row_obs == observer
    def __get_enemys__(self, strat_comp : str)-> list[str]:
        return strat_comp.split(sep = ",")[1:]
    



    

if __name__ == "__main__" :    
    file = "2022.02.03.02.43.33.csv"
    plotter = Harry_Plotter(file)
    # TODO sinnvolle Plots #
    # plot winrate vs 3/2x Strat a  --> 4x3x2 Balken DONE
    # kicks/max kicks in relation zu Turns to Finish 
    # blocks / max blocks in relation tu Turns to finish
    col_names = ["Strategies","Winrate","Average Placement","Average Turns per Game", 
                "Turns to Finish","Average Blocks Created","Average Kicks",
                "Average got Kicked","Game most Kicks","Game most Blocks","Game most got Kicked"]