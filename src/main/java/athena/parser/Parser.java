package athena.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import athena.commands.Command;
import athena.commands.DeadlineCommand;
import athena.commands.DeleteCommand;
import athena.commands.EventCommand;
import athena.commands.FindCommand;
import athena.commands.ListCommand;
import athena.commands.MarkCommand;
import athena.commands.ShutdownCommand;
import athena.commands.TodoCommand;
import athena.commands.UnmarkCommand;
import athena.exceptions.InputErrorCode;
import athena.exceptions.InputException;

/**
 * Encapsulates helper methods to parse user input given to Athena.
 */
public class Parser {
    private static final DateTimeFormatter INPUT_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/yyyy Hmm");

    /**
     * Returns a Command object based on the given input, which encapsulates the user
     * command in an object that can later be executed.
     *
     * @param input Raw user input given to Athena.
     * @return Command object encapsulating the user command.
     * @throws InputException If user input is invalid, such as when providing a non-existent command.
     */
    public static Command getCommand(String input) throws InputException {
        // Separate out the command keyword from the other arguments given
        String[] splitInput = input.split(" ", 2);
        String command = splitInput[0];
        String remainingInput = "";
        if (splitInput.length > 1) {
            remainingInput = splitInput[1];
        }

        // Generate command object to return
        switch (command) {
        case "todo":
            return new TodoCommand(getTaskNameFromInput(remainingInput));
            // No fallthrough necessary
        case "deadline":
            return new DeadlineCommand(getTaskNameFromInput(remainingInput),
                    getDateTimeFromInput(remainingInput, "/by"));
            // No fallthrough necessary
        case "event":
            return new EventCommand(getTaskNameFromInput(remainingInput),
                    getDateTimeFromInput(remainingInput, "/at"));
            // No fallthrough necessary
        case "mark":
            return new MarkCommand(getTaskNumberFromInput(remainingInput));
            // No fallthrough necessary
        case "unmark":
            return new UnmarkCommand(getTaskNumberFromInput(remainingInput));
            // No fallthrough necessary
        case "delete":
            return new DeleteCommand(getTaskNumberFromInput(remainingInput));
            // No fallthrough necessary
        case "find":
            return new FindCommand(getSearchPhraseFromInput(remainingInput));
        case "list":
            return new ListCommand();
            // No fallthrough necessary
        case "bye":
            return new ShutdownCommand();
            // No fallthrough necessary
        default:
            throw new InputException(InputErrorCode.INVALID_COMMAND);
            // No fallthrough necessary
        }
    }

    private static int getTaskNumberFromInput(String input) throws InputException {
        int taskNumber = -1;
        try {
            taskNumber = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new InputException(InputErrorCode.MISSING_TASK_NUMBER);
        }
        return taskNumber;
    }

    private static String getTaskNameFromInput(String input) throws InputException {
        // Remove excess input arising from extra fields like /by and /at
        input = input.split("/")[0].strip();
        if (input.isEmpty()) {
            throw new InputException(InputErrorCode.MISSING_TASK_NAME);
        }
        return input;
    }

    private static String getSearchPhraseFromInput(String input) throws InputException {
        if (input.isEmpty()) {
            throw new InputException(InputErrorCode.MISSING_SEARCH_PHRASE);
        }
        return input;
    }

    private static LocalDateTime getDateTimeFromInput(String input, String keyword)
            throws InputException {
        LocalDateTime dateTime;
        if (!input.contains(keyword)) {
            throw new InputException(InputErrorCode.MISSING_TASK_DATETIME);
        } else {
            // Split by keyword, take the latter part.
            String dateTimeString = input.split(keyword, 2)[1].strip();
            if (dateTimeString.isEmpty()) {
                throw new InputException(InputErrorCode.MISSING_TASK_DATETIME);
            } else {
                try {
                    dateTime = LocalDateTime.parse(dateTimeString, INPUT_FORMATTER);
                } catch (DateTimeParseException e) {
                    throw new InputException(InputErrorCode.INVALID_TASK_DATETIME);
                }
            }
        }
        return dateTime;
    }
}
