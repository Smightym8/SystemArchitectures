package at.fhv.sysarch.lab3.pipeline.filters.pull;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.pipes.pull.IPullPipe;

public class BackFaceCullingFilter<I extends Face, O extends Face> implements IPullFilter<Face, Face> {
    private IPullPipe<Face, Face> pipePredecessor;
    private Face storedFace = null;
    @Override
    public void setPipePredecessor(IPullPipe<Face, Face> pipePredecessor) {
        this.pipePredecessor = pipePredecessor;
    }

    @Override
    public Face pull() {
        prepareNext();
        Face face = storedFace;
        storedFace = null;

        return face;
    }

    @Override
    public boolean hasNext() {
        prepareNext();
        return storedFace != null;
    }

    private void prepareNext() {
        while(pipePredecessor.hasNext() && storedFace == null) {
            Face face = pipePredecessor.pull();

            if(face.getV1().dot(face.getN1()) < 0) {
                storedFace = face;
            }
        }
    }
}
