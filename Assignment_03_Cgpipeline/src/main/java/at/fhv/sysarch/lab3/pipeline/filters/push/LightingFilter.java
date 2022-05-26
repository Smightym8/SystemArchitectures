package at.fhv.sysarch.lab3.pipeline.filters.push;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.PipelineData;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pipes.push.IPushPipe;
import javafx.scene.paint.Color;

public class LightingFilter<I extends Pair<Face, Color>, O extends Pair<Face, Color>> implements IPushFilter<Pair<Face, Color>, Pair<Face, Color>> {
    private IPushPipe<Pair<Face, Color>, Pair<Face, Color>> pipeSuccessor;
    private PipelineData pd;

    @Override
    public void setPipeSuccessor(IPushPipe<Pair<Face, Color>, Pair<Face, Color>> pipeSuccessor) {
        this.pipeSuccessor = pipeSuccessor;
    }

    @Override
    public void push(Pair<Face, Color> data) {
        Face face = data.fst();
        Color color = data.snd();

        float lightModifier = face.getN1().toVec3().getUnitVector().dot(pd.getLightPos().getUnitVector());

        pipeSuccessor.push(new Pair<>(face, color.deriveColor(0, 1, lightModifier, 1)));
    }

    public void setPipelineData(PipelineData pd) {
        this.pd = pd;
    }
}
