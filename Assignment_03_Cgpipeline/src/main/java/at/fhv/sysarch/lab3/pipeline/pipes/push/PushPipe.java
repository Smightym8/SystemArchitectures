package at.fhv.sysarch.lab3.pipeline.pipes.push;

import at.fhv.sysarch.lab3.pipeline.filters.push.IPushFilter;

public class PushPipe<T> implements IPushPipe<T> {
    IPushFilter<T> pushFilter;

    @Override
    public void setSuccessor(IPushFilter<T> pushFilter) {
        this.pushFilter = pushFilter;
    }

    @Override
    public void write(T data) {
        this.pushFilter.write(data);
    }
}
