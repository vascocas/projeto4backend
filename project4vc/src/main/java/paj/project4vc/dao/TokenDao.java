package paj.project4vc.dao;

import paj.project4vc.entity.TokenEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

@Stateless
public class TokenDao extends AbstractDao<TokenEntity> {

    private static final long serialVersionUID = 1L;

    public TokenDao() {
        super(TokenEntity.class);
    }

    public TokenEntity findTokenById(String id) {
        try {
            return (TokenEntity) em.createNamedQuery("Token.findTokenById").setParameter("token", id).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}