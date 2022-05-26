package at.fhv.sysarch.lab3.pipeline.filters.pull;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.pipes.pull.IPullPipe;

public class BackFaceCullingFilter<I extends Face, O extends Face> implements IPullFilter<Face, Face> {
    private IPullPipe<Face, Face> pipePredecessor;
    @Override
    public void setPipePredecessor(IPullPipe<Face, Face> pipePredecessor) {
        this.pipePredecessor = pipePredecessor;
    }

    @Override
    public Face pull() {
        if(!hasNext()) {
            return null;
        }

        Face face = pipePredecessor.pull();

        boolean isCullingFace = face.getV1().dot(face.getN1()) > 0;

        return isCullingFace ? pull() : face;
    }

    @Override
    public boolean hasNext() {
        return pipePredecessor.hasNext();
    }
}
