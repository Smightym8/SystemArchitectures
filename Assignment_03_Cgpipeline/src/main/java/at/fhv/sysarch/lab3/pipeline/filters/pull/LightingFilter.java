package at.fhv.sysarch.lab3.pipeline.filters.pull;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.PipelineData;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pipes.pull.IPullPipe;
import javafx.scene.paint.Color;

public class LightingFilter<I extends Pair<Face, Color>, O extends Pair<Face, Color>> implements IPullFilter<Pair<Face, Color>, Pair<Face, Color>> {
    private IPullPipe<Pair<Face, Color>, Pair<Face, Color>> pipePredecessor;
    private PipelineData pd;
    @Override
    public void setPipePredecessor(IPullPipe<Pair<Face, Color>, Pair<Face, Color>> pipePredecessor) {
        this.pipePredecessor = pipePredecessor;
    }

    @Override
    public Pair<Face, Color> pull() {
        Pair<Face, Color> faceColorPair = pipePredecessor.pull();
        Face face = faceColorPair.fst();
        Color color = faceColorPair.snd();

        float lightModifier = face.getN1().toVec3().getUnitVector().dot(pd.getLightPos().getUnitVector());

        return new Pair<>(face, color.deriveColor(0, 1, lightModifier, 1));
    }

    @Override
    public boolean hasNext() {
        return pipePredecessor.hasNext();
    }

    public void setPipelineData(PipelineData pd) {
        this.pd = pd;
    }
}
