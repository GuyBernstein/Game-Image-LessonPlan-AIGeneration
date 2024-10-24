package com.handson.sentiment.model;

public class GeneratedLessonPlanIn {
    public static String getLessonPlanPrompt(String subject, String topic, Pupil pupil, ActivityType lessonType) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Create a detailed lesson plan for a 6th grade ASD ").append(subject)
                .append(" class, focusing on ").append(topic).append(".\\n\\n")
                .append("Lesson Objective: Design an engaging lesson that effectively teaches ").append(topic)
                .append(" while catering to the student's personality traits and interests.\\n\\n")
                .append("Lesson Type: ").append(lessonType.lessonActivity).append("\\n\\n")
                .append("Tailor the lesson plan specifically for a ").append(getLessonTypeDescription(lessonType)).append(".\\n\\n");

        if (!pupil.getInterests().isEmpty()) {
            prompt.append("Student Interests: Incorporate elements related to ").append(pupil.getInterests())
                    .append(" to enhance engagement.\\n\\n");
        }

        if(!pupil.getPersonalityTraits().isEmpty()) {
            prompt.append("Student Personality: Consider the following traits when designing activities: ")
                    .append(pupil.getPersonalityTraits()).append("\\n\\n");
        }

        prompt.append("Provide a comprehensive lesson plan including:\\n")
                .append("1. Introduction: A brief overview of the lesson\\n")
                .append("2. Objectives: Clear, measurable learning objectives\\n")
                .append("3. Standards: Relevant educational standards addressed\\n")
                .append("4. Materials: List of required materials and resources\\n")
                .append("5. Activities: Detailed description of lesson activities, specifically designed for ")
                .append(lessonType.lessonActivity).append("\\n")
                .append("6. Differentiation: Strategies to accommodate diverse learning needs\\n")
                .append("7. Assessment: Methods to evaluate student understanding\\n")
                .append("8. Closure: Summary and reflection on the lesson\\n\\n")
                .append("Ensure the lesson plan is 6th grade-appropriate, engaging, and promotes active learning ")
                .append("while considering the specific needs of ASD students.");

        return prompt.toString().trim();
    }

    public static String getLessonPlanSystemPrompt() {
        StringBuilder systemRole = new StringBuilder();
        systemRole.append("You are an experienced, innovative educator specializing in creating engaging lesson plans for 6th-grade students with Autism Spectrum Disorder (ASD). ")
                .append("Your expertise includes:\\n\\n")
                .append("1. Designing lessons that cater to various learning styles and sensory needs of ASD students\\n")
                .append("2. Integrating student interests and personalities into lesson activities to enhance engagement\\n")
                .append("3. Crafting clear, measurable learning objectives appropriate for 6th-grade ASD students\\n")
                .append("4. Developing effective assessment strategies that accommodate diverse communication styles\\n")
                .append("5. Incorporating technology, visual aids, and hands-on activities to support learning\\n")
                .append("6. Creating structured, predictable lesson formats that reduce anxiety and support executive functioning\\n")
                .append("7. Implementing strategies to support social skills development within academic contexts\\n")
                .append("8. Adapting lessons for various learning environments (classroom, online, outdoor, etc.)\\n\\n")
                .append("When creating lesson plans, prioritize:\\n")
                .append("- Alignment with educational standards and IEP goals\\n")
                .append("- Engaging and interactive learning experiences that consider sensory sensitivities\\n")
                .append("- Differentiation strategies for diverse learners within the ASD spectrum\\n")
                .append("- Clear, visual, and logical lesson structure with smooth transitions\\n")
                .append("- Incorporation of special interests as motivators and learning tools\\n")
                .append("- Strategies for managing potential behavioral challenges\\n")
                .append("- Opportunities for practicing and generalizing skills across settings\\n\\n")
                .append("Your ultimate goal is to create comprehensive, adaptable lesson plans that:\\n")
                .append("1. Effectively achieve learning objectives for 6th-grade content\\n")
                .append("2. Keep ASD students motivated, engaged, and supported\\n")
                .append("3. Foster independence and self-advocacy skills\\n")
                .append("4. Promote positive social interactions and communication\\n")
                .append("5. Accommodate individual sensory needs and learning preferences\\n")
                .append("6. Prepare students for successful transitions to middle school and beyond\\n\\n")
                .append("Tailor your lesson plans to the specific activity type provided, ensuring that the learning experience is optimized for that particular setting or approach.");

        return systemRole.toString();
    }

    public static String getLessonTypeDescription(ActivityType lessonType) {
        switch (lessonType) {
            case Classroom:
                return "traditional classroom setting with a focus on interactive frontal instruction";
            case Computerized:
                return "computer-based learning environment, integrating digital tools and resources";
            case GroupWork:
                return "collaborative learning experience emphasizing group discussions and teamwork";
            case OutSide:
                return "outdoor educational experience, incorporating the natural environment into the lesson";
            case Remote:
                return "virtual classroom setting, optimizing online tools for remote learning";
            case Emotional:
                return "lesson that particularly addresses emotional intelligence and social skills";
            case Reinforcement:
                return "lesson that reinforces previous learning, with a focus on the student's interests";
            default:
                return "standard lesson format";
        }
    }

}
