package ru.aleskovets.adm.telegram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ru.aleskovets.adm.telegram.config.ADMBotConfig;
import ru.aleskovets.adm.telegram.messages.Messages;
import ru.aleskovets.adm.telegram.model.Participant;
import ru.aleskovets.adm.telegram.model.Participants;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ad on 10/30/2016.
 */
@Component
public class ParticipantController {

    @Autowired
    private ADMBotConfig config;
    private List<Participant> participants;

    @PostConstruct
    private void loadParticipants() {
        final Constructor participantsConstructor = new Constructor(Participants.class);
        final TypeDescription participantsDescription = new TypeDescription(Participant.class);
        participantsDescription.putMapPropertyType("participants", Participants.class, Object.class);
        participantsConstructor.addTypeDescription(participantsDescription);
        Yaml yaml = new Yaml(participantsConstructor);
        Path path = Paths.get(config.getParticipantsFilePath());
        Participants participants = new Participants();

        try {
            participants = (Participants) yaml.load(Files.newInputStream(path));
        } catch (IOException e) {
            System.out.println(Messages.PARTICIPANTS_FILE_LOAD_ERROR);
        }

        List<Participant> participantsList = participants.getParticipants();
        if (participantsList == null) participantsList = new ArrayList<>();

        this.participants = participantsList;
    }

    private void saveParticipants() {
        try {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yaml = new Yaml(options);
            Path path = Paths.get(config.getParticipantsFilePath());
            if (!Files.exists(path)) Files.createDirectories(path.getParent());
            Writer writer = new FileWriter(config.getParticipantsFilePath());
            Participants participantsHolder = new Participants();
            participantsHolder.setParticipants(participants);
            yaml.dump(participantsHolder, writer);
        } catch (IOException e) {
            System.out.println(Messages.PARTICIPANTS_FILE_SAVE_ERROR);
        }
    }

    public String showParticipants() {
        return Stream.of(participants)
                .map(Object::toString)
                .collect(Collectors.joining(",  "));
    }

    public void addParticipant(Participant participant) {
        if (participantExists(participant)) throw new IllegalArgumentException(Messages.ALREADY_PARTICIPATING);

        participants.add(participant);
        saveParticipants();
    }

    public void clearParticipants() {
        participants = new ArrayList<>();
        saveParticipants();
    }

    public Participant selectRandomParticipant(Participant participant) {
        if (!participantExists(participant)) throw new IllegalArgumentException(Messages.NOT_A_PARTICIPANT);
        if (participant.getTarget() != null) return participant.getTarget();

        Participant result = participants
                .stream()
                .filter(p -> !p.equals(participant))
                .filter(p -> !p.isUsed())
                .sorted(Comparator.comparingInt(o -> System.identityHashCode(o) ^ new Random().nextInt()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(Messages.NO_FREE_PARTICIPANTS_TO_ROLL));

        result.setUsed(true);

        participants
                .stream()
                .filter(p -> p.equals(participant))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(Messages.NOT_A_PARTICIPANT))
                .setTarget(result);

        saveParticipants();
        return result;
    }

    private boolean participantExists(Participant participant) {
        return participants
                .stream()
                .filter(p -> p.equals(participant))
                .findFirst()
                .isPresent();
    }
}
