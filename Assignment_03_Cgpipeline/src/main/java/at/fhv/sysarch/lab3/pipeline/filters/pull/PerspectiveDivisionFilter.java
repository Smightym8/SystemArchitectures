package at.fhv.sysarch.lab3.pipeline.filters.pull;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.PipelineData;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pipes.pull.IPullPipe;
import javafx.scene.paint.Color;

public class PerspectiveDivisionFilter<I extends Pair<Face, Color>, O extends Pair<Face, Color>> implements IPullFilter<Pair<Face, Color>, Pair<Face, Color>>{
    private IPullPipe<Pair<Face, Color>, Pair<Face, Color>> pipePredecessor;
    private PipelineData pd;

    @Override
    public void setPipePredecessor(IPullPipe<Pair<Face, Color>, Pair<Face, Color>> pipePredecessor) {
        this.pipePredecessor = pipePredecessor;
    }

    @Override
    public Pair<Face, Color> pull() {
        Pair<Face, Color> faceColorPair = pipePredecessor.pull();

        Face oldFace = faceColorPair.fst();

        Face newFace = new Face(
                pd.getViewportTransform().multiply(oldFace.getV1().multiply(1 / oldFace.getV1().getW())),
                pd.getViewportTransform().multiply(oldFace.getV2().multiply(1 / oldFace.getV2().getW())),
                pd.getViewportTransform().multiply(oldFace.getV3().multiply(1 / oldFace.getV3().getW())),
                oldFace
        );

        return new Pair<>(newFace, faceColorPair.snd());
    }

    @Override
    public boolean hasNext() {
        return pipePredecessor.hasNext();
    }

    public void setPipelineData(PipelineData pd) {
        this.pd = pd;
    }
}
