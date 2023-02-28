package com.example.dz1.objects;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class Token extends Circle {
    private int points;
    private int id;
    private Timeline animacija;

    public Token ( double radius, Translate position, int p, int id ) {
        super ( radius);
        super.setFill(Color.GOLD);
        points = p;
        this.id = id;
        Scale linijaScale = new Scale ( 1, 1 );
        super.getTransforms ( ).addAll (
                position,
                linijaScale);
        animacija = new Timeline (
                new KeyFrame( Duration.seconds ( 0 ), new KeyValue( linijaScale.yProperty(), radius/8, Interpolator.LINEAR ),
                        new KeyValue( linijaScale.xProperty(), radius/8, Interpolator.LINEAR ) ),
                new KeyFrame( Duration.seconds ( 5 ), new KeyValue( linijaScale.yProperty(), radius/8, Interpolator.LINEAR ),
                        new KeyValue( linijaScale.xProperty(), radius/8, Interpolator.LINEAR ) ),
                new KeyFrame( Duration.seconds ( 6 ), new KeyValue ( linijaScale.yProperty ( ), 0.01, Interpolator.LINEAR ),
                        new KeyValue ( linijaScale.xProperty ( ), 0.01, Interpolator.LINEAR ))
        );
        animacija.playFromStart();
    }

    public boolean handleCollision ( Ball ball ) {
       // if (ball.isInHole()) return false; //ako je token vec pokupljen
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
        if(result) {
            animacija.jumpTo(Duration.seconds(6));
            super.setFill(Color.RED);
        }
        return result;
    };
    public int getPoints(){ //svaka rupa ima razlicit broj poena
        return points;
    }

    public int getRedniBr() {
        return id;
    }

    public Timeline getAnimacija(){
        return animacija;
    }
}
