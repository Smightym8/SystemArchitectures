package at.fhv.sysarch.lab3.pipeline.filters.pull;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.obj.Model;
import at.fhv.sysarch.lab3.pipeline.pipes.pull.IPullPipe;

import java.util.ArrayList;
import java.util.List;

public class Source<I, O extends Face> implements IPullFilter<I, Face>{
    private IPullPipe<I, I> pipePredecessor;
    private List<Face> faces;

    public void setModel(Model model) {
        faces =  new ArrayList<>(model.getFaces());
    }

    @Override
    public void setPipePredecessor(IPullPipe<I, I> pipePredecessor) {
       // Source is the first part of the pipeline so it has no predecessor
    }

    @Override
    public Face pull() {
        return !hasNext() ? null : faces.remove(faces.size() - 1);
    }

    @Override
    public boolean hasNext() {
        return !faces.isEmpty();
    }
}
