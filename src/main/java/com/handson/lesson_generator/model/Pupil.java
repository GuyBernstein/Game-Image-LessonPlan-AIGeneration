package com.handson.lesson_generator.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.handson.lesson_generator.util.Dates;
import org.hibernate.validator.constraints.Length;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name="pupil")
public class Pupil implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false, updatable = false)
    private Date createdAt = Dates.nowUTC();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createdAt")
    public LocalDateTime calcCreatedAt() {
        return Dates.atLocalTime(createdAt);
    }

    @NotEmpty
    @Length(max = 60)
    private String fullname;

    private Date birthDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("birthDate")
    public LocalDateTime calcBirthDate() {
        return Dates.atLocalTime(birthDate);
    }

    @Length(max = 500)
    private String interests;

    @Length(max = 500)
    private String personalityTraits;

    @OneToMany(mappedBy = "pupil", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<GeneratedLesson> generatedLessons = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }


    public Collection<GeneratedLesson> getGeneratedLessons() {
        return generatedLessons;
    }

    public void setGeneratedLessons(Collection<GeneratedLesson> generatedLessons) {
        this.generatedLessons = generatedLessons;
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

    public static final class PupilBuilder {
        private Long id;
        private @NotNull Date createdAt;
        private @NotEmpty @Length(max = 60) String fullname;
        private Date birthDate;
        private @Length(max = 500) String interests;
        private @Length(max = 500) String personalityTraits;
        private Collection<GeneratedLesson> generatedLessons;

        private PupilBuilder() {
        }

        public static PupilBuilder aPupil() {
            return new PupilBuilder();
        }

        public PupilBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PupilBuilder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PupilBuilder fullname(String fullname) {
            this.fullname = fullname;
            return this;
        }

        public PupilBuilder birthDate(Date birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public PupilBuilder interests(String interests) {
            this.interests = interests;
            return this;
        }

        public PupilBuilder personalityTraits(String personalityTraits) {
            this.personalityTraits = personalityTraits;
            return this;
        }

        public PupilBuilder generatedLessons(Collection<GeneratedLesson> generatedLessons) {
            this.generatedLessons = generatedLessons;
            return this;
        }

        public Pupil build() {
            Pupil pupil = new Pupil();
            pupil.setId(id);
            pupil.setCreatedAt(createdAt);
            pupil.setFullname(fullname);
            pupil.setBirthDate(birthDate);
            pupil.setInterests(interests);
            pupil.setPersonalityTraits(personalityTraits);
            pupil.setGeneratedLessons(generatedLessons);
            return pupil;
        }
    }
}
