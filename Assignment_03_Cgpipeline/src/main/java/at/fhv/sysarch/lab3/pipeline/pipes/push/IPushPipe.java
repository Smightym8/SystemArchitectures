package at.fhv.sysarch.lab3.pipeline.pipes.push;

import at.fhv.sysarch.lab3.pipeline.filters.push.IPushFilter;

public interface IPushPipe<T> {
    void setSuccessor(IPushFilter<T> pushFilter);
    void write(T data);
}
