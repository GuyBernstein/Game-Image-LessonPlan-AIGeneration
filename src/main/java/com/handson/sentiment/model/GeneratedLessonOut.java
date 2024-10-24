package com.handson.sentiment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.handson.sentiment.service.AWSService;
import com.handson.sentiment.util.Dates;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.joda.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import java.util.Date;

@Entity
@SqlResultSetMapping(name = "GeneratedLessonOut")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeneratedLessonOut {
    @Id
    private Long id;

    private Date createdat;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createdat")
    public LocalDateTime calcCreatedAt() {
        return Dates.atLocalTime(createdat);
    }

    private String imageurl;

    private String gametype;

    private String planactivity;

    private String subject;

    private String topic;

    private String imagedescription;

    private String gamedescription;

    private String plandescription;

    private Integer pupilid;

    public String getGametype() {
        return gametype;
    }

    public String getPlanactivity() {
        return planactivity;
    }

    public static GeneratedLessonOut of(GeneratedLesson generatedLesson, AWSService awsService, AiMessageType type) throws UnirestException, JsonProcessingException {
        GeneratedLessonOut res = new GeneratedLessonOut();
        if (!generateLesson(generatedLesson, awsService, type, res)) return null;
        res.id = generatedLesson.getId();
        res.createdat = generatedLesson.getCreatedAt();
        res.topic = generatedLesson.getTopic();
        res.subject = generatedLesson.getSubject();
        res.pupilid = null;
        return res;
    }

    private static boolean generateLesson(GeneratedLesson generatedLesson, AWSService awsService, AiMessageType type, GeneratedLessonOut res) {
        boolean isInit = type == AiMessageType.INIT;

        switch(type) {
            case INIT:
            case GAME:
                res.gamedescription = awsService.getTextContent(generatedLesson.getGameDescription());
                res.gametype = generatedLesson.getGameType();
                if (!isInit) break;
            case PLAN:
                res.plandescription = awsService.getTextContent(generatedLesson.getPlanDescription());
                res.planactivity = generatedLesson.getPlanActivity();
                if (!isInit) break;
            case IMAGE:
                res.imageurl = awsService.generateLink(generatedLesson.getImageUrl());
                res.imagedescription = generatedLesson.getImageDescription();
                break;
            default:
                return false;
        }
        return true;
    }

    public String getGamedescription() {
        return gamedescription;
    }

    public String getPlandescription() {
        return plandescription;
    }

    public Integer getPupilid() {
        return pupilid;
    }

    public void setPupilid(Integer pupilid) {
        this.pupilid = pupilid;
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

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getImagedescription() {
        return imagedescription;
    }

    public void setImagedescription(String imagedescription) {
        this.imagedescription = imagedescription;
    }
}
