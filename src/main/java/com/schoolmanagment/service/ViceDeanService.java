package com.schoolmanagment.service;

import com.schoolmanagment.entity.concretes.ViceDean;
import com.schoolmanagment.entity.enums.RoleType;
import com.schoolmanagment.exception.ResourceNotFoundException;
import com.schoolmanagment.payload.dto.ViceDeanDto;
import com.schoolmanagment.payload.request.ViceDeanRequest;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.payload.response.ViceDeanResponse;
import com.schoolmanagment.repository.ViceDeanRepository;
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
public class ViceDeanService {

    private final ViceDeanRepository viceDeanRepository;

    private final AdminService adminService;

    private final ViceDeanDto viceDeanDto;

    private final UserRoleService userRoleService;

    private final PasswordEncoder passwordEncoder;

    private final FieldControl fieldControl;

    //Not: Save() ****
    public ResponseMessage<ViceDeanResponse> save(ViceDeanRequest viceDeanRequest) {

        fieldControl.checkDuplicate(
                viceDeanRequest.getUsername(),
                viceDeanRequest.getSsn(),
                viceDeanRequest.getPhoneNumber());
        //adminService.checkDuplicate(viceDeanRequest.getUsername(),viceDeanRequest.getSsn(),viceDeanRequest.getPhoneNumber());

        ViceDean viceDean = createPojoFromDto(viceDeanRequest);
        viceDean.setUserRole(userRoleService.getUserRole(RoleType.ASSISTANTMANAGER));
        //passwordu istersek viceDean den de alabiliriz.
        viceDean.setPassword(passwordEncoder.encode(viceDeanRequest.getPassword()));

        viceDeanRepository.save(viceDean);
        //response nesnesi olusturulacak
        return ResponseMessage.<ViceDeanResponse>builder()
                .message("Vice Dean Saved")
                .httpStatus(HttpStatus.CREATED)
                //burda request gonderemeyiz cunku id si yok,yani istedigimizde kullaniciya ulasamayiz.
                .object(createViceDeanResponse(viceDean))
                .build();
    }

    private ViceDean createPojoFromDto(ViceDeanRequest viceDeanRequest){
        //istersek direkt te yazabiliriz.
        return viceDeanDto.dtoViceDean(viceDeanRequest);
    }

    private ViceDeanResponse createViceDeanResponse(ViceDean viceDean){
        return ViceDeanResponse.builder()
                .userId(viceDean.getId())
                .userName(viceDean.getUsername())
                .name(viceDean.getName())
                .surname(viceDean.getSurname())
                .birthPlace(viceDean.getBirthPlace())
                .birthDate(viceDean.getBirthDate())
                .phoneNumber(viceDean.getPhoneNumber())
                .ssn(viceDean.getSsn())
                .gender(viceDean.getGender())
                .build();
    }

    //Not: Update() ***
    public ResponseMessage<ViceDeanResponse> update(ViceDeanRequest newViceDean, Long managerId) {

        Optional<ViceDean> viceDean = viceDeanRepository.findById(managerId);

        if(viceDean.isEmpty()){
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,managerId));
        }else if(!CheckParameterUpdateMethod.checkParameter(viceDean.get(),newViceDean)){
            fieldControl.checkDuplicate(
                    newViceDean.getUsername(),
                    newViceDean.getSsn(),
                    newViceDean.getPhoneNumber());
            //adminService.checkDuplicate(newViceDean.getUsername(),newViceDean.getSsn(),newViceDean.getPhoneNumber());
        }

        ViceDean updatedViceDean = createUpdatedViceDean(newViceDean,managerId);

        updatedViceDean.setPassword(passwordEncoder.encode(newViceDean.getPassword()));
        updatedViceDean.setUserRole(userRoleService.getUserRole(RoleType.ASSISTANTMANAGER));

        return ResponseMessage.<ViceDeanResponse>builder()
                .message("Vice Dean Updated")
                .httpStatus(HttpStatus.CREATED)
                .object(createViceDeanResponse(updatedViceDean))
                .build();
    }

    private ViceDean createUpdatedViceDean(ViceDeanRequest viceDeanRequest,Long managerId) {
        //NOT_NULL yok o yuzden password burda yazmadik yukarda yazip encode layip setliyecegiz
        return ViceDean.builder()
                .id(managerId)
                .username(viceDeanRequest.getUsername())
                .ssn(viceDeanRequest.getSsn())
                .name(viceDeanRequest.getName())
                .surname(viceDeanRequest.getSurname())
                .birthPlace(viceDeanRequest.getBirthPlace())
                .birthDate(viceDeanRequest.getBirthDate())
                .phoneNumber(viceDeanRequest.getPhoneNumber())
                .gender(viceDeanRequest.getGender())
                .build();
    }

    //Not: delete() ****
    public ResponseMessage<?> delete(Long managerId) {

        Optional<ViceDean> viceDean = viceDeanRepository.findById(managerId);

        if(viceDean.isEmpty()){
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,managerId));
        }

        viceDeanRepository.deleteById(managerId);

        return ResponseMessage.builder()
                .message("Vice Dean Deleted")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    //Not: getById() ***
    public ResponseMessage<ViceDeanResponse> getViceDeanById(Long managerId) {

        Optional<ViceDean> viceDean = viceDeanRepository.findById(managerId);

        if(viceDean.isEmpty()){
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,managerId));
        }

        return ResponseMessage.<ViceDeanResponse>builder()
                .message("Vice Dean Successfully Found")
                .httpStatus(HttpStatus.OK)
                .object(createViceDeanResponse(viceDean.get()))
                .build();
    }

    //Not: getAll() ***
    public List<ViceDeanResponse> getAllViceDean() {

        return viceDeanRepository.findAll().
                stream()
                .map(this::createViceDeanResponse)
                .collect(Collectors.toList());
    }


    //Not: getAllWithPage() ***
    public Page<ViceDeanResponse> getAllWithPage(int page, int size,String sort, String type) {

        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());

        if (Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size,Sort.by(sort).descending());
        }

        return viceDeanRepository.findAll(pageable).map(this::createViceDeanResponse);
    }
}