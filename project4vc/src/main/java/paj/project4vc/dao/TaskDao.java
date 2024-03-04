package aor.paj.proj3_vc_re_jc.dao;


import aor.paj.proj3_vc_re_jc.entity.TaskEntity;
import aor.paj.proj3_vc_re_jc.entity.UserEntity;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;

@Stateless
public class TaskDao extends AbstractDao<TaskEntity> {

    private static final long serialVersionUID = 1L;

    @EJB
    private CategoryDao categoryDao;

    public TaskDao() {
        super(TaskEntity.class);
    }

    public TaskEntity findTaskById(int id) {
        try {
            return (TaskEntity) em.createNamedQuery("Task.findTaskById").setParameter("id", id)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public TaskEntity findTaskByIdAndUser(int id, String user) {
        try {
            return (TaskEntity) em.createNamedQuery("Task.findTaskByIdAndUser").setParameter("id", id).setParameter("creator", user)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public ArrayList<TaskEntity> findAllActiveTasks() {
        try {
            ArrayList<TaskEntity> taskEntityEntities = (ArrayList<TaskEntity>) em.createNamedQuery("Task.findAllActiveTasks").setParameter("deleted", false).getResultList();
            return taskEntityEntities;
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<TaskEntity> findTasksByUser(UserEntity userEntity) {
        try {
            ArrayList<TaskEntity> taskEntityEntities = (ArrayList<TaskEntity>) em.createNamedQuery("Task.findTasksByUser").setParameter("creator", userEntity).setParameter("deleted", false).getResultList();
            return taskEntityEntities;
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<TaskEntity> findTasksByDeleted() {
        try {
            ArrayList<TaskEntity> taskEntityEntities = (ArrayList<TaskEntity>) em.createNamedQuery("Task.findTasksByDeleted").setParameter("deleted", true).getResultList();
            return taskEntityEntities;
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<TaskEntity> findTasksByCategoryId(int categoryId) {
        try {
            String jpql = "SELECT t FROM TaskEntity t WHERE t.category.id = :categoryId AND t.deleted = false";
            TypedQuery<TaskEntity> query = em.createQuery(jpql, TaskEntity.class);
            query.setParameter("categoryId", categoryId);
            return new ArrayList<>(query.getResultList());
        } catch (Exception e) {
            return null;
        }
    }
}