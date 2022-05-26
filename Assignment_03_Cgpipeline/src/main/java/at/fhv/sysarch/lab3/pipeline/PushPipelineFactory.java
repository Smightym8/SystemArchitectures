package at.fhv.sysarch.lab3.pipeline;

import at.fhv.sysarch.lab3.animation.AnimationRenderer;
import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.obj.Model;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.filters.push.*;
import at.fhv.sysarch.lab3.pipeline.pipes.push.PushPipe;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;

public class PushPipelineFactory {
    public static AnimationTimer createPipeline(PipelineData pd) {
        // push from the source (model)
        Source<Face, Face> source = new Source<>();
        PushPipe<Face> sourcePipe = new PushPipe<>();
        source.setPipeSuccessor(sourcePipe);


        // perform model-view transformation from model to VIEW SPACE coordinates
        ModelViewTransformationFilter<Face, Face> modelViewTransformationFilter = new ModelViewTransformationFilter<>();
        sourcePipe.setFilterSuccessor(modelViewTransformationFilter);
        PushPipe<Face> modelViewTransformationPipe = new PushPipe<>();
        modelViewTransformationFilter.setPipeSuccessor(modelViewTransformationPipe);

        // perform backface culling in VIEW SPACE
        BackFaceCullingFilter<Face, Face> backFaceCullingFilter = new BackFaceCullingFilter<>();
        modelViewTransformationPipe.setFilterSuccessor(backFaceCullingFilter);
        PushPipe<Face> backFaceCullingPipe = new PushPipe<>();
        backFaceCullingFilter.setPipeSuccessor(backFaceCullingPipe);

        // TODO 3. perform depth sorting in VIEW SPACE

        // add coloring (space unimportant)
        ColoringFilter<Face, Pair<Face, Color>> coloringFilter = new ColoringFilter<>();
        coloringFilter.setPipelineData(pd);
        backFaceCullingPipe.setFilterSuccessor(coloringFilter);
        PushPipe<Pair<Face, Color>> coloringPipe = new PushPipe<>();
        coloringFilter.setPipeSuccessor(coloringPipe);

        PushPipe<Pair<Face, Color>> projectionTransformationPipe = new PushPipe<>();
        // lighting can be switched on/off
        if (pd.isPerformLighting()) {
            // 4a. perform lighting in VIEW SPACE
            LightingFilter<Pair<Face, Color>, Pair<Face, Color>> lightingFilter = new LightingFilter<>();
            lightingFilter.setPipelineData(pd);
            coloringPipe.setFilterSuccessor(lightingFilter);
            PushPipe<Pair<Face, Color>> lightingPipe = new PushPipe<>();
            lightingFilter.setPipeSuccessor(lightingPipe);
            
            // 5. perform projection transformation on VIEW SPACE coordinates
            ProjectionTransformationFilter<Pair<Face, Color>, Pair<Face, Color>> projectionTransformationFilter = new ProjectionTransformationFilter<>();
            projectionTransformationFilter.setPipelineData(pd);
            lightingPipe.setFilterSuccessor(projectionTransformationFilter);
        } else {
            // 5. perform projection transformation
            ProjectionTransformationFilter<Pair<Face, Color>, Pair<Face, Color>> projectionTransformationFilter = new ProjectionTransformationFilter<>();
            coloringPipe.setFilterSuccessor(projectionTransformationFilter);
            projectionTransformationFilter.setPipelineData(pd);

        }

        // perform perspective division to screen coordinates
        PerspectiveDivisionFilter<Pair<Face, Color>, Pair<Face, Color>> perspectiveDivisionFilter = new PerspectiveDivisionFilter<>();
        perspectiveDivisionFilter.setPipelineData(pd);
        projectionTransformationPipe.setFilterSuccessor(perspectiveDivisionFilter);
        PushPipe<Pair<Face, Color>> perspectiveDivisionPipe = new PushPipe<>();
        perspectiveDivisionFilter.setPipeSuccessor(perspectiveDivisionPipe);

        // feed into the sink (renderer)
        Sink<Pair<Face, Color>, ?> sink = new Sink<>();
        sink.setPipelineData(pd);
        perspectiveDivisionPipe.setFilterSuccessor(sink);

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
                float radian = (float) (totalRotation % (2 * Math.PI));

                // create new model rotation matrix using pd.getModelRotAxis and Matrices.rotate
                Mat4 rotationMatrix = Matrices.rotate(radian, pd.getModelRotAxis());

                // compute updated model-view transformation
                Mat4 modelToViewMatrix = pd.getViewTransform().multiply(rotationMatrix).multiply(pd.getModelTranslation());

                // update model-view filter
                modelViewTransformationFilter.setModelViewMatrix(modelToViewMatrix);

                // trigger rendering of the pipeline
                source.setModel(model);
                source.start();
            }
        };
    }
}