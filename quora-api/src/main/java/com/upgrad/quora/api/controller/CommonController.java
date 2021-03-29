package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.CommonControllerService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private CommonControllerService commonControllerService; // Added dependency in pom.xml

    @RequestMapping(method= RequestMethod.GET,path = "/userprofile/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUser(@PathVariable("userId") final String userUUID,
                                                       @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        // ResourceNotFoundException invokes RestException resourceNotFoundException method..
        String accessToken = authorization.split("Bearer")[0];
        final UserEntity userEntity = commonControllerService.getUser(userUUID,accessToken);
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse()
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .country(userEntity.getCountry())
                .aboutMe(userEntity.getAboutme())
                .dob(userEntity.getDob())
                .emailAddress(userEntity.getEmail())
                .contactNumber(userEntity.getContactnumber())
                .userName(userEntity.getUsername());


        //Return type is of ResponseEntity object..
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }
}
