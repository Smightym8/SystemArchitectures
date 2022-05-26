package at.fhv.sysarch.lab3.pipeline.filters.push;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.obj.Model;
import at.fhv.sysarch.lab3.pipeline.pipes.push.IPushPipe;

import java.util.ArrayList;
import java.util.List;

public class Source<I extends Face, O extends Face> implements IPushFilter<Face, Face> {
    private IPushPipe<Face, Face> pipeSuccessor;

    private List<Face> faces;

    @Override
    public void setPipeSuccessor(IPushPipe<Face, Face> pipeSuccessor) {
        this.pipeSuccessor = pipeSuccessor;
    }

    @Override
    public void push(Face data) {
        pipeSuccessor.push(data);
    }

    public void start() {
        faces.forEach(this::push);

        push(null);
    }

    public void setModel(Model model) {
        faces =  new ArrayList<>(model.getFaces());
    }
}
