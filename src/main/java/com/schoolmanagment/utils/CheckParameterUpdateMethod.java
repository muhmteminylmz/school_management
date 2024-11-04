package com.schoolmanagment.utils;


import com.schoolmanagment.entity.abstracts.User;
import com.schoolmanagment.payload.request.abstracts.BaseUserRequest;

public class CheckParameterUpdateMethod{

    public static boolean checkParameter(User user, BaseUserRequest baseUserRequest){

        return user.getSsn().equalsIgnoreCase(baseUserRequest.getSsn())
                || user.getPhoneNumber().equalsIgnoreCase(baseUserRequest.getPhoneNumber())
                || user.getUsername().equalsIgnoreCase(baseUserRequest.getUsername());
    }
}
