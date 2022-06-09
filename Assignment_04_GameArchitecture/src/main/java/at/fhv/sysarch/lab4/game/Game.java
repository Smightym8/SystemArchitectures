package at.fhv.sysarch.lab4.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.fhv.sysarch.lab4.physics.BallPocketedListener;
import at.fhv.sysarch.lab4.physics.BallsCollisionListener;
import at.fhv.sysarch.lab4.physics.ObjectsRestListener;
import at.fhv.sysarch.lab4.physics.Physics;
import at.fhv.sysarch.lab4.rendering.Renderer;
import javafx.scene.input.MouseEvent;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.RaycastResult;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;

public class Game implements BallsCollisionListener, BallPocketedListener, ObjectsRestListener {
    private final Renderer renderer;
    private final Physics physics;

    List<Ball> balls;
    List<Ball> pocketedBalls;

    private Vector2 cueStart;
    private Vector2 cueEnd;

    private int playerOneScore;
    private int playerTwoScore;
    private boolean isPlayerOneTurn;

    private boolean isFoulOccured;
    private boolean isBallShooted;
    private boolean isWhiteBallPocketed;
    private boolean isWhiteBallCollided;
    private boolean switchPlayer;

    public Game(Renderer renderer, Physics physics) {
        this.renderer = renderer;
        this.physics = physics;
        this.initWorld();
        this.initGameState();
    }

    private void initWorld() {
         balls = new ArrayList<>();
         pocketedBalls = new ArrayList<>();

        for (Ball b : Ball.values()) {
            if (b == Ball.WHITE)
                continue;

            balls.add(b);
        }

        this.placeBalls(balls);

        Ball.WHITE.setPosition(Table.Constants.WIDTH * 0.25, 0);
        physics.getWorld().addBody(Ball.WHITE.getBody());
        renderer.addBall(Ball.WHITE);

        Table table = new Table();
        physics.getWorld().addBody(table.getBody());
        renderer.setTable(table);

        this.physics.setBallPocketedListener(this);
        this.physics.setBallsCollisionListener(this);
        this.physics.setObjectsRestListener(this);

        this.cueStart = new Vector2();
        this.cueEnd = new Vector2();
    }

    private void initGameState() {
        this.playerOneScore = 0;
        this.playerTwoScore = 0;
        this.isPlayerOneTurn = true;
        this.isFoulOccured = false;
        this.isBallShooted = false;
        this.isWhiteBallCollided = false;
        this.switchPlayer = true;

        updateStrikeMessage();
    }

    private void placeBalls(List<Ball> balls) {
        Collections.shuffle(balls);

        // positioning the billard balls IN WORLD COORDINATES: meters
        int row = 0;
        int col = 0;
        int colSize = 5;

        double y0 = -2*Ball.Constants.RADIUS*2;
        double x0 = -Table.Constants.WIDTH * 0.25 - Ball.Constants.RADIUS;

        for (Ball b : balls) {
            double y = y0 + (2 * Ball.Constants.RADIUS * row) + (col * Ball.Constants.RADIUS);
            double x = x0 + (2 * Ball.Constants.RADIUS * col);

            b.setPosition(x, y);
            b.getBody().setLinearVelocity(0, 0);
            renderer.addBall(b);
            physics.getWorld().addBody(b.getBody());

            row++;

            if (row == colSize) {
                row = 0;
                col++;
                colSize--;
            }
        }
    }

    public void onMousePressed(MouseEvent e) {
        if(!isBallShooted) {
            // Remove messages when next ball is being hit
            this.renderer.setFoulMessage("");

            double x = e.getX();
            double y = e.getY();

            double pX = this.renderer.screenToPhysicsX(x);
            double pY = this.renderer.screenToPhysicsY(y);

            cueStart.x = pX;
            cueStart.y = pY;
            this.renderer.setCueStart(x, y);
            this.renderer.setCueEnd(x, y);
            this.renderer.setCueCoordinatesPresent(true);
        }
    }

    public void onMouseReleased(MouseEvent e) {
        if(!isBallShooted) {
            double x = e.getX();
            double y = e.getY();

            double pX = this.renderer.screenToPhysicsX(x);
            double pY = this.renderer.screenToPhysicsY(y);

            this.cueEnd.x = pX;
            this.cueEnd.y = pY;

            Vector2 direction = new Vector2(cueStart.x - cueEnd.x, cueStart.y - cueEnd.y);

            // TODO: Case if direction is zero

            Ray ray = new Ray(cueStart, direction);
            ArrayList<RaycastResult> results = new ArrayList<>();
            boolean result = this.physics.getWorld().raycast(ray, 1.0, false, false, results);

            if(result) {
                if(results.get(0).getBody().getUserData() instanceof Ball) {

                    if (!((Ball) results.get(0).getBody().getUserData()).isWhite()) {
                        this.renderer.setFoulMessage("Wrong ball hit!");
                        isFoulOccured = true;
                    }
                    this.renderer.setStrikeMessage("");
                    this.renderer.setActionMessage("Balls are moving...");
                    isBallShooted = true;
                    // TODO: Calculate a good speed
                    results.get(0).getBody().applyForce(direction.multiply(700));
                }
            }

            // When mouse is released cue is also released
            cueStart.x = 0;
            cueStart.y = 0;
            cueEnd.x = 0;
            cueEnd.y = 0;
            this.renderer.releaseCue();
            this.renderer.setCueCoordinatesPresent(false);
        }
    }

    public void setOnMouseDragged(MouseEvent e) {
        if(!isBallShooted) {
            double x = e.getX();
            double y = e.getY();

            this.renderer.setCueEnd(x, y);
        }
    }

    @Override
    public void onBallsCollide(Ball b1, Ball b2) {
        if(b1.isWhite() || b2.isWhite()) {
            isWhiteBallCollided = true;
        }
    }

    @Override
    public boolean onBallPocketed(Ball b) {
        if (b.isWhite()) {
            isFoulOccured = true;
            this.renderer.setFoulMessage("Foul! White Ball pocketed!");
            isWhiteBallPocketed = true;
        } else {
            switchPlayer = false;
            updatePlayerScores(1);
            pocketedBalls.add(b);
        }

        this.physics.getWorld().removeBody(b.getBody());
        this.renderer.removeBall(b);

        return false;
    }

    @Override
    public void onEndAllObjectsRest() {
        if(isBallShooted) {
            // TODO: Check that game ended and let it start again
            if(balls.size() == pocketedBalls.size()) {
                String winnerMessage = "Player One is the winner!";

                if(playerTwoScore > playerOneScore) {
                    winnerMessage = "Player Two is the winner!";
                } else if (playerTwoScore == playerOneScore) {
                    winnerMessage = "Draw!";
                }

                this.renderer.setActionMessage(winnerMessage);

                placeBalls(this.balls);
                resetWhiteBallPosition();
                initGameState();
            } else {
                this.renderer.setActionMessage("");
                isBallShooted = false;

                if(!isWhiteBallCollided) {
                    this.renderer.setFoulMessage("White ball didn't hit any other ball!");
                    isFoulOccured = true;
                }

                if(isFoulOccured) {
                    isFoulOccured = false;
                    switchPlayer = true;
                    updatePlayerScores(- 1);
                }

                if(isWhiteBallPocketed) {
                    resetWhiteBallPosition();
                    isWhiteBallPocketed = false;
                }

                if(switchPlayer) {
                    changePlayer();
                }

                updateStrikeMessage();

                switchPlayer = true;
                isWhiteBallCollided = false;
            }
        }
    }

    @Override
    public void onStartAllObjectsRest() {

    }

    private void updatePlayerScores(int points) {
        if(isPlayerOneTurn) {
            playerOneScore += points;
        } else {
            playerTwoScore += points;
        }

        this.renderer.setPlayer1Score(playerOneScore);
        this.renderer.setPlayer2Score(playerTwoScore);
    }

    private void changePlayer() {
        isPlayerOneTurn = !isPlayerOneTurn;
        this.renderer.setActionMessage("Switch player");
    }

    private void resetWhiteBallPosition() {
        Ball.WHITE.getBody().setLinearVelocity(0,0);
        Ball.WHITE.setPosition(Table.Constants.WIDTH * 0.25, 0);

        physics.getWorld().addBody(Ball.WHITE.getBody());
        renderer.addBall(Ball.WHITE);
    }

    private void updateStrikeMessage() {
        String message = isPlayerOneTurn ? "Player 1 turn" : "Player 2 turn";
        this.renderer.setStrikeMessage(message);
    }
}