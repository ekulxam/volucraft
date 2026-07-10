package survivalblock.volucraft.client.render;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.*;
import survivalblock.volucraft.common.Volucraft;

import java.lang.Math;

@Environment(EnvType.CLIENT)
public class CubeOfSlotsRenderer extends PictureInPictureRenderer<CubeOfSlotsRenderState> {

    public static final Quaternionfc FLIP = new Quaternionf().rotateZ((float) Math.PI);
    public static final float CUBE_CENTER_OFFSET = 9 / 16F; // cubes are 18x18 in blockbench, so half that and div by 16

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
        final CubeModel model = renderState.unit();
        final CubeModel modelWithItem = renderState.unitWithItem();
        final Quaternionfc rot = renderState.rotation();
        final float expand = calculateExpansion(renderState.lerpExpansion());
        final Translator translator = (x, y, z) -> poseStack.translate(x * expand, y * expand, z * expand);
        final NonNullList<ItemStack> items = renderState.items();
        final int selected = renderState.selected();
        final Identifier texture = renderState.texture();
        final Identifier translucentTexture = renderState.translucent();

        this.minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);

        FeatureRenderDispatcher featureRenderDispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
        SubmitNodeStorage submitNodeStorage = featureRenderDispatcher.getSubmitNodeStorage();

        final CubeModel.State state = new CubeModel.State();

        poseStack.mulPose(FLIP); // because LivingEntity model(?)
        poseStack.translate(0, centerFromScale(renderState.scale()), 0); // translate to center
        for (int i = 0; i < Volucraft.SLOTS; i++) {
            ItemStack stack = items.get(i);
            final CubeModel modelToUse = stack.isEmpty() ? model : modelWithItem;
            poseStack.pushPose(); // push0
            poseStack.pushPose(); // push1
            poseStack.translate(0, CUBE_CENTER_OFFSET, 0); // pivot point
            poseStack.mulPose(FLIP);
            poseStack.mulPose(rot); // rotate around pivot point
            poseStack.translate(0, -CUBE_CENTER_OFFSET, 0); // unpivot point
            transformByIndex(i, translator);
            {
                RenderType maybeTranslucent = modelToUse.renderType(stack.isEmpty() ? texture : translucentTexture);
                submitNodeStorage.submitModel(modelToUse, state, poseStack, maybeTranslucent, 15728880, OverlayTexture.NO_OVERLAY, 0, null);
            }
            if (i == selected) {
                poseStack.pushPose();
                poseStack.translate(0, CUBE_CENTER_OFFSET, 0); // pivot point
                poseStack.scale(1.1F, 1.1F, 1.1F);
                poseStack.translate(0, -CUBE_CENTER_OFFSET, 0); // unpivot point
                RenderType highlight = modelToUse.renderType(renderState.highlightTexture());
                submitNodeStorage.submitModel(modelToUse, state, poseStack, highlight, -1, OverlayTexture.NO_OVERLAY, 0, null);
                poseStack.popPose();
            }
            if (!stack.isEmpty()) {
                renderItem(poseStack, stack, submitNodeStorage);
            }
            poseStack.popPose(); // pop1
            poseStack.popPose(); // pop0
        }

        this.minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
        featureRenderDispatcher.renderAllFeatures();
    }

    private void renderItem(PoseStack poseStack, ItemStack stack, SubmitNodeStorage submitNodeStorage) {
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0, -0.5, 0);
        poseStack.scale(0.9F, 0.9F, 0.9F);
        TrackingItemStackRenderState itemStackRenderState = new TrackingItemStackRenderState();
        ItemDisplayContext displayContext = ItemDisplayContext.NONE;
        this.minecraft.getItemModelResolver().updateForTopItem(itemStackRenderState, stack, displayContext, this.minecraft.level, this.minecraft.player, 0);
        itemStackRenderState.submit(poseStack, submitNodeStorage, 15728880, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    public static float calculateExpansion(float lerpExpansion) {
        return (Math.clamp(lerpExpansion, 0, 1) * 1.5F + 1) * 1.2F;
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

    @FunctionalInterface
    public interface Translator {
        void translate(float x, float y, float z);
    }
}
