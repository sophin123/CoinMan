package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    Texture[] man;
    Rectangle manRectangle;
    Texture deadMan;


    int manY = 0;
    int state = 0;
    int pause = 0;
    float gravity = 0.2f;
    float velocity = 0;
    int score = 0;


    ArrayList<Integer> coinX = new ArrayList<>();
    ArrayList<Integer> coinY = new ArrayList<>();
    ArrayList<Rectangle> coinRectangle = new ArrayList<>();
    Texture coin;
    int coinCount = 0;

    Random randomCoin, randomBomb;

    ArrayList<Integer> bombX = new ArrayList<>();
    ArrayList<Integer> bombY = new ArrayList<>();
    ArrayList<Rectangle> bombRectangle = new ArrayList<>();
    Texture bomb;
    int bombCount = 0;

    BitmapFont font;
    int gameState;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        man = new Texture[4];
        man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");

        manY = Gdx.graphics.getHeight() / 2;

        coin = new Texture("coin.png");
        randomCoin = new Random();


        bomb = new Texture("bomb.png");
        randomBomb = new Random();

        deadMan = new Texture("dizzy-1.png");

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(6);

    }

    public void makeCoin(){
        float height = randomCoin.nextFloat() * Gdx.graphics.getHeight();
        coinY.add((int) height);
        coinX.add(Gdx.graphics.getWidth());
    }

    public void makeBomb(){
        float height = randomBomb.nextFloat() * Gdx.graphics.getHeight();
        bombY.add((int) height);
        bombX.add(Gdx.graphics.getWidth());
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        if (gameState == 0){
            //Waiting to Start

            if (Gdx.input.isTouched()){
                gameState = 1;
            }
        }else if (gameState == 1){
            //Game is live

            if (bombCount < 400){
                bombCount++;
            }else{
                bombCount = 0;
                makeBomb();
            }

            bombRectangle.clear();
            for (int i =0; i<bombX.size(); i++){
                batch.draw(bomb, bombX.get(i), bombY.get(i));
                bombX.set(i, bombX.get(i) - 8);
                bombRectangle.add(new Rectangle(bombX.get(i), bombY.get(i), bomb.getWidth(), bomb.getHeight()));
            }

            //Coin
            if (coinCount < 250){
                coinCount++;
            }else{
                coinCount = 0;
                makeCoin();
            }

            coinRectangle.clear();
            for (int i = 0; i< coinX.size(); i++){
                batch.draw(coin, coinX.get(i), coinY.get(i));
                coinX.set(i, coinX.get(i) - 4);
                coinRectangle.add(new Rectangle(coinX.get(i), coinY.get(i), coin.getWidth(), coin.getHeight()));
            }

            if(Gdx.input.justTouched()){
                velocity = -10;
            }
            if (pause < 8) {
                pause++;
            } else {
                pause = 0;
                if (state < 3) {
                    state++;
                } else {
                    state = 0;
                }
            }

            velocity += gravity;
            manY -= velocity;

            if (manY <=0){
                manY = 0;
            }


        }else if (gameState == 2){
            //Game is over
            if (Gdx.input.isTouched()){
                gameState = 1;
                manY = Gdx.graphics.getHeight() / 2;
                score = 0;
                velocity = 0;
                coinX.clear();
                coinY.clear();
                coinRectangle.clear();
                coinCount = 0;
                bombX.clear();
                bombY.clear();
                coinRectangle.clear();
                coinCount = 0;


            }
        }
        //Bomb

        if (gameState == 2 ){
            batch.draw(deadMan, Gdx.graphics.getWidth() / 2 - man[state].getWidth() / 2, manY);
        }else {
            batch.draw(man[state], Gdx.graphics.getWidth() / 2 - man[state].getWidth() / 2, manY);
        }

        manRectangle = new Rectangle(Gdx.graphics.getWidth() /2 - man[state].getWidth() / 2, manY,man[state].getWidth(), man[state].getHeight()) ;

        for (int i =0; i < coinRectangle.size(); i++){
            if (Intersector.overlaps(manRectangle, coinRectangle.get(i))){
                Gdx.app.log("Coins", "Collision");

                score++;

                coinRectangle.clear();
                coinX.clear();
                coinY.clear();
                break;
            }
        }

        for (int i=0; i< bombRectangle.size(); i++){
           if (Intersector.overlaps(manRectangle, bombRectangle.get(i))){
               Gdx.app.log("Bombs", "Collision");

               bombRectangle.clear();
               bombX.clear();
               bombY.clear();

               gameState = 2;
               break;

           }
        }

        font.draw(batch, String.valueOf(score), 100, 200);
        batch.end();

    }

    @Override
    public void dispose() {
        batch.dispose();

    }
}
