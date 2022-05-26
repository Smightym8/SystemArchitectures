package at.fhv.sysarch.lab3.pipeline.filters.pull;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.PipelineData;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pipes.pull.IPullPipe;
import javafx.scene.paint.Color;

public class ColoringFilter<I extends Face, O extends Pair<Face, Color>> implements IPullFilter<Face, Pair<Face, Color>> {
    private IPullPipe<Face, Face> pipePredecessor;
    private PipelineData pd;

    @Override
    public void setPipePredecessor(IPullPipe<Face, Face> pipePredecessor) {
        this.pipePredecessor = pipePredecessor;
    }

    @Override
    public Pair<Face, Color> pull() {
        if(!hasNext()) {
            return null;
        }

        Face face = pipePredecessor.pull();
        return new Pair<>(face, pd.getModelColor());
    }

    @Override
    public boolean hasNext() {
        return pipePredecessor.hasNext();
    }

    public void setPipelineData(PipelineData pd) {
        this.pd = pd;
    }
}
