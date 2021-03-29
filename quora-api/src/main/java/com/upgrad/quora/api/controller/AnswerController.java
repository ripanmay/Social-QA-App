package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerControllerService;
import com.upgrad.quora.service.business.CommonControllerService;
import com.upgrad.quora.service.business.QuestionControllerService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {
    @Autowired
    private CommonControllerService commonControllerService;

    @Autowired
    private QuestionControllerService questionControllerService;

    @Autowired
    private AnswerControllerService answerControllerService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(
            @RequestHeader("authorization") final String authorization,
            @PathVariable("questionId") final String questionUUID,
            final AnswerRequest answerRequest)
            throws AuthorizationFailedException, InvalidQuestionException {
        String accessToken = authorization.split("Bearer")[0];

        UserEntity user = commonControllerService.getUser(accessToken, "ATHR-002", "User is signed out.Sign in first to post an answer");

       //Expection message pending by himank
        final QuestionEntity questionEntity = questionControllerService.getQuestionById(questionUUID);

        AnswerEntity answer = new AnswerEntity();
        answer.setAns(answerRequest.getAnswer());
        answer.setUser(user);
        answer.setQuestion(questionEntity);
        answer.setDate(LocalDateTime.now());
        answer.setUuid(UUID.randomUUID().toString());
        final AnswerEntity createdAnswerEntity = answerControllerService.createQuestion(answer);

        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid()).status("ANSWER CREATED");

        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);


    }

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(@RequestHeader("authorization") final String authorization,
                                                             @PathVariable("answerId") final String answerUUID,
                                                             final AnswerEditRequest answerRequest) throws AuthorizationFailedException, AnswerNotFoundException {
        String accessToken = authorization.split("Bearer")[0];

        final UserEntity userEntity = commonControllerService.getUser(accessToken, "ATHR-002", "User is signed out.Sign in first to edit an answer");

        final AnswerEntity answerEntity = answerControllerService.getAnswerById(answerUUID);

        answerControllerService.canEditOrDelete(answerEntity, userEntity, false);

        AnswerEntity updateAnswer = new AnswerEntity();
        updateAnswer.setUser(userEntity);
        updateAnswer.setAns(answerRequest.getContent());
        updateAnswer.setUuid(answerEntity.getUuid());
        updateAnswer.setId(answerEntity.getId());
        updateAnswer.setDate(answerEntity.getDate());
        updateAnswer.setQuestion(answerEntity.getQuestion());

        answerControllerService.updateAnswer(updateAnswer);


        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(answerUUID).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);


    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(
            @RequestHeader("authorization") final String authorization,
            @PathVariable("answerId") final String answerUUID  ) throws AuthorizationFailedException, AnswerNotFoundException  {

        String accessToken = authorization.split("Bearer")[0];

        final UserEntity userEntity = commonControllerService.getUser(accessToken, "ATHR-002", "User is signed out.Sign in first to delete an answer");

        final AnswerEntity answerEntity = answerControllerService.getAnswerById(answerUUID);

        answerControllerService.canEditOrDelete(answerEntity, userEntity, true);

        answerControllerService.deleteAnswer(answerEntity);


        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answerUUID).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);


    }

    @RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersByUser(@RequestHeader("authorization") final String authorization,
                                                                           @PathVariable("questionId") final String questionUUID) throws AuthorizationFailedException, UserNotFoundException, InvalidQuestionException, AnswerNotFoundException {
        String accessToken = authorization.split("Bearer")[0];

        final UserEntity userEntity = commonControllerService.getUser(accessToken, "ATHR-002", "User is signed out.Sign in first to get the answers");

        QuestionEntity questionEntity = questionControllerService.getQuestionById(questionUUID);

        List<AnswerEntity> allAnswers = answerControllerService.getAllAnswersByQuestion(questionEntity);
        if(allAnswers.isEmpty()){
            throw new AnswerNotFoundException("ANS-001", "No Answers posted for the question");
        }

        List<AnswerDetailsResponse> qResponseList = new ArrayList<>();

        for (AnswerEntity q : allAnswers) {
            qResponseList.add(new AnswerDetailsResponse().id(q.getUuid()).answerContent(q.getAns()).questionContent(q.getQuestion().getContent()));
        }

        return new ResponseEntity<List<AnswerDetailsResponse>>(qResponseList, HttpStatus.OK);

    }
}
