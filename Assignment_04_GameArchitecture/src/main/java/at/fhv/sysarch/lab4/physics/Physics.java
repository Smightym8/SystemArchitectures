package at.fhv.sysarch.lab4.physics;

import at.fhv.sysarch.lab4.game.Ball;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Step;
import org.dyn4j.dynamics.StepListener;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.PersistedContactPoint;
import org.dyn4j.dynamics.contact.SolvedContactPoint;

public class Physics implements ContactListener, StepListener {

    private World world;
    private BallPocketedListener ballPocketedListener;
    private BallsCollisionListener ballsCollisionListener;
    private ObjectsRestListener objectsRestListener;

    public Physics() {
        this.world = new World();
        this.world.setGravity(World.ZERO_GRAVITY);
        this.world.addListener(this);
    }

    public void setBallPocketedListener(BallPocketedListener ballPocketedListener) {
        this.ballPocketedListener = ballPocketedListener;
    }

    public void setBallsCollisionListener(BallsCollisionListener ballsCollisionListener) {
        this.ballsCollisionListener = ballsCollisionListener;
    }

    public void setObjectsRestListener(ObjectsRestListener objectsRestListener) {
        this.objectsRestListener = objectsRestListener;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void begin(Step step, World world) {

    }

    @Override
    public void updatePerformed(Step step, World world) {

    }

    @Override
    public void postSolve(Step step, World world) {

    }

    @Override
    public void end(Step step, World world) {
        double velocitySumX = world.getBodies().stream().mapToDouble(body -> body.getLinearVelocity().x).sum();
        double velocitySumY = world.getBodies().stream().mapToDouble(body -> body.getLinearVelocity().y).sum();

        if(velocitySumX == 0 && velocitySumY == 0) {
            objectsRestListener.onEndAllObjectsRest();
        }
    }

    @Override
    public void sensed(ContactPoint point) {

    }

    @Override
    public boolean begin(ContactPoint point) {
        Body bodyOne = point.getBody1();
        Body bodyTwo = point.getBody2();

        if (bodyOne.getUserData() instanceof Ball && bodyTwo.getUserData() instanceof Ball) {
            ballsCollisionListener.onBallsCollide((Ball) bodyOne.getUserData(), (Ball) bodyTwo.getUserData());
        }
        return true;
    }

    @Override
    public void end(ContactPoint point) {

    }

    @Override
    public boolean persist(PersistedContactPoint point) {
        if (point.isSensor()) {
            Body ball = point.getBody1().getUserData() instanceof Ball ? point.getBody1() : point.getBody2();
            Body pocket = point.getBody1().getUserData() instanceof Ball ? point.getBody2() : point.getBody1();

            if (pocket.contains(ball.getWorldCenter())) {
                ballPocketedListener.onBallPocketed((Ball) ball.getUserData());
                System.out.println("Ball -" + ball.getUserData() + "- contact with pocket");
            }
        }
        return true;
    }

    @Override
    public boolean preSolve(ContactPoint point) {
        return true;
    }

    @Override
    public void postSolve(SolvedContactPoint point) {

    }
}
