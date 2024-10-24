package com.handson.sentiment.model;

public enum PupilSortField {
    id("p.id") ,
    createdAt ("p.created_at"),

    birthdate("p.birthdate"),
    fullName ("p.fullname"),
    interests("p.interests"),
    personalityTraits("p.personality_traits"),
    lessonsCount ("(select count(gl.id) from generated_lesson gl where gl.pupil_id = p.id ) ");


    public final String fieldName;
    PupilSortField(String fieldName) {
        this.fieldName = fieldName;
    }
}
