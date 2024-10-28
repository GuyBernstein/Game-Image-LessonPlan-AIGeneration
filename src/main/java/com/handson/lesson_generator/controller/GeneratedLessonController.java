package com.handson.lesson_generator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handson.lesson_generator.model.*;
import com.handson.lesson_generator.service.*;
import com.handson.lesson_generator.service.AiTextGenerationService.TextResult;
import com.handson.lesson_generator.service.AIImageGenerationService.ImageResult;
import com.handson.lesson_generator.util.HandsonException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.validation.constraints.Min;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import static com.handson.lesson_generator.model.GeneratedGameIn.getGamePrompt;
import static com.handson.lesson_generator.model.GeneratedGameIn.getGameSystemPrompt;
import static com.handson.lesson_generator.model.GeneratedLessonPlanIn.getLessonPlanPrompt;
import static com.handson.lesson_generator.model.GeneratedLessonPlanIn.getLessonPlanSystemPrompt;
import static com.handson.lesson_generator.util.FPS.FPSBuilder.aFPS;
import static com.handson.lesson_generator.util.FPSCondition.FPSConditionBuilder.aFPSCondition;
import static com.handson.lesson_generator.util.FPSField.FPSFieldBuilder.aFPSField;
import static com.handson.lesson_generator.util.Strings.likeLowerOrNull;


@RestController
@CrossOrigin(origins = {"http://localhost:8080", "https://pupil-lesson-generator.runmydocker-app.com"})
@RequestMapping("/api/pupils/lessons")
public class GeneratedLessonController {

    private final PupilService pupilService;
    private final GeneratedLessonService generatedLessonService;
    private final AIImageGenerationService aiImageGenerationService;
    private final AiTextGenerationService aiTextGenerationService;
    private final AWSService awsService;
    private final Map<String, CompletableFuture<GeneratedLessonOut>> taskFutures;

    private final EntityManager em;

    private final ObjectMapper om;

    public PupilService getPupilService() {
        return pupilService;
    }

    public AIImageGenerationService getAiImageGenerationService() {
        return aiImageGenerationService;
    }

    public AiTextGenerationService getAiTextGenerationService() {
        return aiTextGenerationService;
    }

    public AWSService getAwsService() {
        return awsService;
    }

    public GeneratedLessonService getGeneratedLessonService() {
        return generatedLessonService;
    }

    public Map<String, CompletableFuture<GeneratedLessonOut>> getTaskFutures() {
        return taskFutures;
    }

    public EntityManager getEm() {
        return em;
    }

    public ObjectMapper getOm() {
        return om;
    }


    @Autowired
    public GeneratedLessonController(PupilService pupilService, GeneratedLessonService generatedLessonService, AIImageGenerationService aiImageGenerationService, AiTextGenerationService aiTextGenerationService, AWSService awsService, Map<String, CompletableFuture<GeneratedLessonOut>> taskFutures, EntityManager em, ObjectMapper om) {
        this.pupilService = pupilService;
        this.generatedLessonService = generatedLessonService;
        this.aiImageGenerationService = aiImageGenerationService;
        this.aiTextGenerationService = aiTextGenerationService;
        this.awsService = awsService;
        this.taskFutures = taskFutures;
        this.em = em;
        this.om = om;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<PaginationAndList> search(@RequestParam(required = false) String subject,
                                                    @RequestParam(required = false) Integer fromPupilId,
                                                    @RequestParam(required = false) Integer toPupilId,
                                                    @RequestParam(required = false) String topic,
                                                    @RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "50") @Min(1) Integer count,
                                                    @RequestParam(defaultValue = "id") GeneratedLessonSortField sort, @RequestParam(defaultValue = "asc") SortDirection sortDirection) throws JsonProcessingException {

        var res = aFPS().select(List.of(
                        aFPSField().field("gl.id").alias("id").build(),
                        aFPSField().field("gl.created_at").alias("createdat").build(),
                        aFPSField().field("gl.subject").alias("subject").build(),
                        aFPSField().field("gl.topic").alias("topic").build(),
                        aFPSField().field("gl.image_url").alias("imageurl").build(),
                        aFPSField().field("gl.game_type").alias("gametype").build(),
                        aFPSField().field("gl.plan_activity").alias("planactivity").build(),
                        aFPSField().field("gl.image_description").alias("imagedescription").build(),
                        aFPSField().field("gl.game_description").alias("gamedescription").build(),
                        aFPSField().field("gl.plan_description").alias("plandescription").build(),
                        aFPSField().field("(select max(p.id) from pupil p where gl.pupil_id = p.id) ").alias("pupilid").build()
                ))
                .from(List.of(" generated_lesson gl"))
                .conditions(List.of(
                        aFPSCondition().condition("( lower(subject) like :subject )").parameterName("subject").value(likeLowerOrNull(subject)).build(),
                        aFPSCondition().condition("( lower(topic) like :topic )").parameterName("topic").value(likeLowerOrNull(topic)).build(),
                        aFPSCondition().condition("( (select max(p.id) from pupil p where gl.pupil_id = p.id) >= :fromPupilId )").parameterName("fromPupilId").value(fromPupilId).build(),
                        aFPSCondition().condition("( (select max(p.id) from pupil p where gl.pupil_id = p.id) <= :toPupilId )").parameterName("toPupilId").value(toPupilId).build()
                )).sortField(sort.fieldName).sortDirection(sortDirection).page(page).count(count)
                .itemClass(GeneratedLessonOut.class)
                .build().exec(em, om);
        return ResponseEntity.ok(res);
    }

    @RequestMapping(value = "/{pupilId}/lessons", method = RequestMethod.POST)
    public ResponseEntity<?> insertLesson(@PathVariable Long pupilId,
                                          @RequestBody GeneratedLessonIn generatedLessonIn,
                                          @RequestParam ActivityType activityType,
                                          @RequestParam KnownGame knownGame,
                                          @RequestParam String imageDescription){

        var dbPupil = pupilService.findById(pupilId);
        if (dbPupil.isEmpty()) {
            throw new HandsonException("Pupil:" + pupilId + " not found");
        }

        // Save the initial lesson to the database
        GeneratedLesson lesson = generatedLessonService.save(generatedLessonIn.toGeneratedLesson(dbPupil.get()));

        String taskId = UUID.randomUUID().toString();
        CompletableFuture<GeneratedLessonOut> future = CompletableFuture.supplyAsync(() -> {
            try {
                // Generate lesson Plan
                aiTextGenerationService.setUserPromptPlan(getLessonPlanPrompt(lesson.getTopic(), lesson.getSubject(), dbPupil.get(), activityType));
                aiTextGenerationService.setSystemPromptPlan(getLessonPlanSystemPrompt());
                TextResult textResult = aiTextGenerationService.generateText(AiMessageType.PLAN);

                // Store generated text content in amazon s3
                String bucketPath = "apps/guybv/pupil-" + pupilId + "/plan-" + textResult.getCreated() + ".txt";
                awsService.putTextInBucket(textResult.getContent(), bucketPath);
                lesson.setPlanDescription(bucketPath);

                // Generate Game
                aiTextGenerationService.setUserPromptGame(getGamePrompt(lesson.getTopic(), lesson.getSubject(), dbPupil.get(), knownGame));
                aiTextGenerationService.setSystemPromptGame(getGameSystemPrompt());
                textResult = aiTextGenerationService.generateText(AiMessageType.GAME);

                // Store generated text content in amazon s3
                bucketPath = "apps/guybv/pupil-" + pupilId + "/game-" + textResult.getCreated() + ".txt";
                awsService.putTextInBucket(textResult.getContent(), bucketPath);
                lesson.setGameDescription(bucketPath);

                // Generate image
                GeneratedImageIn generatedImageIn = new GeneratedImageIn(imageDescription);
                GeneratedLesson generatedImage = generatedImageIn.toGeneratedLesson(dbPupil.get());
                ImageResult imageResult = aiImageGenerationService.generateImage(generatedImage.getImageDescription());

                // Store image file in amazon s3
                bucketPath = "apps/guybv/pupil-" + pupilId + "/image-" + imageResult.getCreated() + ".png";
                awsService.putUrlInBucket(imageResult.getImageUrl(), bucketPath);
                lesson.setImageUrl(bucketPath);

                // Update in Database
                lesson.setImageDescription(imageDescription);
                lesson.setGameType(knownGame.getActivityDescription());
                lesson.setPlanActivity(activityType.getLessonActivity());
                return GeneratedLessonOut.of(generatedLessonService.save(lesson), awsService, AiMessageType.INIT);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
        getTaskFutures().put(taskId, future);

        Map<String, String> response = new HashMap<>();
        response.put("taskId", taskId);
        response.put("message", "LESSON GENERATION STARTED");

        return ResponseEntity.accepted().body(response);
    }

    @RequestMapping(value = "/{pupilId}/{lessonId}/regenerate", method = RequestMethod.PUT)
    public ResponseEntity<?> regenerateLesson(@PathVariable Long pupilId,
                                              @PathVariable Long lessonId,
                                              @RequestParam ActivityType activityType,
                                              @RequestParam KnownGame knownGame,
                                              @RequestParam String imageDescription){

        var dbPupil = pupilService.findById(pupilId);
        if (dbPupil.isEmpty()) {
            throw new HandsonException("Pupil:" + pupilId + " not found");
        }

        Optional<GeneratedLesson> dbGeneratedLesson = generatedLessonService.findById(lessonId);
        if (dbGeneratedLesson.isEmpty()) throw new HandsonException("lesson with lessonId: " + lessonId + " not found");

        // Get the lesson from the database and use it to update the generative ai content
        GeneratedLesson lesson = dbGeneratedLesson.get();

        String taskId = UUID.randomUUID().toString();
        CompletableFuture<GeneratedLessonOut> future = CompletableFuture.supplyAsync(() -> {
            try {
                // Generate lesson Plan
                aiTextGenerationService.setUserPromptPlan(getLessonPlanPrompt(lesson.getTopic(), lesson.getSubject(), dbPupil.get(), activityType));
                aiTextGenerationService.setSystemPromptPlan(getLessonPlanSystemPrompt());
                TextResult textResult = aiTextGenerationService.generateText(AiMessageType.PLAN);

                // Store generated text content in amazon s3
                String bucketPath = "apps/guybv/pupil-" + pupilId + "/plan-" + textResult.getCreated() + ".txt";
                awsService.putTextInBucket(textResult.getContent(), bucketPath);
                lesson.setPlanDescription(bucketPath);

                // Generate Game
                aiTextGenerationService.setUserPromptGame(getGamePrompt(lesson.getTopic(), lesson.getSubject(), dbPupil.get(), knownGame));
                aiTextGenerationService.setSystemPromptGame(getGameSystemPrompt());
                textResult = aiTextGenerationService.generateText(AiMessageType.GAME);

                // Store generated text content in amazon s3
                bucketPath = "apps/guybv/pupil-" + pupilId + "/game-" + textResult.getCreated() + ".txt";
                awsService.putTextInBucket(textResult.getContent(), bucketPath);
                lesson.setGameDescription(bucketPath);

                // Generate image
                GeneratedImageIn generatedImageIn = new GeneratedImageIn(imageDescription);
                GeneratedLesson generatedImage = generatedImageIn.toGeneratedLesson(dbPupil.get());
                ImageResult imageResult = aiImageGenerationService.generateImage(generatedImage.getImageDescription());

                // Store image file in amazon s3
                bucketPath = "apps/guybv/pupil-" + pupilId + "/image-" + imageResult.getCreated() + ".png";
                awsService.putUrlInBucket(imageResult.getImageUrl(), bucketPath);
                lesson.setImageUrl(bucketPath);

                // Update in Database
                lesson.setImageDescription(imageDescription);
                lesson.setGameType(knownGame.getActivityDescription());
                lesson.setPlanActivity(activityType.getLessonActivity());
                return GeneratedLessonOut.of(generatedLessonService.save(lesson), awsService, AiMessageType.INIT);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
        getTaskFutures().put(taskId, future);

        Map<String, String> response = new HashMap<>();
        response.put("taskId", taskId);
        response.put("message", "LESSON GENERATION STARTED");

        return ResponseEntity.accepted().body(response);
    }

    @RequestMapping(value = "/{pupilId}/{lessonId}/plan", method = RequestMethod.PUT)
    public ResponseEntity<?> updateGeneratedLessonPlan(@PathVariable Long pupilId,
                                                       @PathVariable Long lessonId,
                                                       @RequestParam ActivityType activityType) {
        var dbLesson = generatedLessonService.findById(lessonId);
        if (dbLesson.isEmpty()) {
            throw new HandsonException("lesson:" + lessonId + " not found");
        }

        var dbPupil = pupilService.findById(pupilId);
        if (dbPupil.isEmpty()) {
            throw new HandsonException("Pupil:" + pupilId + " not found");
        }
        GeneratedLesson lesson = dbLesson.get();
        String taskId = UUID.randomUUID().toString();

        CompletableFuture<GeneratedLessonOut> future = CompletableFuture.supplyAsync(() -> {
            try {
                // Generate lesson Plan
                aiTextGenerationService.setUserPromptPlan(getLessonPlanPrompt(lesson.getTopic(), lesson.getSubject(), dbPupil.get(), activityType));
                aiTextGenerationService.setSystemPromptPlan(getLessonPlanSystemPrompt());
                TextResult textResult = aiTextGenerationService.generateText(AiMessageType.PLAN);

                // Store generated text content in amazon s3
                String bucketPath = "apps/guybv/pupil-" + pupilId + "/plan-" + textResult.getCreated() + ".txt";
                awsService.putTextInBucket(textResult.getContent(), bucketPath);

                // Update in Database
                lesson.setPlanActivity(activityType.getLessonActivity());
                lesson.setPlanDescription(bucketPath);
                return GeneratedLessonOut.of(generatedLessonService.save(lesson),awsService, AiMessageType.PLAN);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
        getTaskFutures().put(taskId, future);

        Map<String, String> response = new HashMap<>();
        response.put("taskId", taskId);
        response.put("message", "LESSON PLAN GENERATION STARTED");

        return ResponseEntity.accepted().body(response);
    }

    @RequestMapping(value = "/{pupilId}/{lessonId}/game", method = RequestMethod.PUT)
    public ResponseEntity<?> updateGeneratedGame(@PathVariable Long pupilId,
                                                 @PathVariable Long lessonId,
                                                 @RequestParam KnownGame knownGame) {
        var dbLesson = generatedLessonService.findById(lessonId);
        if (dbLesson.isEmpty()) {
            throw new HandsonException("lesson:" + lessonId + " not found");
        }
        var dbPupil = pupilService.findById(pupilId);
        if (dbPupil.isEmpty()) {
            throw new HandsonException("Pupil:" + pupilId + " not found");
        }

        GeneratedLesson lesson = dbLesson.get();
        String taskId = UUID.randomUUID().toString();

        CompletableFuture<GeneratedLessonOut> future = CompletableFuture.supplyAsync(() -> {
            try {
                // Generate Game
                aiTextGenerationService.setSystemPromptGame(getGameSystemPrompt());
                aiTextGenerationService.setUserPromptGame(getGamePrompt(lesson.getTopic(), lesson.getSubject(), dbPupil.get(), knownGame));
                TextResult textResult = aiTextGenerationService.generateText(AiMessageType.GAME);

                // Store generated text content in amazon s3
                String bucketPath = "apps/guybv/pupil-" + pupilId + "/game-" + textResult.getCreated() + ".txt";
                awsService.putTextInBucket(textResult.getContent(), bucketPath);

                // Update in Database
                lesson.setGameType(knownGame.getActivityDescription());
                lesson.setGameDescription(bucketPath);
                return GeneratedLessonOut.of(generatedLessonService.save(lesson), awsService, AiMessageType.GAME);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
        getTaskFutures().put(taskId, future);

        Map<String, String> response = new HashMap<>();
        response.put("taskId", taskId);
        response.put("message", "GAME GENERATION STARTED");

        return ResponseEntity.accepted().body(response);
    }

    @RequestMapping(value = "/{pupilId}/{lessonId}/image", method = RequestMethod.PUT)
    public ResponseEntity<?> updateGeneratedImage(@PathVariable Long pupilId,
                                                  @PathVariable Long lessonId,
                                                  @RequestParam String imageDescription) {
        var dbLesson = generatedLessonService.findById(lessonId);
        if (dbLesson.isEmpty()) {
            throw new HandsonException("lesson:" + lessonId + " not found");
        }
        var dbPupil = pupilService.findById(pupilId);
        if (dbPupil.isEmpty()) {
            throw new HandsonException("Pupil:" + pupilId + " not found");
        }

        GeneratedLesson lesson = dbLesson.get();
        String taskId = UUID.randomUUID().toString();

        CompletableFuture<GeneratedLessonOut> future = CompletableFuture.supplyAsync(() -> {
            try {
                // Generate image
                GeneratedImageIn generatedImageIn = new GeneratedImageIn(imageDescription);
                GeneratedLesson generatedImage = generatedImageIn.toGeneratedLesson(dbPupil.get());
                ImageResult imageResult = aiImageGenerationService.generateImage(generatedImage.getImageDescription());

                // Store image file in amazon s3
                String bucketPath = "apps/guybv/pupil-" + pupilId + "/image-" + imageResult.getCreated() + ".png";
                awsService.putUrlInBucket(imageResult.getImageUrl(), bucketPath);

                // Update in Database
                lesson.setImageUrl(bucketPath);
                lesson.setImageDescription(imageDescription);
                return GeneratedLessonOut.of(generatedLessonService.save(lesson),awsService, AiMessageType.IMAGE);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
        getTaskFutures().put(taskId, future);

        Map<String, String> response = new HashMap<>();
        response.put("taskId", taskId);
        response.put("message", "IMAGE GENERATION STARTED");

        return ResponseEntity.accepted().body(response);
    }


    @GetMapping("/{pupilId}/{lessonId}/display")
    public ResponseEntity<?> getOneLesson(@PathVariable Long pupilId, @PathVariable Long lessonId) throws UnirestException, JsonProcessingException {
        Optional<Pupil> dbPupil = pupilService.findById(pupilId);
        if (dbPupil.isEmpty()) throw new HandsonException("Pupil with id: " + pupilId + " not found");

        Optional<GeneratedLesson> dbGeneratedLesson = generatedLessonService.findById(lessonId);
        if (dbGeneratedLesson.isEmpty()) {
            throw new HandsonException("Lesson with id: " + lessonId + " not found");
        }

        return ResponseEntity.ok(GeneratedLessonOut.of(dbGeneratedLesson.get(), awsService, AiMessageType.INIT));
    }

    @GetMapping("/status/{taskId}")
    public ResponseEntity<?> getGenerationStatus(@PathVariable String taskId) throws ExecutionException, InterruptedException {
        CompletableFuture<GeneratedLessonOut> future = getTaskFutures().get(taskId);
        if (future == null) {
            return ResponseEntity.notFound().build();
        }

        if (future.isDone()) {
            GeneratedLessonOut result = future.get();
            getTaskFutures().remove(taskId);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.ok(Map.of("status", "processing"));
        }
    }

    @RequestMapping(value = "/{pupilId}/{lessonId}/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteGeneratedLesson(@PathVariable Long pupilId, @PathVariable Long lessonId)
    {
        Optional<Pupil> dbPupil = pupilService.findById(pupilId);
        if (dbPupil.isEmpty()) throw new HandsonException("Pupil with lessonId: " + pupilId + " not found");

        Optional<GeneratedLesson> dbGeneratedLesson = generatedLessonService.findById(lessonId);
        if (dbGeneratedLesson.isEmpty()) throw new HandsonException("lesson with lessonId: " + lessonId + " not found");

        generatedLessonService.delete(dbGeneratedLesson.get());
        return new ResponseEntity<>("DELETED", HttpStatus.OK);
    }


}
