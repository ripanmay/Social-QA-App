package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerControllerService {

    @Autowired
    private AnswerDao answerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createQuestion(AnswerEntity answerEntity)
    {
        return answerDao.createAnswer(answerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateAnswer(AnswerEntity answerEntity)
    {

        answerDao.updateAnswer(answerEntity);
    }

    public AnswerEntity getAnswerById(final String AnswerUUID) throws AnswerNotFoundException {

        AnswerEntity answer = answerDao.getAnswerById(AnswerUUID);

        if(answer == null)
        {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        return  answer;

    }

    public boolean canEditOrDelete(final AnswerEntity answer, final UserEntity user, boolean isDelete  ) throws AuthorizationFailedException {
        if(answer.getUser().getUuid() != user.getUuid())
        {
            if(isDelete == false) {
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
            }

            if(user.getRole().equals("nonadmin") )
            {
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
            }

        }

        return true;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnswer(AnswerEntity answerEntity)
    {
        answerDao.deleteAnswer(answerEntity);
    }

    public List<AnswerEntity> getAllAnswersByQuestion(QuestionEntity q){
        return answerDao.getAllAnswersByQuestion(q);
    }


}
