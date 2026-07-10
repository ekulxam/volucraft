package survivalblock.volucraft.mixin.client.sodiumfix;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.vertex.VertexSorting;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Arrays;

@Mixin(ProjectionType.class)
public enum ProjectionTypeMixin {
    VOLUCRAFT_ORTHOGRAPHIC(
            values -> {
                int size = values.size();
                if (size <= 0) {
                    return new int[0];
                } else if (size == 1) {
                    return new int[]{0};
                }

                Vector3f scratch = new Vector3f();
                long[] packeds = new long[size];
                int[] indices = new int[size];

                for(int i = 0; i < size; indices[i] = i++) {
                    float scaledZ = -values.get(i, scratch).z() * 10000;
                    packeds[i] = ((long) scaledZ << 32) | (i & 0xFFFFFFFFL);
                }

                Arrays.sort(packeds);

                for (int i = 0; i < size; i++) {
                    // yeah sorry I only needed the long for the sort, you can go now
                    indices[i] = (int) (packeds[size - 1 - i] & 0xFFFFFFFFL);
                }

                return indices;
            },
            (matrix, bias) -> matrix.translate(0.0F, 0.0F, bias / 512.0F)
    );

    @Shadow
    ProjectionTypeMixin(VertexSorting vertexSorting, ProjectionType.LayeringTransform layeringTransform) {
    }
}
