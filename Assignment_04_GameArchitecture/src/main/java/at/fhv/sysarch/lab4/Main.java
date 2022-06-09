package at.fhv.sysarch.lab4;

import at.fhv.sysarch.lab4.game.Game;
import at.fhv.sysarch.lab4.physics.Physics;
import at.fhv.sysarch.lab4.rendering.Renderer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {
    private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final static int SCENE_WIDTH  = screenSize.width;
    private final static int SCENE_HEIGHT = screenSize.height;
    @Override
    public void start(Stage stage) {
        final Group root = new Group();
        final Scene s = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT, Color.BURLYWOOD);
        final Canvas c = new Canvas(SCENE_WIDTH, SCENE_HEIGHT);

        Physics physics = new Physics();

        Renderer renderer = new Renderer(
            c.getGraphicsContext2D(),
            SCENE_WIDTH, 
            SCENE_HEIGHT, physics);
        
        Game game = new Game(renderer, physics);

        c.setOnMousePressed(game::onMousePressed);
        c.setOnMouseReleased(game::onMouseReleased);
        c.setOnMouseDragged(game::setOnMouseDragged);

        root.getChildren().add(c);
        stage.setScene(s);
        stage.setTitle("Billiard Game");
        stage.show();

        renderer.start();
    }

    public static void main(String[] args) {
        launch();
    }
}