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
import java.util.stream.IntStream;

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
        Participants participants = null;

        try {
            participants = (Participants) yaml.load(Files.newInputStream(path));
        } catch (IOException e) {
            System.out.println(Messages.PARTICIPANTS_FILE_LOAD_ERROR);
        }

        this.participants = participants != null ? participants.getParticipants() : new ArrayList<>();
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
        return IntStream.range(0, participants.size())
                .mapToObj(i -> (i + 1) + ". " + participants.get(i).getName())
                .collect(Collectors.joining("\n"));
    }

    public String showDetailedParticipants() {
        return participants
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n---\n"));
    }

    public void addParticipant(Participant participant) {
        if (participantExists(participant)) throw new IllegalArgumentException(Messages.ALREADY_PARTICIPATING);

        participants.add(participant);
        saveParticipants();
    }

    public void clearParticipants() {
        participants = participants
                .stream()
                .peek(p -> p.setTarget(null))
                .peek(p -> p.setUsed(false))
                .collect(Collectors.toList());

        saveParticipants();
    }

    public Participant selectRandomParticipant(Participant participant) {
        if (participants.size() != config.getExpectedNumber()) throw new IllegalArgumentException(Messages.NOT_ENOUGH_PARTICIPANTS);

        Participant user = participants
                .stream()
                .filter(p -> p.equals(participant))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(Messages.NOT_A_PARTICIPANT));

        if (user.getTarget() != null) return user.getTarget();

        Participant result = participants
                .stream()
                .filter(p -> !p.equals(participant))
                .filter(p -> !p.isUsed())
                .filter(p -> !participant.equals(p.getTarget()))
                .sorted(Comparator.comparingInt(o -> System.identityHashCode(o) ^ new Random().nextInt()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(Messages.NO_FREE_PARTICIPANTS_TO_ROLL));

        result.setUsed(true);
        user.setTarget(result);

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
