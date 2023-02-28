package com.example.dz1.objects;

import com.example.dz1.Main;
import com.example.dz1.Utilities;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class Objekat extends Circle {
    private Translate position;
    private Timeline animacija;
    private Point2D speed;

    public Objekat ( double radius) {
        super ( radius);
        double x = (int)( Math.random() * 600 );
        double y = (int)( Math.random() * 800 );
        position = new Translate( x, y );

        Stop stops[]={
                new Stop(0, Color.BLACK),
                new Stop(1, Color.WHITE)
        };
        RadialGradient gr = new RadialGradient(0,0,0.5,0.5,0.5,true, CycleMethod.NO_CYCLE,stops);
        super.setFill(gr);

        Scale linijaScale = new Scale ( 1, 1 );
        super.getTransforms ( ).addAll (
                position,
                linijaScale);

        x = (int)( Math.random() * Main.OBJ_SPEED );
        y = (int)( Math.random() * Main.OBJ_SPEED );
        speed = new Point2D(x, y);
        animacija = new Timeline (
                new KeyFrame( Duration.seconds ( 0 ), new KeyValue( linijaScale.yProperty(), radius/8, Interpolator.LINEAR ),
                        new KeyValue( linijaScale.xProperty(), radius/8, Interpolator.LINEAR ) ),
                new KeyFrame( Duration.seconds (10 ), new KeyValue( linijaScale.yProperty(), radius/8, Interpolator.LINEAR ),
                        new KeyValue( linijaScale.xProperty(), radius/8, Interpolator.LINEAR ) ),
                new KeyFrame( Duration.seconds (11 ), new KeyValue ( linijaScale.yProperty ( ), 0.01, Interpolator.LINEAR ),
                        new KeyValue ( linijaScale.xProperty ( ), 0.01, Interpolator.LINEAR ))
        );
        animacija.playFromStart();
    }

    public boolean handleCollision ( Ball ball ) {
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

        return result;
    };

    public Timeline getAnimacija(){

        return animacija;
    }

    public void update (double ds, double left, double right, double top, double bottom) {

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
            this.speed = new Point2D( -this.speed.getX ( ), this.speed.getY ( ) );
        }
        if ( newY < minY || newY > maxY ) {
            this.speed = new Point2D ( this.speed.getX ( ), -this.speed.getY ( ) );
        }

    }
}
