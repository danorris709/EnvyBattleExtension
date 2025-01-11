package uk.co.envyware.battle.extension;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(EnvyBattleExtension.MOD_ID)
@Mod.EventBusSubscriber(modid = EnvyBattleExtension.MOD_ID)
public class EnvyBattleExtension {

    public static final String MOD_ID = "envybattleextension";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

}
