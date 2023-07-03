package com.mygdx.game.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.mygdx.game.configs.LevelConfig;

public class GameScreen implements Screen {
    private GameStage stage;

    @Override
    public void show() {
        LevelConfig levelOne = new LevelConfig(
            60,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            1);
        stage = new GameStage(this, levelOne);
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
    }

}
