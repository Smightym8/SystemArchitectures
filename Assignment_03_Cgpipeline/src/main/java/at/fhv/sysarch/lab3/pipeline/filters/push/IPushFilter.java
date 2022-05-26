package at.fhv.sysarch.lab3.pipeline.filters.push;

import at.fhv.sysarch.lab3.pipeline.pipes.push.IPushPipe;

public interface IPushFilter<T> {
    void setPipeSuccessor(IPushPipe<T> pipe);
    void write(T input);
}
