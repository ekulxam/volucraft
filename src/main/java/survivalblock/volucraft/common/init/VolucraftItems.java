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
package survivalblock.volucraft.common.init;

import net.minecraft.world.item.Item;
import survivalblock.atmosphere.registrar.ItemRegistrant;
import survivalblock.volucraft.common.Volucraft;

public final class VolucraftItems {
    private VolucraftItems() {
    }

    private static final ItemRegistrant REGISTRANT = new ItemRegistrant(Volucraft.MOD_ID);

    public static final Item AMALGAMATION_TABLE = REGISTRANT.register(
            VolucraftBlocks.AMALGAMATION_TABLE, new Item.Properties().useBlockDescriptionPrefix()
    );

    public static void init() {
        // NO-OP
    }
}
