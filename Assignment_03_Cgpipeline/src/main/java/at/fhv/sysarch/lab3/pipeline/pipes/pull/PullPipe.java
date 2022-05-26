package at.fhv.sysarch.lab3.pipeline.pipes.pull;

import at.fhv.sysarch.lab3.pipeline.filters.pull.IPullFilter;

public class PullPipe<T> implements IPullPipe<T, T>{
    private IPullFilter<?, T> predecessor;

    @Override
    public void setPredecessor(IPullFilter<?, T> predecessor) {
        this.predecessor = predecessor;
    }

    @Override
    public T pull() {
        return predecessor.pull();
    }

    @Override
    public boolean hasNext() {
        return predecessor.hasNext();
    }
}
