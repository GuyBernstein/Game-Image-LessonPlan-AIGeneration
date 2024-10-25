package com.handson.lesson_generator.model;

public enum GeneratedLessonSortField {
    id("gl.id") ,
    createdAt ("gl.created_at"),
    subject ("gl.subject"),
    pupilId ("(select max(p.id) from pupil p where gl.pupil_id = p.id) )"),
    topic("gl.topic");


    public final String fieldName;
    private GeneratedLessonSortField(String fieldName) {
        this.fieldName = fieldName;
    }
}
