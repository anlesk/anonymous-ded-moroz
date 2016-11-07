package ru.aleskovets.adm.telegram.messages;

/**
 * Created by ad on 10/31/2016.
 */
public class Messages {

    public static final String NOT_A_PARTICIPANT = "You are not a participant. Do /participate to participate in event.";
    public static final String ALREADY_PARTICIPATING = "You are already participating!";
    public static final String NO_FREE_PARTICIPANTS_TO_ROLL = "No participant to choose. Please wait for more participants to join.";
    public static final String NOT_ENOUGH_PARTICIPANTS = "Not all participants are in the game. Please wait for more participants to join.";

    public static final String PARTICIPANTS_FILE_SAVE_ERROR = "Unable to save file.";
    public static final String PARTICIPANTS_FILE_LOAD_ERROR = "Unable to load file.";

    public static final String PARTICIPATING_SUCCESS = "You are participant now! Find yourself in a /list. Or do /roll to get your target.";
    public static final String CLEAR_INSTRUCTIONS = "You are about to clear participants list.\nWrite the following to confirm:\nYes, i want to remove these bastards.";
    public static final String CLEAR_CONFIRMATION = "Participant's list has been cleared.";
    public static final String CLEAR_RESTRICTED = "Clear is not allowed!";
}
