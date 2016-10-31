package ru.aleskovets.adm.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.objects.User;
import ru.aleskovets.adm.telegram.controller.ParticipantController;
import ru.aleskovets.adm.telegram.messages.Messages;
import ru.aleskovets.adm.telegram.model.Participant;
import ru.aleskovets.adm.telegram.utils.ParticipantUtils;
import ru.skuptsov.telegram.bot.platform.client.TelegramBotApi;
import ru.skuptsov.telegram.bot.platform.client.command.MessageResponse;
import ru.skuptsov.telegram.bot.platform.handler.annotation.MessageHandler;
import ru.skuptsov.telegram.bot.platform.handler.annotation.MessageMapping;
import ru.skuptsov.telegram.bot.platform.model.UpdateEvent;

import javax.annotation.PostConstruct;

import static ru.skuptsov.telegram.bot.platform.client.command.MessageResponse.sendMessage;

/**
 * Created by ad on 10/29/2016.
 */
@MessageHandler
public class ADMBot {

    @Autowired
    private ParticipantController participantController;
    @Autowired
    private TelegramBotApi telegramBotApi;
    private String botName;

    @PostConstruct
    private void loadBotName() {
        botName = telegramBotApi
                .getMe()
                .get()
                .getUserName();
    }

    @MessageMapping(regexp = "/me(@.*)?")
    public MessageResponse getMe(UpdateEvent updateEvent) {
        User me = updateEvent.getUpdate().getMessage().getFrom();
        StringBuilder sb = new StringBuilder();
        sb.append("First Name: ").append(me.getFirstName())
                .append("\nLast Name: ").append(me.getLastName())
                .append("\nUsername: ").append(me.getUserName())
                .append("\nId: ").append(me.getId());

        return sendMessage(sb.toString(), updateEvent)
                .setCallback(System.out::println);
    }

    @MessageMapping(regexp = "/list(@.*)?")
    public MessageResponse showParticipants(UpdateEvent updateEvent) {
        return sendMessage(participantController.showParticipants(), updateEvent);
    }

    @MessageMapping(regexp = "/participate(@.*)?")
    public MessageResponse participate(UpdateEvent updateEvent) {
        try {
            participantController.addParticipant(ParticipantUtils
                    .buildParticipant(updateEvent.getUpdate().getMessage().getFrom())
            );
            return sendMessage(Messages.PARTICIPATING_SUCCESS, updateEvent);
        } catch (IllegalArgumentException e) {
            return sendMessage(e.getMessage(), updateEvent);
        }
    }

    @MessageMapping(regexp = "/roll(@.*)?")
    public MessageResponse roll(UpdateEvent updateEvent) {
        try {
            Participant participant = participantController
                    .selectRandomParticipant(ParticipantUtils
                            .buildParticipant(updateEvent.getUpdate().getMessage().getFrom())
                    );
            return sendMessage("Your target is:\n" + participant.getName(), updateEvent);
        } catch (IllegalArgumentException e) {
            return sendMessage(e.getMessage(), updateEvent);
        }
    }

    @MessageMapping(regexp = "/clear(@.*)?")
    public MessageResponse clear(UpdateEvent updateEvent) {
        return sendMessage(Messages.CLEAR_INSTRUCTIONS, updateEvent);
    }

    @MessageMapping(text = "Yes, i want to remove these bastards.")
    public MessageResponse confirmClear(UpdateEvent updateEvent) {
        participantController.clearParticipants();
        return sendMessage(Messages.CLEAR_CONFIRMATION, updateEvent);
    }
}
