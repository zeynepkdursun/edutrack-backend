package com.admin.edu_track.services;


import com.admin.edu_track.entities.Lesson;
import com.admin.edu_track.repositories.LessonRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonService {
    private LessonRepository lessonRepo;

    public LessonService(LessonRepository lessonRepo){
        this.lessonRepo = lessonRepo;
    }
    @PostConstruct
    public void crateInitialLessons(){
        if(lessonRepo.count() == 0){
            List<String> lessons = List.of("Turkce", "Sosyal", "Din Kulturu", "Ingilizce", "Matematik", "Fen Bilgisi");
            lessons.forEach(name -> {
                Lesson l = new Lesson();
                l.setName(name);
                lessonRepo.save(l);
            });
            System.out.println(">>> Temel dersler sisteme yüklendi.");
        }
    }
}
