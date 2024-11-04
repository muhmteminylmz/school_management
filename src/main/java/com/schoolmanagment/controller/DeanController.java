package com.schoolmanagment.controller;

import com.schoolmanagment.payload.request.DeanRequest;
import com.schoolmanagment.payload.response.DeanResponse;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.service.DeanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("dean")
public class DeanController {

    private final DeanService deanService;

    //Not: Save() ***
    @PostMapping("/save") //http://localhost:8080/dean/save
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<DeanResponse> save(@RequestBody @Valid DeanRequest deanRequest){

        return deanService.save(deanRequest);
    }

    //Not: UpdateById() ***
    @PutMapping("/update/{userId}") //http://localhost:8080/dean/update/1
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<DeanResponse> update(@RequestBody @Valid DeanRequest deanRequest,
                                                @PathVariable Long userId){

        return deanService.update(deanRequest,userId);
    }

    //Not: Delete() ***
    @DeleteMapping("/delete/{userId}") // http://localhost:8080/dean/delete/1
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<?> delete(@PathVariable Long userId){

        return deanService.deleteDean(userId);
    }

    //Not: getById() ***
    @GetMapping("/getManagerById/{userId}") // http://localhost:8080/dean/getManagerById/1
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<DeanResponse> getDeanById(@PathVariable Long userId){

        return deanService.getDeanById(userId);
    }

    //Not: getAll()
    @GetMapping("/getAll") // http://localhost:8080/dean/getAll
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<DeanResponse> getAll(){
        //Page yapiyla aliriz genelde bu sefer list olarak aldik
        return deanService.getAllDean();
    }

    //Not Search() ****
    @GetMapping("/search") // http://localhost:8080/dean/search
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Page<DeanResponse> search(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "date") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){

        return deanService.search(page,size,sort,type);
    }

}