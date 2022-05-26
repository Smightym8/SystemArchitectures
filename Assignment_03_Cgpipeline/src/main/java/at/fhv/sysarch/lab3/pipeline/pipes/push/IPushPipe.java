package at.fhv.sysarch.lab3.pipeline.pipes.push;

import at.fhv.sysarch.lab3.pipeline.filters.push.IPushFilter;

public interface IPushPipe<I, O> {
    void setFilterSuccessor(IPushFilter<I, ?> pushFilter);

    void push(O data);
}
