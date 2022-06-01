package at.fhv.sysarch.lab3.pipeline.pipes.pull;

import at.fhv.sysarch.lab3.pipeline.filters.pull.IPullFilter;

public interface IPullPipe<I, O> {
    void setPredecessor(IPullFilter<?, O> predecessor);
    I pull();

    boolean hasNext();
}
