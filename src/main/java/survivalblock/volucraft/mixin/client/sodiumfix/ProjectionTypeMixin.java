package survivalblock.volucraft.mixin.client.sodiumfix;

import com.google.common.primitives.Floats;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.vertex.VertexSorting;
import it.unimi.dsi.fastutil.ints.IntArrays;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ProjectionType.class)
public enum ProjectionTypeMixin {
    VOLUCRAFT_ORTHOGRAPHIC(
            values -> {
                int size = values.size();

                Vector3f scratch = new Vector3f();
                float[] keys = new float[values.size()];
                int[] indices = new int[values.size()];

                for(int i = 0; i < values.size(); indices[i] = i++) {
                    keys[i] = -values.get(i, scratch).z();
                }

                IntArrays.mergeSort(indices, (o1, o2) -> Floats.compare(keys[o2], keys[o1]));
                return indices;
            },
            (matrix, bias) -> matrix.translate(0.0F, 0.0F, bias / 512.0F)
    );

    @Shadow
    ProjectionTypeMixin(VertexSorting vertexSorting, ProjectionType.LayeringTransform layeringTransform) {
    }
}
