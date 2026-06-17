package survivalblock.volucraft.client.render;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import survivalblock.volucraft.common.Volucraft;

@Environment(EnvType.CLIENT)
public class CubeOfSlotsRenderer extends PictureInPictureRenderer<CubeOfSlotsRenderState> {
    private final SubmitNodeCollector renderQueue;
    private final Minecraft minecraft;
    public CubeOfSlotsRenderer(PictureInPictureRendererRegistry.Context context) {
        super(context.bufferSource());
        this.renderQueue = context.submitNodeCollector();
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
        final Quaternionfc rot = renderState.rotation();
        final Quaternionfc flip = new Quaternionf().rotateZ((float) Math.PI);
        final float expand = (Math.clamp(renderState.lerpExpansion(), 0, 1) * 1.5F + 1) * 1.2F;
        final Translator translator = (x, y, z) -> poseStack.translate(x * expand, y * expand, z * expand);
        final NonNullList<ItemStack> items = renderState.items();
        final int selected = renderState.selected();
        final Identifier texture = renderState.texture();
        ItemStack stack = items.get(1);
        if (!stack.isEmpty()) {
            renderItem(poseStack, stack);
        }
        poseStack.mulPose(flip); // because LivingEntity model(?)
        poseStack.translate(0, centerFromScale(renderState.scale()), 0); // translate to center
        for (int i = 0; i < Volucraft.SLOTS; i++) {
            this.minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
            poseStack.pushPose(); // push0
            poseStack.pushPose(); // push1
            poseStack.translate(0, (9 / 16F), 0); // pivot point
            poseStack.mulPose(flip);
            poseStack.mulPose(rot); // rotate around pivot point
            poseStack.translate(0, -9 / 16F, 0); // unpivot point
            transformByIndex(i,  translator);
            /*{
                VertexConsumer buffer = this.bufferSource.getBuffer(model.renderType(texture));
                model.renderToBuffer(poseStack, buffer, 15728880, OverlayTexture.NO_OVERLAY);
            }

             */
            if (i == selected) {
                poseStack.pushPose();
                poseStack.translate(0, (9 / 16F), 0); // pivot point
                poseStack.scale(1.1F, 1.1F, 1.1F);
                poseStack.translate(0, -9 / 16F, 0); // unpivot point
                VertexConsumer buffer = this.bufferSource.getBuffer(model.renderType(renderState.highlightTexture()));
                model.renderToBuffer(poseStack, buffer, -1, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
            poseStack.popPose(); // pop1
            poseStack.popPose(); // pop0
        }
    }

    private void renderItem(PoseStack poseStack, ItemStack stack) {
        poseStack.pushPose();
        poseStack.scale(1, 1, -1);
        TrackingItemStackRenderState itemStackRenderState = new TrackingItemStackRenderState();
        ItemDisplayContext displayContext = ItemDisplayContext.NONE;
        this.minecraft.getItemModelResolver().updateForTopItem(itemStackRenderState, stack, displayContext, this.minecraft.level, this.minecraft.player, 0);
        boolean flat = !itemStackRenderState.usesBlockLight();
        if (flat) {
            Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        } else {
            Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
        }
        //poseStack.translate(8, 0, 8);
        itemStackRenderState.submit(poseStack, this.renderQueue, 15728880, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    protected static void transformByIndex(int index, Translator translator) {
        switch (index) {
            case 0: {
                translator.translate(-1, -1, -1);
                return;
            }
            case 1: {
                translator.translate(0, -1, -1);
                return;
            }
            case 2: {
                translator.translate(1, -1, -1);
                return;
            }
            case 3: {
                translator.translate(-1, -1, 0);
                return;
            }
            case 4: {
                translator.translate(0, -1, 0);
                return;
            }
            case 5: {
                translator.translate(1, -1, 0);
                return;
            }
            case 6: {
                translator.translate(-1, -1, 1);
                return;
            }
            case 7: {
                translator.translate(0, -1, 1);
                return;
            }
            case 8: {
                translator.translate(1, -1, 1);
                return;
            }
            case 9: {
                translator.translate(-1, 0, -1);
                return;
            }
            case 10: {
                translator.translate(0, 0, -1);
                return;
            }
            case 11: {
                translator.translate(1, 0, -1);
                return;
            }
            case 12: {
                translator.translate(-1, 0, 0);
                return;
            }
            case 13: {
                translator.translate(0, 0, 0);
                return;
            }
            case 14: {
                translator.translate(1, 0, 0);
                return;
            }
            case 15: {
                translator.translate(-1, 0, 1);
                return;
            }
            case 16: {
                translator.translate(0, 0, 1);
                return;
            }
            case 17: {
                translator.translate(1, 0, 1);
                return;
            }
            case 18: {
                translator.translate(-1, 1, -1);
                return;
            }
            case 19: {
                translator.translate(0, 1, -1);
                return;
            }
            case 20: {
                translator.translate(1, 1, -1);
                return;
            }
            case 21: {
                translator.translate(-1, 1, 0);
                return;
            }
            case 22: {
                translator.translate(0, 1, 0);
                return;
            }
            case 23: {
                translator.translate(1, 1, 0);
                return;
            }
            case 24: {
                translator.translate(-1, 1, 1);
                return;
            }
            case 25: {
                translator.translate(0, 1, 1);
                return;
            }
            case 26: {
                translator.translate(1, 1, 1);
                return;
            }
            default: {
                //noinspection UnnecessaryReturnStatement
                return;
            }
        }
    }

    protected static float centerFromScale(float scale) {
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
