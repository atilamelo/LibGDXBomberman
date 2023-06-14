package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.game.entities.Player;

public class GameScreen implements Screen {

  private TiledMap map;
  private OrthogonalTiledMapRenderer renderer;
  private OrthographicCamera camera;
  private Player player;

  @Override
  public void show() {
    map = new TmxMapLoader().load("maps/map_teste.tmx");
    renderer = new OrthogonalTiledMapRenderer(map);

    camera = new OrthographicCamera();

    TiledMapTileLayer layerBloco =  (TiledMapTileLayer) map.getLayers().get(0);

    player =  new Player(
        new Sprite(new Texture("assets/img/player (1).png")),
        (TiledMapTileLayer) map.getLayers().get(0)
      );

    player.setPosition(
      4 * player.getCollisionLayer().getTileWidth(),
      (player.getCollisionLayer().getHeight() - 6) *
      player.getCollisionLayer().getTileHeight()
    );

    Gdx.input.setInputProcessor(player);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    camera.position.set(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2,0);
    camera.update();

    renderer.setView(camera);
    renderer.render();

    renderer.getBatch().begin();
    player.draw(renderer.getBatch());
    renderer.getBatch().end();
  }

  @Override
  public void resize(int width, int height) {
    camera.viewportWidth = width / 3 ;
    camera.viewportHeight = height /3;

  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {
    dispose();
  }

  @Override
  public void dispose() {
    map.dispose();
    renderer.dispose();
    player.getTexture().dispose();
  }
}
