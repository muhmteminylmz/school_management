package com.schoolmanagment.payload.request;

import com.schoolmanagment.payload.request.abstracts.BaseUserRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class AdminRequest extends BaseUserRequest {

    private boolean built_in;
}