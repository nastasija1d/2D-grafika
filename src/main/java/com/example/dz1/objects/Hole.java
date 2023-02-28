package com.example.dz1.objects;

import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Translate;

public class Hole extends Circle {
	private int points;

	public Hole ( double radius, Translate position, Color c, int p ) {
		super ( radius);
		Stop stops[]={
				new Stop(0, Color.BLACK),
				new Stop(1, c)
		};
		RadialGradient gr = new RadialGradient(0,0,0.5,0.5,0.5,true, CycleMethod.NO_CYCLE,stops);
		super.setFill(gr);
		points=p;
		super.getTransforms ( ).addAll ( position );
	}
	
	public boolean handleCollision ( Ball ball, double limit ) {
		if (ball.brzina()>limit || ball.isInHole()) return false; //ako je lopta vec u rupi ili joj je brzina prevelika, ne moze da upadne
		Bounds ballBounds = ball.getBoundsInParent ( );
		
		double ballX      = ballBounds.getCenterX ( );
		double ballY      = ballBounds.getCenterY ( );
		double ballRadius = ball.getRadius ( );
		
		Bounds holeBounds = super.getBoundsInParent ( );
		
		double holeX      = holeBounds.getCenterX ( );
		double holeY      = holeBounds.getCenterY ( );
		double holeRadius = super.getRadius ( );
		
		double distanceX = holeX - ballX;
		double distanceY = holeY - ballY;
		
		double distanceSquared = distanceX * distanceX + distanceY * distanceY;
		
		boolean result = distanceSquared < ( holeRadius * holeRadius );
		if(result) ball.setInHole(); //ako je upala u rupu, setujemo taj indikator i u klasi Ball da bi prestala da se krece
		return result;
	};
	public int getPoints(){ //svaka rupa ima razlicit broj poena
		return points;
	}
}
