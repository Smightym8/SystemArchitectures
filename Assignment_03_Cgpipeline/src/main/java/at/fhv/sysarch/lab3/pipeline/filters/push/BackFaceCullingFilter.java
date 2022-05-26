package at.fhv.sysarch.lab3.pipeline.filters.push;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.pipes.push.IPushPipe;

public class BackFaceCullingFilter<I extends Face, O extends  Face> implements IPushFilter<Face, Face> {
    private IPushPipe<Face, Face> pipeSuccessor;

    @Override
    public void setPipeSuccessor(IPushPipe<Face, Face> pipeSuccessor) {
        this.pipeSuccessor = pipeSuccessor;
    }

    @Override
    public void push(Face data) {
        if(data.getV1().dot(data.getN1()) < 0) {
            pipeSuccessor.push(data);
        }
    }
}
