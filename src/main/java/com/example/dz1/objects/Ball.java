package com.example.dz1.objects;

import com.example.dz1.Main;
import com.example.dz1.Utilities;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class Ball extends Circle {
	private Translate position;
	private Point2D speed;
	private Timeline animacija;
	private double radius;
	private boolean inHole;
	
	public Ball ( double radius, Translate position, Point2D speed ) {
		super ( radius, Color.RED );
		this.radius = radius;
		this.position = position;
		this.speed = speed;
		this.inHole = false;
		
		super.getTransforms ( ).addAll ( this.position );
	}

	public double brzina(){

		return Math.sqrt(Math.pow(this.speed.getX(),2)+Math.pow(this.speed.getY(),2));
	}

	public boolean update (double ds, double left, double right, double top, double bottom, double dampFactor,
						   double minBallSpeed, Rectangle[] barriers, Rectangle[] fields,Rectangle[] teleports, double su, double sd) {
		if(isInHole())return false; //ako je loptica upala u rupu, vise se ne pomera, zbog animacije
		boolean result = false;
		
		double newX = this.position.getX ( ) + this.speed.getX ( ) * ds;
		double newY = this.position.getY ( ) + this.speed.getY ( ) * ds;
		double radius = super.getRadius ( );
		
		double minX = left + radius;
		double maxX = right - radius;
		double minY = top + radius;
		double maxY = bottom - radius;
		
		this.position.setX ( Utilities.clamp ( newX, minX, maxX ) );
		this.position.setY ( Utilities.clamp ( newY, minY, maxY ) );

		if ( newX < minX || newX > maxX ) {
			this.speed = new Point2D ( -this.speed.getX ( ), this.speed.getY ( ) );
		}
		if ( newY < minY || newY > maxY ) {
			this.speed = new Point2D ( this.speed.getX ( ), -this.speed.getY ( ) );
		}
		for (int i = 0; i < Main.TELEPORT_NUMBER; i++){
			double x = teleports[i].getX();
			double y = teleports[i].getY();
			double w = teleports[i].getWidth();
			double h = teleports[i].getHeight();
			double r = radius;//+Math.sqrt(Math.pow(this.speed.getX(),2)+Math.pow(this.speed.getY(),2))*ds/2;

			if((newY>y-r && newY<y+h) && (newX>x-r && newX<x+w+r)){ //sa gornje strane
				if ((i%2) == 0){
					this.position.setX ( teleports[i+1].getX()+w/2);
					this.position.setY ( teleports[i+1].getY()+r+1+h);
				}else{
					this.position.setX ( teleports[i-1].getX()+w/2);
					this.position.setY ( teleports[i-1].getY()+r+1+h);
				}
			}
			//sa donje strane
			if((newY>y && newY<y+h+r) && (newX>x-r && newX<x+w+r)){
				if ((i%2) == 0){
					this.position.setX ( teleports[i+1].getX()+w/2);
					this.position.setY ( teleports[i+1].getY()-r-1);
				}else{
					this.position.setX ( teleports[i-1].getX()+w/2);
					this.position.setY ( teleports[i-1].getY()-r-1);
				}
				return false;
			}
		}

		for (int i=0; i<3; i++){ //za svaku barijeru proveravamo da li je loptica udarila u nju
			double x = barriers[i].getX();
			double y = barriers[i].getY();
			double w = barriers[i].getWidth();
			double h = barriers[i].getHeight();
			double r = radius+Math.sqrt(Math.pow(this.speed.getX(),2)+Math.pow(this.speed.getY(),2))*ds/2;
			//sa gornje strane
			if((newY>y && newY<y+h+r) && (newX>x-r && newX<x+w+r)){ //sa gornje strane
				this.speed = new Point2D ( this.speed.getX ( ), -this.speed.getY ( ) );
				this.position.setY ( y+h+r);
			}
			//sa donje
			if((newY>y-r && newY<y+h) && (newX>x-r && newX<x+w+r)){
				this.speed = new Point2D ( this.speed.getX ( ), -this.speed.getY ( ) );
				this.position.setY ( y-r );
			}
			//sa leve
			if((newX>x-r && newX<x+w) && (newY>y && newY<y+h) ){
				this.speed = new Point2D ( -this.speed.getX ( ), this.speed.getY ( ) );
				this.position.setX (x-r);
				this.position.setY(newY);
			}
			//sa desne
			if((newX<x+w+r && newX>x) && (newY>y && newY<y+h) ){
				this.speed = new Point2D ( -this.speed.getX ( ), this.speed.getY ( ) );
				this.position.setX (x+w+r);
				this.position.setY(newY);
			}
		}
		double df = dampFactor;
		//ako loptica predje preko leda ili blata, ubrzanje se menja
		if((newX>fields[0].getX() && newX<fields[0].getX()+30) && (newY>fields[0].getY() && newY<fields[0].getY()+30)) df=sd;
		if((newX>fields[1].getX() && newX<fields[1].getX()+30) && (newY>fields[1].getY() && newY<fields[1].getY()+30)) df=sd;
		if((newX>fields[2].getX() && newX<fields[2].getX()+30) && (newY>fields[2].getY() && newY<fields[2].getY()+30)) df=su;
		if((newX>fields[3].getX() && newX<fields[3].getX()+30) && (newY>fields[3].getY() && newY<fields[3].getY()+30)) df=su;

		this.speed = this.speed.multiply(df); //promena intenziteta
		double ballSpeed = this.speed.magnitude ( );
		
		if ( ballSpeed < minBallSpeed ) { //ako je loptica pala ispod minilane brzine zaustavlja se
			result = true;
		}
		return result;
	}

	public Timeline pokreni(){ //animacija upadanja loptice u rupu
		Scale linijaScale = new Scale ( 1, 1 );
		super.getTransforms ( ).addAll (linijaScale);
		animacija = new Timeline (
				new KeyFrame( Duration.seconds ( 0 ), new KeyValue( linijaScale.yProperty(), this.radius/4, Interpolator.LINEAR ),
													  new KeyValue( linijaScale.xProperty(), this.radius/4, Interpolator.LINEAR ) ),
				new KeyFrame( Duration.seconds ( 2 ), new KeyValue ( linijaScale.yProperty ( ), 0.001, Interpolator.LINEAR ),
													  new KeyValue ( linijaScale.xProperty ( ), 0.001, Interpolator.LINEAR ))
		);
		return animacija;
	}
	public boolean isInHole(){ //ispitujemo da li je loptica u rupi
		return inHole;
	}
	public void setInHole(){ //cim loptica upadne u rupu iz klase Hole pozovemo ovu funkciju i loptica se vise ne krece
		inHole = true;
	}

}
