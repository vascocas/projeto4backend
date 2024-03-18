package paj.project4vc.service;

import paj.project4vc.bean.CategoryBean;
import paj.project4vc.bean.TaskBean;
import paj.project4vc.bean.UserBean;
import paj.project4vc.dao.CategoryDao;
import paj.project4vc.dto.*;
import paj.project4vc.entity.CategoryEntity;
import paj.project4vc.enums.TaskPriority;
import paj.project4vc.enums.TaskState;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

@Path("/tasks")
public class TaskService {

    private static final long serialVersionUID = 1L;

    @Inject
    UserBean userBean;
    @Inject
    TaskBean taskBean;
    @Inject
    CategoryBean ctgBean;
    @EJB
    CategoryDao categoryDao;

    // Return all Tasks
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTasks(@HeaderParam("token") String token) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        } else {
            ArrayList<TaskDto> taskDtos = taskBean.getAllTasks();
            if (taskDtos != null) {
                return Response.status(200).entity(taskDtos).build();
            } else {
                return Response.status(404).entity("No tasks found").build();
            }
        }
    }

    // Return Task by Id
    @GET
    @Path("/{taskId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsersTasks(@HeaderParam("token") String token, @PathParam("taskId") int taskId) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        TaskDto task = taskBean.getTask(taskId);
        if (task != null) {
            return Response.status(200).entity(task).build();
        } else {
            return Response.status(404).entity("Task with this id not found").build();
        }
    }

    // Return all Tasks from user
    @GET
    @Path("/all/user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUserTasks(@HeaderParam("token") String token, @PathParam("userId") int userId) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        ArrayList<TaskDto> tasks = taskBean.getUserTasks(token, userId);
        if (tasks != null) {
            return Response.status(200).entity(tasks).build();
        } else {
            return Response.status(404).entity("No tasks found for this user").build();
        }
    }

    // Return all Tasks with same Category
    @GET
    @Path("/all/category/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCategoryTasks(@HeaderParam("token") String token, @PathParam("categoryId") int categoryId) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        ArrayList<TaskDto> tasks = taskBean.getCategoryTasks(token, categoryId);
        if (tasks != null) {
            return Response.status(200).entity(tasks).build();
        } else {
            return Response.status(404).entity("No category tasks found").build();
        }
    }

    // Return all deleted Tasks
    @GET
    @Path("/deletedTasks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDeletedTasks(@HeaderParam("token") String token) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        ArrayList<TaskDto> tasks = taskBean.getDeletedTasks(token);
        if (tasks != null) {
            return Response.status(200).entity(tasks).build();
        } else {
            return Response.status(404).entity("No deleted tasks found").build();
        }
    }

    // Method to check if the provided priority is valid
    private boolean isValidPriority(TaskPriority priority) {
        for (TaskPriority validPriority : TaskPriority.values()) {
            if (validPriority == priority) {
                return true;
            }
        }
        return false;
    }

    // Method to check if the provided state is valid
    private boolean isValidState(TaskState state) {
        for (TaskState validState : TaskState.values()) {
            if (validState == state) {
                return true;
            }
        }
        return false;
    }

    // Add Task
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTask(@HeaderParam("token") String token, TaskDto task) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        if (task.getTitle() == null || task.getTitle().isEmpty()) {
            return Response.status(400).entity("Task title cannot be empty").build();
        }
        if (task.getDescription() == null || task.getDescription().isEmpty()) {
            return Response.status(400).entity("Task description cannot be empty").build();
        }
        // Set default priority if not provided or invalid
        if (task.getPriority() == null || !isValidPriority(task.getPriority())) {
            task.setPriority(TaskPriority.LOW_PRIORITY);
        }
        // Perform date validation
        if (task.getStartDate() == null || task.getEndDate() == null) {
            return Response.status(400).entity("Both start date and end date must be provided").build();
        }
        try {
            LocalDate startDate = task.getStartDate();
            LocalDate endDate = task.getEndDate();

            if (!endDate.isAfter(startDate) && !endDate.isEqual(startDate)) {
                return Response.status(400).entity("End date must be after start date").build();
            }
        } catch (DateTimeParseException e) {
            return Response.status(400).entity("Invalid date format").build();
        }
        // Check if category exists
        CategoryEntity category = categoryDao.findCategoryByName(task.getCategory());
        if (category == null) {
            return Response.status(400).entity("Category does not exist").build();
        }
        TaskDto newTask = taskBean.addTask(token, task);
        if (newTask !=null) {
            return Response.status(200).entity(newTask).build();
        } else {
            return Response.status(404).entity("Impossible to create task. Verify all fields").build();
        }
    }

    // Update Task (Edit the contents of the task)
    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTask(@HeaderParam("token") String token, TaskDto task) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        if (task.getTitle() == null || task.getTitle().isEmpty()) {
            return Response.status(400).entity("Task title cannot be empty").build();
        }
        if (task.getDescription() == null || task.getDescription().isEmpty()) {
            return Response.status(400).entity("Task description cannot be empty").build();
        }
        // Set default priority if not provided or invalid
        if (task.getPriority() == null || !isValidPriority(task.getPriority())) {
            task.setPriority(TaskPriority.LOW_PRIORITY);
        }
        // Perform date validation
        if (task.getStartDate() == null || task.getEndDate() == null) {
            return Response.status(400).entity("Both start date and end date must be provided").build();
        }
        try {
            LocalDate startDate = task.getStartDate();
            LocalDate endDate = task.getEndDate();

            if (!endDate.isAfter(startDate) && !endDate.isEqual(startDate)) {
                return Response.status(400).entity("End date must be after start date").build();
            }
        } catch (DateTimeParseException e) {
            return Response.status(400).entity("Invalid date format").build();
        }
        // Check if category exists
        CategoryEntity category = categoryDao.findCategoryByName(task.getCategory());
        if (category == null) {
            return Response.status(400).entity("Category does not exist").build();
        }
        TaskDto returnTask = taskBean.updateTask(token, task, category);
        if (returnTask != null) {
            return Response.status(200).entity(returnTask).build();
        } else {
            return Response.status(404).entity("Impossible to edit task. Verify all fields").build();
        }
    }

    // Update Task Status (Move task between columns)
    @PUT
    @Path("/status")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTaskStatus(@HeaderParam("token") String token, TaskStateDto newStatus) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        // Set default state if not provided or invalid
        if (newStatus.getState() == null || !isValidState(newStatus.getState())) {
            newStatus.setState(TaskState.TODO);
        }
        if (taskBean.updateTaskStatus(newStatus)) {
            return Response.status(200).entity("Task status updated successfully").build();
        } else {
            return Response.status(404).entity("Impossible to update task status. Task not found or invalid status").build();
        }
    }

    // Remove Task (Recycle bin)
    @PUT
    @Path("/updateDeleted")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateDeleted(@HeaderParam("token") String token, @HeaderParam("taskId") int taskId) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        if (taskBean.removeTask(token, taskId)) {
            return Response.status(200).entity("Task delete successfully").build();
        } else {
            return Response.status(404).entity("Impossible to delete task.").build();
        }
    }

    // Restore Task from Recycle bin
    @PUT
    @Path("/restoreDeleted")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRestoreTask(@HeaderParam("token") String token, @HeaderParam("taskId") int taskId) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        if (taskBean.restoreDeletedTask(token, taskId)) {
            return Response.status(200).entity("Task restored successfully").build();
        } else {
            return Response.status(404).entity("Impossible to restored task.").build();
        }
    }

    // Remove Task Permanently
    @DELETE
    @Path("/{taskId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeTask(@HeaderParam("token") String token, @PathParam("taskId") int taskId) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        if (taskBean.removeTaskPermanently(token, taskId)) {
            return Response.status(200).entity("Task deleted permanently").build();
        }
        return Response.status(404).entity("Impossible to delete task.").build();
    }

    // Remove all Tasks from user (Recycle bin)
    @PUT
    @Path("/updateDeleted/userTasks")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAllUserTasks(@HeaderParam("token") String token, RoleDto user) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        if (taskBean.removeAllUserTasks(token, user)) {
            return Response.status(200).entity("Tasks deleted successfully").build();
        } else {
            return Response.status(404).entity("No tasks found to delete.").build();
        }
    }

    // Return all Categories
    @GET
    @Path("/categories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCategories(@HeaderParam("token") String token) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        ArrayList<CategoryDto> categories = ctgBean.getAllCategories();
        if (categories != null) {
            return Response.status(200).entity(categories).build();
        } else {
            return Response.status(404).entity("No task categories found").build();
        }
    }

    // Add Task Category
    @POST
    @Path("/category")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCategory(@HeaderParam("token") String token, CategoryDto category) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        if (category == null || category.getName() == null || category.getName().isEmpty()) {
            return Response.status(400).entity("Category name cannot be empty").build();
        }
        if (ctgBean.addCategory(token, category)) {
            return Response.status(200).entity("Task category added successfully").build();
        } else {
            return Response.status(404).entity("Impossible to add task category.").build();
        }
    }

    // Remove Task Category
    @DELETE
    @Path("/category")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeCategory(@HeaderParam("token") String token, CategoryDto category) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        if (ctgBean.removeCategory(token, category)) {
            return Response.status(200).entity("Task category removed successfully").build();
        } else {
            return Response.status(404).entity("Impossible to remove task category.").build();
        }
    }

    // Update Task Category
    @PUT
    @Path("/category")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCategory(@HeaderParam("token") String token, CategoryDto category) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        if (category == null || category.getName() == null || category.getName().isEmpty()) {
            return Response.status(400).entity("Category name cannot be empty").build();
        }
        if (ctgBean.updateCategoryName(token, category)) {
            return Response.status(200).entity("Task category updated successfully").build();
        } else {
            return Response.status(404).entity("Impossible to update task category.").build();
        }
    }
}