package at.fhv.sysarch.lab3.pipeline;

import at.fhv.sysarch.lab3.animation.AnimationRenderer;
import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.obj.Model;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.filters.pull.*;
import at.fhv.sysarch.lab3.pipeline.pipes.pull.PullPipe;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;

public class PullPipelineFactory {
    public static AnimationTimer createPipeline(PipelineData pd) {
        // pull from the source (model)
        Source<?, Face> source = new Source<>();
        PullPipe<Face> sourcePipe = new PullPipe<>();
        sourcePipe.setPredecessor(source);

        // perform model-view transformation from model to VIEW SPACE coordinates
        ModelViewTransformationFilter<Face, Face> modelViewTransformationFilter = new ModelViewTransformationFilter<>();
        modelViewTransformationFilter.setPipePredecessor(sourcePipe);
        PullPipe<Face> modelViewPipe = new PullPipe<>();
        modelViewPipe.setPredecessor(modelViewTransformationFilter);

        // perform backface culling in VIEW SPACE
        BackFaceCullingFilter<Face, Face> backFaceCullingFilter = new BackFaceCullingFilter<>();
        backFaceCullingFilter.setPipePredecessor(modelViewPipe);
        PullPipe<Face> backFaceCullingPipe = new PullPipe<>();
        backFaceCullingPipe.setPredecessor(backFaceCullingFilter);

        // perform depth sorting in VIEW SPACE
        DepthSortingFilter<Face, Face> depthSortingFilter = new DepthSortingFilter<>();
        depthSortingFilter.setPipePredecessor(backFaceCullingPipe);
        PullPipe<Face> depthSortingPipe = new PullPipe<>();
        depthSortingPipe.setPredecessor(depthSortingFilter);

        // add coloring (space unimportant)
        ColoringFilter<Face, Pair<Face, Color>> coloringFilter = new ColoringFilter<>();
        coloringFilter.setPipelineData(pd);
        coloringFilter.setPipePredecessor(depthSortingPipe);
        PullPipe<Pair<Face, Color>> coloringPipe = new PullPipe<>();
        coloringPipe.setPredecessor(coloringFilter);

        ProjectionTransformationFilter<Pair<Face, Color>, Pair<Face, Color>> projectionTransformationFilter = new ProjectionTransformationFilter<>();
        // lighting can be switched on/off
        if (pd.isPerformLighting()) {
            // perform lighting in VIEW SPACE
            LightingFilter<Pair<Face, Color>, Pair<Face, Color>> lightingFilter = new LightingFilter<>();
            lightingFilter.setPipePredecessor(coloringPipe);
            lightingFilter.setPipelineData(pd);
            PullPipe<Pair<Face, Color>> lightingPipe = new PullPipe<>();
            lightingPipe.setPredecessor(lightingFilter);
            
            // perform projection transformation on VIEW SPACE coordinates
            projectionTransformationFilter.setPipePredecessor(lightingPipe);
            projectionTransformationFilter.setPipelineData(pd);
        } else {
            // perform projection transformation
            projectionTransformationFilter.setPipePredecessor(coloringPipe);
            projectionTransformationFilter.setPipelineData(pd);
        }

        PullPipe<Pair<Face, Color>> projectionPipe = new PullPipe<>();
        projectionPipe.setPredecessor(projectionTransformationFilter);

        // perform perspective division to screen coordinates
        PerspectiveDivisionFilter<Pair<Face, Color>, Pair<Face, Color>> perspectiveDivisionFilter = new PerspectiveDivisionFilter<>();
        perspectiveDivisionFilter.setPipePredecessor(projectionPipe);
        perspectiveDivisionFilter.setPipelineData(pd);
        PullPipe<Pair<Face, Color>> perspectivePipe = new PullPipe<>();
        perspectivePipe.setPredecessor(perspectiveDivisionFilter);

        // feed into the sink (renderer)
        Sink<Pair<Face, Color>, ?> sink = new Sink<>();
        sink.setPipePredecessor(perspectivePipe);
        sink.setPipelineData(pd);

        // returning an animation renderer which handles clearing of the
        // viewport and computation of the fraction
        return new AnimationRenderer(pd) {
            // rotation variable goes in here
            float totalRotation = 0f;

            /** This method is called for every frame from the JavaFX Animation
             * system (using an AnimationTimer, see AnimationRenderer). 
             * @param fraction the time which has passed since the last render call in a fraction of a second
             * @param model    the model to render 
             */
            @Override
            protected void render(float fraction, Model model) {
                // compute rotation in radians
                totalRotation = totalRotation + fraction;
                float radian = (float) Math.toRadians(totalRotation);

                // create new model rotation matrix using pd.getModelRotAxis and Matrices.rotate
                Mat4 rotationMatrix = Matrices.rotate(radian, pd.getModelRotAxis());

                // compute updated model-view transformation
                Mat4 modelToViewMatrix = pd.getViewTransform().multiply(rotationMatrix).multiply(pd.getModelTranslation());

                // update model-view filter
                modelViewTransformationFilter.setModelViewMatrix(modelToViewMatrix);

                // trigger rendering of the pipeline
                source.setModel(pd.getModel());
                sink.start();
            }
        };
    }
}