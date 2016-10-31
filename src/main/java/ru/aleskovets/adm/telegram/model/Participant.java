package ru.aleskovets.adm.telegram.model;

import java.util.Objects;

/**
 * Created by ad on 10/30/2016.
 */
public class Participant extends Entry {

    private Participant target;

    public Participant getTarget() {
        return target;
    }

    public void setTarget(Participant target) {
        this.target = target;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(getName())
                .append(" nick: ").append(getNick())
                .append(" (Id: ").append(getId()).append(")")
                .append(" target: ").append(getTarget() != null ? getTarget().getName() : null);

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Participant that = (Participant) o;

        return Objects.equals(getId(), that.getId());

    }

    @Override
    public int hashCode() {
        return 31 + (target != null ? target.hashCode() : 0);
    }
}
