package at.fhv.sysarch.lab3.pipeline.filters.push;

import at.fhv.sysarch.lab3.pipeline.pipes.push.IPushPipe;

public interface IPushFilter<I, O> {
    void setPipeSuccessor(IPushPipe<O, O> pipeSuccessor);

    void push(I data);
}
