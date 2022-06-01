package at.fhv.sysarch.lab3.pipeline.filters.push;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.PipelineData;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pipes.push.IPushPipe;
import javafx.scene.paint.Color;

public class PerspectiveDivisionFilter<I extends Pair<Face, Color>, O extends Pair<Face, Color>> implements IPushFilter<Pair<Face, Color>, Pair<Face, Color>> {
    private IPushPipe<Pair<Face, Color>, Pair<Face, Color>> pipeSuccessor;
    private PipelineData pd;

    @Override
    public void setPipeSuccessor(IPushPipe<Pair<Face, Color>, Pair<Face, Color>> pipeSuccessor) {
        this.pipeSuccessor = pipeSuccessor;
    }

    @Override
    public void push(Pair<Face, Color> data) {
        Face oldFace = data.fst();

        Face newFace = new Face(
                pd.getViewportTransform().multiply(oldFace.getV1().multiply(1 / oldFace.getV1().getW())),
                pd.getViewportTransform().multiply(oldFace.getV2().multiply(1 / oldFace.getV2().getW())),
                pd.getViewportTransform().multiply(oldFace.getV3().multiply(1 / oldFace.getV3().getW())),
                oldFace
        );

        pipeSuccessor.push(new Pair<>(newFace, data.snd()));
    }

    public void setPipelineData(PipelineData pd) {
        this.pd = pd;
    }
}
