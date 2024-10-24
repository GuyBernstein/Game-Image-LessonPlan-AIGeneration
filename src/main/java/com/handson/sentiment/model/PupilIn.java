package com.handson.sentiment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.handson.sentiment.util.Dates;
import org.hibernate.validator.constraints.Length;
import org.joda.time.LocalDate;

import java.io.Serializable;

import static com.handson.sentiment.model.Pupil.PupilBuilder.aPupil;

public class PupilIn implements Serializable {

    @Length(max = 60)
    private String fullname;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate birthDate;

    @Length(max = 500)
    private String interests;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getPersonalityTraits() {
        return personalityTraits;
    }

    public void setPersonalityTraits(String personalityTraits) {
        this.personalityTraits = personalityTraits;
    }

    @Length(max = 500)
    private String personalityTraits;

    public Pupil toPupil() {
        return aPupil().createdAt(Dates.nowUTC())
                .fullname(fullname)
                .birthDate(Dates.atUtc(birthDate))
                .personalityTraits(personalityTraits)
                .interests(interests)
                .build();
    }


    public void updatePupil(Pupil pupil) {
        pupil.setBirthDate(Dates.atUtc(birthDate));
        pupil.setFullname(fullname);
        pupil.setInterests(interests);
        pupil.setPersonalityTraits(personalityTraits);
    }

}
