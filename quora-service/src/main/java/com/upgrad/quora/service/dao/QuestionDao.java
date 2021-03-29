package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext // container managed
    EntityManager entityManager; // provided by dependency spring-boot-starter-data-jpa

    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestions() {
        return entityManager.createQuery("SELECT q from QuestionEntity q", QuestionEntity.class).getResultList();
    }


    public List<QuestionEntity> getAllQuestionsByUser(UserEntity user) {
        //Named Query could'nt be used because of multiple results
        return entityManager.createQuery("SELECT q from QuestionEntity q where q.user.uuid = :userUUID", QuestionEntity.class)
                .setParameter("userUUID", user.getUuid())
                .getResultList();

    }

    public QuestionEntity getQuestionById(final String QuestionUUID) {

        try {
            return entityManager
                    .createNamedQuery("questionByuuid", QuestionEntity.class)
                    .setParameter("uuid", QuestionUUID)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }


    }

    public void updateQuestion(QuestionEntity questionEntity) {
        entityManager.merge(questionEntity);
    }


    public void deleteQuestion(QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
    }
}
