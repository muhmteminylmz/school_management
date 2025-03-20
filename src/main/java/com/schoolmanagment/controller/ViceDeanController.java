package com.schoolmanagment.controller;

import com.schoolmanagment.payload.request.ViceDeanRequest;
import com.schoolmanagment.payload.response.ResponseMessage;
import com.schoolmanagment.payload.response.ViceDeanResponse;
import com.schoolmanagment.service.ViceDeanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("vicedean")
public class ViceDeanController {

    private final ViceDeanService viceDeanService;

    //Not : Save() ****
    @PostMapping("/save") // http://localhost:8080/vicedean/save
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<ViceDeanResponse> save(@RequestBody @Valid ViceDeanRequest viceDeanRequest){

        return viceDeanService.save(viceDeanRequest);
    }

    //Not : Update() ****
    @PutMapping("/update/{userId}") // http://localhost:8080/vicedean/update/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<ViceDeanResponse> update(@RequestBody @Valid ViceDeanRequest viceDeanRequest
            ,@PathVariable Long userId){

        return viceDeanService.update(viceDeanRequest,userId);
    }

    //Not: Delete() ***
    @DeleteMapping("/delete/{userId}") // http://localhost:8080/vicedean/delete/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<?> delete(@PathVariable Long userId){
    //Hic bisey donmuyecek

        return viceDeanService.delete(userId);
    }

    //Not: getById() ***
    @GetMapping("/getViceDeanById/{userId}") // http://localhost:8080/vicedean/getViceDeanById/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<ViceDeanResponse> getViceDeanById(@PathVariable Long userId){

        return viceDeanService.getViceDeanById(userId);
    }

    //Not: getAll() ***
    @GetMapping("/getAll") // http://localhost:8080/vicedean/getAll
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public List<ViceDeanResponse> getAll(){

        return viceDeanService.getAllViceDean();
    }

    //Not: getAllWithPage() ***
    @GetMapping("/search") // http://localhost:8080/vicedean/search
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public Page<ViceDeanResponse> getAllWithPage(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "date") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){

        return viceDeanService.getAllWithPage(page,size,sort,type);
    }
}