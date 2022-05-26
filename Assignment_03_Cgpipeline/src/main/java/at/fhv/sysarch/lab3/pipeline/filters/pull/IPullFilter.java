package at.fhv.sysarch.lab3.pipeline.filters.pull;

import at.fhv.sysarch.lab3.pipeline.pipes.pull.IPullPipe;

public interface IPullFilter<I, O> {
    void setPipePredecessor(IPullPipe<I, I> pipePredecessor);
    O pull();
    boolean hasNext();
}
