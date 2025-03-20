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

    public ResponseMessage<LessonProgramResponse> save(LessonProgramRequest request) {

        Set<Lesson> lessons = lessonService.getAllLessonByLessonIdList(request.getLessonIdList());
        EducationTerm educationTerm = educationTermService.getById(request.getEducationTermId());

        if (lessons.isEmpty()){
            throw new ResourceNotFoundException(Messages.NOT_FOUND_LESSON_IN_LIST);
        } else if (TimeControl.check(request.getStartTime(),request.getStopTime())) {
            throw new BadRequestException(Messages.TIME_NOT_VALID_MESSAGE);
        }
        LessonProgram lessonProgram = lessonProgramRequestToDto(request,lessons);
        lessonProgram.setEducationTerm(educationTerm);

        LessonProgram savedLessonProgram = lessonProgramRepository.save(lessonProgram);
        return ResponseMessage.<LessonProgramResponse>builder()
                .message("Lesson Program is Created")
                .httpStatus(HttpStatus.CREATED)
                .object(createLessonProgramResponseForSaveMethod(savedLessonProgram))
                .build();
    }
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
    }

    public List<LessonProgramResponse> getAllLessonProgramUnassigned() {

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
        lessonProgramRepository.findById(id).orElseThrow(() -> {
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_LESSON_MESSAGE,id));
        });

        lessonProgramRepository.deleteById(id);

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
    }

    //Not: getLessonProgramById() *** Teacher method
    public Set<LessonProgram> getLessonProgramById(Set<Long> lessonIdList) {

        return lessonProgramRepository.getLessonProgramByLessonProgramIdList(lessonIdList);
    }

}