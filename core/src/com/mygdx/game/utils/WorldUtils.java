package com.mygdx.game.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.box2d.BombermanUserData;

public class WorldUtils {

    public static World createWorld(){
        return new World(new Vector2(), true);
    }

    public static Body createBomberman(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(Constants.BOMBERMAN_X, Constants.BOMBERMAN_Y));
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Constants.BOMBERMAN_WIDTH / 2, Constants.BOMBERMAN_HEIGHT / 2);
        Body body = world.createBody(bodyDef);

        body.createFixture(shape, Constants.BOMBERMAN_DENSITY);
        body.resetMassData();
        body.setUserData(new BombermanUserData(Constants.BOMBERMAN_WIDTH, Constants.BOMBERMAN_HEIGHT));
        shape.dispose();
        
        return body;
    }


}
