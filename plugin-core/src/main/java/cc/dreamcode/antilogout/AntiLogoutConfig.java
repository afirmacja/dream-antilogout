package cc.dreamcode.antilogout;

import cc.dreamcode.antilogout.location.Region;
import cc.dreamcode.notice.minecraft.MinecraftNoticeType;
import cc.dreamcode.notice.minecraft.bukkit.BukkitNotice;
import cc.dreamcode.platform.bukkit.component.configuration.Configuration;
import cc.dreamcode.platform.persistence.StorageConfig;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Getter
@SuppressWarnings("FieldMayBeFinal")
@Configuration(child = "config.yml")
@Header("## dream-antilogout (Main-Config) ##")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class AntiLogoutConfig extends OkaeriConfig {

    public StorageConfig storageWrapper = new StorageConfig("dream_antilogout");
    private PluginWrapper pluginWrapper = new PluginWrapper();
    private MessageWrapper messageWrapper = new MessageWrapper();

    @Getter
    @SuppressWarnings("FieldMayBeFinal")
    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class PluginWrapper extends OkaeriConfig {

        @Comment("Debug pokazuje dodatkowe informacje do konsoli. Lepiej wyłączyć. :P")
        private boolean debug = true;

        private List<String> allowedCommands = Arrays.asList(
                "tell",
                "msg",
                "w"
        );
        @Comment("Regiony w których nie ma antylogouta. (WORLDGUARD)")
        private List<String> blockedRegions = Arrays.asList(
                "spawn",
                "wybojisko"
        );
        @Comment("Regiony w których nie ma antylogouta. (tutaj jak nie ma worldguarda)")
        private List<Region> regions = Arrays.asList(
                new Region(-10, 10, 25, 50, -10, 10),
                new Region(-20, 20, 25, 50, -20, 20)
        );


        private Duration combatTime = Duration.ofSeconds(21), protectionTime = Duration.ofMinutes(5);

        private boolean fromMonsters = false;
        @Setter
        private boolean pvpEnabled = true;
        private boolean protectionAfterDeath = false;
        private boolean canDamagePlayersWhileInCreative = false;
        private String commandNotAllowedWhileInCombatBypass = "dream.command.bypass";
        private String antiLogoutBypass = "dream.antilog.bypass";
    }

    @Getter
    @SuppressWarnings("FieldMayBeFinal")
    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class MessageWrapper extends OkaeriConfig {

        private BukkitNotice mistakeUsage = BukkitNotice.of(MinecraftNoticeType.CHAT, "&7Poprawne uzycie: &5{usage}");
        private BukkitNotice mistakePlayerNotFound = BukkitNotice.of(MinecraftNoticeType.CHAT, "&cPodanego gracza nie znaleziono.");

        private BukkitNotice errorNoPermission = BukkitNotice.of(MinecraftNoticeType.CHAT, "&cNie posiadasz uprawnien.");
        private BukkitNotice errorInGameOnlyCommand = BukkitNotice.of(MinecraftNoticeType.CHAT, "&cNie jestes aby to zrobic.");
        private BukkitNotice errorCannotDoAtMySelf = BukkitNotice.of(MinecraftNoticeType.CHAT, "&cNie mozesz tego zrobic na sobie.");
        private BukkitNotice errorNumberFormatException = BukkitNotice.of(MinecraftNoticeType.CHAT, "&cPodana liczba nie jest cyfra.");
        private BukkitNotice errorYouHaveProtection = BukkitNotice.of(MinecraftNoticeType.CHAT,
                "&cPosiadasz aktualnie ochrone startową! Zostało &4{time}&c! Jeśli chcesz ją wyłączyć użyj &4/wylaczochrone&c.");
        private BukkitNotice errorAttackedPlayerHasProtection = BukkitNotice.of(MinecraftNoticeType.CHAT,
                "&cTen gracz posiada aktualnie ochrone startową! Zostało &4{time}&c!");
        private BukkitNotice errorCannotDamageWhileInCreative = BukkitNotice.of(MinecraftNoticeType.CHAT,
                "&cNie możesz atakować graczy podczas bycia w trybie kreatywnym!");
        private BukkitNotice errorCannotUseCommandWhileInCombat = BukkitNotice.of(MinecraftNoticeType.CHAT,
                "&cNie możesz użyć tej komendy podczas walki!");

        private BukkitNotice successReloaded = BukkitNotice.of(MinecraftNoticeType.CHAT, "&aPomyslnie przeladowano konfiguracje.");
        private BukkitNotice successChangedPvpStatus = BukkitNotice.of(MinecraftNoticeType.CHAT, "&aPomyslnie zmieniono status pvp na {status}.");

        private BukkitNotice announceLoggedOutWhileInCombat = BukkitNotice.of(MinecraftNoticeType.CHAT, "&cGracz &4{player} &cwylogowal sie podczas walki!");
    }

}