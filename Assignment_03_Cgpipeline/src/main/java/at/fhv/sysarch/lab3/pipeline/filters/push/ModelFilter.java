package at.fhv.sysarch.lab3.pipeline.filters.push;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.pipes.push.IPushPipe;

public class ModelFilter<T extends Face> implements IPushFilter<T> {
    IPushPipe<T> pushPipe;

    @Override
    public void setPipeSuccessor(IPushPipe<T> pipe) {
        this.pushPipe = pipe;
    }

    @Override
    public void write(T input) {
        // TODO: Implement
    }
}
