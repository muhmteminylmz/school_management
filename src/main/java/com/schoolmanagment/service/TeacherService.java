package com.schoolmanagment.service;

import com.schoolmanagment.entity.concretes.LessonProgram;
import com.schoolmanagment.entity.concretes.Teacher;
import com.schoolmanagment.entity.enums.RoleType;
import com.schoolmanagment.exception.BadRequestException;
import com.schoolmanagment.exception.ResourceNotFoundException;
import com.schoolmanagment.payload.dto.TeacherRequestDto;
import com.schoolmanagment.payload.request.ChooseLessonTeacherRequest;
import com.schoolmanagment.payload.request.TeacherRequest;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.payload.response.TeacherResponse;
import com.schoolmanagment.repository.TeacherRepository;
import com.schoolmanagment.utils.CheckSameLessonProgram;
import com.schoolmanagment.utils.FieldControl;
import com.schoolmanagment.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final LessonProgramService lessonProgramService;
    private final FieldControl fieldControl;
    private final PasswordEncoder passwordEncoder;
    private final TeacherRequestDto teacherRequestDto;
    private final UserRoleService userRoleService;
    private final AdvisorTeacherService advisorTeacherService;

    public ResponseMessage<TeacherResponse> save(TeacherRequest teacherRequest) {

        Set<LessonProgram> lessons = lessonProgramService.getLessonProgramById(teacherRequest.getLessonsIdList());

        if (lessons.isEmpty()) {

            throw new BadRequestException(Messages.LESSON_PROGRAM_NOT_FOUND_MESSAGE);
        }else {
            fieldControl.checkDuplicate(teacherRequest.getUsername(),
                                        teacherRequest.getSsn(),
                                        teacherRequest.getPhoneNumber(),
                                        teacherRequest.getEmail());
        }

        //dto-pojo donusumu
        Teacher teacher = teacherRequestToDto(teacherRequest);

        //rol bilgisi setleniyor
        teacher.setUserRole(userRoleService.getUserRole(RoleType.TEACHER));

        //ders programi ekleniyor
        teacher.setLessonsProgramList(lessons);

        //sifre encode ediliyor
        teacher.setPassword(passwordEncoder.encode(teacherRequest.getPassword()));

        //DB ye kayit islemi
        Teacher savedTeacher = teacherRepository.save(teacher);

        //Advisor ise advisorTeacher tablosun degistiriyoruz.
        if (teacherRequest.isAdvisorTeacher()){
            advisorTeacherService.saveAdvisorTeacher(savedTeacher);
        }

        return ResponseMessage.<TeacherResponse>builder().
                message("Teacher saved successfully")
                .httpStatus(HttpStatus.CREATED)
                .object(createTeacherResponse(savedTeacher))
                .build();
    }

    private Teacher teacherRequestToDto(TeacherRequest teacherRequest){

        return teacherRequestDto.dtoTeacher(teacherRequest);
    }

    private TeacherResponse createTeacherResponse(Teacher teacher){
        return TeacherResponse.builder()
                .userId(teacher.getId())
                .userName(teacher.getUsername())
                .name(teacher.getName())
                .surname(teacher.getSurname())
                .birthDate(teacher.getBirthDate())
                .birthPlace(teacher.getBirthPlace())
                .ssn(teacher.getSsn())
                .phoneNumber(teacher.getPhoneNumber())
                .gender(teacher.getGender())
                .email(teacher.getEmail())
                .build();
    }

    //Not: getAll() ***
    public List<TeacherResponse> getAllTeacher() {

        return teacherRepository.findAll()
                        .stream()
                        .map(this::createTeacherResponse)
                        .collect(Collectors.toList());
    }

    //Not: updateTeacherById() ***
    public ResponseMessage<TeacherResponse> update(TeacherRequest newTeacher, Long userId) {

        //id uzerinden teacher nesnesi getiriliyor
        Optional<Teacher> teacher = teacherRepository.findById(userId);
        //DTO uzerinden eklenecek lessonlar getiriliyor
        Set<LessonProgram> lessons = lessonProgramService.getLessonProgramById(newTeacher.getLessonsIdList());

        if (teacher.isEmpty()){
            throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);
        } else if (lessons.isEmpty()) {
            throw new BadRequestException(Messages.LESSON_PROGRAM_NOT_FOUND_MESSAGE);
        } else if (!checkParameterForUpdateMethod(teacher.get(),newTeacher)) {
            fieldControl.checkDuplicate(
                    newTeacher.getUsername(),
                    newTeacher.getSsn(),
                    newTeacher.getPhoneNumber(),
                    newTeacher.getEmail());
        }

        Teacher updatedTeacher = createUpdatedTeacher(newTeacher,userId);
        //password encode ediyoruz
        updatedTeacher.setPassword(passwordEncoder.encode(newTeacher.getPassword()));
        //lesson program setliyoruz.
        updatedTeacher.setLessonsProgramList(lessons); //TODO buraya bakilacak

        Teacher savedTeacher = teacherRepository.save(updatedTeacher);

        advisorTeacherService.updateAdvisorTeacher(newTeacher.isAdvisorTeacher(),savedTeacher);

        return ResponseMessage.<TeacherResponse>builder()
                .object(createTeacherResponse(savedTeacher)) //updatedTeacher da yazilabilir
                .message("Teacher updated Successfully")
                .httpStatus(HttpStatus.OK)
                .build();

    }

    private boolean checkParameterForUpdateMethod(Teacher teacher,TeacherRequest newTeacherRequest) {
        return teacher.getSsn().equalsIgnoreCase(newTeacherRequest.getSsn())
                || teacher.getUsername().equalsIgnoreCase(newTeacherRequest.getUsername())
                || teacher.getPhoneNumber().equalsIgnoreCase(newTeacherRequest.getPhoneNumber())
                || teacher.getEmail().equalsIgnoreCase(newTeacherRequest.getEmail());
    }

    private Teacher createUpdatedTeacher(TeacherRequest teacher,Long id){
        return Teacher.builder()
                .id(id)
                .username(teacher.getUsername())
                .name(teacher.getName())
                .surname(teacher.getSurname())
                .ssn(teacher.getSsn())
                .birthDate(teacher.getBirthDate())
                .birthPlace(teacher.getBirthPlace())
                .phoneNumber(teacher.getPhoneNumber())
                .isAdvisor(teacher.isAdvisorTeacher())
                .userRole(userRoleService.getUserRole(RoleType.TEACHER))
                .gender(teacher.getGender())
                .email(teacher.getEmail())
                .build();
    }

    //Not: getTeacherByName() ***
    public List<TeacherResponse> getTeacherByName(String teacherName) {

        return teacherRepository.getTeacherByNameContaining(teacherName)
                .stream()
                .map(this::createTeacherResponse)
                .collect(Collectors.toList());
    }

    //Not: deleteTeacher() ***
    public ResponseMessage<?> deleteTeacher(Long id) {

        teacherRepository.findById(id).orElseThrow(() -> {
           throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);
        });

        //lessonPrgoramda teacher kaldirilacak??
        teacherRepository.deleteById(id);

        return ResponseMessage.builder()
                .message("Teacher is deleted successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    //Not: getTeacherById() ***
    public ResponseMessage<TeacherResponse> getSavedTeacherById(Long id) {

        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));

        return ResponseMessage.<TeacherResponse>builder()
                .object(createTeacherResponse(teacher))
                .message("Teacher Successfully found")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    //Not: getAllWithPage() ***
    public Page<TeacherResponse> search(int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        if (Objects.equals(type,"desc")){
            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        }

        return teacherRepository.findAll(pageable)
                .map(this::createTeacherResponse);
    }

    //Not: addLessonProgramToTeachersLessonsProgram() ***
    public ResponseMessage<TeacherResponse> chooseLesson(ChooseLessonTeacherRequest chooseLessonRequest) {

        //Teacher yoksa?
        Teacher teacher = teacherRepository.findById(chooseLessonRequest.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));
        //LessonProgram getiriliyor
        Set<LessonProgram> lessonPrograms =lessonProgramService.getLessonProgramById(chooseLessonRequest.getLessonProgramId());
        //LessonProgram ici bos mu kontrol
        if (lessonPrograms.isEmpty()){
            throw new ResourceNotFoundException(Messages.LESSON_PROGRAM_NOT_FOUND_MESSAGE);
        }
        //Teacher in mevcut ders programi getiriliyor
        Set<LessonProgram> existLessonProgram = teacher.getLessonsProgramList();
        CheckSameLessonProgram.checkLessonPrograms(existLessonProgram,lessonPrograms);
        existLessonProgram.addAll(lessonPrograms);

        teacher.setLessonsProgramList(existLessonProgram);

        Teacher savedTeacher = teacherRepository.save(teacher);

        return ResponseMessage.<TeacherResponse>builder()
                .message("LessonProgram added to Teacher")
                .httpStatus(HttpStatus.CREATED)
                .object(createTeacherResponse(savedTeacher))
                .build();
    }

    //StudentInfoService icin eklendi
    public Teacher getTeacherByUsername(String username) {
        //findByUsername(username).orEF daha clean ancak biz bunu gorelim diye yazdik.
        if (!teacherRepository.existsByUsername(username)){
            throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);
        }

        return teacherRepository.getTeacherByUsername(username);
    }

    public Set<Teacher> getTeacherByIds(List<Long> teacherIdList) {
        return teacherRepository.findByIdsEquals(teacherIdList);
    }
}