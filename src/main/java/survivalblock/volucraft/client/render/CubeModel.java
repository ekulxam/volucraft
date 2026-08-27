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
package survivalblock.volucraft.client.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import survivalblock.volucraft.common.Volucraft;

import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("JavadocReference")
public class CubeModel extends Model<CubeModel.State> {
    /**
     * @see RenderPipelines#ENTITY_TRANSLUCENT
     * @see RenderPipelines#ENTITY_TRANSLUCENT_EMISSIVE
     */
    private static final Function<Boolean, RenderPipeline> PIPELINE = Util.memoize(opaque ->
            RenderPipelines.register(
                    RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
                        .withLocation(Volucraft.id("pipeline/cube_" + (opaque ? "opaque" : "translucent")))
                        .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                        .withShaderDefine("PER_FACE_LIGHTING")
                        .withSampler("Sampler1")
                        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                        .withCull(false)
                        .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, opaque))
                        .build()
            )
    );
    /**
     * @see net.minecraft.client.renderer.rendertype.RenderTypes#ENTITY_TRANSLUCENT
     * @see net.minecraft.client.renderer.rendertype.RenderTypes#ENTITY_TRANSLUCENT_EMISSIVE
     */
    private static final Function<Boolean, Function<Identifier, RenderType>> CUBE = Util.memoize(opaque ->
            Util.memoize(texture -> {
                RenderSetup state = RenderSetup.builder(PIPELINE.apply(opaque))
                        .withTexture("Sampler0", texture)
                        .useOverlay()
                        .useLightmap()
                        .affectsCrumbling()
                        .sortOnUpload()
                        .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                        .createRenderSetup();
                return RenderType.create("volucraft:cube", state);
            })
    );

    public CubeModel(ModelPart root, Function<Identifier, RenderType> renderTypeFunction) {
        super(root, renderTypeFunction);
    }

    public CubeModel(ModelPart root) {
        this(root, CUBE.apply(false));
    }

    public RenderType renderType(Identifier texture, boolean opaque) {
        return CUBE.apply(opaque).apply(texture);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-9F, 0.0F, -9F, 18.0F, 18.0F, 18.0F), PartPose.ZERO);

        return LayerDefinition.create(mesh, 128, 128);
    }

    static {
        PIPELINE.apply(true);
        PIPELINE.apply(false);
    }

    public static final class State {
        @SuppressWarnings("InstantiationOfUtilityClass")
        public static final State INSTANCE = new State();

        private State() {
        }
    }
}
