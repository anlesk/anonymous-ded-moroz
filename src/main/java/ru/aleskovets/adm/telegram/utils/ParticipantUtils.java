package ru.aleskovets.adm.telegram.utils;

import org.telegram.telegrambots.api.objects.User;
import ru.aleskovets.adm.telegram.model.Participant;

/**
 * Created by ad on 10/31/2016.
 */
public class ParticipantUtils {

    public static Participant buildParticipant(User user) {
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String nick = user.getUserName();
        Integer id = user.getId();

        String name = "unknown?!";
        if (firstName != null && !firstName.isEmpty()) name = firstName;
        if (lastName != null && !lastName.isEmpty()) name += " " + lastName;

        Participant p = new Participant();
        p.setName(name);
        p.setId(id);
        p.setNick(nick);

        return p;
    }
}
