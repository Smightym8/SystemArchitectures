package at.fhv.sysarch.lab3.pipeline.filters.push;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.PipelineData;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pipes.push.IPushPipe;
import javafx.scene.paint.Color;

public class ColoringFilter<I extends Face, O extends Pair<Face, Color>> implements IPushFilter<Face, Pair<Face, Color>> {
    private IPushPipe<Pair<Face, Color>, Pair<Face, Color>> pipeSuccessor;
    private PipelineData pd;

    @Override
    public void setPipeSuccessor(IPushPipe<Pair<Face, Color>, Pair<Face, Color>> pipeSuccessor) {
        this.pipeSuccessor = pipeSuccessor;
    }

    @Override
    public void push(Face data) {
        pipeSuccessor.push(new Pair<>(data, pd.getModelColor()));
    }

    public void setPipelineData(PipelineData pd) {
        this.pd = pd;
    }
}
