package com.schoolmanagment.payload.dto;

import com.schoolmanagment.entity.concretes.Lesson;
import com.schoolmanagment.entity.concretes.LessonProgram;
import com.schoolmanagment.payload.request.LessonProgramRequest;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class LessonProgramDto {

    public LessonProgram dtoLessonProgram(LessonProgramRequest request, Set<Lesson> lessons){
        return LessonProgram.builder()
                .day(request.getDay())
                .lesson(lessons)
                .startTime(request.getStartTime())
                .stopTime(request.getStopTime())
                .build();
    }
}