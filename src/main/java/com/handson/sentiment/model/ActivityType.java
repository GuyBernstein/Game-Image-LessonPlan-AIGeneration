package com.handson.sentiment.model;

public enum ActivityType {
    Classroom("Frontal lecture in class"),
    Computerized("A computerized class"),
    GroupWork("Group discussion"),
    OutSide("Lesson outside the classroom"),
    Remote("Online lesson"),
    Emotional("Based on personality traits"),
    Reinforcement("Based on interests");
    public final String lessonActivity;

    public String getLessonActivity() {
        return lessonActivity;
    }

    ActivityType(String lessonActivity) {
        this.lessonActivity = lessonActivity;
    }
}
