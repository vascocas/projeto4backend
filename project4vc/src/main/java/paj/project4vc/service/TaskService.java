package aor.paj.proj3_vc_re_jc.service;

import aor.paj.proj3_vc_re_jc.bean.CategoryBean;
import aor.paj.proj3_vc_re_jc.bean.TaskBean;
import aor.paj.proj3_vc_re_jc.bean.UserBean;
import aor.paj.proj3_vc_re_jc.dao.CategoryDao;
import aor.paj.proj3_vc_re_jc.dto.*;
import aor.paj.proj3_vc_re_jc.entity.CategoryEntity;
import aor.paj.proj3_vc_re_jc.enums.TaskPriority;
import aor.paj.proj3_vc_re_jc.enums.TaskState;
import aor.paj.proj3_vc_re_jc.enums.UserRole;
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
        }
       else {

        return taskBean.getAllTasks();
    }
    }

    // Return Task by Id
    @GET
    @Path("/task")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsersTasks(@HeaderParam("token") String token, @HeaderParam("taskId") int taskId) {
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
    @Path("/userTasks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUserTasks(@HeaderParam("token") String token, @HeaderParam("username") String username) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        return taskBean.getUserTasks(token, username);
    }

    // Return all deleted Tasks
    @GET
    @Path("/deletedTasks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDeletedTasks(@HeaderParam("token") String token) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        return taskBean.getDeletedTasks(token);
    }

    // Return all Tasks with same Category
    @GET
    @Path("/categoryTasks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCategoryTasks(@HeaderParam("token") String token, @HeaderParam("categoryName") String categoryName) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        return taskBean.getCategoryTasks(token, categoryName);
    }

    // Add Task
    @POST
    @Path("/addTask")
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
        // Set default priority if not provided
        if (task.getPriority() == null) {
            task.setPriority(TaskPriority.LOW_PRIORITY);
        } else {
            // Validate if the provided priority is within the possible values
            try {
                TaskPriority.valueOf(task.getPriority().name());
            } catch (IllegalArgumentException e) {
                return Response.status(400).entity("Invalid priority input value").build();
            }
        }
        // Perform date validation
        if (task.getStartDate() == null || task.getEndDate() == null) {
            return Response.status(400).entity("Both start date and end date must be provided").build();
        }
        try {
            LocalDate startDate = LocalDate.parse(task.getStartDate());
            LocalDate endDate = LocalDate.parse(task.getEndDate());

            if (!endDate.isAfter(startDate)) {
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
        boolean added = taskBean.addTask(token, task);
        if (added) {
            return Response.status(201).entity("Task created successfully").build();
        } else {
            return Response.status(404).entity("Impossible to create task. Verify all fields").build();
        }
    }

    // Update Task (Edit the contents of the task)
    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
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
        // Validate if the provided priority is within the possible values
        try {
            TaskPriority.valueOf(task.getPriority().name());
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity("Invalid priority input value").build();
        }
        // Perform date validation
        if (task.getStartDate() == null || task.getEndDate() == null) {
            return Response.status(400).entity("Both start date and end date must be provided").build();
        }
        try {
            LocalDate startDate = LocalDate.parse(task.getStartDate());
            LocalDate endDate = LocalDate.parse(task.getEndDate());

            if (!endDate.isAfter(startDate)) {
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
        boolean updated = taskBean.updateTask(token, task, category);
        if (updated) {
            return Response.status(200).entity("Task updated successfully").build();
        } else {
            return Response.status(404).entity("Impossible to edit task. Verify all fields").build();
        }
    }

    // Update Task Status (Move task between columns)
    @PUT
    @Path("/status")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTaskStatus(@HeaderParam("token") String token, @HeaderParam("taskId") int taskId,
                                     UpdateTaskStateDto newStatus) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        // Validate if the provided task state is within the possible values
        if (newStatus == null || newStatus.getState() == null) {
            return Response.status(400).entity("Invalid task state input value").build();
        }
        try {
            TaskState.valueOf(newStatus.getState().name());
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity("Invalid task state input value").build();
        }
        boolean updated = taskBean.updateTaskStatus(taskId, newStatus.getState());
        if (updated) {
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
        return taskBean.removeTask(token, taskId);
    }

    // Restore Task from Recycle bin
    @PUT
    @Path("/restoreDeleted")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRestoreTask(@HeaderParam("token") String token, @HeaderParam("taskId") int taskId) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        return taskBean.restoreDeletedTask(token, taskId);
    }

    // Remove Task Permanently
    @DELETE
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeTask(@HeaderParam("token") String token, @HeaderParam("taskId") int taskId) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        return taskBean.removeTaskPermanently(token, taskId);

    }

    // Remove all Tasks from user (Recycle bin)
    @PUT
    @Path("/updateDeleted/userTasks")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAllUserTasks(@HeaderParam("token") String token) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        return taskBean.removeAllUserTasks(token);
    }

    // Return all Categories
    @GET
    @Path("/category/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCategories(@HeaderParam("token") String token) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        return ctgBean.getAllCategories();
    }

    // Add Task Category
    @POST
    @Path("/category/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCategory(@HeaderParam("token") String token, CategoryDto category) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        if (category == null || category.getName() == null || category.getName().isEmpty()) {
            return Response.status(400).entity("Category name cannot be empty").build();
        }
        return ctgBean.addCategory(token, category);
    }

    // Remove Task Category
    @DELETE
    @Path("/category/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeCategory(@HeaderParam("token") String token, CategoryDto category) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        return ctgBean.removeCategory(token, category);
    }

    // Update Task Category
    @PUT
    @Path("/category/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCategory(@HeaderParam("token") String token, CategoryDto category) {
        if (!userBean.tokenExist(token)) {
            return Response.status(401).entity("Invalid token").build();
        }
        if (category == null || category.getName() == null || category.getName().isEmpty()) {
            return Response.status(400).entity("Category name cannot be empty").build();
        }
        return ctgBean.updateCategoryName(token, category);
    }
}