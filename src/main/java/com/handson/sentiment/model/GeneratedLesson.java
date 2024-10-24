package com.handson.sentiment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.handson.sentiment.util.Dates;
import org.hibernate.validator.constraints.Length;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="generated_lesson")
public class GeneratedLesson implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false, updatable = false)
    private Date createdAt = Dates.nowUTC();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createdAt")
    public LocalDateTime calcCreatedAt() {
        return Dates.atLocalTime(createdAt);
    }

    @JsonIgnore
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "pupilId")
    private Pupil pupil;

    @Length(max = 500)
    private String imageUrl;

    @Length(max = 500)
    private String gameType;

    @Length(max = 500)
    private String planActivity;

    @Length(max = 500)
    private String imageDescription;

    @Length(max = 500)
    private String planDescription;

    @Length(max = 500)
    private String gameDescription;

    @Length(max = 500)
    private String subject;

    @Length(max = 500)
    private String topic;

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

    public Pupil getPupil() {
        return pupil;
    }

    public void setPupil(Pupil pupil) {
        this.pupil = pupil;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getPlanActivity() {
        return planActivity;
    }

    public void setPlanActivity(String planActivity) {
        this.planActivity = planActivity;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }

    public String getPlanDescription() {
        return planDescription;
    }

    public void setPlanDescription(String planDescription) {
        this.planDescription = planDescription;
    }

    public String getGameDescription() {
        return gameDescription;
    }

    public void setGameDescription(String gameDescription) {
        this.gameDescription = gameDescription;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }


    public static final class GeneratedLessonBuilder {
        private Long id;
        private @NotNull Date createdAt;
        private @NotNull Pupil pupil;
        private @Length(max = 500) String imageUrl;
        private @Length(max = 500) String gameType;
        private @Length(max = 500) String planActivity;
        private @Length(max = 500) String imageDescription;
        private @Length(max = 500) String planDescription;
        private @Length(max = 500) String gameDescription;
        private @Length(max = 500) String subject;
        private @Length(max = 500) String topic;

        private GeneratedLessonBuilder() {
        }

        public static GeneratedLessonBuilder aGeneratedLesson() {
            return new GeneratedLessonBuilder();
        }

        public GeneratedLessonBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public GeneratedLessonBuilder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public GeneratedLessonBuilder pupil(Pupil pupil) {
            this.pupil = pupil;
            return this;
        }

        public GeneratedLessonBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public GeneratedLessonBuilder gameType(String gameType) {
            this.gameType = gameType;
            return this;
        }

        public GeneratedLessonBuilder planActivity(String planActivity) {
            this.planActivity = planActivity;
            return this;
        }

        public GeneratedLessonBuilder imageDescription(String imageDescription) {
            this.imageDescription = imageDescription;
            return this;
        }

        public GeneratedLessonBuilder planDescription(String planDescription) {
            this.planDescription = planDescription;
            return this;
        }

        public GeneratedLessonBuilder gameDescription(String gameDescription) {
            this.gameDescription = gameDescription;
            return this;
        }

        public GeneratedLessonBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public GeneratedLessonBuilder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public GeneratedLesson build() {
            GeneratedLesson generatedLesson = new GeneratedLesson();
            generatedLesson.setId(id);
            generatedLesson.setCreatedAt(createdAt);
            generatedLesson.setPupil(pupil);
            generatedLesson.setImageUrl(imageUrl);
            generatedLesson.setGameType(gameType);
            generatedLesson.setPlanActivity(planActivity);
            generatedLesson.setImageDescription(imageDescription);
            generatedLesson.setPlanDescription(planDescription);
            generatedLesson.setGameDescription(gameDescription);
            generatedLesson.setSubject(subject);
            generatedLesson.setTopic(topic);
            return generatedLesson;
        }
    }
}
