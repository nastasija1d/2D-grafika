package com.example.dz1;

import com.example.dz1.objects.*;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;

public class Main extends Application implements EventHandler<MouseEvent> {
	public static final double WINDOW_WIDTH  = 600;
	public static final double WINDOW_HEIGHT = 800;
	private static final double FENCE = 25;
	
	private static final double PLAYER_WIDTH            = 20;
	private static final double PLAYER_HEIGHT           = 80;
	private static final double PLAYER_MAX_ANGLE_OFFSET = 60;
	private static final double PLAYER_MIN_ANGLE_OFFSET = -60;
	
	private static final double MS_IN_S            = 1e3;
	private static final double NS_IN_S            = 1e9;
	private static final double MAXIMUM_HOLD_IN_S  = 3;
	private static final double MAXIMUM_BALL_SPEED = 1500;
	private static final double BALL_RADIUS        = Main.PLAYER_WIDTH / 4;
	private static final double BALL_DAMP_FACTOR   = 0.995;
	private static final double BALL_DAMP_FACTOR_SU   = 1.5;
	private static final double BALL_DAMP_FACTOR_SD   = 0.8;
	private static final double MIN_BALL_SPEED     = 1;
	public static final int TELEPORT_NUMBER = 2;
	public static final int GAME_DURATION = 60;
	
	private static final double HOLE_RADIUS = 3 * BALL_RADIUS;
	private static final int ATTEMPTS = 5;
	private static final int NUM_OF_TOKENS = 15;
	private static final int NUM_OF_OBJ = 3;
	public static final int OBJ_SPEED = 100;
	public static final int OBJ_DURATION = 15;

	
	private Group root;
	private Player player;
	private Ball ball;
	private long time;
	private Hole holes[];
	private Rectangle barriers[];
	private Rectangle fields[];
	private Circle remaining[];
	private Token token[];
	private Rectangle teleport[];
	private DuzinaPritiska pritisak;
	private DuzinaIgre igra;
	private ImageView imageView;
	private int score;
	private Text text;
	private Text vreme;
	private Timeline animacija;
	private int pokusaj;
	private int teren;
	private int top;
	private boolean kraj;
	private int numberTokens;
	private Objekat obj;
	private int numberObj;

	private Scene pocetniMeni(Stage stage, Scene scene, ImagePattern pozadina){
		//kreiranje pocetne scene za izbor terena i topa
		Group meni = new Group();
		Scene scene1 = new Scene(meni, 600, 800, pozadina);
		Button dugme = new Button("Izaberi");
		dugme.getTransforms().addAll(new Translate(230,700));
		dugme.setPrefSize(140,30);

		Image image0 = new Image(Main.class.getClassLoader().getResourceAsStream("teren0.jpg"));
		ImageView iv0 = new ImageView(image0);
		iv0.setFitHeight(190); iv0.setFitWidth(140);
		Image image1 = new Image(Main.class.getClassLoader().getResourceAsStream("teren1.jpg"));
		ImageView iv1 = new ImageView(image1);
		iv1.setFitHeight(190); iv1.setFitWidth(140);
		Image image2 = new Image(Main.class.getClassLoader().getResourceAsStream("teren2.jpg"));
		ImageView iv2 = new ImageView(image2);
		iv2.setFitHeight(190); iv2.setFitWidth(140);

		Button teren1 = new Button();
		teren1.setPrefSize(150,200);
		teren1.getTransforms().addAll(new Translate(35, 100));
		teren1.setOnAction(e->teren=0);
		teren1.setGraphic(iv0);
		Button teren2 = new Button();
		teren2.setPrefSize(150,200);
		teren2.getTransforms().addAll(new Translate(225, 100));
		teren2.setOnAction(e->teren=1);
		teren2.setGraphic(iv1);
		Button teren3 = new Button();
		teren3.setPrefSize(150,200);
		teren3.getTransforms().addAll(new Translate(415, 100));
		teren3.setOnAction(e->teren=2);
		teren3.setGraphic(iv2);

		Label lb1 = new Label("EASY");
		lb1.getTransforms().addAll(new Translate(85,305));
		lb1.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20));
		Label lb2 = new Label("MEDIUM");
		lb2.getTransforms().addAll(new Translate(265,305));
		lb2.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20));
		Label lb3 = new Label("HARD");
		lb3.getTransforms().addAll(new Translate(465,305));
		lb3.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20));

		Image i0 = new Image(Main.class.getClassLoader().getResourceAsStream("top0.jpg"));
		ImageView t0 = new ImageView(i0);
		t0.setFitHeight(140); t0.setFitWidth(90);
		Image i1 = new Image(Main.class.getClassLoader().getResourceAsStream("top1.jpg"));
		ImageView t1 = new ImageView(i1);
		t1.setFitHeight(140); t1.setFitWidth(90);
		Image i2 = new Image(Main.class.getClassLoader().getResourceAsStream("top2.jpg"));
		ImageView t2 = new ImageView(i2);
		t2.setFitHeight(140); t2.setFitWidth(90);
		Image i3 = new Image(Main.class.getClassLoader().getResourceAsStream("top3.jpg"));
		ImageView t3 = new ImageView(i3);
		t3.setFitHeight(140); t3.setFitWidth(90);

		Button top1 = new Button();
		top1.setPrefSize(100,150);
		top1.getTransforms().addAll(new Translate(40,400));
		top1.setOnAction(e->top=0);
		top1.setGraphic(t0);
		Button top2 = new Button();
		top2.setPrefSize(100,150);
		top2.getTransforms().addAll(new Translate(180,400));
		top2.setOnAction(e->top=1);
		top2.setGraphic(t1);
		Button top3 = new Button();
		top3.setPrefSize(100,150);
		top3.getTransforms().addAll(new Translate(320,400));
		top3.setOnAction(e->top=2);
		top3.setGraphic(t2);
		Button top4 = new Button();
		top4.setPrefSize(100,150);
		top4.getTransforms().addAll(new Translate(460,400));
		top4.setOnAction(e->top=3);
		top4.setGraphic(t3);
		Label l1 = new Label("1000");
		l1.getTransforms().addAll(new Translate(70,560));
		l1.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20));
		Label l2 = new Label("1500");
		l2.getTransforms().addAll(new Translate(210,560));
		l2.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20));
		Label l3 = new Label("2000");
		l3.getTransforms().addAll(new Translate(350,560));
		l3.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20));
		Label l4 = new Label("2500");
		l4.getTransforms().addAll(new Translate(490,560));
		l4.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20));

		meni.getChildren().addAll(dugme, teren1, teren2, teren3, top1, top2, top3, top4, l1, l2, l3, l4, lb1, lb2, lb3);
		dugme.setOnAction(e->{stage.setScene(scene); addFields(); addBarriers(); addHoles(); player.style(top);});
		return scene1;
	}

	private void addHoles ( ) {
		Translate hole0Position = new Translate(Main.WINDOW_WIDTH / 2, Main.WINDOW_HEIGHT * 0.1);
		Translate hole1Position = new Translate(Main.WINDOW_WIDTH / 2, Main.WINDOW_HEIGHT * 0.4);
		Translate hole2Position = new Translate(Main.WINDOW_WIDTH / 3, Main.WINDOW_HEIGHT * 0.25);
		Translate hole3Position = new Translate(Main.WINDOW_WIDTH * 2 / 3, Main.WINDOW_HEIGHT * 0.25);

		if (teren == 1 || teren == 2){
			hole0Position = new Translate(Main.WINDOW_WIDTH / 2, Main.WINDOW_HEIGHT * 0.1);
			hole1Position = new Translate(Main.WINDOW_WIDTH / 2, Main.WINDOW_HEIGHT * 0.4);
			hole2Position = new Translate(Main.WINDOW_WIDTH / 4, Main.WINDOW_HEIGHT * 0.25);
			hole3Position = new Translate(Main.WINDOW_WIDTH * 3 / 4, Main.WINDOW_HEIGHT * 0.25);
		}

		Hole hole0 = new Hole ( Main.HOLE_RADIUS, hole0Position, Color.ORANGE, 20);
		Hole hole1 = new Hole ( Main.HOLE_RADIUS, hole1Position, Color.LIGHTGREEN, 5 );
		Hole hole2 = new Hole ( Main.HOLE_RADIUS, hole2Position, Color.YELLOW, 10 );
		Hole hole3 = new Hole ( Main.HOLE_RADIUS, hole3Position, Color.YELLOW, 10 );

		this.root.getChildren ( ).addAll ( hole0, hole1, hole2, hole3 );
		this.holes = new Hole[] {hole0, hole1, hole2, hole3,};
	}

	private void addBarriers (){
		Rectangle b1=new Rectangle(WINDOW_WIDTH/5, WINDOW_HEIGHT/2, WINDOW_WIDTH/5,20);
		Rectangle b2=new Rectangle(WINDOW_WIDTH*3/5, WINDOW_HEIGHT/2, WINDOW_WIDTH/5,20);
		Rectangle b3=new Rectangle(WINDOW_WIDTH/2 -10, WINDOW_HEIGHT/7+10, 20,WINDOW_WIDTH/5);

		if (teren == 1){
			b1 = new Rectangle(WINDOW_WIDTH/5, WINDOW_HEIGHT/3, WINDOW_WIDTH/5,20);
			b2 = new Rectangle(WINDOW_WIDTH*3/5, WINDOW_HEIGHT/3, WINDOW_WIDTH/5,20);
			b3 = new Rectangle(WINDOW_WIDTH*2/5, WINDOW_HEIGHT/2, WINDOW_WIDTH/5,20);
		}
		if (teren == 2){
			b1 = new Rectangle(WINDOW_WIDTH/5, WINDOW_HEIGHT/3, WINDOW_WIDTH/5,20);
			b2 = new Rectangle(WINDOW_WIDTH*3/5, WINDOW_HEIGHT/3, WINDOW_WIDTH/5,20);
			b3 = new Rectangle(WINDOW_WIDTH*2/5, WINDOW_HEIGHT/4, WINDOW_WIDTH/5,20);
		}

		b1.setFill(Color.GRAY);
		b2.setFill(Color.GRAY);
		b3.setFill(Color.GRAY);
		this.root.getChildren().addAll(b1, b2, b3);
		this.barriers = new Rectangle[]{b1, b2, b3};
	}

	private void addFields(){
		Rectangle f1=new Rectangle(100, 100, 30,30);
		Rectangle f2=new Rectangle(WINDOW_WIDTH-130, 550, 30,30);
		Rectangle f3=new Rectangle(100, 550, 30,30);
		Rectangle f4=new Rectangle(WINDOW_WIDTH-130, 100, 30,30);

		if (teren == 2){
			f1=new Rectangle(120, 100, 30,30);
			f4=new Rectangle(WINDOW_WIDTH-150, 400, 30,30);
			f3=new Rectangle(120, 400, 30,30);
			f2=new Rectangle(WINDOW_WIDTH-150, 100, 30,30);
		}

		f1.setFill(Color.BROWN);
		f2.setFill(Color.BROWN);
		f3.setFill(Color.LIGHTBLUE);
		f4.setFill(Color.LIGHTBLUE);

		this.root.getChildren().addAll(f1, f2, f3, f4);
		this.fields = new Rectangle[]{f1, f2, f3, f4};
	}

	private void addRemaining(){
		this.remaining = new Circle[Main.ATTEMPTS];
		for (int i=0; i<Main.ATTEMPTS; i++){
			Circle c = new Circle(5,Color.RED);
			c.getTransforms().addAll(
					new Translate(500+i*15,10)
			);
			this.root.getChildren().addAll(c);
			remaining[i]=c;
		}
	}

	private void addTeleport(){
		teleport = new Rectangle[TELEPORT_NUMBER];
		for (int j = 0; j < TELEPORT_NUMBER; j++){
			double x = Math.random() * (600 - 2 * FENCE-20) + FENCE;
			double y = Math.random() * 600  + 100;
			Rectangle r = new Rectangle(x,y,20,5);
			r.setFill(Color.BLACK);
			//r.getTransforms().addAll( new Translate(x,y) );
			teleport[j] = r;
			this.root.getChildren().addAll(r);
		}
	}

	private void addToken(int id){
		double r = Main.BALL_RADIUS * 2;
		double x, y;

		while(true){
			boolean b = true;
			x = Math.random()*(Main.WINDOW_WIDTH - 4*FENCE) + 2*FENCE;
			y = Math.random()*(Main.WINDOW_HEIGHT - 4*FENCE) + 2*FENCE;
			for (int i = 0; i < 3; i++){
				if ((x>barriers[i].getX()-r && x<barriers[i].getX()+barriers[i].getWidth()+r)
						&&(y>barriers[i].getY()-r && y<barriers[i].getY()+barriers[i].getHeight()+r)){
					b = false;
				}
			}
			if (b) break;
		}
		int p = (int)(Math.random()*3);
		Translate t = new Translate(x, y);
		Token tok = new Token(Main.BALL_RADIUS*2, t,2, id);
		token[id] = tok;
		tok.getAnimacija().setOnFinished(e->{token[id]=null;});
		this.root.getChildren().addAll(tok);
	}

	@Override public void start ( Stage stage ) throws IOException {
		this.root  = new Group ( );
		score=0; //na pocetku je skor 0, nije iskoriscen nijedan pokusaj
		pokusaj=0;
		teren = 0; //inicijalno su selektovani prvi teren i prvi top
		top = 0;
		kraj = false;
		numberTokens = 0;
		numberObj = 0;
		token = new Token[Main.NUM_OF_TOKENS];

		//kreiranje glavne scene  za igru
		Image slika = new Image(Main.class.getClassLoader().getResourceAsStream("fence.jpg"));
		ImagePattern pozadina = new ImagePattern(slika);
		Scene scene = new Scene ( this.root, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT, pozadina);

		Image image = new Image(Main.class.getClassLoader().getResourceAsStream("grass.jpg"));
		imageView = new ImageView(image);
		imageView.setX(FENCE); imageView.setY(FENCE);
		imageView.setFitHeight(WINDOW_HEIGHT-3*FENCE);
		imageView.setFitWidth(WINDOW_WIDTH-2*FENCE);
		Group trava = new Group(imageView);

		Translate playerPosition = new Translate (
				Main.WINDOW_WIDTH / 2 - Main.PLAYER_WIDTH / 2,
				Main.WINDOW_HEIGHT - Main.PLAYER_HEIGHT -FENCE
		);
		
		this.player = new Player (Main.PLAYER_WIDTH, Main.PLAYER_HEIGHT, playerPosition);
		this.pritisak = new DuzinaPritiska(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.igra = new DuzinaIgre();

		vreme = new Text();
		vreme.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 25));
		vreme.setX(WINDOW_WIDTH-60); vreme.setY(WINDOW_HEIGHT - 30);
		vreme.setFill(Color.WHITE);
		vreme.setText("A");
		Group vr = new Group(vreme);

		text = new Text();
		text.setText("0"); text.setX(30); text.setY(25);
		text.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 30));
		Group tx = new Group(text);

		//dodajemo pozadinu, igraca, skor, crvenu liniju levo, rupe, barijere, polja i loptice za poteze
		this.root.getChildren ( ).addAll (trava, this.player, this.pritisak , tx, this.igra, vr);
		//this.addHoles ( );
		//this.addBarriers();
		//this.addFields();
		this.addRemaining();
		this.addTeleport();
		
		scene.addEventHandler (
				MouseEvent.MOUSE_MOVED,
				mouseEvent -> this.player.handleMouseMoved (
						mouseEvent,
						Main.PLAYER_MIN_ANGLE_OFFSET,
						Main.PLAYER_MAX_ANGLE_OFFSET
				)
		);

		scene.addEventHandler ( MouseEvent.ANY, this );

		scene.setOnKeyPressed(new EventHandler<KeyEvent>(){
			@Override public void handle(KeyEvent event){
				if(event.getCode()== KeyCode.SPACE){
					root.getChildren( ).remove(ball);
					ball = null;
					if (pokusaj == Main.ATTEMPTS) pokusaj++;
				}
			}
		});

		Timer timer = new Timer (
				deltaNanoseconds -> {
					double deltaSeconds = ( double ) deltaNanoseconds / Main.NS_IN_S;
					int d = GAME_DURATION - ((int) this.igra.getVreme());
					if (d == 0) kraj = true;
					vreme.setText(" "+d);
					if(((int) this.igra.getVreme()) > numberTokens * Main.GAME_DURATION / Main.NUM_OF_TOKENS){//ako je proslo odrednjeno vreme
						this.addToken(numberTokens);
						numberTokens++;
					}
					if(((int) this.igra.getVreme()) > 5+numberObj * Main.GAME_DURATION / (Main.NUM_OF_OBJ+1)){//ako je proslo odrednjeno vreme
						this.obj = new Objekat(3*Main.BALL_RADIUS);
						root.getChildren().addAll(obj);
						obj.getAnimacija().setOnFinished(e->obj = null);
						numberObj++;
					}
					if (obj != null){
						obj.update(deltaSeconds,FENCE, Main.WINDOW_WIDTH-FENCE, FENCE, Main.WINDOW_HEIGHT-2*FENCE);
						if (this.ball != null){
							if (obj.handleCollision(ball)){
								root.getChildren( ).remove(ball);
								ball = null;
								if (pokusaj == Main.ATTEMPTS) pokusaj++;
							}
						}
					}

					if ( this.ball != null ) {
						boolean stopped = this.ball.update ( //unosi se ograda, prepreke, koeficijenti usporenja
								deltaSeconds,
								FENCE, Main.WINDOW_WIDTH-FENCE, FENCE, Main.WINDOW_HEIGHT-2*FENCE,
								Main.BALL_DAMP_FACTOR, Main.MIN_BALL_SPEED,this.barriers,
								this.fields,this.teleport, Main.BALL_DAMP_FACTOR_SU,Main.BALL_DAMP_FACTOR_SD
						);
						for(int j = 0; j < numberTokens; j++){
							if (token[j] != null) {
								if(token[j].handleCollision(ball)){
									if (j%2 == 0){
										score += 2;
										text.setText(" "+score);
									}else{
										igra.vrati(5);
									}
								}
							}
						}
						for (int i = 0; i < 4; i++) { //za sve 4 rupe ispitujemo da li je loptica upala u neku od njih
							if (holes[i].handleCollision(this.ball, Main.MAXIMUM_BALL_SPEED / 2)) {
								score += holes[i].getPoints(); //broj poena se povecava u odnosu na to koja je rupa
								animacija = this.ball.pokreni(); //pokrece se animacija upadanja lopte
								text.setText(" "+score);
								animacija.setOnFinished(e->{
									root.getChildren( ).remove(ball); //kad se zavrsi animacija loptica treba da se ukloni
									ball = null;
									if (pokusaj == Main.ATTEMPTS) pokusaj++;
								});
								animacija.playFromStart();//pokrecemo animaciju i cekamo 2s da se zavrsi
							}
						}
						if ( stopped || kraj ) { //stoped je true ako je brzina manja od minimalne
							this.root.getChildren( ).remove(this.ball);
							this.ball = null;
							if (pokusaj == Main.ATTEMPTS) pokusaj++;
							if (kraj){
								Text krajTX = new Text("VREME JE ISTEKLO!");
								krajTX.setX(140); krajTX.setY(400);
								krajTX.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 30));
								krajTX.setFill(Color.RED);
								this.root.getChildren().addAll(krajTX);
							}
						}
					}
					if (pokusaj > Main.ATTEMPTS){
						Text krajTX = new Text("NEMATE VISE POKUSAJA!");
						krajTX.setX(130); krajTX.setY(400);
						krajTX.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 30));
						krajTX.setFill(Color.RED);
						this.root.getChildren().addAll(krajTX);
						igra.zaustavi();
					}
				}
		);
		timer.start ( );

		//pravimo pocetni meni i on se prvo pojavljuje
		Scene scene1 = this.pocetniMeni(stage, scene, pozadina);
		scene.setCursor ( Cursor.NONE );
		stage.setTitle ( "Golfer" );
		stage.setResizable ( false );
		stage.setScene ( scene1);
		stage.show ( );
		stage.setX(500);
		stage.setY(0);
	}
	
	public static void main ( String[] args ) {
		launch ( );
	}
	
	@Override public void handle ( MouseEvent mouseEvent ) {

		if ( mouseEvent.getEventType ( ).equals ( MouseEvent.MOUSE_PRESSED ) && mouseEvent.isPrimaryButtonDown ( )
				&& pokusaj < Main.ATTEMPTS && this.ball == null && !kraj) {

			this.time = System.currentTimeMillis ( );
			if (pokusaj == 0) igra.pokreni(); //da pocne da odbrojava vreme
			this.root.getChildren( ).remove(this.remaining[pokusaj]); //pri svakom pucanju se broj loptica smanji za 1
			this.pokusaj++;
			if (this.ball == null) this.pritisak.pokreni(); //ona crvena linija sa strane

		} else if ( mouseEvent.getEventType ( ).equals ( MouseEvent.MOUSE_RELEASED) ) {
			if ( this.time != - 1 && this.ball == null ) {
				double value        = ( System.currentTimeMillis ( ) - this.time ) / Main.MS_IN_S;
				double deltaSeconds = Utilities.clamp ( value, 0, Main.MAXIMUM_HOLD_IN_S );
				
				double ballSpeedFactor = deltaSeconds / Main.MAXIMUM_HOLD_IN_S * player.maxSpeed();
				
				Translate ballPosition = this.player.getBallPosition ( );
				Point2D   ballSpeed    = this.player.getSpeed ( ).multiply ( ballSpeedFactor );
				
				this.ball = new Ball ( Main.BALL_RADIUS, ballPosition, ballSpeed );
				this.root.getChildren ( ).addAll ( this.ball );

				this.pritisak.restart(); //prvo se restartuje da bi nestala crvena linija pritiskom na dugme
				this.pritisak.zaustavi(); //a onda se zaustavlja animacija
			}
			this.time = -1;
		}
	}

}