package athena;

import java.io.IOException;

import athena.commands.Command;
import athena.commands.ShutdownCommand;
import athena.exceptions.InputException;
import athena.parser.Parser;
import athena.storage.Storage;
import athena.tasks.TaskList;
import athena.ui.MainWindow;
import athena.ui.Messages;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Provides the point of entry into the Athena program. Initializes the program and handles
 * the main program logic.
 */
public class Athena extends Application {
    private static final String SAVE_DIRECTORY = "data";
    private static final String SAVE_FILENAME = "athena.txt";

    private TaskList taskList;
    private final Storage storage;
    private boolean isActive;

    /**
     * Initializes the program by loading task data from disk if any and initializing all
     * requisite objects and data structures.
     *
     * @throws IOException If unable to load from disk.
     */
    public Athena() throws IOException {
        storage = new Storage(SAVE_DIRECTORY, SAVE_FILENAME);
        initTaskList();
        assert taskList != null;
        isActive = true;
    }

    private void initTaskList() throws IOException {
        if (storage.hasExistingSave()) {
            taskList = storage.loadFromDisk();
        } else {
            taskList = new TaskList();
        }
    }

    public String getResponse(String input) {
        String response = "";
        try {
            Command command = Parser.getCommand(input);
            response = command.execute(taskList);
            assert !response.isEmpty();
            if (command instanceof ShutdownCommand) {
                isActive = false;
            }
        } catch (InputException e) { // return error message instead
            return e.getMessage();
        }

        try {
            saveIfTaskListModified();
        } catch (IOException e) {
            response += "\n" + Messages.SAVE_ERROR_MESSAGE + e.getMessage();
        }
        return response;
    }

    private void saveIfTaskListModified() throws IOException {
        if (taskList.wasModified()) {
            storage.saveToDisk(taskList);
            taskList.setNotModified();
        }
    }

    public boolean getIsActive() {
        return isActive;
    }

    @Override
    public void start(Stage stage) {
        try {
            stage = new MainWindow(new Athena());
            stage.show();
        } catch (IOException e) {
            System.out.println("Fatal error: Could not load file from disk.");
        }
    }
}
