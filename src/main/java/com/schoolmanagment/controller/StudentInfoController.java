package com.schoolmanagment.controller;

import com.schoolmanagment.entity.concretes.Admin;
import com.schoolmanagment.entity.concretes.StudentInfo;
import com.schoolmanagment.payload.request.StudentInfoRequestWithoutTeacherId;
import com.schoolmanagment.payload.request.UpdateStudentInfoRequest;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.payload.response.StudentInfoResponse;
import com.schoolmanagment.service.StudentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("studentInfo")
public class StudentInfoController {

    private final StudentInfoService studentInfoService;

    //Not: save() ***
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @PostMapping("/save")
    public ResponseMessage<StudentInfoResponse> save(HttpServletRequest httpServletRequest,
            @RequestBody @Valid StudentInfoRequestWithoutTeacherId studentInfoRequestWithoutTeacherId){
        //httpServletRequest in amaci teacher a ulasmak

        String username = httpServletRequest.getHeader("username");

        return studentInfoService.save(username,studentInfoRequestWithoutTeacherId);
    }

    //Not: delete() ***
    @DeleteMapping("/delete/{studentInfoId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseMessage<?> delete(@PathVariable Long studentInfoId){
        return studentInfoService.delete(studentInfoId);
    }

    //Not: update() ***
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    @PutMapping("/update/{studentInfoId}")
    //@PatchMapping de yapilabilir.Put da setlenmeyen data null a ceker(gider)
    public ResponseMessage<StudentInfoResponse> update(@PathVariable Long studentInfoId,
            @RequestBody @Valid UpdateStudentInfoRequest updateStudentInfoRequest){
        return studentInfoService.update(updateStudentInfoRequest,studentInfoId);
    }

    //Not: getAllForAdmin() ***
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/getAllForAdmin")
    public ResponseEntity<Page<StudentInfoResponse>> getAll(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ){
        Pageable pageable = PageRequest.of(page,size, Sort.by("id").descending());
        Page<StudentInfoResponse> studentInfoResponse = studentInfoService.getAllForAdmin(pageable);

        return new  ResponseEntity<>(studentInfoResponse,HttpStatus.OK);
    }

    //Not: getAllForTeacher ***
    //--> Bir ogretmen kendi ogrencilerinin bilgilerini almak istedigi zaman bu method calisacak
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @GetMapping("/getAllForTeacher")
    public ResponseEntity<Page<StudentInfoResponse>> getAllForTeacher(
        HttpServletRequest httpServletRequest,
        @RequestParam(value = "page") int page,
        @RequestParam(value = "size") int size
    ){
        Pageable pageable = PageRequest.of(page,size, Sort.by("id").descending());

        String username = httpServletRequest.getHeader("username");

        Page<StudentInfoResponse> studentInfoResponse = studentInfoService.getAllTeacher(username,pageable);

        return new  ResponseEntity<>(studentInfoResponse,HttpStatus.OK);
    }


    //Not: getAllForStudent ***
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    @GetMapping("/getAllForStudent")
    public ResponseEntity<Page<StudentInfoResponse>> getAllByStudent(
            HttpServletRequest httpServletRequest,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ){
        Pageable pageable = PageRequest.of(page,size, Sort.by("id").descending());
        String username = (String) httpServletRequest.getAttribute("username");

        Page<StudentInfoResponse> studentInfoResponse = studentInfoService.getAllStudent(username,pageable);

        return new  ResponseEntity<>(studentInfoResponse,HttpStatus.OK);
    }


    //Not: getStudentInfoByStudentId ***
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    @GetMapping("/getByStudentId/{studentId}")
    public ResponseEntity<List<StudentInfoResponse>> getStudentId(@PathVariable Long studentId){
        List<StudentInfoResponse> studentInfoResponse = studentInfoService.getStudentInfoByStudentId(studentId);

        return ResponseEntity.ok(studentInfoResponse);
    }

    //Not: getStudentInfoById ***
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    @GetMapping("/get/{id}")
    public ResponseEntity<StudentInfoResponse> get(@PathVariable Long id){

        StudentInfoResponse studentInfoResponse = studentInfoService.findStudentInfoById(id);

        return ResponseEntity.ok(studentInfoResponse);
    }

    //Not: getAllWithPage ***
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/search")
    public Page<StudentInfoResponse> search(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "date") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){

        return studentInfoService.search(page,size,sort,type);
    }

}