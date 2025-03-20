package com.schoolmanagment.controller;

import com.schoolmanagment.entity.concretes.Admin;
import com.schoolmanagment.payload.request.MeetRequestWithoutId;
import com.schoolmanagment.payload.request.UpdateMeetRequest;
import com.schoolmanagment.payload.response.MeetResponse;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.service.MeetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("meet")
public class MeetController {

    private final MeetService meetService;


    //Not: save() ***
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @PostMapping("/save")
    public ResponseMessage<MeetResponse> save(HttpServletRequest httpServletRequest, @RequestBody @Valid MeetRequestWithoutId meetRequestWithoutId) {

        String username = (String) httpServletRequest.getAttribute("username");
        return meetService.save(username,meetRequestWithoutId);
    }

    //Not: getAll() ***
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/getAll")
    public List<MeetResponse> getAll() {
        return meetService.getAll();
    }

    //Not: getMeetById() ***
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/getMeetById/{meetId}")
    public ResponseMessage<MeetResponse> getMeetById(@PathVariable Long meetId) {
        return meetService.getMeetById(meetId);
    }

    //Not: getAllMeetByAdvisorAsPage() ***
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @GetMapping("/getAllMeetByAdvisorAsPage")
    public ResponseEntity<Page<MeetResponse>> getAllMeetByAdvisorAsPage(
            HttpServletRequest httpServletRequest,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ) {
        String username = (String) httpServletRequest.getAttribute("username");
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<MeetResponse> meet = meetService.getAllMeetByAdvisorAsPage(username,pageable);
        return ResponseEntity.ok(meet);
    }

    //Not : getAllMeetByAdvisorTeacherAsList() ***
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @GetMapping("/getAllMeetByAdvisorTeacherAsList")
    public ResponseEntity<List<MeetResponse>> getAllMeetByAdvisorTeacher(HttpServletRequest httpServletRequest){

        String username = (String) httpServletRequest.getAttribute("username");

        List<MeetResponse> meet = meetService.getAllMeetByAdvisorTeacher(username);
        return ResponseEntity.ok(meet);
    }

    //Not : delete() ***
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    @DeleteMapping("/delete/{meetId}")
    public ResponseMessage<?> delete(@PathVariable Long meetId){
        return meetService.delete(meetId);
    }


    //Not : update() ***
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    @PutMapping("/update/{meetId}")
    public ResponseMessage<MeetResponse> update(@PathVariable Long meetId,@RequestBody @Valid UpdateMeetRequest meetRequest){

        return meetService.update(meetRequest,meetId);
    }

    //Not : getAllMeetByStudent() ***
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    @GetMapping("/getAllMeetByStudent")
    public List<MeetResponse> getAllMeetByStudent(HttpServletRequest httpServletRequest){

        String username = (String) httpServletRequest.getAttribute("username");
        return meetService.getAllMeetByStudent(username);
    }

    //Not : getAllWithPage() ***
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/search")
    public Page<MeetResponse> search(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size
    ){
        return meetService.search(page, size);
    }


}