package com.schoolmanagment.controller;

import com.schoolmanagment.payload.request.EducationTermRequest;
import com.schoolmanagment.payload.response.EducationTermResponse;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.service.EducationTermService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("educationTerms")
public class EducationTermController {

    private final EducationTermService educationTermService;

    //Not: Save() ***
    @PostMapping("/save") // http://localhost:8080/educationTerms/save
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<EducationTermResponse> save(@RequestBody @Valid EducationTermRequest educationTermRequest) {

        return educationTermService.save(educationTermRequest);
    }

    //Not: getById() ***
    @GetMapping("/{id}") // http://localhost:8080/educationTerms/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public EducationTermResponse get(@PathVariable Long id){

        return educationTermService.get(id);
    }

    //Not: getAll() ***
    @GetMapping("/getAll") // http://localhost:8080/educationTerms/getAll
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public List<EducationTermResponse> getAll(){

        return educationTermService.getAll();
    }

    //Not: getAllWithPage() ***
    @GetMapping("/search") // http://localhost:8080/educationTerms/search?page=0&size=10&sort=startDate&type=desc
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public Page<EducationTermResponse> getAllWithPage(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "date") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){

        return educationTermService.getAllWithPage(page,size,sort,type);
    }

    //Not: delete() ****
    @DeleteMapping("/delete/{educationId}") // http://localhost:8080/educationTerms/delete/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<?> delete(@PathVariable Long educationId){

        return educationTermService.delete(educationId);
    }

    @PutMapping("/update/{id}") // http://localhost:8080/educationTerms/update/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<EducationTermResponse> update(@RequestBody @Valid EducationTermRequest educationTermRequest
            ,@PathVariable Long id){

        return educationTermService.update(id,educationTermRequest);
    }
}