package com.handson.sentiment.model;

import com.handson.sentiment.util.Dates;

import static com.handson.sentiment.model.GeneratedLesson.GeneratedLessonBuilder.aGeneratedLesson;

public class GeneratedImageIn {
    
    private String description;

    public GeneratedImageIn(String description) {
        this.description = description;
    }

    public GeneratedLesson toGeneratedLesson(Pupil pupil) {
        String prompt = getImagePrompt(pupil);

        return aGeneratedLesson().createdAt(Dates.nowUTC())
                .pupil(pupil)
                .imageDescription(prompt)
                .build();
    }

    public String getDescription() {
        return description;
    }

    private String getImagePrompt(Pupil pupil) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Create a simple, fun outline drawing for a child to paint in class. ");
        prompt.append("The drawing should depict ").append(description).append(". ");

        if (!pupil.getInterests().isEmpty()) {
            prompt.append("Include elements of ").append(pupil.getInterests()).append(" that the child likes. ");
        }

        if (!pupil.getPersonalityTraits().isEmpty()) {
            prompt.append("The drawing should reflect ").append(pupil.getPersonalityTraits()).append(" personality traits. ");
        }
        prompt.append("Keep the lines clear and shapes basic, perfect for young artists to color in.");

        return prompt.toString().trim();
    }

}
