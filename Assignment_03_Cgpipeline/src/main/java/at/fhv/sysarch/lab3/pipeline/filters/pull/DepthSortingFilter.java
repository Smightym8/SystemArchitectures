package at.fhv.sysarch.lab3.pipeline.filters.pull;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.pipes.pull.IPullPipe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DepthSortingFilter<I extends Face, O extends Face> implements IPullFilter<Face, Face>{
    private IPullPipe<Face, Face> pipePredecessor;
    private List<Face> sortedFaces = new ArrayList<>();

    @Override
    public void setPipePredecessor(IPullPipe<Face, Face> pipePredecessor) {
        this.pipePredecessor = pipePredecessor;
    }

    @Override
    public Face pull() {
        // Pull all faces
        while (pipePredecessor.hasNext()) {
            sortedFaces.add(pipePredecessor.pull());
        }

        // Sort faces
        sortedFaces.sort(Comparator.comparing(face -> face.getV1().getZ() + face.getV2().getZ() + face.getV3().getZ()));

        return sortedFaces.isEmpty() ? null : sortedFaces.remove(0);
    }

    @Override
    public boolean hasNext() {
        return !sortedFaces.isEmpty() || pipePredecessor.hasNext();
    }
}
