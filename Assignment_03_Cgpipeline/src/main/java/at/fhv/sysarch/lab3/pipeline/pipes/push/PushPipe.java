package at.fhv.sysarch.lab3.pipeline.pipes.push;

import at.fhv.sysarch.lab3.pipeline.filters.push.IPushFilter;

public class PushPipe<I> implements IPushPipe<I, I>{
    private IPushFilter<I, ?> pushFilter;
    @Override
    public void setFilterSuccessor(IPushFilter<I, ?> pushFilter) {
        this.pushFilter = pushFilter;
    }

    @Override
    public void push(I data) {
        pushFilter.push(data);
    }
}
