package com.handson.sentiment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.handson.sentiment.util.Dates;
import org.joda.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import java.util.Date;

@Entity
@SqlResultSetMapping(name = "PupilOut")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PupilOut {

    @Id
    private Long id;

    private Date createdat;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createdat")
    public LocalDateTime calcCreatedAt() {
        return Dates.atLocalTime(createdat);
    }
    private String fullname;
    private Date birthdate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("birthdate")
    public LocalDateTime calcBirthDate() {
        return Dates.atLocalTime(birthdate);
    }

    private String interests;

    private String personalitytraits;

    private Integer lessonscount;

    public Integer getLessonscount() {
        return lessonscount;
    }

    public void setLessonscount(Integer lessonscount) {
        this.lessonscount = lessonscount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedat() {
        return createdat;
    }

    public void setCreatedat(Date createdat) {
        this.createdat = createdat;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getPersonalitytraits() {
        return personalitytraits;
    }

    public void setPersonalitytraits(String personalitytraits) {
        this.personalitytraits = personalitytraits;
    }
}
