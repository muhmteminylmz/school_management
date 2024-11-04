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
@RequiredArgsConstructor//final field lardan constructor olusturur boylece constructor enjection yapariz.Kod kalabaligi azalir.
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;


    //Note: save() ****
    public ResponseMessage<ContactMessageResponse> save(ContactMessageRequest contactMessageRequest) {

        //Ayni kullanici ayni gun sadece 1 mesaj gonderebilmeli
        /*boolean isSameMessageWithSameEmailForToday =
                contactMessageRepository.existsByEmailEqualsAndDateEquals(contactMessageRequest.getEmail(), LocalDate.now());
        //Suan ihtiyac kalmadi o yuzden comment e aldik
        if(isSameMessageWithSameEmailForToday) throw new ConflictException(String.format(ALREADY_SEND_A_MESSAGE_TODAY));*/
            //message disinda ID veya veri gireceksek StringFormat kullanilabilir.

        //DTO-POJO donusumu (genelde kutuphane kullanilmaz,cunku kutuphaneler degisiklik algilayamaya biliyor.)
        ContactMessage contactMessage = createObject(contactMessageRequest);

        ContactMessage savedData = contactMessageRepository.save(contactMessage);
        //Genelde sadece kayit basariyla gerceklestirildi yeterli

        return ResponseMessage.<ContactMessageResponse>builder().
                message("Contact Message Created Successfully").
                httpStatus(HttpStatus.CREATED).
                object(createResponse(savedData)).//Request dto yu bosuna validasyon yapmayalim(DB ye gidiyor valid lazim) diye kullanmiyoruz.
                build();
        //~ Objenin turu neyse o demek(burda calismadi)
    }

    //DTO-POJO donusumu icin yardimci method(bu class disinda kullanilmiyacak)
    private ContactMessage createObject(ContactMessageRequest contactMessageRequest){
        //builder la burda method chain yaparak setleme yapiyoruz.
        //Ayrica istedigimiz kadar field i setleyebiliriz(All yada No args Cons onemsenmez.).
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
            //hic bisey kullanmadiysa default kullan
        }

        return contactMessageRepository.
                findAll(pageable).
                map(this::createResponse);
                //map(r-> createResponse(r));
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