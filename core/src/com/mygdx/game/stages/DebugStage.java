package com.mygdx.game.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.networking.WorldServer;
import com.mygdx.game.utils.GameManager;

public class DebugStage extends Stage {
    private OrthographicCamera gamecam;
    private FitViewport gameport;
    private WorldServer worldServer;
    private Box2DDebugRenderer box2drender;

    // This will be our viewport measurements while working with the debug renderer
    private static final int VIEWPORT_WIDTH = GameManager.MAP_WIDTH;
    private static final int VIEWPORT_HEIGHT = GameManager.MAP_HEIGHT;

    public DebugStage() {
        worldServer = new WorldServer();
        
        // Box2d
        box2drender = new Box2DDebugRenderer();

        setupViewPort();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        
        gameport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.setViewport(gameport);

        box2drender.render(worldServer.world, gamecam.combined);

        super.draw();
    }
    
    private void setupViewPort() {
        gamecam = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        gamecam.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        gamecam.update();
        gameport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, gamecam);
        this.setViewport(gameport);
    }
    
}
