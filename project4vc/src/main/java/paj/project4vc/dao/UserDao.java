package paj.project4vc.dao;

import paj.project4vc.entity.UserEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import java.util.ArrayList;

@Stateless
public class UserDao extends AbstractDao<UserEntity> {

    private static final long serialVersionUID = 1L;

    public UserDao() {
        super(UserEntity.class);
    }

    public UserEntity findUserByToken(String token) {
        try {
            return (UserEntity) em.createNamedQuery("User.findUserByToken").setParameter("token", token)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public UserEntity findUserByUsername(String username) {
        try {
            return (UserEntity) em.createNamedQuery("User.findUserByUsername").setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public UserEntity findUserById(int id) {
        try {
            return (UserEntity) em.createNamedQuery("User.findUserById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public ArrayList<UserEntity> allUsers() {
        try {
            ArrayList<UserEntity> userEntityEntities = (ArrayList<UserEntity>) em.createNamedQuery("User.findAllUsers").getResultList();
            return userEntityEntities;
        } catch (Exception e) {
            return null;
        }
    }
}




