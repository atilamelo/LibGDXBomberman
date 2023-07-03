package com.mygdx.game.stages;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.utils.GameManager;

public class Hud implements Disposable {

    public Stage stage;
    private Viewport viewport;

    // score && time tracking variables
    private Integer worldTimer;
    private float timeCount;
    private static Integer score;
    private boolean timeUp;

    // Scene2D Widgets
    private Label countdownLabel, timeLabel, linkLabel, livesLabel, amountOfLivesLabel;
    private static Label scoreLabel;

    public Hud(SpriteBatch sb, int livesBomberman) {
        // define tracking variables
        worldTimer = 5;
        timeCount = 0;
        score = 0;

        // setup the HUD viewport using a new camera seperate from gamecam
        // define stage using that viewport and games spritebatch
        viewport = new FitViewport(GameManager.GAME_WIDTH, GameManager.GAME_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        // define labels using the String, and a Label style consisting of a font and
        // color
        countdownLabel = new Label(String.format("%03d", worldTimer),
                new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        countdownLabel.setFontScale(GameManager.PPM /2);
        scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel.setFontScale(GameManager.PPM / 2);
        timeLabel = new Label("LEFT TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel.setFontScale(GameManager.PPM  / 2);
        linkLabel = new Label("POINTS", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        linkLabel.setFontScale(GameManager.PPM / 2);
        livesLabel = new Label("LEFT", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        livesLabel.setFontScale(GameManager.PPM / 2);
        amountOfLivesLabel = new Label(String.format("%02d", livesBomberman), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        amountOfLivesLabel.setFontScale(GameManager.PPM / 2);

        // define a table used to organize hud's labels
        Table table = new Table();
        table.top();
        table.setFillParent(true);

        // add labels to table, padding the top, and giving them all equal width with
        // expandX
        table.add(linkLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.add(livesLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(countdownLabel).expandX();
        table.add(amountOfLivesLabel).expandX();

        // add table to the stage
        stage.addActor(table);

    }

    public void update(float dt) {
        timeCount += dt;
        if (timeCount >= 1) {
            if (worldTimer > 0) {
                worldTimer--;
            } else {
                timeUp = true;
            }
            countdownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }

    public static void addScore(int value) {
        score += value;
        scoreLabel.setText(String.format("%06d", score));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public boolean isTimeUp() {
        return timeUp;
    }

    public static Label getScoreLabel() {
        return scoreLabel;
    }

    public static Integer getScore() {
        return score;

    }
}
