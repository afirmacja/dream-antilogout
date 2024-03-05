package cc.dreamcode.antilogout.user;

import eu.okaeri.persistence.document.Document;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
public final class User extends Document {

    private String nickname;

    private long lastAttackTime, protection;

    public boolean isInCombat() {
        return this.lastAttackTime > System.currentTimeMillis();
    }

    public void resetCombat() {
        this.lastAttackTime = 0L;
    }

    public UUID getUniqueId() {
        return getPath().toUUID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getUniqueId(), this.nickname);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof User)) {
            return false;
        }

        User user = (User) object;
        return this.getUniqueId().equals(user.getUniqueId());
    }

    public boolean hasProtection() {
        return this.protection > System.currentTimeMillis();
    }
}
