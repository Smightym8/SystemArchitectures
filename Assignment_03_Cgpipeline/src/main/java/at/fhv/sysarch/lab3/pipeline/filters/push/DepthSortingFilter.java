package at.fhv.sysarch.lab3.pipeline.filters.push;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.pipes.push.IPushPipe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DepthSortingFilter<I extends Face, O extends Face> implements IPushFilter<Face, Face> {
    private IPushPipe<Face, Face> pipeSuccessor;
    private List<Face> faces = new ArrayList<>();

    @Override
    public void setPipeSuccessor(IPushPipe<Face, Face> pipeSuccessor) {
        this.pipeSuccessor = pipeSuccessor;
    }

    @Override
    public void push(Face data) {
        if(data == null) {
            faces.sort(Comparator.comparing(face -> face.getV1().getZ() + face.getV2().getZ() + face.getV3().getZ()));
            faces.forEach(face -> pipeSuccessor.push(face));
            faces.clear();
        } else {
            faces.add(data);
        }
    }
}
