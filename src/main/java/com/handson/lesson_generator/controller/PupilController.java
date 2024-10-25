package com.handson.lesson_generator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handson.lesson_generator.model.*;
import com.handson.lesson_generator.service.PupilService;
import com.handson.lesson_generator.util.HandsonException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

import static com.handson.lesson_generator.util.Dates.atUtc;
import static com.handson.lesson_generator.util.FPS.FPSBuilder.aFPS;
import static com.handson.lesson_generator.util.FPSCondition.FPSConditionBuilder.aFPSCondition;
import static com.handson.lesson_generator.util.FPSField.FPSFieldBuilder.aFPSField;
import static com.handson.lesson_generator.util.Strings.likeLowerOrNull;


@RestController
@RequestMapping("/api/pupils")
public class PupilController {
    private final PupilService pupilService;

    private final EntityManager em;

    private final ObjectMapper om;


    public PupilService getPupilService() {
        return pupilService;
    }

    public EntityManager getEm() {
        return em;
    }

    public ObjectMapper getOm() {
        return om;
    }

    @Autowired
    public PupilController(PupilService pupilService, EntityManager em, ObjectMapper om) {
        this.pupilService = pupilService;
        this.em = em;
        this.om = om;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<PaginationAndList> search(@RequestParam(required = false) String fullName,
                                                    @RequestParam(required = false) String interests,
                                                    @RequestParam(required = false) String personalityTraits,
                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromBirthDate,
                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toBirthDate,
                                                    @RequestParam(required = false) Integer fromLessonsCount,
                                                    @RequestParam(required = false) Integer toLessonsCount,
                                                    @RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "50") @Min(1) Integer count,
                                                    @RequestParam(defaultValue = "id") PupilSortField sort,
                                                    @RequestParam(defaultValue = "asc") SortDirection sortDirection) throws JsonProcessingException {

        var res =aFPS().select(List.of(
                        aFPSField().field("p.id").alias("id").build(),
                        aFPSField().field("p.created_at").alias("createdat").build(),
                        aFPSField().field("p.fullname").alias("fullname").build(),
                        aFPSField().field("p.birth_date").alias("birthdate").build(),
                        aFPSField().field("p.interests").alias("interests").build(),
                        aFPSField().field("p.personality_traits").alias("personalitytraits").build(),
                        aFPSField().field("(select count(gl.id) from generated_lesson gl where gl.pupil_id = p.id ) ").alias("lessonscount").build()
                ))
                .from(List.of(" pupil p"))
                .conditions(List.of(
                        aFPSCondition().condition("( lower(fullname) like :fullName )").parameterName("fullName").value(likeLowerOrNull(fullName)).build(),
                        aFPSCondition().condition("( lower(p.personality_traits) like :personalityTraits )").parameterName("personalityTraits").value(likeLowerOrNull(personalityTraits)).build(),
                        aFPSCondition().condition("( lower(interests) like :interests )").parameterName("interests").value(likeLowerOrNull(interests)).build(),
                        aFPSCondition().condition("( p.birth_Date >= :fromBirthDate )").parameterName("fromBirthDate").value(atUtc(fromBirthDate)).build(),
                        aFPSCondition().condition("( p.birth_Date <= :toBirthDate )").parameterName("toBirthDate").value(atUtc(toBirthDate)).build(),
                        aFPSCondition().condition("( (select count(gl.id) from generated_lesson gl where gl.pupil_id = p.id ) >= :fromLessonsCount )").parameterName("fromLessonsCount").value(fromLessonsCount).build(),
                        aFPSCondition().condition("( (select count(gl.id) from generated_lesson gl where gl.pupil_id = p.id ) <= :toLessonsCount )").parameterName("toLessonsCount").value(toLessonsCount).build()
                )).sortField(sort.fieldName).sortDirection(sortDirection).page(page).count(count)
                .itemClass(PupilOut.class)
                .build().exec(em, om);
        return ResponseEntity.ok(res);
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getOnePupil(@PathVariable Long id)
    {
        return new ResponseEntity<>(pupilService.findById(id), HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> insertPupil(@RequestBody PupilIn pupilIn)
    {
        Pupil pupil = pupilIn.toPupil();
        pupil = pupilService.save(pupil);
        return new ResponseEntity<>(pupil, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePupil(@PathVariable Long id, @RequestBody PupilIn pupil)
    {
        Optional<Pupil> dbPupil = pupilService.findById(id);
        if (dbPupil.isEmpty()) throw new HandsonException("Pupil with id: " + id + " not found");
        pupil.updatePupil(dbPupil.get());
        Pupil updatedPupil = pupilService.save(dbPupil.get());
        return new ResponseEntity<>(updatedPupil, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePupil(@PathVariable Long id)
    {
        Optional<Pupil> dbPupil = pupilService.findById(id);
        if (dbPupil.isEmpty()) throw new HandsonException("Pupil with id: " + id + " not found");
        pupilService.delete(dbPupil.get());
        return new ResponseEntity<>("DELETED", HttpStatus.OK);
    }

}
