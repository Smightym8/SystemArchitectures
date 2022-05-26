package at.fhv.sysarch.lab3.pipeline.filters.push;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.pipes.push.IPushPipe;
import com.hackoeur.jglm.Mat4;

public class ModelViewTransformationFilter<I extends  Face, O extends Face> implements IPushFilter<Face, Face> {
    private IPushPipe<Face, Face> pipeSuccessor;

    private Mat4 modelViewMatrix;

    @Override
    public void setPipeSuccessor(IPushPipe<Face, Face> pipeSuccessor) {
        this.pipeSuccessor = pipeSuccessor;
    }

    @Override
    public void push(Face data) {
        if(data == null) {
            pipeSuccessor.push(null);
        } else {
            pipeSuccessor.push(
                    new Face(
                            modelViewMatrix.multiply(data.getV1()),
                            modelViewMatrix.multiply(data.getV2()),
                            modelViewMatrix.multiply(data.getV3()),
                            modelViewMatrix.multiply(data.getN1()),
                            modelViewMatrix.multiply(data.getN2()),
                            modelViewMatrix.multiply(data.getN3())
                    )
            );
        }
    }

    public void setModelViewMatrix(Mat4 modelViewMatrix) {
        this.modelViewMatrix = modelViewMatrix;
    }
}
