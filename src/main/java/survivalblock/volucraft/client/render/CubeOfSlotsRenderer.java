package survivalblock.volucraft.client.render;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Util;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class CubeOfSlotsRenderer extends PictureInPictureRenderer<CubeOfSlotsRenderState> {
    public CubeOfSlotsRenderer(PictureInPictureRendererRegistry.Context context) {
        this(context.bufferSource());
    }

	public CubeOfSlotsRenderer(final MultiBufferSource.BufferSource bufferSource) {
		super(bufferSource);
	}

	@Override
	public Class<CubeOfSlotsRenderState> getRenderStateClass() {
		return CubeOfSlotsRenderState.class;
	}

    @Override
    protected void renderToTexture(CubeOfSlotsRenderState renderState, PoseStack poseStack) {
        CubeModel model = renderState.unit();
        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
        poseStack.mulPose(new Quaternionf().rotateZ((float) Math.PI)); // GuiEntityRenderer does it too and it works
        poseStack.translate(0, 7.5, 0); // translate to center
        poseStack.pushPose(); // push
        poseStack.translate(0, (9 / 16F), 0); // pivot point
        poseStack.mulPose(Axis.XP.rotationDegrees(Util.getMillis() / 10F)); // rotate around pivot point
        poseStack.mulPose(Axis.YP.rotationDegrees(Util.getMillis() / 10F));
        poseStack.translate(0, -9 / 16F, 0); // unpivot point
        VertexConsumer buffer = this.bufferSource.getBuffer(model.renderType(renderState.texture())); // render
        model.renderToBuffer(poseStack, buffer, 15728880, OverlayTexture.NO_OVERLAY);
        poseStack.popPose(); // pop
    }

    @Override
    protected String getTextureLabel() {
        return "volumetric slot model";
    }
}
