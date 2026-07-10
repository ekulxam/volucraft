package survivalblock.volucraft.common.compat;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;

public class VolucraftMixinCanceller implements MixinCanceller {
    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        return mixinClassName.contains("sodium.mixin.features.render.immediate.buffer_builder.sorting.VertexSortingMixin");
    }
}