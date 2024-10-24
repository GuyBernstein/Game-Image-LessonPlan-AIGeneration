package com.handson.sentiment.service;


import com.handson.sentiment.model.Pupil;
import com.handson.sentiment.repo.PupilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PupilService {

    @Autowired
    PupilRepository repository;

    public Iterable<Pupil> all() {
        return repository.findAll();
    }

    public Optional<Pupil> findById(Long id) {
        return repository.findById(id);
    }

    public Pupil save(Pupil pupil) {
        return repository.save(pupil);
    }

    public void delete(Pupil pupil) {
        repository.delete(pupil);
    }

}