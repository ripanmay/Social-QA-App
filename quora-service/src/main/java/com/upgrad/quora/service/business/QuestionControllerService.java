package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionControllerService {

    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity)
    {
        return questionDao.createQuestion(questionEntity);
    }

  //  @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions(){
        return questionDao.getAllQuestions();
    }

    public List<QuestionEntity> getAllQuestionsByUser(UserEntity user){
        return questionDao.getAllQuestionsByUser(user);
    }

    public QuestionEntity getQuestionById(final String QuestionUUID) throws InvalidQuestionException {

        QuestionEntity question = questionDao.getQuestionById(QuestionUUID);

        if(question == null)
        {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        return  question;

    }

    @Transactional(propagation = Propagation.REQUIRED)
     public void updateQuestion(QuestionEntity questionEntity)
    {

        questionDao.updateQuestion(questionEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestion(QuestionEntity questionEntity)
    {
        questionDao.deleteQuestion(questionEntity);
    }





}
