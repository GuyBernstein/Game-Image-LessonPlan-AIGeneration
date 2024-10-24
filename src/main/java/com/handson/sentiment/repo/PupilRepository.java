package com.handson.sentiment.repo;

import com.handson.sentiment.model.Pupil;
import org.springframework.data.repository.CrudRepository;

public interface PupilRepository extends CrudRepository<Pupil,Long> {

}
