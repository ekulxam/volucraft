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

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Ease;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import survivalblock.volucraft.client.compat.config.VolucraftClientConfig;
import survivalblock.volucraft.common.Volucraft;

import java.util.List;

import static net.minecraft.util.LightCoordsUtil.FULL_BRIGHT;

@Environment(EnvType.CLIENT)
public class CubeOfSlotsRenderer extends PictureInPictureRenderer<CubeOfSlotsRenderState> {
    public static final Quaternionfc FLIP = new Quaternionf().rotateZ((float) Math.PI);
    public static final float CUBE_CENTER_OFFSET = 9 / 16F; // cubes are 18x18 in blockbench, so half that and div by 16

    private static final int[] GAMECUBE_PATH = { 2, 5, 8, 7, 6, 15, 24, 21, 18, 19, 20, 11 }; // gotta love the magic numbers
    public static final float BOUNCE_THRESHOLD = 0.8F;

    public static ColorComputer COLOR_COMPUTER = (stack, gameCubeAnimationProgress) -> {
        if (stack.isEmpty()) {
            return getColor(gameCubeAnimationProgress);
        }
        return ((VolucraftClientConfig.INSTANCE.getCubeWithItemAlpha() & 0xFF) << 24) | 0xFFFFFF;
    };

    private final Minecraft minecraft;

    public CubeOfSlotsRenderer(PictureInPictureRendererRegistry.Context context) {
        super(context.bufferSource());
        this.minecraft = context.minecraft();
    }

	@Override
	public Class<CubeOfSlotsRenderState> getRenderStateClass() {
		return CubeOfSlotsRenderState.class;
	}

    @SuppressWarnings({"Convert2MethodRef", "RedundantSuppression"})
    @Override
    protected void renderToTexture(CubeOfSlotsRenderState renderState, PoseStack poseStack) {
        final Quaternionfc rot = renderState.rotation();
        final float expand = calculateExpansion(renderState.lerpExpansion());
        final Translator translator = (x, y, z) -> poseStack.translate(x * expand, y * expand, z * expand);
        final List<CubeOfSlotsRenderState.ItemStackWith3DSlot> items = renderState.items();
        final int selected = renderState.selected();
        final Identifier texture = renderState.texture();

        final CubeModel modelToUse = renderState.model();

        this.minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);

        poseStack.mulPose(FLIP); // because LivingEntity model(?)
        poseStack.translate(0, centerFromScale(renderState.scale()), 0); // translate to center

        float anim = renderState.gameCubeAnimationProgress();

        if (anim < 1.0F) {
            if (anim > BOUNCE_THRESHOLD) {
                float bounceTime = getBounceTime(anim);
                float bounceScale = (float) (Math.sin(bounceTime * Math.PI) * 0.3F * (1.0F - bounceTime));
                poseStack.mulPose(Axis.XN.rotation(bounceScale));
            }
            anim *= (1 / BOUNCE_THRESHOLD);
        }

        pass(poseStack, anim, items, rot, translator, (matrices, threeDimensional, submitNodeStorage, _) -> {
            final int color = threeDimensional.color();
            RenderType renderType = CubeModel.renderType(texture, color);
            submitNodeStorage.submitModel(modelToUse, CubeModel.State.INSTANCE, matrices, renderType, FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color, null, 0, null);
        });

        pass(poseStack, anim, items, rot, translator, (matrices, _, submitNodeStorage, i) -> {
            if (i != selected) {
                return;
            }
            matrices.pushPose();
            matrices.translate(0, CUBE_CENTER_OFFSET, 0); // pivot point
            matrices.scale(1.1F, 1.1F, 1.1F);
            matrices.translate(0, -CUBE_CENTER_OFFSET, 0); // unpivot point
            final int highlightColor = renderState.highlightColor();
            RenderType highlight = CubeModel.renderType(renderState.highlightTexture(), highlightColor);
            submitNodeStorage.submitModel(modelToUse, CubeModel.State.INSTANCE, matrices, highlight, -1, OverlayTexture.NO_OVERLAY, highlightColor, null, 0, null);
            matrices.popPose();
        });

        pass(poseStack, anim, items, rot, translator, (matrices, threeDimensional, submitNodeStorage, _) -> {
            if (threeDimensional.shouldRender()) {
                renderItem(matrices, threeDimensional, submitNodeStorage);
            }
        });
    }

    private void pass(PoseStack poseStack, float anim, List<CubeOfSlotsRenderState.ItemStackWith3DSlot> items, Quaternionfc rot, Translator translator, SlotRenderer action) {
        final FeatureRenderDispatcher featureRenderDispatcher = this.minecraft.gameRenderer.getFeatureRenderDispatcher();
        final SubmitNodeStorage submitNodeStorage = featureRenderDispatcher.getSubmitNodeStorage();

        for (int i = 0; i < Volucraft.SLOTS; i++) {
            if (anim < 1.0F) {
                float appearanceThreshold = 1.0F;
                for (int j = 0; j < GAMECUBE_PATH.length; j++) {
                    if (GAMECUBE_PATH[j] == i) {
                        appearanceThreshold *= ((float) j / GAMECUBE_PATH.length);
                        break;
                    }
                }

                if (anim < appearanceThreshold) {
                    continue;
                }
            }

            CubeOfSlotsRenderState.ItemStackWith3DSlot threeDimensional = items.get(i);
            poseStack.pushPose(); // push0
            poseStack.pushPose(); // push1
            poseStack.translate(0, CUBE_CENTER_OFFSET, 0); // pivot point
            poseStack.mulPose(FLIP);
            poseStack.mulPose(rot); // rotate around pivot point
            poseStack.translate(0, -CUBE_CENTER_OFFSET, 0); // unpivot point
            transformByIndex(i, translator);
            action.render(poseStack, threeDimensional, submitNodeStorage, i);
            poseStack.popPose(); // pop1
            poseStack.popPose(); // pop0
        }

        featureRenderDispatcher.renderAllFeatures();
    }

    private void renderItem(PoseStack poseStack, CubeOfSlotsRenderState.ItemStackWith3DSlot threeDimensional, SubmitNodeCollector renderQueue) {
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0, -0.5, 0);
        poseStack.scale(0.9F, 0.9F, 0.9F);
        ItemStackRenderState state = threeDimensional.itemStackRenderState();
        state.submit(poseStack, renderQueue, FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    public static float calculateExpansion(float lerpExpansion) {
        float clamped = Math.clamp(lerpExpansion, 0, 1);
        return (Ease.inOutCubic(clamped) * 1.5F + 1) * 1.2F;
    }

    /**
     * Phanastrae's algorithm
     */
    public static void transformByIndex(int index, Translator translator) {
        int x = (index % Volucraft.SIDE_LENGTH) - 1;
        int y = (index / (Volucraft.SIDE_LENGTH * Volucraft.SIDE_LENGTH)) - 1;
        int z = ((index / Volucraft.SIDE_LENGTH) % Volucraft.SIDE_LENGTH) - 1;

        translator.translate(x, y, z);
    }

    public static float centerFromScale(float scale) {
        if (scale == 40F) {
            return 1.5F;
        }
        if (scale == 30F) {
            return 2F;
        }
        if (scale == 20F) {
            return 3F;
        }
        if (scale == 15F) {
            return 5F;
        }
        if (scale == 11F) {
            return 6.5F;
        }
        if (scale == 10F) {
            return 7.5F;
        }
        return 1F; // what is this relationship
    }

    @Override
    protected String getTextureLabel() {
        return "volumetric slots model";
    }

    @ApiStatus.Internal
    public ProjectionType getMaybeCustomProjectionType() {
        return ProjectionType.VOLUCRAFT_ORTHOGRAPHIC;
    }

    public static int getColor(float anim) {
        if (anim < 1.0F) {
            int purple = 0x6354C2;
            int cubeAlpha = 255;
            if (anim > BOUNCE_THRESHOLD) {
                float bounceTime = getBounceTime(anim);
                cubeAlpha = Mth.lerpInt(bounceTime, 255, VolucraftClientConfig.INSTANCE.getCubeAlpha()) & 0xFF;
                purple = ARGB.srgbLerp(bounceTime, purple, 0xFFFFFF);
            }

            return (cubeAlpha << 24) | purple;
        }
        return ((VolucraftClientConfig.INSTANCE.getCubeAlpha() & 0xFF) << 24) | 0xFFFFFF;
    }

    public static float getBounceTime(float anim) {
        return (anim - BOUNCE_THRESHOLD) / (1 - BOUNCE_THRESHOLD); // 0 to 1;
    }

    @FunctionalInterface
    public interface Translator {
        void translate(float x, float y, float z);
    }

    @FunctionalInterface
    public interface SlotRenderer {
        void render(PoseStack poseStack, CubeOfSlotsRenderState.ItemStackWith3DSlot threeDimensional, SubmitNodeCollector renderQueue, int index);
    }

    @FunctionalInterface
    public interface ColorComputer {
        int getColor(ItemStack stack, float gameCubeAnimationProgress);
    }
}
