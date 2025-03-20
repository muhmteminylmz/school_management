package com.schoolmanagment.controller;

import com.schoolmanagment.entity.concretes.Admin;
import com.schoolmanagment.payload.request.ChooseLessonTeacherRequest;
import com.schoolmanagment.payload.request.TeacherRequest;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.payload.response.TeacherResponse;
import com.schoolmanagment.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("teachers")
public class TeacherController {

    private final TeacherService teacherService;

    //Not: save() ***
    @PostMapping("/save") // http://localhost:8080/teachers
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<TeacherResponse> save(@RequestBody @Valid TeacherRequest teacherRequest){

        return teacherService.save(teacherRequest);
    }

    //Not: getAll() ***
    @GetMapping("/getAll") // http://localhost:8080/teachers/getAll
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public List<TeacherResponse> getAllTeacher(){
        return teacherService.getAllTeacher();
    }

    //Not: updateTeacherById() ***
    @PutMapping("/update/{userId}") // http://localhost:8080/teachers/update/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<TeacherResponse> update(@RequestBody @Valid TeacherRequest teacher,
                                                   @PathVariable Long userId){
        return teacherService.update(teacher,userId);
    }

    //Not: getTeacherByName() ***
    @GetMapping("/getTeacherByName") // http://localhost:8080/teachers/getTeacherByName
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public List<TeacherResponse> getTeacherByName(@RequestParam(name = "name") String teacherName){
        return teacherService.getTeacherByName(teacherName);
    }

    //Not: deleteTeacher() ***
    @DeleteMapping("/delete/{id}") // http://localhost:8080/teachers/delete/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<?> deleteTeacher(@PathVariable Long id){
        return teacherService.deleteTeacher(id);
    }

    //Not: getTeacherById() ***
    @GetMapping("/getSavedTeacherById/{id}") // http://localhost:8080/teachers/getTeacherById
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<TeacherResponse> getSavedTeacherById(@PathVariable Long id){
        return teacherService.getSavedTeacherById(id);
    }

    //Not: getAllWithPage() ***
    @GetMapping("/search") // http://localhost:8080/teachers/search
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Page<TeacherResponse> search(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "name") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){
        return teacherService.search(page, size, sort, type);
    }

    //Not: addLessonProgramToTeachersLessonsProgram() ***
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @PostMapping("/chooseLesson") // http://localhost:8080/teachers/chooseLesson
    public ResponseMessage<TeacherResponse> chooseLesson(@RequestBody @Valid ChooseLessonTeacherRequest chooseLessonRequest){
        return teacherService.chooseLesson(chooseLessonRequest);
    }

}