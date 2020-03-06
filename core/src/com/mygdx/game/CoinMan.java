package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.awt.Font;
import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int manPos=0;
	int pause=0;
	float gravity = 0.2f;
	float velocity = 0;
	float manY = 0;

	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	Texture coin;
	int coinCount = 0;
	Random random;

	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	int bombCount=0;
	Texture bomb;

	ArrayList<Rectangle> coinRectangle = new ArrayList<Rectangle>();
	ArrayList<Rectangle> bombRectangle = new ArrayList<Rectangle>();
	Rectangle manRectangle;

	int score = 0;
	BitmapFont font;



	int gameState = 0;
	Texture dizzy;


	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0]= new Texture("frame-1.png");
		man[1]= new Texture("frame-2.png");
		man[2]= new Texture("frame-3.png");
		man[3]= new Texture("frame-4.png");
		manY = Gdx.graphics.getHeight()/2;

		coin = new Texture("coin.png");
		random = new Random();

		bomb = new Texture("bomb.png");

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		dizzy = new Texture("dizzy-1.png");


	}

	public void makeCoin ()
	{
		float height = random.nextFloat() * Gdx.graphics.getHeight();

		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makeBomb()
	{
		float height = random.nextFloat() * Gdx.graphics.getHeight();

		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();

		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1)
		{
			// GAME IS LIVE

			// BOMBS
			if (bombCount<300)
				bombCount++;
			else
			{
				bombCount=0;
				makeBomb();
			}

			bombRectangle.clear();
			for (int i=0;i<bombXs.size();i++)
			{
				batch.draw(bomb, bombXs.get(i), bombYs.get(i));
				bombRectangle.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
				bombXs.set(i, bombXs.get(i)-16);
			}


			// COINS
			if (coinCount<100)
				coinCount++;
			else
			{
				coinCount=0;
				makeCoin();
			}

			coinRectangle.clear();
			for (int i=0;i<coinXs.size();i++)
			{
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinRectangle.add(new Rectangle (coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
				coinXs.set(i, coinXs.get(i)-10);

			}


			if (Gdx.input.justTouched())
				velocity=-10;

			if (pause<8)
				pause++;
			else
			{
				pause=0;
				if (manPos < 3)
					manPos++;
				else
					manPos = 0;
			}


			velocity+=gravity;
			manY-=velocity;

			if (manY<=0)
				manY=0;


		}

		else if (gameState == 0)
		{
			// WAITING TO START
			if (Gdx.input.justTouched())
				gameState=1;
		}

		else if (gameState == 2)
		{
			// GAME OVER

			coinXs.clear();
			coinYs.clear();
			coinRectangle.clear();
			coinCount = 0;

			bombXs.clear();
			bombYs.clear();
			bombRectangle.clear();
			bombCount = 0;

			manPos=0;
			pause=0;
			velocity=0;

			if (Gdx.input.justTouched())
			{
				gameState = 0;
				manY = Gdx.graphics.getHeight()/2;
				score = 0;
			}

		}


		if (gameState == 2)
		{
			batch.draw(dizzy, Gdx.graphics.getWidth()/2 - man[manPos].getWidth()/2, manY);
		}
		else
		{
			batch.draw(man[manPos], Gdx.graphics.getWidth() / 2 - man[manPos].getWidth() / 2, manY);
		}

		manRectangle = new Rectangle(Gdx.graphics.getWidth()/2 - man[manPos].getWidth()/2, manY, man[manPos].getWidth(), man[manPos].getHeight());

		for (int i=0;i<coinRectangle.size();i++)
		{
			if (Intersector.overlaps(manRectangle, coinRectangle.get(i)))
			{
				Gdx.app.log("Coin", "Collision!");
				coinRectangle.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				score++;
			}
		}

		for (int i=0;i<bombRectangle.size();i++) {
			if (Intersector.overlaps(manRectangle, bombRectangle.get(i))) {
				Gdx.app.log("Bomb", "Collison!");
				gameState = 2;
			}
		}

		font.draw(batch, Integer.toString(score), 100, 200);


		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
