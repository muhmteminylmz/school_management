package com.schoolmanagment.controller;

import com.schoolmanagment.entity.concretes.Lesson;
import com.schoolmanagment.payload.request.LessonRequest;
import com.schoolmanagment.payload.response.LessonResponse;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    //Not: Save() ***
    @PostMapping("/save") // http://localhost:8080/lessons/save
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<LessonResponse> save(@RequestBody @Valid LessonRequest lessonRequest) {

        return lessonService.save(lessonRequest);
    }

    //Not: Delete() ***
    @DeleteMapping("/delete/{id}") // http://localhost:8080/lessons/delete/2
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage deleteLesson(@PathVariable Long id) {

        return lessonService.deleteLesson(id);
    }

    //Not: getLessonByLessonName() ****
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/getLessonByName") // http://localhost:8080/lessons/getLessonByName?lessonName=Math
    public ResponseMessage<LessonResponse> getLessonByLessonName(@RequestParam String lessonName) {
        return lessonService.getLessonByLessonName(lessonName);
    }

    //Not: getAll() ****
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/getAll") // http://localhost:8080/lessons/getAll
    public List<LessonResponse> getAllLesson() {
        return lessonService.getAllLesson();
    }

    //Not: getAllWithPage() ****
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/search") // http://localhost:8080/lessons/search
    public Page<LessonResponse> search(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "date") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ) {

        return lessonService.search(page,size,sort,type);
    }

    //Note: getAllLessonByLessonId() ***
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/getAllLessonByLessonId") // http://localhost:8080/lessons/getAllLessonByLessonId
    public Set<Lesson> getAllLessonByLessonId(@RequestParam("lessonId") Set<Long> idList) {

        return lessonService.getAllLessonByLessonIdList(idList);
    }

}