package com.schoolmanagment.service;

import com.schoolmanagment.entity.concretes.EducationTerm;
import com.schoolmanagment.exception.ResourceNotFoundException;
import com.schoolmanagment.payload.request.EducationTermRequest;
import com.schoolmanagment.payload.response.EducationTermResponse;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.repository.EducationTermRepository;
import com.schoolmanagment.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationTermService {

    private final EducationTermRepository educationTermRepository;


    public ResponseMessage<EducationTermResponse> save(EducationTermRequest educationTermRequest) {

        checkEducationTermDate(educationTermRequest);

        EducationTerm savedEducationTerm = educationTermRepository.save(createEducationTerm(educationTermRequest));

        return ResponseMessage.<EducationTermResponse>builder()
                .message("Education Term created")
                .object(createEducationTermResponse(savedEducationTerm))
                .httpStatus(HttpStatus.CREATED)
                .build();
    }

    private EducationTerm createEducationTerm(EducationTermRequest request){

        return EducationTerm.builder()
                .term(request.getTerm())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .lastRegistrationDate(request.getLastRegistrationDate())
                .build();
    }

    private EducationTermResponse createEducationTermResponse(EducationTerm educationTerm){

        return EducationTermResponse.builder()
                .id(educationTerm.getId())
                .term(educationTerm.getTerm())
                .startDate(educationTerm.getStartDate())
                .endDate(educationTerm.getEndDate())
                .lastRegistrationDate(educationTerm.getLastRegistrationDate())
                .build();
    }

    public EducationTermResponse get(Long id) {

        checkEducationTermExists(id);

        return createEducationTermResponse(educationTermRepository.findByIdEquals(id));
    }

    public List<EducationTermResponse> getAll() {

        return educationTermRepository.findAll()
                .stream()
                .map(this::createEducationTermResponse)
                .collect(Collectors.toList());
    }

    public Page<EducationTermResponse> getAllWithPage(int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
        if (Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size, Sort.by(sort).descending());
        }
        return educationTermRepository.findAll(pageable).map(this::createEducationTermResponse);
    }

    public ResponseMessage<?> delete(Long id) {

        checkEducationTermExists(id);

        educationTermRepository.deleteById(id);

        return ResponseMessage.builder()
                .message("Education term deleted successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public ResponseMessage<EducationTermResponse> update(Long id,EducationTermRequest request) {

        checkEducationTermExists(id);

        if (request.getStartDate()!= null && request.getLastRegistrationDate()!= null){
            if (request.getLastRegistrationDate().isAfter(request.getStartDate())){
                throw new ResourceNotFoundException(Messages.EDUCATION_START_DATE_IS_EARLIER_THAN_LAST_REGISTRATION_DATE);
            }
        }

        //startDate-endDate kontrolu
        if (request.getStartDate()!= null && request.getEndDate()!= null){
            if (request.getEndDate().isBefore(request.getStartDate())){
                throw new ResourceNotFoundException(Messages.EDUCATION_END_DATE_IS_EARLIER_THAN_START_DATE);
            }
        }

        ResponseMessage.ResponseMessageBuilder<EducationTermResponse> responseResponseMessageBuilder
                = ResponseMessage.builder();

        EducationTerm updated = createUpdatedEducationTerm(id,request);

        educationTermRepository.save(updated);

        return responseResponseMessageBuilder
                .object(createEducationTermResponse(updated))
                .message("Education Term updated successfully")
                .build();
    }

    private EducationTerm createUpdatedEducationTerm(Long id,EducationTermRequest request){
        return EducationTerm.builder()
                .id(id)
                .term(request.getTerm())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .lastRegistrationDate(request.getLastRegistrationDate())
                .build();
    }

    //Not: getById lessonProgram icin yaazildi
    public EducationTerm getById(Long educationTermId) {

        checkEducationTermExists(educationTermId);

        return educationTermRepository.findByIdEquals(educationTermId);
    }


// ---> EDUCATION-TERM-SERVICE <---
    private void checkEducationTermExists(Long id){
        if (!educationTermRepository.existsByIdEquals(id)){
            throw new ResourceNotFoundException(String.format(Messages.EDUCATION_TERM_NOT_FOUND_MESSAGE,id));
        }
    }

    private void checkEducationTermDate(EducationTermRequest educationTermRequest){
        //son kaayit tarifi, ders doneminin baslangic tarihinden sonra olmamali
        if (educationTermRequest.getLastRegistrationDate().isAfter(educationTermRequest.getStartDate())) {
            throw new ResourceNotFoundException(Messages.EDUCATION_START_DATE_IS_EARLIER_THAN_LAST_REGISTRATION_DATE);
        }
        //bitis tarihi baslangic tarihinden once olmamali
        if (educationTermRequest.getEndDate().isBefore(educationTermRequest.getStartDate())) {
            throw new ResourceNotFoundException(Messages.EDUCATION_END_DATE_IS_EARLIER_THAN_START_DATE);
        }
        //ayni term ve baglangic tarihine sahip birden fazla kayip varmi kontrolu
        if (educationTermRepository.existsByTermAndYearEquals(educationTermRequest.getTerm(),educationTermRequest.getStartDate().getYear())){
            throw new ResourceNotFoundException(Messages.EDUCATION_TERM_IS_ALREADY_EXIST_BY_TERM_AND_YEAR_MESSAGE);
        }
    }

}