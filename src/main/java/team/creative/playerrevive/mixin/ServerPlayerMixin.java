package team.creative.playerrevive.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionSet;
import team.creative.playerrevive.PlayerRevive;
import team.creative.playerrevive.api.PlayerExtender;
import team.creative.playerrevive.server.PlayerReviveServer;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements PlayerExtender {
    
    @Unique
    private float overkill;
    
    @Inject(method = "permissions()Lnet/minecraft/server/permissions/PermissionSet;", at = @At("HEAD"), require = 1, cancellable = true)
    public void getPermissionLevel(CallbackInfoReturnable<PermissionSet> callback) {
        if (PlayerRevive.CONFIG.bleeding.changePermissionLevel && PlayerReviveServer.isBleeding((ServerPlayer) (Object) this))
            callback.setReturnValue(switch (PlayerRevive.CONFIG.bleeding.permissionLevel) {
                case 0 -> LevelBasedPermissionSet.ALL;
                case 1 -> LevelBasedPermissionSet.MODERATOR;
                case 2 -> LevelBasedPermissionSet.GAMEMASTER;
                case 3 -> LevelBasedPermissionSet.ADMIN;
                case 4 -> LevelBasedPermissionSet.OWNER;
                default -> throw new IllegalArgumentException("Unexpected value: " + PlayerRevive.CONFIG.bleeding.permissionLevel);
            });
    }
    
    @Override
    public float getOverkill() {
        return overkill;
    }
    
    @Override
    public void setOverkill(float overkill) {
        this.overkill = overkill;
    }
    
}
