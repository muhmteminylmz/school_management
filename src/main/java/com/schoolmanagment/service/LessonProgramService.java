package com.schoolmanagment.service;

import com.schoolmanagment.entity.concretes.*;
import com.schoolmanagment.exception.BadRequestException;
import com.schoolmanagment.exception.ResourceNotFoundException;
import com.schoolmanagment.payload.dto.LessonProgramDto;
import com.schoolmanagment.payload.request.LessonProgramRequest;
import com.schoolmanagment.payload.request.LessonProgramRequestForUpdate;
import com.schoolmanagment.payload.response.LessonProgramResponse;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.payload.response.TeacherResponse;
import com.schoolmanagment.repository.LessonProgramRepository;
import com.schoolmanagment.repository.LessonRepository;
import com.schoolmanagment.utils.CreateResponseObjectForService;
import com.schoolmanagment.utils.Messages;
import com.schoolmanagment.utils.TimeControl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonProgramService {

    private final LessonProgramRepository lessonProgramRepository;

    private final LessonService lessonService;

    private final EducationTermService educationTermService;

    private final LessonProgramDto lessonProgramDto;

    private final CreateResponseObjectForService createResponseObjectForService;

    private final StudentService studentService;

    private final TeacherService teacherService;

    public ResponseMessage<LessonProgramResponse> save(LessonProgramRequest request) {

        //Lesson Programda olacak dersleri LessonService uzerinden getiriyoruz.
        Set<Lesson> lessons = lessonService.getAllLessonByLessonIdList(request.getLessonIdList());
        //educationTerm id ile getiriliyor
        EducationTerm educationTerm = educationTermService.getById(request.getEducationTermId());
        //yukarda gelen lessons ici bos degilse zaman kontrolu yapiliyor
        if (lessons.isEmpty()){
            throw new ResourceNotFoundException(Messages.NOT_FOUND_LESSON_IN_LIST);
        } else if (TimeControl.check(request.getStartTime(),request.getStopTime())) {
            throw new BadRequestException(Messages.TIME_NOT_VALID_MESSAGE);
        }
        //DTO-POJO donusumu
        LessonProgram lessonProgram = lessonProgramRequestToDto(request,lessons);
        //lessonProgram da educationTerm bilgisi setleniyor
        lessonProgram.setEducationTerm(educationTerm);

        //lessonProgram DB ye kaydediliyor
        LessonProgram savedLessonProgram = lessonProgramRepository.save(lessonProgram);
        //ResponseMessage objesi olusturuluyor
        return ResponseMessage.<LessonProgramResponse>builder()
                .message("Lesson Program is Created")
                .httpStatus(HttpStatus.CREATED)
                .object(createLessonProgramResponseForSaveMethod(savedLessonProgram))
                .build();
    }
    //Config de de olusturabiliyoruz.Ancak normalde birini secmeliyiz
    //Request i de response u da ayni yontemle yapmaliyiz(service veya config)
    private LessonProgram lessonProgramRequestToDto(LessonProgramRequest request,Set<Lesson> lessons){
        return lessonProgramDto.dtoLessonProgram(request, lessons);
    }

    private LessonProgramResponse createLessonProgramResponseForSaveMethod(LessonProgram lessonProgram){
        return LessonProgramResponse.builder()
                .day(lessonProgram.getDay())
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .lessonProgramId(lessonProgram.getId())
                .lessonName(lessonProgram.getLesson())
                .build();
    }


    public List<LessonProgramResponse> getAll() {

        return lessonProgramRepository.findAll().stream()
                .map(this::createLessonProgramResponse)
                .collect(Collectors.toList());
    }

    private LessonProgramResponse createLessonProgramResponse(LessonProgram lessonProgram){
        return LessonProgramResponse.builder()
                .day(lessonProgram.getDay())
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .lessonProgramId(lessonProgram.getId())
                //.lessonName(lessonProgram.getLesson())
                .teachers(lessonProgram.getTeachers()
                        .stream().map(this::createTeacherResponse)
                        .collect(Collectors.toSet()))
                .students(lessonProgram.getStudents()
                        .stream()
                        .map(createResponseObjectForService::createStudentResponse)
                        .collect(Collectors.toSet())
                )
                .build();
    }

    public TeacherResponse createTeacherResponse(Teacher teacher){
        return TeacherResponse.builder()
                .userId(teacher.getId())
                .name(teacher.getName())
                .surname(teacher.getSurname())
                .birthDate(teacher.getBirthDate())
                .birthPlace(teacher.getBirthPlace())
                .phoneNumber(teacher.getPhoneNumber())
                .ssn(teacher.getSsn())
                .email(teacher.getEmail())
                .gender(teacher.getGender())
                .userName(teacher.getUsername())
                .build();
    }

    public LessonProgramResponse getByLessonProgramId(Long id) {

         LessonProgram lessonProgram = lessonProgramRepository.findById(id).orElseThrow(() -> {
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_LESSON_MESSAGE,id));
        });

         return createLessonProgramResponse(lessonProgram);
        //return lessonProgramRepository.findById(id).
        //       map(this::createLessonProgramResponse).get();
    }

    public List<LessonProgramResponse> getAllLessonProgramUnassigned() {

        //Method turetme
        return lessonProgramRepository.findByTeachers_IdNull()
                .stream()
                .map(this::createLessonProgramResponse)
                .collect(Collectors.toList());
    }

    public List<LessonProgramResponse> getAllLessonProgramAssigned() {

        return lessonProgramRepository.findByTeachers_IdNotNull()
                .stream()
                .map(this::createLessonProgramResponse)
                .collect(Collectors.toList());
    }


    public ResponseMessage<?> delete(Long id) {
        //id kontrolu
        lessonProgramRepository.findById(id).orElseThrow(() -> {
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_LESSON_MESSAGE,id));
        });

        lessonProgramRepository.deleteById(id);

        //Bu lessonProgram a dahil olan teacher ve studentlardada degisiklik yapilmasi gerekiyor, biz bunu
        //lessonProgram entity sinifi icinde @PreRemove ile yaptik.

        return ResponseMessage.builder()
                .message("Lesson Program is deleted Successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public Set<LessonProgramResponse> getLessonProgramByTeacher(String username) {

        return lessonProgramRepository.getLessonProgramByTeacherUsername(username)
                .stream()
                .map(this::createLessonProgramResponseForTeacher)
                .collect(Collectors.toSet());
    }

    private LessonProgramResponse createLessonProgramResponseForTeacher(LessonProgram lessonProgram){
        return LessonProgramResponse.builder()
                .day(lessonProgram.getDay())
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .lessonProgramId(lessonProgram.getId())
                .lessonName(lessonProgram.getLesson())
                .students(lessonProgram.getStudents()
                        .stream()
                        .map(createResponseObjectForService::createStudentResponse)
                        .collect(Collectors.toSet()))
                .build();
    }

    public Set<LessonProgramResponse> getLessonProgramByStudent(String username) {

        return lessonProgramRepository.getLessonProgramByStudentUsername(username)
                .stream()
                .map(this::createLessonProgramResponseForStudent)
                .collect(Collectors.toSet());
    }

    private LessonProgramResponse createLessonProgramResponseForStudent(LessonProgram lessonProgram){
        return LessonProgramResponse.builder()
                .day(lessonProgram.getDay())
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .lessonProgramId(lessonProgram.getId())
                .lessonName(lessonProgram.getLesson())
                .teachers(lessonProgram.getTeachers().stream()
                        .map(this::createTeacherResponse)
                        .collect(Collectors.toSet()))
                .build();
    }

    public Page<LessonProgramResponse> search(int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        if (Objects.equals(type, "desc")) {
            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        }

        return lessonProgramRepository
                .findAll(pageable)
                .map(this::createLessonProgramResponse);
                //cagiran kisiye response gonderildi(pojo yerine)
    }

    //Not: getLessonProgramById() *** Teacher method
    public Set<LessonProgram> getLessonProgramById(Set<Long> lessonIdList) {

        return lessonProgramRepository.getLessonProgramByLessonProgramIdList(lessonIdList);
    }
/*
    //Normalde programda yok o yuzden kontroller cok siki tutulmayacak
    //Not: update() ***
    public ResponseMessage<LessonProgramResponse> update(Long lessonProgramId, LessonProgramRequestForUpdate lessonProgramRequest) {

        LessonProgram lessonProgram = lessonProgramRepository.findById(lessonProgramId).orElseThrow(() ->
                new ResourceNotFoundException(Messages.LESSON_PROGRAM_NOT_FOUND_MESSAGE));

        //Lesson icin turunu Set -> List yaptik
        List<Lesson> lessons = lessonService.getAllLessonByLessonIdList(lessonProgramRequest.getLessonIdList());
        EducationTerm educationTerm = educationTermService.getById(lessonProgramRequest.getEducationTermId());

        if (lessons.isEmpty()){
            throw new ResourceNotFoundException(Messages.NOT_FOUND_LESSON_IN_LIST);
        } else if (TimeControl.check(lessonProgramRequest.getStartTime(),lessonProgramRequest.getStopTime())) {
            throw new BadRequestException(Messages.TIME_NOT_VALID_MESSAGE);
        }

        //Ogrenci bilgileri guncellenecek
        if (lessonProgramRequest.getStudentIdList() != null && !lessonProgramRequest.getStudentIdList().isEmpty()){
            Set<Student> students = studentService.getStudentByIds(lessonProgramRequest.getStudentIdList());
            lessonProgram.setStudents(students);
        }


        //Ogretmen bilgileri guncellenecek
        if (lessonProgramRequest.getTeacherIdList() != null && !lessonProgramRequest.getTeacherIdList().isEmpty()){
            Set<Teacher> teachers = teacherService.getTeacherByIds(lessonProgramRequest.getTeacherIdList());
            lessonProgram.setTeachers(teachers);
        }

        //start-stop time cakismasi kod uzamasin diye yazilmadi
        lessonProgram.setLesson(lessons);
        lessonProgram.setDay(lessonProgramRequest.getDay());
        lessonProgram.setEducationTerm(educationTerm);
        lessonProgram.setStartTime(lessonProgramRequest.getStartTime());
        lessonProgram.setStopTime(lessonProgram.getStopTime());

        LessonProgram savedLessonProgram = lessonProgramRepository.save(lessonProgram);

        return ResponseMessage.<LessonProgramResponse>builder()
                .message("Lesson Program Saved Successfully")
                .httpStatus(HttpStatus.OK)
                //objeyi belirtmekle ugrasmadik
                .build();
    }*/
}