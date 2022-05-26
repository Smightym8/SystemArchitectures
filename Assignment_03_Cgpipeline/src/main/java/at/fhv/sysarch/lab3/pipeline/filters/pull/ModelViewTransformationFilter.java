package at.fhv.sysarch.lab3.pipeline.filters.pull;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.pipes.pull.IPullPipe;
import com.hackoeur.jglm.Mat4;

public class ModelViewTransformationFilter<I extends Face, O extends Face> implements IPullFilter<Face, Face> {
    private IPullPipe<Face, Face> pipePredecessor;
    private Mat4 modelViewMatrix;

    @Override
    public void setPipePredecessor(IPullPipe<Face, Face> pipePredecessor) {
        this.pipePredecessor = pipePredecessor;
    }

    @Override
    public Face pull() {
        Face face = pipePredecessor.pull();

        return new Face(
                modelViewMatrix.multiply(face.getV1()),
                modelViewMatrix.multiply(face.getV2()),
                modelViewMatrix.multiply(face.getV3()),
                modelViewMatrix.multiply(face.getN1()),
                modelViewMatrix.multiply(face.getN2()),
                modelViewMatrix.multiply(face.getN3())
        );
    }

    @Override
    public boolean hasNext() {
        return pipePredecessor.hasNext();
    }

    public void setModelViewMatrix(Mat4 modelViewMatrix) {
        this.modelViewMatrix = modelViewMatrix;
    }
}
