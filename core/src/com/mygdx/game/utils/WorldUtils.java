package com.mygdx.game.utils;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.box2d.BombermanUserData;

public class WorldUtils {

    public static World createWorld() {
        return new World(new Vector2(), true);
    }

    public static Body createBomberman(World world) {
        // Body Def
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(Constants.BOMBERMAN_X, Constants.BOMBERMAN_Y));
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 0f;
        CircleShape shape = new CircleShape();
        shape.setRadius(Constants.BOMBERMAN_COLLISION_RADIUS);
        
        // Create body 
        Body body = world.createBody(bodyDef);
        
        // Fixture Def 
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.5f; 
        fixtureDef.friction = 0.0f; 
        fixtureDef.restitution = 0.0f;


        body.createFixture(fixtureDef);
        body.resetMassData();
        body.setUserData(new BombermanUserData(Constants.BOMBERMAN_WIDTH, Constants.BOMBERMAN_HEIGHT));
        shape.dispose();

        return body;
    }

    public static void createMap(World world, TiledMap map) {
        // create body and fixture variables
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;
        float width = Constants.TILES_WIDTH;
        float height = Constants.TILES_HEIGHT;
        
        TiledMapTileLayer mapLayer = (TiledMapTileLayer) map.getLayers().get(0);
        
        for(int x = 0; x < mapLayer.getWidth(); x++){
            for(int y = 0; y < mapLayer.getHeight(); y++){
                Cell cell = mapLayer.getCell(x, y);
                if(cell.getTile().getProperties().containsKey("collision")){
                    bdef.type = BodyDef.BodyType.StaticBody;
                    bdef.position.set((x + width / 2),
                            (y + height / 2));

                    body = world.createBody(bdef);

                    shape.setAsBox(width / 2, height / 2);
                    fdef.shape = shape;
                    body.createFixture(fdef);
                }
            }
        }
    }

}
