package task.service;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    InMemoryTaskManagerTest() {
        super(Managers.getDefaultTaskManager());
    }
}