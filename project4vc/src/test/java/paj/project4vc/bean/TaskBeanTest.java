package paj.project4vc.bean;


import org.hibernate.grammars.hql.HqlParser;
import paj.project4vc.dao.CategoryDao;
import paj.project4vc.dao.TaskDao;
import paj.project4vc.dao.UserDao;
import paj.project4vc.dto.TaskDto;
import paj.project4vc.dto.TaskStateDto;
import paj.project4vc.entity.CategoryEntity;
import paj.project4vc.entity.TaskEntity;
import paj.project4vc.entity.TokenEntity;
import paj.project4vc.entity.UserEntity;
import paj.project4vc.enums.TaskPriority;
import paj.project4vc.enums.TaskState;
import paj.project4vc.enums.UserRole;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

class TaskBeanTest {
    TaskBean taskBean;
    TaskDao taskDao;
    UserDao userDao;
    CategoryDao categoryDao;
    Set<TaskEntity> userTasksSet;
    Set<TokenEntity> userTokensSet;
    Set<TaskEntity> categoryTasksSet;
    ArrayList<TaskEntity> tasksList;

    @BeforeEach
    void setup() {

        // The class under test
        taskBean = new TaskBean();

        // Creating mock objects
        taskDao = mock(TaskDao.class);
        userDao = mock(UserDao.class);
        categoryDao = mock(CategoryDao.class);

        // taskBean uses the mock object previously created
        taskBean.setTaskDao(taskDao);
        taskBean.setUserDao(userDao);
        taskBean.setCategoryDao(categoryDao);

        // Preparation: create one user
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("userTest");
        userEntity.setPassword("password");
        userEntity.setTokens(userTokensSet);

        userEntity.setEmail("test@dei.uc.pt");
        userEntity.setFirstName("FirstName");
        userEntity.setLastName("LastName");
        userEntity.setPhone("963963963");
        userEntity.setPhoto("https://example.com/profile_photos/random_user123.jpg");
        userEntity.setDeleted(false);
        userEntity.setRole(UserRole.PRODUCT_OWNER);
        userEntity.setTasks(userTasksSet);

        // Create one task category
        CategoryEntity ctgEntity = new CategoryEntity();
        ctgEntity.setId(1);
        ctgEntity.setCategoryName("Frontend");
        ctgEntity.setTasks(categoryTasksSet);

        // Create one task
        TaskEntity tEntity = new TaskEntity();
        tEntity.setId(1);
        tEntity.setTitle("Tarefa_teste1");
        tEntity.setDescription("Esta tarefa serve para testes");
        // Creating a start date
        LocalDate startDate = LocalDate.of(2024, 3, 1);
        tEntity.setStartDate(startDate);
        // Creating an end date
        LocalDate endDate = LocalDate.of(2024, 3, 31);
        tEntity.setEndDate(endDate);
        tEntity.setPriority(TaskPriority.LOW_PRIORITY);
        tEntity.setCreator(userEntity);
        tEntity.setCategory(ctgEntity);
        tEntity.setState(TaskState.TODO);
        tEntity.setDeleted(false);

        // Create a list of tasks
        userTasksSet = new HashSet<TaskEntity>();
        categoryTasksSet = new HashSet<TaskEntity>();
        tasksList = new ArrayList<TaskEntity>();
        userTasksSet.add(tEntity);
        categoryTasksSet.add(tEntity);
        tasksList.add(tEntity);

        userEntity.setTasks(userTasksSet);
        ctgEntity.setTasks(categoryTasksSet);


        // Define behaviours
        when(taskDao.find(0)).thenReturn(null);
        when(taskDao.findTasksByUser(userEntity)).thenReturn(tasksList);
        when(taskDao.findTaskById(1)).thenReturn(tEntity);
        when(taskDao.findTaskById(0)).thenReturn(null);
        when(taskDao.findTaskById(5)).thenReturn(null);
        when(userDao.findUserByUsername("userTest")).thenReturn(userEntity);
        when(userDao.findUserByUsername("noUser")).thenReturn(null);
        when(userDao.findUserByToken("token_id")).thenReturn(userEntity);
        when(userDao.findUserByToken("token123")).thenReturn(null);
        when(categoryDao.findCategoryByName("Frontend")).thenReturn(ctgEntity);
        when(categoryDao.findCategoryByName("NonexistentCategory")).thenReturn(null);
        when(taskDao.findTasksByDeleted()).thenReturn(null);
        doNothing().when(taskDao).persist(isA(TaskEntity.class));
    }

    @Test
    void testGetTaskById() {
        // Tests
        assertEquals(taskBean.getTask(1).getTitle(), "Tarefa_teste1");
        assertEquals(taskBean.getTask(1).getDescription(), "Esta tarefa serve para testes");
        assertNull(taskBean.getTask(0), "There is no task with this id, then it should return null");

        // Verifications
        // Verifies whether findTaskById() is called
        verify(taskDao, atLeastOnce()).findTaskById(1);

        // Verifies whether findTaskById() is called
        verify(taskDao, atLeastOnce()).findTaskById(0);
    }

    @Test
    void testMoveTaskColumn() {
        TaskEntity taskEntity = new TaskEntity();
        TaskStateDto newGoodState = new TaskStateDto(1, TaskState.DOING);
        TaskStateDto newBadState = new TaskStateDto(1, TaskState.DOING);

        // Tests
        taskBean.updateTaskStatus(newGoodState);
        assertEquals(TaskState.DOING, taskDao.findTaskById(1).getState());
        assertFalse(taskBean.updateTaskStatus(newBadState));

        // Verifications
        verify(taskDao, atLeast(2)).findTaskById(1);
        verify(taskDao, never()).findTaskById(0);
        verify(taskDao, atLeastOnce()).findTaskById(5);
    }

    @Test
    void testPersistTask() {
        // tests
        // Creating a start date
        LocalDate startDate = LocalDate.of(2024, 3, 1);
        // Creating an end date
        LocalDate endDate = LocalDate.of(2024, 3, 31);

        TaskDto testDto = new TaskDto(8, "Task_test", "Description for Task", TaskState.TODO, TaskPriority.MEDIUM_PRIORITY,
                startDate, endDate, false, "Frontend");

        assertTrue(taskBean.addTask("token_id", testDto));
        ArgumentCaptor<TaskEntity> taskEntityCaptor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(taskDao, times(1)).persist(taskEntityCaptor.capture());
        TaskEntity capturedTaskEntity = taskEntityCaptor.getValue();
        assertEquals("Task_test", capturedTaskEntity.getTitle());
        assertEquals("Description for Task", capturedTaskEntity.getDescription());
        assertEquals(TaskState.TODO, capturedTaskEntity.getState());
        assertEquals(TaskPriority.MEDIUM_PRIORITY, capturedTaskEntity.getPriority());
        assertEquals("2024-03-01", capturedTaskEntity.getStartDate());
        assertEquals("2024-03-10", capturedTaskEntity.getEndDate());
        assertFalse(capturedTaskEntity.isDeleted());
        assertEquals("Frontend", capturedTaskEntity.getCategory().getCategoryName());

        // Verifications
        // Verifies whether persist is called
        verify(taskDao, times(1)).persist(isA(TaskEntity.class));
        // verifies whether findUserByToken() is called
        verify(userDao, times(1)).findUserByToken(ArgumentMatchers.any(String.class));
        verify(userDao, times(1)).findUserByToken("token_id");
    }

    @Test
    void testUpdateTask() {
        // Creating a start date
        LocalDate startDate = LocalDate.of(2024, 3, 1);
        // Creating an end date
        LocalDate endDate = LocalDate.of(2024, 3, 31);
        // Tests
        TaskDto taskDto = new TaskDto(1, "UpdatedTask", "Updated Description", TaskState.DOING, TaskPriority.HIGH_PRIORITY,
                startDate, endDate, false, "Frontend");
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(1);
        categoryEntity.setCategoryName("Frontend");

        assertTrue(taskBean.updateTask("token_id", taskDto, categoryEntity));

        // Verifications
        // Verifies whether findTaskById() is called
        verify(taskDao, times(1)).findTaskById(1);
        // Verifies whether findUserByToken() is called
        verify(userDao, times(1)).findUserByToken("token_id");
        // Verifies whether findCategoryByName() is called
        verify(categoryDao, times(0)).findCategoryByName("Frontend");
    }
}