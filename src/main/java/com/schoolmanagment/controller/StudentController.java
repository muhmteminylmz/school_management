package com.schoolmanagment.controller;

import com.schoolmanagment.entity.concretes.Admin;
import com.schoolmanagment.entity.concretes.Student;
import com.schoolmanagment.payload.request.ChooseLessonProgramWithId;
import com.schoolmanagment.payload.request.StudentRequest;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.payload.response.StudentResponse;
import com.schoolmanagment.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("students")
public class StudentController {

    private final StudentService studentService;

    /*
    {
    "username" : "student2",
    "password" : "12345678"
}
     */
    //Not: Save() ***
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @PostMapping("/save")
    public ResponseMessage<StudentResponse> save(@RequestBody @Valid StudentRequest studentRequest){
        return studentService.save(studentRequest);
    }

    //Not: changeActiveStatus() ***
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/changeStatus")
    //Put Mapping yapmadik cunku tum verileri setlememiz gerekirdi.
    //Bu islemleri GetMapping le de yapabiliyoruz
    public ResponseMessage<?> changeStatus(@RequestParam Long id,@RequestParam boolean status){
        return studentService.changeStatus(id,status);
    }

    //Not: getAllStudent() ***
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/getAll")
    public List<StudentResponse> getAllStudent(){
        return studentService.getAllStudent();
    }

    //Not: updateStudent() ***
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @PutMapping("/update/{userId}")
    public ResponseMessage<StudentResponse> updateStudent(@PathVariable Long userId,
                                                          @RequestBody @Valid StudentRequest studentRequest){
        return studentService.updateStudent(userId,studentRequest);
    }

    //Not: deleteStudent() ***
    @DeleteMapping("/delete/{studentId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<?> deleteStudent(@PathVariable Long studentId){
        return studentService.deleteStudent(studentId);
    }

    //Not: getStudentByName() ***
    @GetMapping("/getStudentByName")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public List<StudentResponse> getStudentByName(@RequestParam(name = "name") String studentName){
        return studentService.getStudentByName(studentName);
    }

    //Not: getStudentById() ***
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/getStudentById")
    //donen deger POJO olmamali DTO olarak donmemiz gerkiyor. ResponseMessage<StudentResponse>
    public Student getStudentById(@RequestParam(name = "id") Long id){
        return studentService.getStudentByIdForResponse(id);
    }

    //Not: getAllStudentWithPage() ***
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/search")
    public Page<StudentResponse> search(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "name") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){
        return studentService.search(page, size, sort, type);
    }

    //Not: chooseLessonProgramById() ***
    @PostMapping("chooseLesson")
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    //fazla id almamiz gerekirse requestParam lada alabiliriz
    public ResponseMessage<StudentResponse> chooseLesson(HttpServletRequest request,
                                                         @RequestBody @Valid ChooseLessonProgramWithId chooseLessonProgramRequest){
        //bu kisim servicede yazilirsa daha iyi olur
        String username = (String) request.getAttribute("username");
        return studentService.chooseLesson(username,chooseLessonProgramRequest);
    }

    //Not: getAllStudentByAdvisorId() ***
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @GetMapping("/getAllByAdvisorId")
    public List<StudentResponse> getAllByAdvisorId(HttpServletRequest request){

        String username = (String) request.getAttribute("username");

        return studentService.getAllStudentByTeacher_Username(username);
    }

}
