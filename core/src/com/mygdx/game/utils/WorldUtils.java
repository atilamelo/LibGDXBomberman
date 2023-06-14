package com.mygdx.game.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class WorldUtils {

    public static World createWorld(){
        return new World(new Vector2(), true);
    }

    public static Body createBomberman(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(new Vector2(Constants.BOMBERMAN_X, Constants.BOMBERMAN_Y));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bodyDef);
        
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Constants.BOMBERMAN_WIDTH / 2, Constants.BOMBERMAN_HEIGHT / 2);

        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
        shape.dispose();
        
        return body;
    }


}
