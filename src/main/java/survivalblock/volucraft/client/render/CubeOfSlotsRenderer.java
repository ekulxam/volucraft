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
        //poseStack.translate(0, -9, 0); // pivot point
        poseStack.mulPose(new Quaternionf().rotateZ((float) Math.PI).rotateY((Util.getMillis() / 10F) * (float) Math.PI / 180F));
        poseStack.translate(0, 1, 0);
        //poseStack.mulPose(Axis.YP.rotationDegrees(45));
        //poseStack.mulPose(Axis.XP.rotationDegrees(Util.getMillis() / 10F));
        VertexConsumer buffer = this.bufferSource.getBuffer(model.renderType(renderState.texture()));
        model.renderToBuffer(poseStack, buffer, 15728880, OverlayTexture.NO_OVERLAY);
    }

    @Override
    protected String getTextureLabel() {
        return "volumetric slot model";
    }
}
