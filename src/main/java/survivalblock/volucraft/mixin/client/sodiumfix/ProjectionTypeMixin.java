/*
 * Copyright (c) 2026-present ekulxam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

                for (int i = 0; i < size; indices[i] = i++) {
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
