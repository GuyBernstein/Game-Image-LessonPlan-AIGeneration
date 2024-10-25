package com.handson.lesson_generator.repo;

import com.handson.lesson_generator.model.Pupil;
import org.springframework.data.repository.CrudRepository;

public interface PupilRepository extends CrudRepository<Pupil,Long> {

}
