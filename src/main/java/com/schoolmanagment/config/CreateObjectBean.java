package com.schoolmanagment.config;

import com.schoolmanagment.entity.concretes.LessonProgram;
import com.schoolmanagment.payload.dto.DeanDto;
import com.schoolmanagment.payload.dto.LessonProgramDto;
import com.schoolmanagment.payload.dto.TeacherRequestDto;
import com.schoolmanagment.payload.dto.ViceDeanDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreateObjectBean {

    @Bean
    public DeanDto deanDto(){
        return new DeanDto();
    }

    @Bean
    public ViceDeanDto viceDeanDto(){
        return new ViceDeanDto();
    }

    @Bean
    public LessonProgramDto lessonProgramRequestDto(){
        return new LessonProgramDto();
    }

    @Bean
    public TeacherRequestDto teacherRequestDto(){
        return new TeacherRequestDto();
    }


}