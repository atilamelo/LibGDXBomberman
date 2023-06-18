package com.mygdx.game.utils;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
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

        // Shape of Bomberman
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Constants.BOMBERMAN_B2D_WIDTH, Constants.BOMBERMAN_B2D_HEIGHT);
        
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

    /*
     * CRÉDITOS: Baseado na solução de Brent Aureli Code
     * https://www.youtube.com/watch?v=AmLDslUdepo&list=PLZm85UZQLd2SXQzsF-a0-pPF6IWDDdrXt&index=7
     */
    public static void createMap(World world, TiledMap map) {
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        for(MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            rect.width = rect.width / Constants.PPM;
            rect.height = rect.height / Constants.PPM;
            rect.x = rect.x / Constants.PPM;
            rect.y = rect.y / Constants.PPM;

            bdef.type = BodyType.StaticBody;
            bdef.position.set(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);
            fdef.shape = shape; 
            body.createFixture(fdef);

        }

        shape.dispose();
    }

}
