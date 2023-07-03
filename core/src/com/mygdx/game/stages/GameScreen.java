package com.mygdx.game.stages;

import java.lang.System.Logger.Level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.mygdx.game.BombermanGame;
import com.mygdx.game.configs.BombermanConfig;
import com.mygdx.game.configs.LevelConfig;

public class GameScreen implements Screen {
    BombermanGame game;
    private GameStage stage;
    private int currentLevel;
    private LevelConfig levelConfig;
    private BombermanConfig currentBombermanConfig;
    
    public GameScreen(BombermanGame game) {
        currentLevel = 1;
        this.game = game;
        currentBombermanConfig = BombermanConfig.initialBombermanConfig;
    }

    @Override
    public void show() {
        if(currentLevel > 50){
            System.out.println("Jogo terminado!");
            Gdx.app.exit();
        }
        stage = new GameStage(this, LevelConfig.getLevelConfig(currentLevel), currentBombermanConfig);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the stage
        stage.draw();
        stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void gameOver(){
        System.out.println("Game Over");
        Gdx.app.exit();
        
    }

    public void nextLevel(BombermanConfig bombermanConfig){
        this.currentBombermanConfig = bombermanConfig;
        this.currentLevel++;
        show();
    }

    public void restartLevel(BombermanConfig bombermanConfig){
        this.currentBombermanConfig = bombermanConfig;
        show();
    }

}
