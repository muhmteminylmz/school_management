package com.schoolmanagment.service;

import com.schoolmanagment.entity.concretes.AdvisorTeacher;
import com.schoolmanagment.entity.concretes.Meet;
import com.schoolmanagment.entity.concretes.Student;
import com.schoolmanagment.exception.BadRequestException;
import com.schoolmanagment.exception.ConflictException;
import com.schoolmanagment.exception.ResourceNotFoundException;
import com.schoolmanagment.payload.request.MeetRequestWithoutId;
import com.schoolmanagment.payload.request.UpdateMeetRequest;
import com.schoolmanagment.payload.response.MeetResponse;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.repository.MeetRepository;
import com.schoolmanagment.repository.StudentRepository;
import com.schoolmanagment.utils.Messages;
import com.schoolmanagment.utils.TimeControl;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.message.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetService {

    private final MeetRepository meetRepository;
    private final AdvisorTeacherService advisorTeacherService;
    private final StudentRepository studentRepository;
    private final StudentService studentService;

    //Not: save() ***
    public ResponseMessage<MeetResponse> save(String username, MeetRequestWithoutId meetRequest) {

        AdvisorTeacher advisorTeacher = advisorTeacherService.getAdvisorTeacherByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_ADVISOR_MESSAGE_WITH_USERNAME,username)));

        //toplanti saat kontrolu
        if (TimeControl.check(meetRequest.getStartTime(),meetRequest.getStopTime()))
            throw new BadRequestException(Messages.TIME_NOT_VALID_MESSAGE);

        //toplantiya katilacak ogrenciler icin yeni meeting saatlerinde cakisma varmi
        for (Long studentId: meetRequest.getStudentIds()) {
            boolean check = studentRepository.existsById(studentId);
            if (!check) throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,studentId));

            checkMeetConflict(studentId,meetRequest.getDate(),meetRequest.getStartTime(),meetRequest.getStopTime());
        }

        //Meet e katilacak olan Student lar getiriliyor
        List<Student> students = studentService.getStudentByIds(meetRequest.getStudentIds());
        //Meet nesnesi olusturup ilgili field lar setleniyor
        Meet meet = new Meet();
        meet.setDate(meetRequest.getDate());
        meet.setStartTime(meetRequest.getStartTime());
        meet.setStopTime(meetRequest.getStopTime());
        meet.setAdvisorTeacher(advisorTeacher);
        meet.setStudentList(students);
        meet.setDescription(meetRequest.getDescription());

        Meet savedMeet = meetRepository.save(meet);

        return ResponseMessage.<MeetResponse>builder()
                .httpStatus(HttpStatus.CREATED)
                .object(createMeetResponse(savedMeet))
                .message("Meet Saved Successfully")
                .build();
    }

    private void checkMeetConflict(Long studentId, LocalDate date, LocalTime startTime, LocalTime stopTime) {

        List<Meet> meets = meetRepository.findByStudentList_IdEquals(studentId);
        //TODO : meet size kontrol edilecek
        for (Meet meet: meets) {

            LocalTime existingStartTime = meet.getStartTime();
            LocalTime existingStopTime = meet.getStopTime();

            if (meet.getDate().equals(date) &&
                    ((startTime.isAfter(existingStartTime) && startTime.isBefore(existingStopTime)) || // yeni gelen meetingin startTime bilgisi mevcut mettinglerden herhangi birinin startTime ve stopTime arasinda mi ???
                    (stopTime.isAfter(existingStartTime) && stopTime.isBefore(existingStopTime)) || // yeni gelen meetingin stopTime bilgisi mevcut mettinglerden herhangi birinin startTime ve stopTime arasinda mi ?
                    (startTime.isBefore(existingStartTime) && stopTime.isAfter(existingStopTime)) ||
                    (startTime.equals(existingStartTime) && stopTime.equals(existingStopTime)))) {

                throw new ConflictException(Messages.MEET_EXIST_MESSAGE);
            }

        }
    }

    private MeetResponse createMeetResponse(Meet meet){

        return MeetResponse.builder()
                .id(meet.getId())
                .date(meet.getDate())
                .startTime(meet.getStartTime())
                .stopTime(meet.getStopTime())
                .description(meet.getDescription())
                .advisorTeacherId(meet.getAdvisorTeacher().getId())
                .teacherSsn(meet.getAdvisorTeacher().getTeacher().getSsn())
                .teacherName(meet.getAdvisorTeacher().getTeacher().getName())
                .students(meet.getStudentList())
                .build();
    }

    //Not: getAll() ***
    public List<MeetResponse> getAll() {

        return meetRepository.findAll().stream()
                .map(this::createMeetResponse)
                .collect(Collectors.toList());
    }

    //Not: getMeetById() ***
    public ResponseMessage<MeetResponse> getMeetById(Long meetId) {

        Meet meet = meetRepository.findById(meetId).orElseThrow(()->
                new ResourceNotFoundException(String.format(Messages.MEET_NOT_FOUND_MESSAGE,meetId)));
        return ResponseMessage.<MeetResponse>builder()
                .httpStatus(HttpStatus.OK)
                .object(createMeetResponse(meet))
                .build();
    }

    //Not: getAllMeetByAdvisorAsPage() ***
    public Page<MeetResponse> getAllMeetByAdvisorAsPage(String username, Pageable pageable) {

        AdvisorTeacher advisorTeacher = advisorTeacherService.getAdvisorTeacherByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_ADVISOR_MESSAGE_WITH_USERNAME, username)));

        return meetRepository.findByAdvisorTeacher_IdEquals(advisorTeacher.getId(),pageable)
                .map(this::createMeetResponse);
    }

    //Not : getAllMeetByAdvisorTeacherAsList() ***
    public List<MeetResponse> getAllMeetByAdvisorTeacher(String username) {

        AdvisorTeacher advisorTeacher = advisorTeacherService.getAdvisorTeacherByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_ADVISOR_MESSAGE_WITH_USERNAME, username)));

        return meetRepository.getByAdvisorTeacher_IdEquals(advisorTeacher.getId())
                .stream().map(this::createMeetResponse)
                .collect(Collectors.toList());
    }

    //Not : delete() ***
    public ResponseMessage<?> delete(Long meetId) {

        Meet meet = meetRepository.findById(meetId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.MEET_NOT_FOUND_MESSAGE,meetId)));

        meetRepository.deleteById(meetId);

        return ResponseMessage.builder()
                .message("Meet Deleted Successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    //Not : update() ***
    public ResponseMessage<MeetResponse> update(UpdateMeetRequest meetRequest, Long meetId) {

        Meet getMeet = meetRepository.findById(meetId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.MEET_NOT_FOUND_MESSAGE,meetId)));

        //Time Control
        if (TimeControl.check(meetRequest.getStartTime(),meetRequest.getStopTime())){
            throw new BadRequestException(Messages.TIME_NOT_VALID_MESSAGE);
        }

        //her ogrenci icin meet conflict kontrolu
        if (!(getMeet.getDate().equals(meetRequest.getDate()) &&
            ((meetRequest.getStartTime().equals(getMeet.getStartTime()) &&
            meetRequest.getStopTime().equals(getMeet.getStopTime()))))) {
            for (Long studentId : meetRequest.getStudentIds()) {
                checkMeetConflict(studentId, meetRequest.getDate(), meetRequest.getStartTime(), meetRequest.getStopTime());
            }
        }

        //TODO request'ten gelen id'lere ait ogrenci var mi kontrolu
        List<Student> students = studentService.getStudentByIds(meetRequest.getStudentIds());
        //DTO -> POJO
        Meet meet = createUpdatedMeet(meetRequest,meetId);
        meet.setStudentList(students);
        meet.setAdvisorTeacher(getMeet.getAdvisorTeacher());


        Meet updatedMeet = meetRepository.save(meet);

        return ResponseMessage.<MeetResponse>builder()
                .httpStatus(HttpStatus.OK)
                .message("Meet Updated Successfully")
                .object(createMeetResponse(updatedMeet))
                .build();
    }

    private Meet createUpdatedMeet(UpdateMeetRequest updateMeetRequest,Long id){
        return Meet.builder()
                .id(id)
                .startTime(updateMeetRequest.getStartTime())
                .stopTime(updateMeetRequest.getStopTime())
                .date(updateMeetRequest.getDate())
                .description(updateMeetRequest.getDescription())
                .build();
    }

    //Not : getAllMeetByStudent() ***
    public List<MeetResponse> getAllMeetByStudent(String username) {

        Student student = studentService.getStudentByUsernameForOptional(username).orElseThrow(() ->
                new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));

        return meetRepository.findByStudentList_IdEquals(student.getId())
                .stream()
                .map(this::createMeetResponse)
                .collect(Collectors.toList());
    }

    //Not : getAllWithPage() ***
    public Page<MeetResponse> search(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return meetRepository.findAll(pageable)
                .map(this::createMeetResponse);
    }
}