package com.schoolmanagment.service;

import com.schoolmanagment.entity.concretes.AdvisorTeacher;
import com.schoolmanagment.entity.concretes.Teacher;
import com.schoolmanagment.entity.enums.RoleType;
import com.schoolmanagment.exception.ResourceNotFoundException;
import com.schoolmanagment.payload.response.AdvisorTeacherResponse;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.repository.AdvisorTeacherRepository;
import com.schoolmanagment.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvisorTeacherService {

    //Bize gelen request advisorTeacher degilde baska endpoint den gelicekse ve AdvisorTeacher
    //uzerinden yapilmasi gerekiyorsa controller a eklenmez sadece service de olur.

    private final AdvisorTeacherRepository advisorTeacherRepository;
    private final UserRoleService userRoleService;

    public ResponseMessage<?> deleteAdvisorTeacher(Long id) {

        AdvisorTeacher advisorTeacher = advisorTeacherRepository.findById(id).orElseThrow(() ->
                 new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));

        advisorTeacherRepository.deleteById(advisorTeacher.getId()); //direkt id de olur

        return ResponseMessage.<AdvisorTeacher>builder()
                .message("Advisor Teacher deleted Successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public List<AdvisorTeacherResponse> getAllAdvisorTeacher() {

        return advisorTeacherRepository.findAll().stream()
                .map(this::createResponseObject)
                .collect(Collectors.toList());
    }

    private AdvisorTeacherResponse createResponseObject(AdvisorTeacher advisorTeacher){
        return AdvisorTeacherResponse.builder()
                .advisorTeacherId(advisorTeacher.getId())
                .teacherName(advisorTeacher.getTeacher().getName())
                .teacherSurname(advisorTeacher.getTeacher().getSurname())
                .teacherSSN(advisorTeacher.getTeacher().getSsn())
                .build();

    }


    public Page<AdvisorTeacherResponse> search(int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());

        if (Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size, Sort.by(sort).descending());
        }

        return advisorTeacherRepository.findAll(pageable)
                .map(this::createResponseObject);
    }

    //NOT: TeacherService icin gerekli methodlar *****

    //Not: saveAdvisorTeacher() ***
    public void saveAdvisorTeacher(Teacher teacher) {
        AdvisorTeacher advisorTeacherBuilder = AdvisorTeacher.builder()
                .teacher(teacher)
                .userRole(userRoleService.getUserRole(RoleType.ADVISORTEACHER))
                .build();

        advisorTeacherRepository.save(advisorTeacherBuilder);
    }


    //Not: updateAdvisorTeacher() ***
    public void updateAdvisorTeacher(boolean status, Teacher teacher) {
        //teacherId ile iliskilendirilmis AdvisorTeacher nesnesini DB den bulup getiriyoruz.
        Optional<AdvisorTeacher> advisorTeacher =
                advisorTeacherRepository.getAdvisorTeacherByTeacher_Id(teacher.getId());

        //suan eksik o yuzden build yapmadik.
        AdvisorTeacher.AdvisorTeacherBuilder advisorTeacherBuilder = AdvisorTeacher.builder()
                .teacher(teacher)
                .userRole(userRoleService.getUserRole(RoleType.ADVISORTEACHER));

        if (advisorTeacher.isPresent()) {
            if (status) {
                advisorTeacherBuilder.id(advisorTeacher.get().getId());
                advisorTeacherRepository.save(advisorTeacherBuilder.build());
            } else {
                advisorTeacherRepository.deleteById(advisorTeacher.get().getId());
            }
        } else {//TODO buraya bakilacak
            advisorTeacherRepository.save(advisorTeacherBuilder.build());
            //eger zaten advisorTeacher sa id si var diger veriler guncelleniyor.
        }

    }

    //Not: StudentService icin gerekli method
    public Optional<AdvisorTeacher> getAdvisorTeacherById(Long id) {
        return advisorTeacherRepository.findById(id);
    }

    //MeetService icin gerekli method
    public Optional<AdvisorTeacher> getAdvisorTeacherByUsername(String username) {

        //findByUsername de calisir biraz daha ogrenelim diye
        return advisorTeacherRepository.findByTeacher_UsernameEquals(username);
    }
}