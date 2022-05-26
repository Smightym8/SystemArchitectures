package at.fhv.sysarch.lab3.pipeline.filters.push;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.PipelineData;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pipes.push.IPushPipe;
import javafx.scene.paint.Color;

public class ProjectionTransformationFilter<I extends Pair<Face, Color>, O extends Pair<Face, Color>> implements IPushFilter<Pair<Face, Color>, Pair<Face, Color>> {
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
                pd.getProjTransform().multiply(oldFace.getV1()),
                pd.getProjTransform().multiply(oldFace.getV2()),
                pd.getProjTransform().multiply(oldFace.getV3()),
                oldFace
        );

        pipeSuccessor.push(new Pair<>(newFace, data.snd()));
    }

    public void setPipelineData(PipelineData pd) {
        this.pd = pd;
    }
}
