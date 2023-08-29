package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.BombermanGame;
import com.mygdx.game.utils.GameManager;


public class MenuScreen implements Screen {


    private Stage stage;
    private Table table_background;
    private Table table_menu;
    private TextButton buttonPlay, buttonExit;
    private BitmapFont white; 
    private AssetManager assetManager;
    private BombermanGame game;

    public MenuScreen(BombermanGame game) {
        this.game = game;
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);

        stage.act(delta);
        stage.draw();
        
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        stage.getViewport().apply(true); // apply the viewport changes
        stage.getCamera().position.set(stage.getWidth() / 2, stage.getHeight() / 2, 0); // center the camera on the stage
    }
    
    @Override
    public void show() {

        // Loads assets for the game
        assetManager = new AssetManager();
        assetManager.load("menu/menu_background.png", Texture.class);
        assetManager.finishLoading();

        stage = new Stage(new FitViewport(GameManager.GAME_WIDTH, GameManager.GAME_HEIGHT));

        // Fill background 
        table_background = new Table();
        table_background.setFillParent(true);
        table_background.background(new TextureRegionDrawable(new TextureRegion(assetManager.get("menu/menu_background.png", Texture.class))));
        stage.addActor(table_background);

        table_menu = new Table();
        table_menu.setSize(stage.getWidth(), stage.getHeight());
        table_menu.align(Align.center | Align.top);

        table_menu.setPosition(0, Gdx.graphics.getHeight());

        white = new BitmapFont(Gdx.files.internal("font/white.fnt"), false);
        white.getData().setScale(5f);

        TextButtonStyle textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = white;

        buttonExit = new TextButton(">EXIT", textButtonStyle);
        buttonPlay = new TextButton(">START", textButtonStyle);
        buttonPlay.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setScreen(new GameScreen(game));
            }
        });
        buttonPlay.setTouchable(Touchable.enabled);


        
        buttonExit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                Gdx.app.exit();
            }
        });
        buttonExit.setTouchable(Touchable.enabled);


        table_menu.padTop(1500);
        table_menu.row();  
        table_menu.add(buttonPlay).fillX();
        table_menu.row();  
        table_menu.add(buttonExit).fillX().height(100).padTop(50);
        table_menu.pack();

        table_menu.setPosition(
            (stage.getWidth() - table_menu.getWidth()) / 2f,
            (stage.getHeight() - table_menu.getHeight())
        ); // center the table on the stage
        stage.addActor(table_menu);

        Gdx.input.setInputProcessor(stage); // set the stage as the input processor
    }


    @Override
    public void dispose(){
        stage.dispose();
        assetManager.dispose();
    }
    

    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
        dispose();
    }


    @Override
    public void pause() {
    }


    @Override
    public void resume() {
    }
}