package com.flappybird.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture topTube, bottomTube, background, birds[] = new Texture[3], gameOver;
	int tubesNumber = 4, spaceBetweenTubes, FlagBird, tubeSpeed = 5, jumpHeight = 20;
	int isGameStart = 0;
	float shift[] = new float[tubesNumber], tubeX[] = new float[tubesNumber], distanceBetweenTubes, flyHeight,
			fallingSpeed;
	Random rand = new Random();
	Circle birdCircle;
	Rectangle[] topTubesRectangles, bottomTubesRectangle;
	int score = 0, passedTubeIndex = 0;
	BitmapFont scoreFont;
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("background.png");
		birds[0] = new Texture("bird_wings_up.png");
		birds[1] = new Texture("bird_wings_down.png");
		birds[2] = new Texture("bird_wings_down.png");
		topTube = new Texture("top_tube.png");
		bottomTube = new Texture("bottom_tube.png");
		spaceBetweenTubes = Math.max(700, Gdx.graphics.getHeight() - topTube.getHeight() - bottomTube.getHeight());
		distanceBetweenTubes = Gdx.graphics.getWidth() / 2;
		birdCircle = new Circle();
		topTubesRectangles = new Rectangle[tubesNumber];
		bottomTubesRectangle = new Rectangle[tubesNumber];
		gameOver = new Texture("game_over.png");
		scoreFont = new BitmapFont();
		scoreFont.setColor(Color.WHITE);
		scoreFont.getData().setScale(10);
		Initialization();
	}
	public void Initialization(){
		flyHeight = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;
		for(int i = 0; i < tubesNumber; i++) {
			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + i * distanceBetweenTubes + Gdx.graphics.getWidth();
			shift[i] = (rand.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - spaceBetweenTubes - 400);
			topTubesRectangles[i] = new Rectangle();
			bottomTubesRectangle[i] = new Rectangle();
		}
	}
    public int FlagBirdReturn(int FlagBird){
		if(FlagBird == 1)
			return 0;
		else if (FlagBird == 0)
			return 1;
		else
			return 2;
	}
	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		if(isGameStart == 0){
		if(Gdx.input.isTouched()) {
			isGameStart = 1;
		}}
		else if(isGameStart == 2){
			tubeSpeed = 0;
			for(int i = 0; i < tubesNumber; i++) {
				if (tubeX[i] < -1 * topTube.getWidth()) {
					tubeX[i] = distanceBetweenTubes * tubesNumber;
					continue;
				}
				tubeX[i] -= tubeSpeed;
				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + spaceBetweenTubes / 2 + shift[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - spaceBetweenTubes / 2 - bottomTube.getHeight() + shift[i]);
			}
			batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2,
					Gdx.graphics.getHeight() / 2 + gameOver.getHeight() / 2);
			FlagBird = 2;
			if(Gdx.input.isTouched()) {
				isGameStart = 1;
				tubeSpeed = 5;
				FlagBird = 0;
				score = 0;
				Initialization();
			}
		}
		if(isGameStart == 1) {
			if (Gdx.input.isTouched())
				fallingSpeed = -1 * jumpHeight;
			if(flyHeight > 0) {
				fallingSpeed++;
				flyHeight -= fallingSpeed;
			}
			else isGameStart = 2;
			if(tubeX[passedTubeIndex] < Gdx.graphics.getWidth() / 2 - topTube.getWidth()){
				score++;
				passedTubeIndex = (passedTubeIndex + 1) % tubesNumber;
			}
		}
		if(isGameStart == 1)
		for(int i = 0; i < tubesNumber; i++) {
			if(tubeX[i] < -1 * topTube.getWidth()) {
				tubeX[i] = distanceBetweenTubes * tubesNumber;
				continue;
			}
			tubeX[i] -= tubeSpeed;
			batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + spaceBetweenTubes / 2 + shift[i]);
			batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - spaceBetweenTubes / 2 - bottomTube.getHeight() + shift[i]);
			topTubesRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + spaceBetweenTubes / 2 + shift[i],
					topTube.getWidth(), topTube.getHeight());
			bottomTubesRectangle[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - spaceBetweenTubes / 2
					- bottomTube.getHeight() + shift[i], bottomTube.getWidth(), bottomTube.getHeight());
		}
		FlagBird = FlagBirdReturn(FlagBird);
		batch.draw(birds[FlagBird], Gdx.graphics.getWidth() / 2 - birds[FlagBird].getWidth() / 2, flyHeight);
		scoreFont.draw(batch, String.valueOf(score), Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight());
		batch.end();
		birdCircle.set(Gdx.graphics.getWidth() / 2, flyHeight + birds[FlagBird].getHeight() / 2,
				birds[FlagBird].getWidth() / 2);
		for(int i = 0; i < tubesNumber; i++){
			if(Intersector.overlaps(birdCircle, topTubesRectangles[i]) ||
					Intersector.overlaps(birdCircle, bottomTubesRectangle[i])){
				isGameStart = 2;
			}
		}
	}
	@Override
	public void dispose () {

	}
}
