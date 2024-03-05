package cc.dreamcode.antilogout.user;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class UserCache {

    private final Map<UUID, User> userMap = Maps.newConcurrentMap();

    public void add(final User user) {
        this.userMap.put(user.getUniqueId(), user);
    }

    public void remove(final User user) {
        this.userMap.remove(user.getUniqueId());
    }

    public User findUserByUniqueId(final UUID uniqueId) {
        return this.userMap.get(uniqueId);
    }

    public User findUserByNickname(final String nickname, final boolean ignoreCase) {
        return this.userMap.values().stream().filter(user -> ignoreCase ? user.getNickname().equalsIgnoreCase(nickname) : user.getNickname().equals(nickname)).findFirst().orElse(null);
    }

    public User findUserByPlayer(final Player player) {
        return this.findUserByUniqueId(player.getUniqueId());
    }

    public Collection<User> values() {
        return Collections.unmodifiableCollection(this.userMap.values());
    }

}