package com.mygdx.game.stages;

import com.mygdx.game.configs.BombermanConfig;
import com.mygdx.game.configs.LevelConfig;
import com.mygdx.game.networking.GameClient;
import com.mygdx.game.screens.GameScreen;

public class MultiplayerStage extends GameStage {
    public GameClient client;

    public MultiplayerStage(GameScreen gameScreen, LevelConfig levelConfig, BombermanConfig bombermanConfig) {
        super(gameScreen, levelConfig, bombermanConfig);
        setupNetworking();

    }
    
    private void setupNetworking() {
        try{
            client = new GameClient("localhost");
        } catch (Exception e){
            System.out.println("Não foi possível conectar ao servidor");
        }
    }

}
