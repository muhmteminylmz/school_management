package com.schoolmanagment.service;

import com.schoolmanagment.entity.concretes.AdvisorTeacher;
import com.schoolmanagment.entity.concretes.LessonProgram;
import com.schoolmanagment.entity.concretes.Student;
import com.schoolmanagment.entity.enums.RoleType;
import com.schoolmanagment.exception.ResourceNotFoundException;
import com.schoolmanagment.payload.request.ChooseLessonProgramWithId;
import com.schoolmanagment.payload.request.StudentRequest;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.payload.response.StudentResponse;
import com.schoolmanagment.repository.StudentRepository;
import com.schoolmanagment.utils.CheckParameterUpdateMethod;
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

@RequiredArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final AdvisorTeacherService advisorTeacherService;
    private final FieldControl fieldControl;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;

    private final LessonProgramService lessonProgramService;

    public ResponseMessage<StudentResponse> save(StudentRequest studentRequest) {

         AdvisorTeacher advisorTeacher = advisorTeacherService.getAdvisorTeacherById(studentRequest.getAdvisorTeacherId()).orElseThrow(() ->
                 new ResourceNotFoundException(String.format
                         (Messages.NOT_FOUND_ADVISOR_MESSAGE, studentRequest.getAdvisorTeacherId())));

         //Dublicate kontrolu
        fieldControl.checkDuplicate(studentRequest.getUsername(),studentRequest.getSsn(),
                studentRequest.getPhoneNumber(),studentRequest.getEmail());

        Student student = studentRequestToDto(studentRequest);

        student.setStudentNumber(lastNumber());
        student.setAdvisorTeacher(advisorTeacher);
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        student.setUserRole(userRoleService.getUserRole(RoleType.STUDENT));
        student.setActive(true);

        //Response nesnesi olusturuluyor
        return ResponseMessage.<StudentResponse>builder()
                .httpStatus(HttpStatus.CREATED)
                .message("Student saved Successfully")
                .object(createStudentResponse(studentRepository.save(student)))
                .build();
    }

    private Student studentRequestToDto(StudentRequest studentRequest){
        return Student.builder()
                .fatherName(studentRequest.getFatherName())
                .motherName(studentRequest.getMotherName())
                .birthDate(studentRequest.getBirthDate())
                .birthPlace(studentRequest.getBirthPlace())
                .name(studentRequest.getName())
                .surname(studentRequest.getSurname())
                .password(studentRequest.getPassword())
                .username(studentRequest.getUsername())
                .ssn(studentRequest.getSsn())
                .email(studentRequest.getEmail())
                .phoneNumber(studentRequest.getPhoneNumber())
                .gender(studentRequest.getGender())
                .build();
    }

    private int lastNumber(){
        if (!studentRepository.findStudent()){
            return 1000;
        }
        return studentRepository.getMaxStudentNumber() + 1;
    }

    private StudentResponse createStudentResponse(Student student){
        return StudentResponse.builder()
                .userId(student.getId())
                .userName(student.getUsername())
                .name(student.getName())
                .surname(student.getSurname())
                .birthDate(student.getBirthDate())
                .birthPlace(student.getBirthPlace())
                .phoneNumber(student.getPhoneNumber())
                .gender(student.getGender())
                .email(student.getEmail())
                .fatherName(student.getFatherName())
                .motherName(student.getMotherName())
                .studentNumber(student.getStudentNumber())
                .isActive(student.isActive())
                .build();
    }

    //Not: changeActiveStatus() ***
    public ResponseMessage<?> changeStatus(Long id, boolean status) {

        Student student = studentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));

        student.setActive(status);

        studentRepository.save(student);

        return ResponseMessage.builder()
                .message("Student is " + (status ? "active" : "passive"))
                .httpStatus(HttpStatus.OK)
                .build();
    }

    //Not: updateStudent() ***
    public List<StudentResponse> getAllStudent() {
        return studentRepository.findAll().stream()
                .map(this::createStudentResponse)
                .collect(Collectors.toList());
    }

    public ResponseMessage<StudentResponse> updateStudent(Long userId, StudentRequest studentRequest) {

        Student student = studentRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));

        AdvisorTeacher advisorTeacher = advisorTeacherService.getAdvisorTeacherById(studentRequest.getAdvisorTeacherId()).orElseThrow(() ->
                new ResourceNotFoundException(String.format
                        (Messages.NOT_FOUND_ADVISOR_MESSAGE, studentRequest.getAdvisorTeacherId())));

        //Dublicate kontrolu
        if (!CheckParameterUpdateMethod.checkParameter(student,studentRequest)) {

            fieldControl.checkDuplicate(studentRequest.getUsername(), studentRequest.getSsn(),
                    studentRequest.getPhoneNumber(), studentRequest.getEmail());
        }
        //DTO -> POJO
        Student updatedStudent = createUpdatedStudent(studentRequest,userId);
        updatedStudent.setPassword(passwordEncoder.encode(studentRequest.getPassword()));
        updatedStudent.setAdvisorTeacher(advisorTeacher);
        updatedStudent.setStudentNumber(student.getStudentNumber());
        updatedStudent.setActive(true);

        studentRepository.save(updatedStudent);

        return ResponseMessage.<StudentResponse>builder()
                .object(createStudentResponse(updatedStudent))
                .httpStatus(HttpStatus.OK)
                .message("Student updated Successfully")
                .build();
    }

    private Student createUpdatedStudent(StudentRequest studentRequest,Long userId){
        return Student.builder()
                .id(userId)
                .fatherName(studentRequest.getFatherName())
                .motherName(studentRequest.getMotherName())
                .birthDate(studentRequest.getBirthDate())
                .birthPlace(studentRequest.getBirthPlace())
                .name(studentRequest.getName())
                .surname(studentRequest.getSurname())
                .password(studentRequest.getPassword())
                .username(studentRequest.getUsername())
                .ssn(studentRequest.getSsn())
                .email(studentRequest.getEmail())
                .phoneNumber(studentRequest.getPhoneNumber())
                .gender(studentRequest.getGender())
                .userRole(userRoleService.getUserRole(RoleType.STUDENT))
                .build();
    }

    //Not: deleteStudent() ***
    public ResponseMessage<?> deleteStudent(Long studentId) {

        Student student = studentRepository.findById(studentId).orElseThrow(() ->
                new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));

        studentRepository.deleteById(studentId);
        //TODO : Delete islemi calismiyor.   (DB ile alakali)
        return ResponseMessage.builder()
                .message("Student is deleted Successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    //Not: getStudentByName() ***
    public List<StudentResponse> getStudentByName(String studentName) {

        return studentRepository.getByNameContaining(studentName).stream()
                .map(this::createStudentResponse)
                .collect(Collectors.toList());
    }

    //Not: getStudentById() ***
    public Student getStudentByIdForResponse(Long id) {
        //TODO
        return studentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));
    }


    public Page<StudentResponse> search(int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());


        if (Objects.equals(type,"desc")){
            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        }

        return studentRepository.findAll(pageable)
                .map(this::createStudentResponse);
    }

    //Not: chooseLessonProgramById() ***
    public ResponseMessage<StudentResponse> chooseLesson(String username,
                                                         ChooseLessonProgramWithId chooseLessonProgramRequest) {
        //student ve LessonProgram kontrolu
        Student student = studentRepository.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));

        Set<LessonProgram> lessonPrograms = lessonProgramService.getLessonProgramById(chooseLessonProgramRequest.getLessonProgramId());

        if (lessonPrograms.isEmpty()){
            throw new ResourceNotFoundException(Messages.LESSON_PROGRAM_NOT_FOUND_MESSAGE);
        }

        Set<LessonProgram> currentLessonProgram = student.getLessonsProgramList();

        //lesson icin dublicate kontrolu
        CheckSameLessonProgram.checkLessonPrograms(currentLessonProgram,lessonPrograms);

        currentLessonProgram.addAll(lessonPrograms);

        student.setLessonsProgramList(currentLessonProgram);

        Student savedStudent = studentRepository.save(student);

        return ResponseMessage.<StudentResponse>builder()
                .httpStatus(HttpStatus.OK)
                .message("Lessons added to Student")
                .object(createStudentResponse(savedStudent))
                .build();
    }
    //Not: getAllStudentByAdvisorUsername() ***
    public List<StudentResponse> getAllStudentByTeacher_Username(String username) {

        return studentRepository.getStudentByAdvisorTeacher_Username(username).stream()
                .map(this::createStudentResponse)
                .collect(Collectors.toList());
    }

    public boolean existByUsername(String username) {
        return studentRepository.existsByUsername(username);
    }

    public boolean existById(Long studentId) {
        return studentRepository.existsById(studentId);
    }

    //Meet icin gerekli method
    public List<Student> getStudentByIds(Long[] studentIds) {

        return studentRepository.findByIdsEquals(studentIds);
    }

    //MeetService icin
    public Optional<Student> getStudentByUsernameForOptional(String username) {

        return studentRepository.findByUsernameEqualsForOptional(username);
    }

    public Set<Student> getStudentByIds(List<Long> studentIds) {
        return studentRepository.findByIdsEquals(studentIds);
    }
}