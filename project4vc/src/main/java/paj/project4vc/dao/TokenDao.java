package aor.paj.proj3_vc_re_jc.dao;

import aor.paj.proj3_vc_re_jc.entity.TokenEntity;
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