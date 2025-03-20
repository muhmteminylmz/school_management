package com.schoolmanagment.controller;

import com.schoolmanagment.payload.request.LessonProgramRequest;
import com.schoolmanagment.payload.request.LessonProgramRequestForUpdate;
import com.schoolmanagment.payload.response.LessonProgramResponse;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.service.LessonProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("lessonPrograms")
@RequiredArgsConstructor
public class LessonProgramController {

    private final LessonProgramService lessonProgramService;

    //Not: Save() ***
    @PostMapping("/save") // http://localhost:8080/lessonPrograms/save
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<LessonProgramResponse> save(@RequestBody @Valid LessonProgramRequest lessonProgramRequest){

        return lessonProgramService.save(lessonProgramRequest);
    }

    //Not: getAll() ***
    @GetMapping("/getAll") // http://localhost:8080/lessonPrograms/getAll
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAll(){
        return lessonProgramService.getAll();
    }

    //Not: getById() ***
    @GetMapping("/getById/{id}") // http://localhost:8080/lessonPrograms/getById/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public LessonProgramResponse getById(@PathVariable Long id){
        return lessonProgramService.getByLessonProgramId(id);
    }

    //Not: getAllLessonProgramUnassigned() ***
    @GetMapping("/getAllUnassigned") // http://localhost:8080/lessonPrograms/getAllUnassigned
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllUnassigned(){
        return lessonProgramService.getAllLessonProgramUnassigned();
    }

    //Not: getAllLessonProgramAssigned() ***
    @GetMapping("/getAllAssigned") // http://localhost:8080/lessonPrograms/getAllAssigned
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllAssigned(){
        return lessonProgramService.getAllLessonProgramAssigned();
    }

    //Not: Delete() ***
    @DeleteMapping("/delete/{id}") // http://localhost:8080/lessonPrograms/delete/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<?> delete(@PathVariable Long id){
        return lessonProgramService.delete(id);
    }

    //Not: getLessonProgramByTeacher() ***
    @GetMapping("/getLessonProgramByTeacher") // http://localhost:8080/lessonPrograms/GetLessonProgramByTeacher
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public Set<LessonProgramResponse> getLessonProgramByTeacherId(HttpServletRequest httpServletRequest){
        //normalde endpoint le istedigimizi alirdik.Bu sefer request ten almak istiyoruz.End point etki etmiyecek
        //yada istersek Service katinda currentLogin uzerinden getPrincible() alabiliriz.
        String username = (String) httpServletRequest.getAttribute("username");

        return lessonProgramService.getLessonProgramByTeacher(username);
    }

    //Not: getLessonProgramByStudent() ***
    @GetMapping("/getLessonProgramByStudent") // http://localhost:8080/lessonPrograms/GetLessonProgramByStudent
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public Set<LessonProgramResponse> getLessonProgramByStudent(HttpServletRequest httpServletRequest) {

        //getPrinciple() kullanabiliriz,ancak o zaman anlik login olanlarin bilgisini alabiliriz
        //mudur ogrencinin ders programini bu sekilde alamaz.
        String username = (String) httpServletRequest.getAttribute("username");

        return lessonProgramService.getLessonProgramByStudent(username);
    }

    //Not: GetAllWithPage() ***
    @GetMapping("/search") // http://localhost:8080/lessonPrograms/search
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','STUDENT','TEACHER')")
    public Page<LessonProgramResponse> search(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "day") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){
        return lessonProgramService.search(page,size,sort,type);
    }
/*
    Ihtiyaci karsilamadigi icin iptal edildi.

    //Not: update() ***
    @PutMapping("/update/{lessonProgramId}") // http://localhost:8080/lessonPrograms/update/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public ResponseMessage<LessonProgramResponse> update(
            @PathVariable Long lessonProgramId,
            @RequestBody @Valid LessonProgramRequestForUpdate lessonProgramRequest
            ){
        return lessonProgramService.update(lessonProgramId,lessonProgramRequest);
    }*/

}