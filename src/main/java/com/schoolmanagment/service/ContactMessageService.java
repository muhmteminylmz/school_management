package com.schoolmanagment.service;

import com.schoolmanagment.entity.concretes.ContactMessage;
import com.schoolmanagment.exception.ConflictException;
import com.schoolmanagment.payload.request.ContactMessageRequest;
import com.schoolmanagment.payload.response.ContactMessageResponse;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Objects;

import static com.schoolmanagment.utils.Messages.ALREADY_SEND_A_MESSAGE_TODAY;

@Service
@RequiredArgsConstructor
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;


    //Note: save() ****
    public ResponseMessage<ContactMessageResponse> save(ContactMessageRequest contactMessageRequest) {

        //Ayni kullanici ayni gun sadece 1 mesaj gonderebilmeli
        ContactMessage contactMessage = createObject(contactMessageRequest);

        ContactMessage savedData = contactMessageRepository.save(contactMessage);

        return ResponseMessage.<ContactMessageResponse>builder().
                message("Contact Message Created Successfully").
                httpStatus(HttpStatus.CREATED).
                object(createResponse(savedData)).
                build();
    }

    //DTO-POJO donusumu icin yardimci method(bu class disinda kullanilmiyacak)
    private ContactMessage createObject(ContactMessageRequest contactMessageRequest){
        return ContactMessage.builder().
                name(contactMessageRequest.getName()).
                subject(contactMessageRequest.getSubject()).
                email(contactMessageRequest.getEmail()).
                message(contactMessageRequest.getMessage()).
                date(LocalDate.now()).
                build();
    }

    //POJO-DTO donusumu icin yardimci method
    private ContactMessageResponse createResponse(ContactMessage contactMessage){

        return ContactMessageResponse.builder().
                name(contactMessage.getName()).
                subject(contactMessage.getSubject()).
                message(contactMessage.getMessage()).
                date(contactMessage.getDate()).
                build();
    }

    //Not: getAll() ****
    public Page<ContactMessageResponse> getAll(int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        if (Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size,Sort.by(sort).descending());
        }

        return contactMessageRepository.
                findAll(pageable).
                map(this::createResponse);
    }
    //Not: SearchByEmail() ****
    public Page<ContactMessageResponse> searchByEmail(String email, int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        if (Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size,Sort.by(sort).descending());
        }

        return contactMessageRepository.
                findByEmailEquals(email,pageable).
                map(this::createResponse);
    }

    //Not : SearchBySubject() ****
    public Page<ContactMessageResponse> searchBySubject(String subject, int page, int size, String sort, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        if (Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size,Sort.by(sort).descending());
        }

        return contactMessageRepository.
                findBySubjectEquals(subject,pageable).
                map(this::createResponse);
    }



}