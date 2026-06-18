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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import survivalblock.volucraft.common.Volucraft;

@Environment(EnvType.CLIENT)
public class CubeOfSlotsRenderer extends PictureInPictureRenderer<CubeOfSlotsRenderState> {

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
        final Quaternionfc flip = new Quaternionf().rotateZ((float) Math.PI);
        final float expand = (Math.clamp(renderState.lerpExpansion(), 0, 1) * 1.5F + 1) * 1.2F;
        final Translator translator = (x, y, z) -> poseStack.translate(x * expand, y * expand, z * expand);
        final NonNullList<ItemStack> items = renderState.items();
        final int selected = renderState.selected();
        final Identifier texture = renderState.texture();
        final Identifier translucentTexture = renderState.translucent();

        poseStack.mulPose(flip); // because LivingEntity model(?)
        poseStack.translate(0, centerFromScale(renderState.scale()), 0); // translate to center
        for (int i = 0; i < Volucraft.SLOTS; i++) {
            ItemStack stack = items.get(i);
            final CubeModel modelToUse = stack.isEmpty() ? model : modelWithItem;
            this.minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
            poseStack.pushPose(); // push0
            poseStack.pushPose(); // push1
            poseStack.translate(0, (9 / 16F), 0); // pivot point
            poseStack.mulPose(flip);
            poseStack.mulPose(rot); // rotate around pivot point
            poseStack.translate(0, -9 / 16F, 0); // unpivot point
            transformByIndex(i, translator);
            {
                VertexConsumer buffer = this.bufferSource.getBuffer(modelToUse.renderType(stack.isEmpty() ? texture : translucentTexture));
                model.renderToBuffer(poseStack, buffer, 15728880, OverlayTexture.NO_OVERLAY);
            }
            if (i == selected) {
                poseStack.pushPose();
                poseStack.translate(0, (9 / 16F), 0); // pivot point
                poseStack.scale(1.1F, 1.1F, 1.1F);
                poseStack.translate(0, -9 / 16F, 0); // unpivot point
                VertexConsumer buffer = this.bufferSource.getBuffer(modelToUse.renderType(renderState.highlightTexture()));
                model.renderToBuffer(poseStack, buffer, -1, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
            if (!stack.isEmpty()) {
                renderItem(poseStack, stack);
            }
            poseStack.popPose(); // pop1
            poseStack.popPose(); // pop0
        }
    }

    private void renderItem(PoseStack poseStack, ItemStack stack) {
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0, -0.5, 0);
        poseStack.scale(0.9F, 0.9F, 0.9F);
        TrackingItemStackRenderState itemStackRenderState = new TrackingItemStackRenderState();
        ItemDisplayContext displayContext = ItemDisplayContext.NONE;
        this.minecraft.getItemModelResolver().updateForTopItem(itemStackRenderState, stack, displayContext, this.minecraft.level, this.minecraft.player, 0);
        boolean flat = !itemStackRenderState.usesBlockLight();
        if (flat) {
            Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        } else {
            Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
        }
        FeatureRenderDispatcher featureRenderDispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
        SubmitNodeStorage submitNodeStorage = featureRenderDispatcher.getSubmitNodeStorage();
        itemStackRenderState.submit(poseStack, submitNodeStorage, 15728880, OverlayTexture.NO_OVERLAY, 0);
        featureRenderDispatcher.renderAllFeatures();
        poseStack.popPose();
    }

    /**
     * Phanastrae's algorithm
     */
    protected static void transformByIndex(int index, Translator translator) {
        int x = (index % Volucraft.SIDE_LENGTH) - 1;
        int z = ((index / Volucraft.SIDE_LENGTH) % Volucraft.SIDE_LENGTH) - 1;
        int y = (index / (Volucraft.SIDE_LENGTH * Volucraft.SIDE_LENGTH)) - 1;

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
