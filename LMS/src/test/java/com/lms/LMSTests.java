package com.lms;

import static com.lms.LMSTestFunctions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

import com.lms.business.models.QuestionRequest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class LMSTests {

    static String adminEmail = "admin@example.com";
    static String adminPassword = "password123";

    static String instructorEmail = "instructor@example.com";
    static String instructorPassword = "password123";

    static String studentId = "S01";
    static String studentEmail = "student@example.com";
    static String studentPassword = "password123";

    static String adminToken;
    static String instructorToken;
    static String studentToken;

    static Boolean instructorCreated = false;
    static Boolean studentCreated = false;

    static Boolean courseCreated = false;
    static String lastCourseIdCreated;

    static int numberOfAddedQuestions = 0;

    static String lastQuizTrueAnswers;

    static String lastQuizId;

    static boolean quizSubmissionExist = false;

    QuestionBankTests questionBankTests = new QuestionBankTests();
    QuizTests quizTests = new QuizTests();
    SetupTests setupTests = new SetupTests();
    CourseTests courseTests = new CourseTests();

    @Nested
    @Order(0)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class SetupTests {

        @Test
        @Order(1)
        void testSignupAdmin() throws IOException {
            if (adminToken == null) {
                LMSResponse signUpAdmin = signup(
                        "A01",
                        "John",
                        "Doe",
                        adminEmail,
                        adminPassword,
                        "Admin"
                );

                // System.out.println(
                //   signUpAdmin.code == 200 ? "Admin created" : "Admin creation failed"
                // );

                LMSResponse loginAdmin = login(adminEmail, adminPassword);
                adminToken = getToken(loginAdmin.body);

                System.out.println("AdminToken: \n" + "Bearer " + adminToken);
                // the prented text in the debug console
                assertEquals(200, loginAdmin.code);
            }
        }

        @Test
        @Order(2)
        void testCreateInstructor() throws IOException {
            if (adminToken == null) {
                testSignupAdmin();
            }

            LMSResponse createInstructor = createUser(
                    adminToken,
                    "I01",
                    "John",
                    "Doe",
                    instructorEmail,
                    instructorPassword,
                    "Instructor"
            );

            System.out.println("Instructor created: " + createInstructor.code);
            instructorCreated = true;

            assertEquals(200, createInstructor.code);
        }

        @Test
        @Order(3)
        void testLoginInstructor() throws IOException {
            if (!instructorCreated) {
                testCreateInstructor();
            }

            LMSResponse loginInstructor = login(instructorEmail, instructorPassword);
            instructorToken = getToken(loginInstructor.body);

            System.out.println("InstructorToken: \n" + "Bearer " + instructorToken);
            assertEquals(200, loginInstructor.code);
        }

        @Test
        @Order(4)
        void testCreateStudent() throws IOException {
            if (adminToken == null) {
                testSignupAdmin();
            }

            LMSResponse createStudent = createUser(
                    adminToken,
                    studentId,
                    "John",
                    "Doe",
                    studentEmail,
                    studentPassword,
                    "Student"
            );

            System.out.println("Student created: " + createStudent.code);
            studentCreated = true;
            assertEquals(200, createStudent.code);
        }

        @Test
        @Order(5)
        void testLoginStudent() throws IOException {
            if (!studentCreated) {
                testCreateStudent();
            }

            LMSResponse loginStudent = login(studentEmail, studentPassword);
            studentToken = getToken(loginStudent.body);

            System.out.println("StudentToken: \n" + "Bearer " + studentToken);
            assertEquals(200, loginStudent.code);
        }
    }

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CourseTests {
        @Test
        void testCreateCourse() throws IOException {
            if (instructorToken == null) {
                setupTests.testLoginInstructor();
            }

            String courseId = "C01";
            String courseTitle = "Introduction to Java";
            String courseDescription = "A beginner-level course on Java programming.";
            int courseDuration = 30;
            String profId = "Prof01";

            LMSResponse createCourseResponse = createCourse(
                    instructorToken,
                    courseId,
                    courseTitle,
                    courseDescription,
                    courseDuration,
                    profId
            );

            System.out.println(
                    "Course creation response code: " + createCourseResponse.code
            );
            //assertEquals(200, createCourseResponse.code);
            boolean successMessageFound = createCourseResponse.body.contains(
                    "successfully!"
            );
            if (successMessageFound) {
                String[] parts = createCourseResponse.body.split(" ");
                lastCourseIdCreated = parts[1];
                courseCreated = true;
                System.out.println(
                        "Course " + lastCourseIdCreated + " created successfully."
                );
            }
            assertTrue(
                    successMessageFound,
                    "Course creation failed or success message not found in response."
            );
        }
    }


    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class LessonTests {
        @Test
        void testCreateLesson() throws IOException {
            if (lastCourseIdCreated == null) {
                courseTests.testCreateCourse();
            }

            String lessonId = "L01";
            String lessonTitle = "Java Basics";
            String lessonContent = "Introduction to Java programming concepts."; // Can be text or a URL

            LMSResponse createLessonResponse = createLesson(
                    instructorToken,
                    lessonId,
                    lessonTitle,
                    lessonContent,
                    lastCourseIdCreated
            );

            System.out.println(
                    "Lesson creation response code: " + createLessonResponse.code
            );

            // Assert that the response body contains the string "successfully!"
            boolean successMessageFound = createLessonResponse.body.contains(
                    "successfully"
            );
            System.out.println(createLessonResponse.body);

            assertTrue(
                    successMessageFound,
                    "Lesson creation failed or success message not found in response."
            );
        }
    }


    @Nested
    @Order(3)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class QuestionBankTests {

        @Test
        @Order(1)
        public void testAddMCQQuestionToQuestionBank() throws IOException {
            if (instructorToken == null) {
                setupTests.testLoginInstructor();
            }

            if (lastCourseIdCreated == null) {
                courseTests.testCreateCourse();
            }

            String type = "MCQ";
            String questionText = "What is the capital of France?";
            int grade = 5;
            List<String> options = List.of("Paris", "London", "Berlin", "Rome");
            String correctAnswer = "Paris";

            QuestionRequest questionRequest = new QuestionRequest(
                    type,
                    questionText,
                    grade,
                    options,
                    correctAnswer,
                    null
            );

            LMSResponse response = addQuestionToQuestionBank(
                    instructorToken,
                    lastCourseIdCreated,
                    questionRequest
            );

            System.out.println("Add question response code: " + response.code);
//        System.out.println(response.body);

            assertEquals(200, response.code);
            assertTrue(
                    response.body.contains("successfully"),
                    "Question addition failed or success message not found in response."
            );
            numberOfAddedQuestions++;
        }

        @Test
        @Order(1)
        public void testAddTrueFalseQuestionToQuestionBank() throws IOException {
            if (instructorToken == null) {
                setupTests.testLoginInstructor();
            }


            if (lastCourseIdCreated == null) {
                courseTests.testCreateCourse();
            }

            String type = "TrueFalse";
            String questionText = "Paris is the capital of France?";
            int grade = 1;
            boolean correctAnswerBoolean = true;

            QuestionRequest questionRequest = new QuestionRequest(
                    type,
                    questionText,
                    grade,
                    null,
                    null,
                    correctAnswerBoolean
            );

            LMSResponse response = addQuestionToQuestionBank(
                    instructorToken,
                    lastCourseIdCreated,
                    questionRequest
            );


            System.out.println("Add question response code: " + response.code);
            assertEquals(200, response.code);
            assertTrue(
                    response.body.contains("successfully"),
                    "Question addition failed or success message not found in response."
            );
            numberOfAddedQuestions++;
        }

        @Test
        @Order(1)
        public void testAddShortAnswerQuestionToQuestionBank() throws IOException {
            if (instructorToken == null) {
                setupTests.testLoginInstructor();
            }


            if (lastCourseIdCreated == null) {
                courseTests.testCreateCourse();
            }

            String type = "ShortAnswer";
            String questionText = "What is the capital of France?";
            int grade = 2;
            String correctAnswer = "Paris";

            QuestionRequest questionRequest = new QuestionRequest(
                    type,
                    questionText,
                    grade,
                    null,
                    correctAnswer,
                    null
            );


            LMSResponse response = addQuestionToQuestionBank(
                    instructorToken,
                    lastCourseIdCreated,
                    questionRequest
            );

            System.out.println("Add question response code: " + response.code);

            assertEquals(200, response.code);
            assertTrue(
                    response.body.contains("successfully"),
                    "Question addition failed or success message not found in response."
            );
            numberOfAddedQuestions++;
        }

        @Test
        @Order(1)
        public void testAddMultipleQuestionToQuestionBank() throws IOException {
            if (instructorToken == null) {
                setupTests.testLoginInstructor();
            }

            if (lastCourseIdCreated == null) {
                courseTests.testCreateCourse();
            }

            String type1 = "MCQ";
            String questionText1 = "What is the capital of France?";
            int grade1 = 5;
            List<String> options1 = List.of("Paris", "London", "Berlin", "Rome");
            String correctAnswer1 = "Paris";

            QuestionRequest questionRequest1 = new QuestionRequest(
                    type1,
                    questionText1,
                    grade1,
                    options1,
                    correctAnswer1,
                    null
            );

            String type2 = "TrueFalse";
            String questionText2 = "Paris is the capital of France?";
            int grade2 = 1;
            boolean correctAnswerBoolean2 = true;

            QuestionRequest questionRequest2 = new QuestionRequest(
                    type2,
                    questionText2,
                    grade2,
                    null,
                    null,
                    correctAnswerBoolean2
            );

            String type3 = "ShortAnswer";
            String questionText3 = "What is the capital of France?";
            int grade3 = 2;
            String correctAnswer3 = "Paris";

            QuestionRequest questionRequest3 = new QuestionRequest(
                    type3,
                    questionText3,
                    grade3,
                    null,
                    correctAnswer3,
                    null
            );

            List<QuestionRequest> questionRequests = List.of(questionRequest1, questionRequest2, questionRequest3);

            LMSResponse response = addMultipleQuestionsToQuestionBank(
                    instructorToken,
                    lastCourseIdCreated,
                    questionRequests
            );

            System.out.println("Add questions response code: " + response.code);
            assertEquals(200, response.code);
            assertTrue(
                    response.body.contains("successfully"),
                    "Questions addition failed or success message not found in response."
            );
            numberOfAddedQuestions += 3;
        }

        @Test
        @Order(2)
        public void testGetQuestionsFromQuestionBank() throws IOException {
            if (instructorToken == null) {
                setupTests.testLoginInstructor();
            }

            if (lastCourseIdCreated == null) {
                courseTests.testCreateCourse();
            }


            LMSResponse response = getQuestionsFromQuestionBank(
                    instructorToken,
                    lastCourseIdCreated
            );

            System.out.println("Get questions response code: " + response.code);

            assertEquals(200, response.code);
            assertNotNull(response.body);
            assertTrue(response.body.contains("["));
        }

        @Test
        @Order(3)
        public void testDeleteQuestion() throws IOException {
            testAddMultipleQuestionToQuestionBank();

            LMSResponse getQuestsResponse = getQuestionsFromQuestionBank(
                    instructorToken,
                    lastCourseIdCreated
            );


            String questionIdToDelete = getQuestsResponse.body.split("\"")[3];

            LMSResponse deleteQuestResponse = deleteQuestionFromQuestionBank(instructorToken, lastCourseIdCreated, questionIdToDelete);

            System.out.println("Delete question response code: " + deleteQuestResponse.code);
            assertEquals(200, deleteQuestResponse.code);
            assertTrue(deleteQuestResponse.body.contains("successfully"));

            LMSResponse getQuestsResponseAfterDelete = getQuestionsFromQuestionBank(
                    instructorToken,
                    lastCourseIdCreated
            );

            String questionsListAfterDelete = getQuestsResponseAfterDelete.body;
            String questionIdAfterDelete = getQuestsResponseAfterDelete.body.split("\"")[3];

            assertNotEquals(questionIdToDelete, questionIdAfterDelete);
            assertFalse(questionsListAfterDelete.contains(questionIdToDelete));
            assertTrue(questionsListAfterDelete.contains(questionIdAfterDelete));
            numberOfAddedQuestions--;
        }

    }

    @Nested
    @Order(4)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class QuizTests {

        @Test
        @Order(1)
        public void testCreateQuiz() throws IOException {
            if (lastCourseIdCreated == null) {
                courseTests.testCreateCourse();
            }
            if (numberOfAddedQuestions < 1) {
                questionBankTests.testAddMultipleQuestionToQuestionBank();
            }

            // Implement create quiz request and return response
            String title = "Quiz Title";
            int questionNumber = numberOfAddedQuestions;
            int duration = 60;
            String status = "opened";


            LMSResponse response = createQuiz(instructorToken, lastCourseIdCreated, title, questionNumber, duration, status);
            System.out.println("Create quiz response code: " + response.code);
            try {
                lastQuizTrueAnswers = response.body.split("\"correctAnswers\":")[1].substring(0, response.body.split("\"correctAnswers\":")[1].length() - 1);
            } catch (Exception e) {
                lastQuizTrueAnswers = "unable to split the body";
                System.out.println(lastQuizTrueAnswers + "Create quiz response body: " + response.body);
            }
            System.out.println("Last Quiz answers: " + lastQuizTrueAnswers);
            try {
                lastQuizId = response.body.split("\"")[3];
            } catch (Exception e) {
                lastQuizId = null;
            }
            System.out.println("last created quiz:" + lastQuizId);
            assertEquals(200, response.code);
        }

        @Test
        @Order(2)
        public void testGetAllQuizzes() throws IOException {
            if (instructorToken == null) {
                setupTests.testLoginInstructor();
            }

            if (lastQuizId == null) {
                testCreateQuiz();
            }

            LMSResponse response = getAllQuizzes(instructorToken);

            System.out.println("Get quizzes response code: " + response.code);
//        System.out.println("Get quizzes response body: " + response.body);

            assertEquals(200, response.code);
            assertNotNull(response.body);
            assertTrue(response.body.contains("["));
        }

        @Test
        @Order(2)
        public void testGetQuizById() throws IOException {
            if (instructorToken == null) {
                setupTests.testLoginInstructor();
            }
            if (lastQuizId == null) {
                testCreateQuiz();
            }

            LMSResponse response = getQuizById(instructorToken, lastQuizId);
            System.out.println("Get quiz by id response code: " + response.code);
            System.out.println("Get quiz by id response body: " + response.body);
            assertEquals(200, response.code);
        }

        @Test
        @Order(3)
        public void testDeleteQuiz() throws IOException {
            if (lastQuizId == null) {
                testCreateQuiz();
            }

            LMSResponse response = deleteQuiz(instructorToken, lastQuizId);
            System.out.println("Delete quiz response code: " + response.code);

            assertEquals(200, response.code);
            assertTrue(response.body.contains("deleted"));
        }

        @Test
        @Order(4)
        public void testAssignQuiz() throws IOException {
            if (lastQuizId == null) {
                testCreateQuiz();
            }

            LMSResponse deleteResponse = deleteQuiz(instructorToken, lastQuizId);
            assertTrue(deleteResponse.body.contains("deleted"));
            LMSResponse assignResponse = assignQuiz(instructorToken, lastQuizId);
            assertTrue(assignResponse.body.contains("opened"));
            System.out.println("Assign quiz response code: " + assignResponse.code);

            assertEquals(200, assignResponse.code);
        }
    }

    @Nested
    @Order(5)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class QuizSubmissionTests {

        @Test
        @Order(1)
        public void testSubmitQuiz() throws IOException {
            if (lastQuizId == null) {
                quizTests.testCreateQuiz();
            }

            if (studentToken == null) {
                setupTests.testLoginStudent();
            }

            LMSResponse response = submitQuiz(studentToken, lastQuizId, lastQuizTrueAnswers);
            System.out.println("Submit quiz response code: " + response.code);
//        System.out.println("Submit quiz response body: " + response.body);
            quizSubmissionExist = true;
            assertEquals(200, response.code);
            assertTrue(response.body.contains(lastQuizId));
        }

        @Test
        @Order(2)
        public void testGetAllSubmissions() throws IOException {
            if (instructorToken == null) {
                setupTests.testLoginInstructor();
            }

            LMSResponse response = getAllQuizzesSubmissions(instructorToken);

            assertEquals(200, response.code);
            assertNotNull(response.body);
            assertTrue(response.body.contains("["));
        }

        @Test
        @Order(2)
        public void testGetSubmissionsByQuiz() throws IOException {
            if (!quizSubmissionExist) {
                testSubmitQuiz();
            }

            LMSResponse response = getSubmissionsByQuiz(instructorToken, lastQuizId);
            System.out.println("Get submissions by quiz response code: " + response.code);
            System.out.println("Get submissions by quiz response body: " + response.body);
            assertEquals(200, response.code);
            assertTrue(response.body.contains(lastQuizId));
        }
    }

    @Nested
    @Order(6)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ProgressTests {

        @Test
        public void testGetAllStudentProgress() throws IOException {
            if (instructorToken == null) {
                setupTests.testLoginInstructor();
            }

            LMSResponse response = getAllStudentProgress(instructorToken);
            System.out.println("Get all student progress response code: " + response.code);
            System.out.println("Get all student progress response body: " + response.body);

            assertEquals(200, response.code);
            assertNotNull(response.body);
            assertTrue(response.body.contains("["));
        }

        @Test
        public void testGetStudentProgressByStudentId() throws IOException {
            if (instructorToken == null) {
                setupTests.testLoginInstructor();
            }

            if (studentToken == null) {
                setupTests.testLoginStudent();
            }

//        Claims claims = Jwts.parser().setSigningKey("3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b").parseClaimsJws(studentToken).getBody();
//
//        String userEmail = claims.get("sub", String.class);
//        Optional<User> user = userService.findByEmail(userEmail);
//        System.out.println(user);


            LMSResponse response = getStudentProgressByStudentId(instructorToken, studentId);
            System.out.println("Get student progress response code: " + response.code);
            System.out.println("Get student progress response body: " + response.body);

            assertEquals(200, response.code);
            assertNotNull(response.body);
            assertTrue(response.body.contains(studentId));
        }

        @Test
        public void testGetStudentProgressByCourseId() throws IOException {
            if (instructorToken == null) {
                setupTests.testLoginInstructor();
            }
            if (lastCourseIdCreated == null) {
                courseTests.testCreateCourse();
            }
            if (studentToken == null) {
                setupTests.testLoginStudent();
            }

            //enroll
            String url = "http://localhost:8080/enrollments/enroll?studentId=" + studentId + "&courseId=" + lastCourseIdCreated;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(new byte[0]))
                    .header("Authorization", "Bearer " + studentToken)
                    .build();
            client.newCall(request).execute();
            ////////////////////////////////////////////////////////


            LMSResponse response = getStudentProgressByCourseId(instructorToken, studentId, lastCourseIdCreated);
            System.out.println("Get student progress by course response code: " + response.code);
            System.out.println("Get student progress by course response body: " + response.body);

            assertEquals(200, response.code);
            assertNotNull(response.body);
            assertTrue(response.body.contains(studentId));
            assertTrue(response.body.contains(lastCourseIdCreated));
        }

        @Test
        public void testGetCourseProgress() throws IOException {
            if (instructorToken == null) {
                setupTests.testLoginInstructor();
            }
            if (lastCourseIdCreated == null) {
                courseTests.testCreateCourse();
            }

            LMSResponse response = getCourseProgress(instructorToken, lastCourseIdCreated);
            System.out.println("Get course progress response code: " + response.code);
            System.out.println("Get course progress response body: " + response.body);

            assertEquals(200, response.code);
            assertNotNull(response.body);
            assertTrue(response.body.contains(lastCourseIdCreated));
        }
    }
}