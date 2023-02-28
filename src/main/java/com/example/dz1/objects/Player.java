package com.example.dz1.objects;

import com.example.dz1.Utilities;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Player extends Group {
	
	private double width;
	private double height;
	private double radius;
	private Translate position;
	private Rotate rotate;
	private double max;
	private Circle base;
	private Path cannon;
	
	public Player ( double width, double height, Translate position ) {
		this.width = width;
		this.height = height;
		this.position = position;
		this.max = 1000;

		radius = width/2;
		base = new Circle(radius,Color.ORANGE);
		base.getTransforms().addAll(
				new Translate(radius, height-radius)
		);

		cannon = new Path(
				new MoveTo(width/4,0),
				new LineTo(0, height-radius),
				new HLineTo(width),
				new LineTo(width * 3/4, 0),
				new ClosePath()
		);
		cannon.setFill(Color.LIGHTBLUE);
		super.getChildren ( ).addAll ( cannon, base );
		
		this.rotate = new Rotate ( );
		
		super.getTransforms ( ).addAll (
				position,
				new Translate ( width / 2, height-radius ),
				rotate,
				new Translate ( -width / 2, -height+radius )
		);
	}
	
	public void handleMouseMoved ( MouseEvent mouseEvent, double minAngleOffset, double maxAngleOffset ) {
		Bounds bounds = super.getBoundsInParent ( );
		
		double startX = bounds.getCenterX ( );
		double startY = bounds.getMaxY ( );
		
		double endX = mouseEvent.getX ( );
		double endY = mouseEvent.getY ( );
		
		Point2D direction     = new Point2D ( endX - startX, endY - startY ).normalize ( );
		Point2D startPosition = new Point2D ( 0, -1 );
		
		double angle = ( endX > startX ? 1 : -1 ) * direction.angle ( startPosition );
		
		this.rotate.setAngle ( Utilities.clamp ( angle, minAngleOffset, maxAngleOffset ) );
	}
	
	public Translate getBallPosition ( ) {
		double startX = this.position.getX ( ) + this.width / 2;
		double startY = this.position.getY ( ) + this.height - this.radius;
		
		double x = startX + Math.sin ( Math.toRadians ( this.rotate.getAngle ( ) ) ) * this.height;
		double y = startY - Math.cos ( Math.toRadians ( this.rotate.getAngle ( ) ) ) * this.height;
		
		Translate result = new Translate ( x, y );
		
		return result;
	}
	
	public Point2D getSpeed ( ) {
		double startX = this.position.getX ( ) + this.width / 2;
		double startY = this.position.getY ( ) + this.height - this.radius;
		
		double endX = startX + Math.sin ( Math.toRadians ( this.rotate.getAngle ( ) ) ) * this.height;
		double endY = startY - Math.cos ( Math.toRadians ( this.rotate.getAngle ( ) ) ) * this.height;
		
		Point2D result = new Point2D ( endX - startX, endY - startY );
		
		return result.normalize ( );
	}
	public void style(int i){
		if (i == 1){
			base.setFill(Color.WHITE);
			cannon.setFill(Color.BLUE);
			max=1500;
		}
		if (i == 2){
			base.setFill(Color.BLACK);
			cannon.setFill(Color.RED);
			max=2000;
		}
		if (i == 3){
			base.setFill(Color.GRAY);
			cannon.setFill(Color.ORANGE);
			max=2500;
		}
	}

	public double maxSpeed(){
		return max;
	}
}
