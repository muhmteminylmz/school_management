package com.schoolmanagment.service;

import com.schoolmanagment.entity.concretes.Dean;
import com.schoolmanagment.entity.enums.RoleType;
import com.schoolmanagment.exception.ResourceNotFoundException;
import com.schoolmanagment.payload.dto.DeanDto;
import com.schoolmanagment.payload.request.DeanRequest;
import com.schoolmanagment.payload.response.DeanResponse;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.repository.DeanRepository;
import com.schoolmanagment.utils.CheckParameterUpdateMethod;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeanService {

    private final DeanRepository deanRepository;

    //private final AdminService adminService;//Bunlari burda kullanabilmemiz sebebi @Service deki component

    private final DeanDto deanDto;

    private final UserRoleService userRoleService;

    private final PasswordEncoder passwordEncoder;
    private final FieldControl fieldControl;

    //Not: Save() ***
    public ResponseMessage<DeanResponse> save(DeanRequest deanRequest) {

        //Dublicate kontrolu
        fieldControl.checkDuplicate(deanRequest.getUsername(),
                deanRequest.getSsn(),
                deanRequest.getPhoneNumber());
        /*adminService.checkDuplicate(
                deanRequest.getUsername(),
                deanRequest.getSsn(),
                deanRequest.getPhoneNumber());*/

        //DTO-POJO donusum
        Dean dean = createDtoForDean(deanRequest);

        dean.setUserRole(userRoleService.getUserRole(RoleType.MANAGER));
        dean.setPassword(passwordEncoder.encode(dean.getPassword()));

        //DB ye kayit
        Dean savedDean = deanRepository.save(dean);
        //savedDean kullaniyoruz cunku dean de id gibi bilgiler yok

        return ResponseMessage.<DeanResponse>builder().
                message("Dean saved").
                httpStatus(HttpStatus.CREATED).
                object(createDeanResponse(savedDean)).
                build();
    }

    private Dean createDtoForDean(DeanRequest deanRequest){//createDtoToPOJO

        return deanDto.dtoDean(deanRequest);
    }

    private DeanResponse createDeanResponse(Dean dean){
        return  DeanResponse.builder().
                userId(dean.getId()).
                userName(dean.getUsername()).
                name(dean.getName()).
                surname(dean.getSurname()).
                birthDate(dean.getBirthDate()).
                birthPlace(dean.getBirthPlace()).
                ssn(dean.getSsn()).
                phoneNumber(dean.getPhoneNumber()).
                gender(dean.getGender()).
                build();
    }

    //Not: Update() ***
    public ResponseMessage<DeanResponse> update(DeanRequest newDean, Long deanId) {

        Optional<Dean> dean = checkDeanExists(deanId);

        deanRepository.deleteById(deanId);

        /*
        Optional<Dean> dean  = deanRepository.findById(deanId);

        //Dean objesi bos olma kontrolu
        if (!dean.isPresent()){ // isEmpty() de kullanilabilir

            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, deanId));
        }else */if (!CheckParameterUpdateMethod.checkParameter(dean.get(),newDean)) {

                    fieldControl.checkDuplicate(newDean.getUsername(),
                    newDean.getSsn(),
                    newDean.getPhoneNumber());
            //adminService.checkDuplicate(newDean.getUsername(),newDean.getSsn(),newDean.getPhoneNumber());
        }

        //guncellenen yeni bilgilerle Dean objesini kaydediyoruz
        Dean updatedDean = createUpdatedDean(newDean,deanId);
        updatedDean.setPassword(passwordEncoder.encode(newDean.getPassword()));

        return ResponseMessage.<DeanResponse>builder().
                message("Dean Updated Succesfully").
                httpStatus(HttpStatus.OK).
                object(createDeanResponse(updatedDean)).
                build();
    }

    //Yardimci method
    private Dean createUpdatedDean(DeanRequest deanRequest, Long managerId){

        return Dean.builder().
                id(managerId).
                username(deanRequest.getUsername()).
                ssn(deanRequest.getSsn()).
                name(deanRequest.getName()).
                surname(deanRequest.getSurname()).
                birthPlace(deanRequest.getBirthPlace()).
                birthDate(deanRequest.getBirthDate()).
                phoneNumber(deanRequest.getPhoneNumber()).
                gender(deanRequest.getGender()).
                userRole(userRoleService.getUserRole(RoleType.MANAGER)).
                build();
    }

    //Not: Delete()
    public ResponseMessage<?> deleteDean(Long deanId) {

        Optional<Dean> dean = checkDeanExists(deanId);

        deanRepository.deleteById(deanId);

        return ResponseMessage.builder().
                message("Dean Deleted").
                httpStatus(HttpStatus.OK).
                build();
    }

    public ResponseMessage<DeanResponse> getDeanById(Long deanId) {

        //odev : asagida surekli tekrar ettigimiz kod gurubu method haline cevirip cagirilacak

        Optional<Dean> dean = checkDeanExists(deanId);

        deanRepository.deleteById(deanId);

        //Zaten gelen objeyi tekrar geri dondurmemizin sebebi FRONT-END tarafi istedi.
        return ResponseMessage.<DeanResponse>builder().
                message("Dean Successfully Found").
                httpStatus(HttpStatus.OK).
                object(createDeanResponse(dean.get())).
                build();

    }

    //Not: getAll() ***
    public List<DeanResponse> getAllDean() {

        return deanRepository.findAll().stream().
                map(this::createDeanResponse).
                collect(Collectors.toList());
    }

    public Page<DeanResponse> search(int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page, size,Sort.by(sort).ascending());

        if (Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size,Sort.by(sort).descending());
        }

        return deanRepository.findAll(pageable).map(this::createDeanResponse);
    }

    private Optional<Dean> checkDeanExists(Long deanId){
        Optional<Dean> dean = deanRepository.findById(deanId);

        if (dean.isEmpty()){

            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, deanId));
        }
        return dean;
    }
}