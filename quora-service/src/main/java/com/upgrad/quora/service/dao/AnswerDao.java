package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext // container managed
    EntityManager entityManager; // provided by dependency spring-boot-starter-data-jpa

    public AnswerEntity createAnswer(AnswerEntity answerEntity){
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public void updateAnswer(AnswerEntity answerEntity) {
        entityManager.merge(answerEntity);
    }

    public AnswerEntity getAnswerById(String answerUUID) {
        try {
            return entityManager
                    .createNamedQuery("answerByuuid", AnswerEntity.class)
                    .setParameter("uuid", answerUUID)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void deleteAnswer(AnswerEntity answerEntity) {
        entityManager.remove(answerEntity);
    }

    public List<AnswerEntity> getAllAnswersByQuestion(QuestionEntity q) {
        //Named Query could'nt be used because of multiple results
        return entityManager.createQuery("SELECT q from AnswerEntity q where q.question.uuid = :questionUUID", AnswerEntity.class)
                .setParameter("questionUUID", q.getUuid())
                .getResultList();

    }
}
