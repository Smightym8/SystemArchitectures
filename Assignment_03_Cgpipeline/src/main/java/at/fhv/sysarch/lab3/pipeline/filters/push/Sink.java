package at.fhv.sysarch.lab3.pipeline.filters.push;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.PipelineData;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pipes.push.IPushPipe;
import at.fhv.sysarch.lab3.rendering.RenderingMode;
import com.hackoeur.jglm.Vec2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Sink <I extends Pair<Face, Color>, O> implements IPushFilter<Pair<Face, Color>, O> {
    private PipelineData pd;
    private GraphicsContext graphicsContext;

    @Override
    public void setPipeSuccessor(IPushPipe<O, O> pipeSuccessor) {
        // Sink is the last part of the pipeline and has no successor
    }

    @Override
    public void push(Pair<Face, Color> data) {
        this.graphicsContext = pd.getGraphicsContext();
        Face face = data.fst();
        Color color = data.snd();

        Vec2 vec2_1 = face.getV1().toScreen();
        Vec2 vec2_2 = face.getV2().toScreen();
        Vec2 vec2_3 = face.getV3().toScreen();

        pd.getGraphicsContext().setFill(color);
        pd.getGraphicsContext().setStroke(color);

        if(pd.getRenderingMode() == RenderingMode.POINT) {
            graphicsContext.fillOval(
                    vec2_1.getX(),
                    vec2_1.getY(),
                    5,
                    5
            );
        } else {
            graphicsContext.strokeLine(
                    vec2_1.getX(), vec2_1.getY(),
                    vec2_2.getX(), vec2_2.getY()
            );

            graphicsContext.strokeLine(
                    vec2_2.getX(), vec2_2.getY(),
                    vec2_3.getX(), vec2_3.getY()
            );

            graphicsContext.strokeLine(
                    vec2_1.getX(), vec2_1.getY(),
                    vec2_3.getX(), vec2_3.getY()
            );

            if(pd.getRenderingMode() == RenderingMode.FILLED) {
                graphicsContext.fillPolygon(
                        new double[]{
                                vec2_1.getX(),
                                vec2_2.getX(),
                                vec2_3.getX()
                        },

                        new double[] {
                                vec2_1.getY(),
                                vec2_2.getY(),
                                vec2_3.getY()
                        },

                        3
                );
            }
        }
    }

    public void setPipelineData(PipelineData pd) {
        this.pd = pd;
    }
}
