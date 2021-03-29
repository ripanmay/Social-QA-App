package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {
    @PersistenceContext // container managed
    EntityManager entityManager; // provided by dependency spring-boot-starter-data-jpa
    public UserEntity createUser(UserEntity entity){
        entityManager.persist(entity);
        return entity;
    }

    public UserEntity getUser(final String userUuid){
        try{
            return entityManager
                    .createNamedQuery("userByUuid",UserEntity.class)
                    .setParameter("uuid",userUuid)
                    .getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    public UserEntity getUserByEmail(final String email){
        try{
            return entityManager
                    .createNamedQuery("userByEmail",UserEntity.class)
                    .setParameter("email",email)
                    .getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    public UserEntity getUserByName(final String username){
        try{
            return entityManager
                    .createNamedQuery("userByName",UserEntity.class)
                    .setParameter("username",username)
                    .getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    public void deleteUser(final UserEntity userEntity){
        entityManager.remove(userEntity);
    }


    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity){
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    public void updateAuthToken(final UserAuthTokenEntity updatedAuthTokenEntity){
        entityManager.merge(updatedAuthTokenEntity);
    }

    public void updateUser(final UserEntity updatedUserEntity){
        entityManager.merge(updatedUserEntity);
    }

    public UserAuthTokenEntity getUserAuthTokenEntity(final String userAuthToken){
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class)
                    .setParameter("accessToken", userAuthToken)
                    .getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }
}
