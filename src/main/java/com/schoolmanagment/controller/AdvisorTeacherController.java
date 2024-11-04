package com.schoolmanagment.controller;

import com.schoolmanagment.entity.concretes.Admin;
import com.schoolmanagment.entity.concretes.AdvisorTeacher;
import com.schoolmanagment.payload.response.AdvisorTeacherResponse;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.service.AdvisorTeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("advisorTeacher")
public class AdvisorTeacherController {

    private final AdvisorTeacherService advisorTeacherService;


    //Not: deleteAdvisorTeacher() ***
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<?> deleteAdvisorTeacher(@PathVariable Long id){
        return advisorTeacherService.deleteAdvisorTeacher(id);
    }

    //Not: getAllAdvisorTeacher() ***
    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public List<AdvisorTeacherResponse> getAllAdvisorTeacher(){
        return advisorTeacherService.getAllAdvisorTeacher();
    }

    //Not: getAllAdvisorTeacherWithPage() ***
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Page<AdvisorTeacherResponse> search(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "date") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){
        return advisorTeacherService.search(page,size,sort,type);
    }

}