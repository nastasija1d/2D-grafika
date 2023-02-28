package com.example.dz1.objects;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class DuzinaPritiska extends Rectangle{

    //private double x;
    //private double y;
    private double max;
    private Timeline animacija;

    public DuzinaPritiska(double width, double height){
        super(0, 0,0.02*width,0.001*height);
        super.setFill(Color.RED);
        max = height;
        Scale linijaScale = new Scale ( 1, 1 );
        super.getTransforms ( ).addAll (
                new Translate(width*0.02,height),
                new Rotate(180),
                linijaScale);

        animacija = new Timeline (
                new KeyFrame( Duration.seconds ( 0 ), new KeyValue( linijaScale.yProperty ( ), 0, Interpolator.LINEAR ) ),
                new KeyFrame ( Duration.seconds ( 3 ), new KeyValue ( linijaScale.yProperty ( ), max*1.25, Interpolator.LINEAR ) ),
                new KeyFrame ( Duration.seconds ( 20 ), new KeyValue ( linijaScale.yProperty ( ), max*1.25, Interpolator.LINEAR ) )
        );
    }
    public void pokreni(){animacija.playFromStart();}
    public void zaustavi(){animacija.stop();}
    public void restart(){
        animacija.jumpTo(Duration.seconds(0));
    }
}
