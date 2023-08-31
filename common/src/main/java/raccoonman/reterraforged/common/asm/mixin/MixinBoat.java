/*
 * MIT License
 *
 * Copyright (c) 2021 TerraForged
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package raccoonman.reterraforged.common.asm.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;

@Mixin(Boat.class)
class MixinBoat {
	
	@Inject(
		at = @At("RETURN"),
		method = "isUnderwater",
		cancellable = true
	)
    private void onIsUnderwater(CallbackInfoReturnable<Boat.Status> callback) {
        if (callback.getReturnValue() != null && shouldFloat((Boat) (Object) this)) {
            // Clear the status so the boat doesn't sink
            callback.setReturnValue(null);
        }
    }
	
	private static boolean shouldFloat(Boat boat) {
        // Ignore if not being ridden by a player
        if (!(boat.getFirstPassenger() instanceof Player)) return false;

        var pos = boat.position();
        double targetY = boat.getWaterLevelAbove() - boat.getBbHeight() + 0.101D;
        double deltaY = targetY - pos.y;

        // Can't go up if it's greater than half a block
        if (deltaY > 0.8) return false;

        // Lerp by 50% until the height diff is very small
        double lerp = deltaY > 0.01 ? 0.5 : 1.0;

        boat.setPos(pos.x, pos.y + deltaY * lerp, pos.z);

        return true;
	}
}
