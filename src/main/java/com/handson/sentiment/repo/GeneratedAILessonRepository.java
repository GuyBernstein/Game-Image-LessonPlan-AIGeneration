package com.handson.sentiment.repo;

import com.handson.sentiment.model.GeneratedLesson;
import org.springframework.data.repository.CrudRepository;

public interface GeneratedAILessonRepository extends CrudRepository<GeneratedLesson, Long> {
}
