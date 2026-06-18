package survivalblock.volucraft.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.volucraft.common.Volucraft;

@Mixin(ResourceLoader.class)
public interface ResourceLoaderMixin {

    @WrapOperation(method = "registerBuiltinPack(Lnet/minecraft/resources/Identifier;Lnet/fabricmc/loader/api/ModContainer;Lnet/fabricmc/fabric/api/resource/v1/pack/PackActivationType;)Z", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/impl/resource/ResourceLoaderImpl;registerBuiltinPack(Lnet/minecraft/resources/Identifier;Ljava/lang/String;Lnet/fabricmc/loader/api/ModContainer;Lnet/fabricmc/fabric/api/resource/v1/pack/PackActivationType;)Z"))
    private static boolean datapack(Identifier id, String subPath, ModContainer container, PackActivationType activationType, Operation<Boolean> original) {
        if (Volucraft.datapacking) {
            if (subPath.startsWith("resourcepacks/")) {
                subPath = subPath.replaceFirst("resourcepacks/", "datapacks/");
            }
        }
        return original.call(id, subPath, container, activationType);
    }

    @WrapOperation(method = "registerBuiltinPack(Lnet/minecraft/resources/Identifier;Lnet/fabricmc/loader/api/ModContainer;Lnet/minecraft/network/chat/Component;Lnet/fabricmc/fabric/api/resource/v1/pack/PackActivationType;)Z", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/impl/resource/ResourceLoaderImpl;registerBuiltinPack(Lnet/minecraft/resources/Identifier;Ljava/lang/String;Lnet/fabricmc/loader/api/ModContainer;Lnet/minecraft/network/chat/Component;Lnet/fabricmc/fabric/api/resource/v1/pack/PackActivationType;)Z"))
    private static boolean datapack(Identifier id, String subPath, ModContainer container, Component displayName, PackActivationType activationType, Operation<Boolean> original) {
        if (Volucraft.datapacking) {
            if (subPath.startsWith("resourcepacks/")) {
                subPath = subPath.replaceFirst("resourcepacks/", "datapacks/");
            }
        }
        return original.call(id, subPath, container, displayName, activationType);
    }
}
