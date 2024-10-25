package com.handson.lesson_generator.repo;

import com.handson.lesson_generator.model.GeneratedLesson;
import org.springframework.data.repository.CrudRepository;

public interface GeneratedAILessonRepository extends CrudRepository<GeneratedLesson, Long> {
}
