package uk.co.envyware.battle.extension.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.pokemon.species.gender.Gender;
import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.pixelmonmod.pixelmon.battles.status.StatusType;
import com.pixelmonmod.pixelmon.client.ClientProxy;
import com.pixelmonmod.pixelmon.client.gui.Resources;
import com.pixelmonmod.pixelmon.client.gui.ScreenHelper;
import com.pixelmonmod.pixelmon.client.gui.ScreenParticleEngine;
import com.pixelmonmod.pixelmon.client.gui.battles.PixelmonClientData;
import com.pixelmonmod.pixelmon.client.gui.battles.pokemonOverlays.OpponentElement;
import com.pixelmonmod.pixelmon.client.gui.widgets.PixelmonWidget;
import com.pixelmonmod.pixelmon.client.storage.ClientStorageManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import uk.co.envyware.battle.extension.EnvyBattleExtension;

import java.text.DecimalFormat;

@Mixin(value = OpponentElement.class, remap = false)
public abstract class MixinOpponentElement extends PixelmonWidget {

    @Shadow @Final private PixelmonClientData enemy;

    @Shadow @Final private static ResourceLocation OPPONENT;

    @Shadow @Final private ScreenParticleEngine particleEngine;

    @Shadow @Final private static ResourceLocation WARNING;

    @Shadow @Final private static ResourceLocation CAUTION;

    @Shadow @Final private static ResourceLocation HEALTHY;

    @Shadow @Final private Screen parent;

    @Shadow @Final private static ResourceLocation CAUGHT;

    @Shadow @Final private static ResourceLocation SHINY;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void drawElement(MatrixStack matrix, float scale) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 771);
        ScreenHelper.drawImage(matrix, OPPONENT, this.x, (this.y - 3), 160.0F, 50.0F, this.zLevel);
        float healthPercent = (float)(this.enemy.health.get() / this.enemy.maxHealth);
        ScreenHelper.drawBar(matrix, this.x + 44, (this.y + 20), 109.0F, 10.0F, healthPercent, this.enemy.getHealthColor());
        ScreenHelper.drawImage(healthPercent <= 0.5F ? (healthPercent <= 0.25F ? WARNING : CAUTION) : HEALTHY, matrix, (this.x - 10), (this.y - 18), 60.0F, 60.0F, this.zLevel);
        ScreenHelper.drawImage(ScreenHelper.getPokemonSprite(this.enemy, this.parent.getMinecraft()), matrix, (this.x + 1), (this.y - 3), 40.0F, 40.0F, this.zLevel);
        this.particleEngine.drawAtOffset(matrix, this.enemy.pokemonUUID.toString(), (this.x + 5), (this.y + 1), RandomHelper.rand.nextInt(26), RandomHelper.rand.nextInt(26));
        float offset = 0.0F;
        if (ClientStorageManager.pokedex.hasCaught(this.enemy.species)) {
            ScreenHelper.drawImage(matrix, CAUGHT, (this.x + 52), (this.y + 5), 8.0F, 8.0F, this.zLevel);
            offset += 9.0F;
        }

        Stats form = enemy.species.getForm(enemy.form);

        if (form.getAbilities().isHiddenAbility(enemy.moveset.getAbility())) {
            ScreenHelper.drawImage(matrix, Resources.exclamation_mark, (this.x + 52) + offset, (this.y + 5), 5.0F, 8.0F, this.zLevel);
            offset += 7.0F;
        }

        double mouseX = Minecraft.getInstance().mouseHandler.xpos();
        double mouseY = Minecraft.getInstance().mouseHandler.ypos();
        EnvyBattleExtension.LOGGER.info("Mouse X: " + mouseX + " Mouse Y: " + mouseY + " " + this.isMouseOver(mouseX, mouseY));

        if (isMouseOver(mouseX, mouseY)) {

        }

        if (this.enemy.getGender() != Gender.NONE) {
            ScreenHelper.drawImage(this.enemy.getGender() == Gender.MALE ? Resources.male : Resources.female, matrix, (this.x + 52) + offset, (this.y + 5), 5.0F, 8.0F, this.zLevel);
            offset += 7.0F;
        }

        ScreenHelper.drawScaledString(matrix, this.enemy.getDisplayName(), (this.x + 52) + offset, this.y + 5.75F, this.enemy.palette.equalsIgnoreCase("shiny") ? -7545 : -986896, 16.0F);
        ScreenHelper.drawScaledStringRightAligned(matrix, "Lv." + this.enemy.level, (this.x + 149), this.y + 7.0F, -986896, false, 12.0F);
        if (PixelmonConfigProxy.getGraphics().isAdvancedBattleInformation()) {
            DecimalFormat df = new DecimalFormat(".#");
            String percentage = df.format((double)healthPercent * (double)100.0F).replace(".0", "");
            if (percentage.isEmpty()) {
                percentage = "0";
            }

            ScreenHelper.drawScaledStringRightAligned(matrix, percentage + "%", (this.x + 145), this.y + 22.0F, -986896, false, 14.0F);
        }

        if (this.enemy.status != -1 && StatusType.getEffect(this.enemy.status) != null) {
            float[] texturePair2 = StatusType.getTexturePos(StatusType.getEffect(this.enemy.status));
            ScreenHelper.bindTexture(Resources.status);
            ScreenHelper.simpleDrawImageQuad(matrix, (this.x + 54 + ScreenHelper.getStringWidth(this.enemy.getDisplayName())) + offset, (this.y + 4), 10.5F, 10.5F, texturePair2[0] / 768.0F, texturePair2[1] / 768.0F, (texturePair2[0] + 240.0F) / 768.0F, (texturePair2[1] + 240.0F) / 768.0F, this.zLevel);
        }

        if (ClientProxy.battleManager.catchCombo != 0) {
            ScreenHelper.drawScaledString(matrix, I18n.get("gui.battle.catch_combo", ClientProxy.battleManager.catchCombo), (this.x + 22) + offset, this.y + 38.75F, -986896, 12.0F);
        }

        if (this.enemy.palette.equalsIgnoreCase("shiny") && RandomHelper.rand.nextInt(80) == 0) {
            int size = 7 + RandomHelper.rand.nextInt(7);
            this.particleEngine.addParticle(new ScreenParticleEngine.GuiParticle(this.enemy.pokemonUUID.toString(), SHINY, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.8F, 0.3F, 0.0F, size, size, 120, (particle, matrixStack) -> {
                int x = particle.age;
                int m = particle.maxAge;
                int h = m / 2;
                particle.a = (float)(x <= h ? x : h - (x - h)) / (float)h;
            }));
        }

    }

}
