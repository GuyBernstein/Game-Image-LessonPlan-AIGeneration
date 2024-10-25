package com.handson.lesson_generator.service;

import com.handson.lesson_generator.repo.GeneratedAILessonRepository;
import com.handson.lesson_generator.model.GeneratedLesson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GeneratedLessonService {
    @Autowired
    GeneratedAILessonRepository repository;

    public Iterable<GeneratedLesson> all() {
        return repository.findAll();
    }

    public Optional<GeneratedLesson> findById(Long id) {
        return repository.findById(id);
    }
    
    public GeneratedLesson save(GeneratedLesson generatedLesson) {
        return repository.save(generatedLesson);
    }

    public void delete(GeneratedLesson generatedLesson) {
        repository.delete(generatedLesson);
    }
}
