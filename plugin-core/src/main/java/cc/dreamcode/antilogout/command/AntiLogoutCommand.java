package cc.dreamcode.antilogout.command;

import cc.dreamcode.antilogout.AntiLogoutConfig;
import cc.dreamcode.command.bukkit.BukkitCommand;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public final class AntiLogoutCommand extends BukkitCommand {

    private final AntiLogoutConfig antiLogoutConfig;

    @Inject
    public AntiLogoutCommand(AntiLogoutConfig antiLogoutConfig) {
        super("antilogout");
        this.antiLogoutConfig = antiLogoutConfig;
    }

    @Override
    public void content(@NonNull CommandSender source, @NonNull String[] args) {
        if (args.length < 1) {
            return;
        }

        switch (args[0].toLowerCase()) {
            case "reload": {
                this.antiLogoutConfig.load(true);
                this.antiLogoutConfig.getMessageWrapper().getSuccessReloaded().send(source);
                break;
            }
            case "pvp": {
                boolean pvp = !this.antiLogoutConfig.getPluginWrapper().isPvpEnabled();
                this.antiLogoutConfig.getPluginWrapper().setPvpEnabled(pvp);
                this.antiLogoutConfig.getMessageWrapper().getSuccessReloaded().send(source, Collections.singletonMap("status", pvp ? "włączone" : "wyłączone"));
                break;
            }
        }
    }

    @Override
    public List<String> tab(@NonNull CommandSender source, @NonNull String[] args) {
        return null;
    }
}
