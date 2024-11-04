package com.schoolmanagment.service;

import com.schoolmanagment.entity.concretes.*;
import com.schoolmanagment.entity.enums.Note;
import com.schoolmanagment.exception.ConflictException;
import com.schoolmanagment.exception.ResourceNotFoundException;
import com.schoolmanagment.payload.request.StudentInfoRequestWithoutTeacherId;
import com.schoolmanagment.payload.request.UpdateStudentInfoRequest;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.payload.response.StudentInfoResponse;
import com.schoolmanagment.payload.response.StudentResponse;
import com.schoolmanagment.repository.StudentInfoRepository;
import com.schoolmanagment.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentInfoService {

    private final StudentInfoRepository studentInfoRepository;

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final LessonService lessonService;
    private final EducationTermService educationTermService;

    @Value("${midterm.exam.impact.percentage}")
    private Double midtermExamPercentage;

    @Value("${final.exam.impact.percentage}")
    private Double finalExamPercentage;



    //Not: save() ***
    public ResponseMessage<StudentInfoResponse> save(String username, StudentInfoRequestWithoutTeacherId studentInfoRequest) {

       //DTO ve request den gelen Student,Teacher,Lesson ve EducationTerm getiriliyor
       Student student = studentService.getStudentByIdForResponse(studentInfoRequest.getStudentId());
       Teacher teacher = teacherService.getTeacherByUsername(username);
       Lesson lesson = lessonService.getLessonById(studentInfoRequest.getLessonId());
       EducationTerm educationTerm = educationTermService.getById(studentInfoRequest.getEducationTermId());

       //lesson cakisma varmi kontrolu
        if (checkSameLesson(studentInfoRequest.getStudentId(),lesson.getLessonName())){
            throw new ConflictException(String.format(Messages.ALREADY_REGISTER_LESSON_MESSAGE,lesson.getLessonName()));
        }

        //Ders notu ortalamasi aliniyor
        Double noteAverage = calculateExamAverage(studentInfoRequest.getMidtermExam(),studentInfoRequest.getFinalExam());

        //Ders notu alfabetik olarak hesaplaniyor.
        Note note = checkLetterGrade(noteAverage);

        //DTO-> POJO
        StudentInfo studentInfo = createDto(studentInfoRequest,note,noteAverage);
        //normalde id degerleri var ancak bize fazlasi lazim
        //DTO da olmayan field lar setleniyor.
        studentInfo.setStudent(student);
        studentInfo.setTeacher(teacher);
        studentInfo.setEducationTerm(educationTerm);
        studentInfo.setLesson(lesson);
        //DB ye kayit islemi

        StudentInfo savedStudentInfo = studentInfoRepository.save(studentInfo);

        //Response objesi olustuluruyor
        return ResponseMessage.<StudentInfoResponse>builder()
                .message("Student Info Saved Successfully")
                .object(createResponse(savedStudentInfo))
                .httpStatus(HttpStatus.OK)
                .build();
    }

    private boolean checkSameLesson(Long studentId, String lessonName){
        return studentInfoRepository.getAllByStudentId_Id(studentId)
                .stream()
                .anyMatch((e) -> e.getLesson().getLessonName().equalsIgnoreCase(lessonName));
    }

    private Double calculateExamAverage(Double midTermExam,Double finalExam){

        return (midTermExam*midtermExamPercentage) + (finalExam*finalExamPercentage);
    }

    private Note checkLetterGrade(Double average){

        if (average<50.0){
            return Note.FF;
        }else if (average>=50.0 && average<55){
            return Note.DD;
        }else if (average>=55.0 && average<60){
            return Note.DC;
        }else if (average>=60.0 && average<65){
            return Note.CC;
        }else if (average>=65.0 && average<70){
            return Note.CB;
        }else if (average>=70.0 && average<75){
            return Note.BB;
        }else if (average>=75.0 && average<80){
            return Note.BA;
        }else {
            return Note.AA;
        }
    }

    private StudentInfo createDto(StudentInfoRequestWithoutTeacherId studentInfoRequest,
                                  Note note,Double average){
        return StudentInfo.builder()
                .infoNote(studentInfoRequest.getInfoNote())
                .absentee(studentInfoRequest.getAbsentee())
                .midtermExam(studentInfoRequest.getMidtermExam())
                .finalExam(studentInfoRequest.getFinalExam())
                .examAverage(average)
                .letterGrade(note)
                .build();
    }

    private StudentInfoResponse createResponse(StudentInfo studentInfo){
        return StudentInfoResponse.builder()
                .lessonName(studentInfo.getLesson().getLessonName())
                .creditScore(studentInfo.getLesson().getCreditScore())
                .isCompulsory(studentInfo.getLesson().getIsCompulsory())
                .educationTerm(studentInfo.getEducationTerm().getTerm())
                .id(studentInfo.getId())
                .absentee(studentInfo.getAbsentee())
                .midtermExam(studentInfo.getMidtermExam())
                .finalExam(studentInfo.getFinalExam())
                .infoNote(studentInfo.getInfoNote())
                .Note(studentInfo.getLetterGrade())
                .average(studentInfo.getExamAverage())
                .studentResponse(createStudentResponse(studentInfo.getStudent()))
                .build();
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
                .motherName(student.getMotherName())
                .fatherName(student.getFatherName())
                .studentNumber(student.getStudentNumber())
                .isActive(student.isActive())
                .build();
    }

    //Not: delete() ***
    public ResponseMessage<?> delete(Long studentInfoId) {

      if (!studentInfoRepository.existsByIdEquals(studentInfoId)){
          throw new ResourceNotFoundException(String.format(Messages.STUDENT_INFO_NOT_FOUND_MESSAGE,studentInfoId));
      }

      studentInfoRepository.deleteById(studentInfoId);

      return ResponseMessage.builder()
              .message("Student Info deleted successfully")
              .httpStatus(HttpStatus.OK)
              .build();
    }

    //Not: update() ***
    public ResponseMessage<StudentInfoResponse> update(UpdateStudentInfoRequest studentInfoRequest, Long studentInfoId) {

        Lesson lesson = lessonService.getLessonById(studentInfoRequest.getLessonId());

        StudentInfo getStudentInfo = getStudentInfoById(studentInfoId);
        EducationTerm educationTerm = educationTermService.getById(studentInfoRequest.getEducationTermId());

        //Dersnot ortalamasi hesaplaniyor

        Double notAverage = calculateExamAverage(studentInfoRequest.getMidtermExam(),studentInfoRequest.getFinalExam());

        //Alfabetik Not belirlenecek
        Note note = checkLetterGrade(notAverage);

        //DTO --> POJO
        StudentInfo studentInfo = createUpdatedStudent(studentInfoRequest,studentInfoId,lesson,educationTerm,note,notAverage);
        //Student ve Teacher nesneleri etkiliyor(Put Mapping den dolayi setlemessek null a cekilirler)
        studentInfo.setStudent(getStudentInfo.getStudent());
        studentInfo.setTeacher(getStudentInfo.getTeacher());

        //DB kayit islemi
        StudentInfo updatedStudentInfo = studentInfoRepository.save(studentInfo);

        //Response objesi olusturuluyor
        return ResponseMessage.<StudentInfoResponse>builder()
                .object(createResponse(updatedStudentInfo))
                .message("Student Info updated successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    private StudentInfo getStudentInfoById(Long studentInfoId) {

        if (!studentInfoRepository.existsByIdEquals(studentInfoId)) {
            throw new ResourceNotFoundException(String.format(Messages.STUDENT_INFO_NOT_FOUND_MESSAGE, studentInfoId));
        }
        return studentInfoRepository.findByIdEquals(studentInfoId);
    }

    private StudentInfo createUpdatedStudent(UpdateStudentInfoRequest studentInfoRequest,
                                             Long studentinfoRequestId,
                                             Lesson lesson,
                                             EducationTerm educationTerm,
                                             Note note,
                                             Double average){
        return StudentInfo.builder()
                .id(studentinfoRequestId)
                .lesson(lesson)
                .infoNote(studentInfoRequest.getInfoNote())
                .midtermExam(studentInfoRequest.getMidtermExam())
                .finalExam(studentInfoRequest.getFinalExam())
                .absentee(studentInfoRequest.getAbsentee())
                .educationTerm(educationTerm)
                .examAverage(average)
                .letterGrade(note)
                .build();
    }

    //Not: getAllForAdmin() ***
    public Page<StudentInfoResponse> getAllForAdmin(Pageable pageable) {

        return studentInfoRepository.findAll(pageable)
                .map(this::createResponse);
    }

    //Not: getAllForTeacher ***
    public Page<StudentInfoResponse> getAllTeacher(String username, Pageable pageable) {
        //pageable yapiyi kendisi otomatik algiliyor(Page geri donduruyor)
        return studentInfoRepository.findByTeacherId_UsernameEquals(username,pageable)
                .map(this::createResponse);
    }

    //Not: getAllForStudent ***
    public Page<StudentInfoResponse> getAllStudent(String username, Pageable pageable) {

        boolean student = studentService.existByUsername(username);

        if (!student)
            throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);


        return studentInfoRepository.findByStudentId_UsernameEquals(username,pageable)
                .map(this::createResponse);
    }

    //Not: getStudentInfoByStudentId ***
    public List<StudentInfoResponse> getStudentInfoByStudentId(Long studentId) {

        if (!studentService.existById(studentId)){
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,studentId));
        }

        if (!studentInfoRepository.existByStudent_IdEquals(studentId)){
            throw new ResourceNotFoundException(String.format(Messages.STUDENT_INFO_NOT_FOUND_BY_STUDENT_ID,studentId));
        }

        return studentInfoRepository.findByStudent_IdEquals(studentId)
                .stream().map(this::createResponse)
                .collect(Collectors.toList());
    }

    //Not: getStudentInfoById ***
    public StudentInfoResponse findStudentInfoById(Long id) {

        if (!studentInfoRepository.existsByIdEquals(id)){
            throw new ResourceNotFoundException(String.format(Messages.STUDENT_INFO_NOT_FOUND_MESSAGE,id));
        }

        return createResponse(studentInfoRepository.findByIdEquals(id));
    }

    //Not: getAllWithPage ***
    public Page<StudentInfoResponse> search(int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page,size, Sort.by(type,sort));

        return studentInfoRepository.findAll(pageable)
                .map(this::createResponse);
    }



}