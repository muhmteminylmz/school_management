package com.schoolmanagment.controller;

import com.schoolmanagment.payload.request.ContactMessageRequest;
import com.schoolmanagment.payload.response.ContactMessageResponse;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.service.ContactMessageService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("contactMessages")
@RequiredArgsConstructor
public class ContactMessage {

    private final ContactMessageService contactMessageService;


    //Not: save() *****
    @PostMapping("/save")
    public ResponseMessage<ContactMessageResponse> save(@Valid @RequestBody ContactMessageRequest contactMessageRequest) {
        //Kendi ResponseMessage olusturmamizin ana amaci istedigim class i return de kullanabilrmemiz(ContactMessage dondurme)
        //ContactMessageRequest bir DTO

        //ResponseMessage i Service katinda dondurecegiz.
        return contactMessageService.save(contactMessageRequest);
    }

    //Not: getAll() *****
    @GetMapping("/getAll")
    //Genelde boyle hardcode olarak yazilir(cok Role yok)
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Page<ContactMessageResponse> getAll(
        //Cok fazla ContactMessage olabileceginden Page kullanmaliyiz.
        @RequestParam(value = "page",defaultValue = "0") int page,
        @RequestParam(value = "size",defaultValue = "10") int size,
        @RequestParam(value = "sort",defaultValue = "date") String sort,
        @RequestParam(value = "type",defaultValue = "desc") String type
        //Normalde direkt frontend e atardik bu sefer default deger atadik
){
        return contactMessageService.getAll(page,size,sort,type);

    }


    //Not: SearchByEmail() *****
    @GetMapping("/searchByEmail")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Page<ContactMessageResponse> searchByEmail(
            @RequestParam(value = "email") String email,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "date") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){
        return contactMessageService.searchByEmail(email,page,size,sort,type);
    }


    //Not: SearchBySubject() *****
    @GetMapping("/searchBySubject")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Page<ContactMessageResponse> searchBySubject(
            @RequestParam(value = "email") String subject,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "date") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){
        return contactMessageService.searchBySubject(subject,page,size,sort,type);
    }


}
//ODEV : Hepsinin Postman de END-POINTLERI test edilecek