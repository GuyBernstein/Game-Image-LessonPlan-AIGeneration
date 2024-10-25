package com.handson.lesson_generator.model;

import com.handson.lesson_generator.util.Dates;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

import static com.handson.lesson_generator.model.GeneratedLesson.GeneratedLessonBuilder.aGeneratedLesson;

public class GeneratedLessonIn implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty
    @Length(max = 500)
    private String subject;

    @NotEmpty
    @Length(max = 500)
    private String topic;

    public GeneratedLesson toGeneratedLesson(Pupil pupil) {

        return aGeneratedLesson().createdAt(Dates.nowUTC())
                .pupil(pupil)
                .topic(topic)
                .subject(subject)
                .build();
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
}
