package com.handson.lesson_generator.model;


public class GeneratedGameIn  {

    public static String getGamePrompt(String subject, String topic, Pupil pupil, KnownGame knownGame) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Create an educational adaptation of ").append(knownGame.name())
                .append(" for a 6th grade ").append(subject).append(" AST class, focusing on ").append(topic).append(".\\n\\n")
                .append("Game Objective: Maintain the core mechanics of ").append(knownGame.name())
                .append(" while reinforcing ").append(topic).append(".\\n\\n")
                .append("Original Game: ").append(knownGame.getActivityDescription()).append("\\n");

        if (!pupil.getInterests().isEmpty()) {
            prompt.append("Student Interests: Incorporate elements related to ").append(pupil.getInterests())
                    .append(" to enhance engagement.\\n");
        }
        if (!pupil.getPersonalityTraits().isEmpty()) {
            prompt.append("Student Personality: Consider the following traits when designing activities: ")
                    .append(pupil.getPersonalityTraits()).append("\\n");
        }
        prompt.append("\\nProvide a detailed adaptation plan including:\\n")
                .append("1. Specific rule modifications to incorporate ").append(topic).append("\\n")
                .append("2. Examples of ").append(topic).append(" integration in gameplay\\n")
                .append("Ensure the adaptation is classroom-appropriate, and maximizes student participation.");

        return prompt.toString().trim();
    }

    public static String getGameSystemPrompt() {
        StringBuilder systemRole = new StringBuilder();
        systemRole.append("You are an innovative educational game designer specializing in adapting popular games for 6th-grade AST classes. ")
                  .append("Your expertise lies in:\\n\\n")
                  .append("1. Seamlessly integrating educational content into game mechanics\\n")
                  .append("2. Optimizing games for classroom environments\\n")
                  .append("3. Maximizing student engagement and participation\\n")
                  .append("4. Personalizing game elements based on student interests and personality traits\\n\\n")
                  .append("When designing games, prioritize:\\n")
                  .append("- Simplicity and clarity in rules\\n")
                  .append("- Balancing educational value with entertainment\\n")
                  .append("Your ultimate goal is to create engaging, educational games that effectively reinforce learning objectives.");
        return systemRole.toString();
    }

    public static String getGameDescription(KnownGame game) {
        switch (game) {
            case Hangman:
                return "A classic word-guessing game where players attempt to uncover a hidden word by suggesting letters, with each incorrect guess bringing them closer to 'hanging' a stick figure.";
            case Memory:
                return "A cognitive card game that challenges players to find matching pairs by flipping over face-down cards, testing and improving memory skills.";
            case Charades:
                return "An energetic party game where participants act out words or phrases without speaking, encouraging creativity and non-verbal communication skills.";
            case Bingo:
                return "A popular game of chance where players mark off numbers on their cards as they are randomly called, aiming to be the first to complete a winning pattern.";
            case ScavengerHunt:
                return "An adventurous game that involves searching for specific items or completing tasks from a predetermined list, often played in teams and encouraging exploration and problem-solving.";
            default:
                return "A fun and engaging game to enhance learning and social interaction.";
        }
    }

}
