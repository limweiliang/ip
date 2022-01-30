package athena.commands;

import athena.tasks.TaskList;
import athena.ui.Ui;

public class ListCommand extends Command {
    @Override
    public void execute(Ui ui, TaskList taskList) {
        ui.showTaskList();
    }
}