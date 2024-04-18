package task.models;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import task.service.FileBackedTaskManager;
import task.service.Managers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatusTest {
    private static final FileBackedTaskManager MANAGER = Managers.getFileBackedTaskManager();

    @Test
    @Order(1)
    public void calculateEpicStatus() {

        Epic epic = new Epic();
        epic.setStatus(Status.NEW);
        epic.setName("Купить дом.");
        epic.setDescription("Начать выбирать новое жильё.");
        epic.setStartTime(LocalDateTime.now());
        MANAGER.addNewEpic(epic);

        final Integer idEpic = epic.getId();

        Subtask subtask = new Subtask();
        subtask.setStatus(Status.NEW);
        subtask.setName("test_subtask");
        subtask.setDescription("test");
        subtask.setStartTime(LocalDateTime.now().plusMinutes(30));
        MANAGER.addNewSubtask(subtask, idEpic);

        subtask = new Subtask();
        subtask.setStatus(Status.NEW);
        subtask.setName("test2_subtask");
        subtask.setDescription("test2");
        subtask.setStartTime(LocalDateTime.now().plusMinutes(60));
        MANAGER.addNewSubtask(subtask, idEpic);

        assertEquals(epic.getStatus(), Status.NEW);

        MANAGER.getAllSubtasks().forEach(s -> {
            s.setStatus(Status.DONE);
            MANAGER.updateSubtask(s);
        });
        assertEquals(epic.getStatus(), Status.DONE);


        Optional<Subtask> firstSubtaskOpt = MANAGER.getAllSubtasks().stream().findFirst();
        if (firstSubtaskOpt.isPresent()) {
            Subtask firstSubtask = firstSubtaskOpt.get();
            firstSubtask.setStatus(Status.NEW);
            MANAGER.updateSubtask(firstSubtask);
        }
        assertEquals(epic.getStatus(), Status.IN_PROGRESS);

        MANAGER.getAllSubtasks().forEach(s -> {
            s.setStatus(Status.IN_PROGRESS);
            MANAGER.updateSubtask(s);
        });
        assertEquals(epic.getStatus(), Status.IN_PROGRESS);
    }

}