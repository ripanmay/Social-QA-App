package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserControllerService;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserControllerService userControllerService; // Added dependency in pom.xml

    @Autowired
    private UserDao userDao;

    @RequestMapping(method = RequestMethod.POST,path = "/user/signup",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {

        final UserEntity userEntity = new UserEntity();

        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUsername(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setSalt("abc123");
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutme(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin"); // default
        userEntity.setContactnumber(signupUserRequest.getContactNumber());

        final UserEntity createdUserEntity = userControllerService.signup(userEntity);
        /* Constructing json response:
            1. User uuid.
            2. Status of the request.
         */
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED); // CREATED - code is 201..*/
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException, SignUpRestrictedException {
        /* Basic asdewdasceasdaisdiebdenceunudbas==
           username:passowrd --> base64 encoded format..
         */
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic")[0]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":"); //username:password

        UserAuthTokenEntity userAuthTokenEntity = userControllerService.signin(decodedArray[0], decodedArray[1]);
        UserEntity user = userAuthTokenEntity.getUser();

        /*
            Returning just the uuid of the user..
         */
        SigninResponse authorizedSignInResponse =  new SigninResponse().id(user.getUuid()).message("SIGNED IN SUCCESSFULLY");

        // passing access-token in headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthTokenEntity.getAccessToken());

        return new ResponseEntity<SigninResponse>(authorizedSignInResponse,headers, HttpStatus.CREATED); // CREATED - code is 201..*/
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> sigout(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException, SignUpRestrictedException, SignOutRestrictedException {

        String accessToken = authorization.split("Bearer")[0];
        UserEntity userEntity = userControllerService.signout(accessToken);
        SignoutResponse signoutResponse = new SignoutResponse().id(userEntity.getUuid()).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(signoutResponse,HttpStatus.CREATED); // CREATED - code is 201..*/
    }
}
