package survivalblock.volucraft.common.init;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import survivalblock.volucraft.common.recipe.extrude.ExtrusionFormula;

public final class VolucraftRegistries {
    public static final ResourceKey<Registry<ExtrusionFormula.Extruder<?, ?>>> EXTRUDER_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("volucraft", "extruder"));

    public static final Registry<ExtrusionFormula.Extruder<?, ?>> EXTRUDER = FabricRegistryBuilder.create(EXTRUDER_KEY)
            .attribute(RegistryAttribute.MODDED)
            .buildAndRegister();
}
