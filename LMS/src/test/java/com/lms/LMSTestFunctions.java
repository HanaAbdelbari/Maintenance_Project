package com.lms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.business.models.QuestionRequest;

import java.io.IOException;
import java.util.List;

import com.lms.business.models.QuizRequest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LMSTestFunctions {
    static OkHttpClient client = new OkHttpClient();

    public static LMSResponse signup(
            String id,
            String firstName,
            String lastName,
            String email,
            String password,
            String role
    ) throws IOException {
        String url = "http://localhost:8080/auth/signup";

        String jsonData =
                "{\"id\":\"" +
                        id +
                        "\",\"firstName\":\"" +
                        firstName +
                        "\",\"lastName\":\"" +
                        lastName +
                        "\",\"email\":\"" +
                        email +
                        "\",\"password\":\"" +
                        password +
                        "\",\"role\":\"" +
                        role +
                        "\"}";

        RequestBody body = RequestBody.create(
                jsonData,
                okhttp3.MediaType.get("application/json; charset=utf-8")
        );
        Request request = new Request.Builder().url(url).post(body).build();

        Response response = client.newCall(request).execute();
        return new LMSResponse(response.code(), response.body().string());
    }

    public static LMSResponse login(String email, String password)
            throws IOException {
        String url = "http://localhost:8080/auth/login";

        String jsonData =
                "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

        RequestBody body = RequestBody.create(
                jsonData,
                okhttp3.MediaType.get("application/json; charset=utf-8")
        );
        Request request = new Request.Builder().url(url).post(body).build();

        Response response = client.newCall(request).execute();
        return new LMSResponse(response.code(), response.body().string());
    }

    public static String getToken(String body)
            throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(body);
        return rootNode.get("token").asText();
    }

    public static LMSResponse createUser(
            String adminToken,
            String id,
            String firstName,
            String lastName,
            String email,
            String password,
            String role
    ) throws IOException {
        String url = "http://localhost:8080/admin/createUser";

        String jsonData =
                "{\"id\":\"" +
                        id +
                        "\",\"firstName\":\"" +
                        firstName +
                        "\",\"lastName\":\"" +
                        lastName +
                        "\",\"email\":\"" +
                        email +
                        "\",\"password\":\"" +
                        password +
                        "\",\"role\":\"" +
                        role +
                        "\"}";

        RequestBody body = RequestBody.create(
                jsonData,
                okhttp3.MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer " + adminToken)
                .build();

        Response response = client.newCall(request).execute();
        return new LMSResponse(response.code(), response.body().string());
    }

    public static LMSResponse createCourse(
            String instructorToken,
            String id,
            String title,
            String description,
            int duration,
            String Profid
    ) throws IOException {
        String url = "http://localhost:8080/courses";

        String jsonData =
                "{\"id\":\"" +
                        id +
                        "\",\"title\":\"" +
                        title +
                        "\",\"description\":\"" +
                        description +
                        "\",\"duration\":" +
                        duration +
                        ",\"profId\":\"" +
                        Profid +
                        "\"}";

        RequestBody body = RequestBody.create(
                jsonData,
                okhttp3.MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer " + instructorToken)
                .build();

        Response response = client.newCall(request).execute();
        return new LMSResponse(response.code(), response.body().string());
    }

    public static LMSResponse createLesson(
            String instructorToken,
            String id,
            String title,
            String content,
            String courseId
    ) throws IOException {
        String url = "http://localhost:8080/courses/" + courseId + "/lessons";

        // Create the JSON data for the lesson
        String jsonData =
                "{\"id\":\"" +
                        id +
                        "\",\"title\":\"" +
                        title +
                        "\",\"content\":\"" +
                        content +
                        "\"}";

        // Set up the request body with the JSON data
        RequestBody body = RequestBody.create(
                jsonData,
                okhttp3.MediaType.get("application/json; charset=utf-8")
        );

        // Build the request with the appropriate headers
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer " + instructorToken)
                .build();

        // Execute the request and return the response
        Response response = client.newCall(request).execute();
        return new LMSResponse(response.code(), response.body().string());
    }

    public static LMSResponse addQuestionToQuestionBank(
            String instructorToken,
            String courseId,
            QuestionRequest questionRequest
    ) throws IOException {
        String url = "http://localhost:8080/questionBank/" + courseId + "/add1";

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(questionRequest);
        } catch (Exception e) {
            return new LMSResponse(500, "Error creating JSON");
        }
//    System.out.println(jsonString);

        // Set up the request body with the JSON data
        RequestBody body = RequestBody.create(
                jsonString,
                okhttp3.MediaType.get("application/json; charset=utf-8")
        );

        // Build the request with the appropriate headers
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer " + instructorToken)
                .build();

        // Execute the request and return the response
        Response response = client.newCall(request).execute();

        return new LMSResponse(response.code(), response.body().string());
    }

    public static LMSResponse addMultipleQuestionsToQuestionBank(
            String instructorToken,
            String courseId,
            List<QuestionRequest> questionRequests
    ) throws IOException {
        String url = "http://localhost:8080/questionBank/" + courseId + "/add";

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(questionRequests);
        } catch (Exception e) {
            return new LMSResponse(500, "Error creating JSON");
        }
//    System.out.println(jsonString);

        // Set up the request body with the JSON data
        RequestBody body = RequestBody.create(
                jsonString,
                okhttp3.MediaType.get("application/json; charset=utf-8")
        );

        // Build the request with the appropriate headers
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer " + instructorToken)
                .build();

        // Execute the request and return the response
        Response response = client.newCall(request).execute();

        return new LMSResponse(response.code(), response.body().string());
    }

    public static LMSResponse getQuestionsFromQuestionBank(String instructorToken, String courseId) {
        String url = "http://localhost:8080/questionBank/" + courseId + "/questions";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .header("Authorization", "Bearer " + instructorToken)
                .build();

        Response response;


        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            return new LMSResponse(500, "Error executing request");
        }

        try {
            return new LMSResponse(response.code(), response.body().string());
        } catch (IOException e) {
            return new LMSResponse(500, "Error reading response body");
        }
    }

    public static LMSResponse deleteQuestionFromQuestionBank(String instructorToken, String courseId, String questionId) {
        String url = "http://localhost:8080/questionBank/" + courseId + "/questions/" + questionId;

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .header("Authorization", "Bearer " + instructorToken)
                .build();


        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            return new LMSResponse(500, "Error executing request");
        }

        try {
            return new LMSResponse(response.code(), response.body().string());
        } catch (IOException e) {
            return new LMSResponse(500, "Error reading response body");
        }
    }


    public static LMSResponse createQuiz(String instructorToken, String courseId, String title,
                                         int questionNumber,
                                         int duration,
                                         String status) throws IOException {
        String url = "http://localhost:8080/quizzes";

        QuizRequest quizRequest = new QuizRequest(courseId, title, questionNumber, duration, status);
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(quizRequest);
        } catch (Exception e) {
            return new LMSResponse(500, "Error creating JSON");
        }

        // Set up the request body with the JSON data
        RequestBody body = RequestBody.create(
                jsonString,
                okhttp3.MediaType.get("application/json; charset=utf-8")
        );


        // Build the request with the appropriate headers
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer " + instructorToken)
                .build();


        // Execute the request and return the response
        Response response = client.newCall(request).execute();

        return new LMSResponse(response.code(), response.body().string());
    }

    public static LMSResponse getAllQuizzes(String instructorToken) {
        String url = "http://localhost:8080/quizzes";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .header("Authorization", "Bearer " + instructorToken)
                .build();

        Response response;


        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            return new LMSResponse(500, "Error executing request");
        }

        try {
            return new LMSResponse(response.code(), response.body().string());
        } catch (IOException e) {
            return new LMSResponse(500, "Error reading response body");
        }
    }

    public static LMSResponse getQuizById(String instructorToken, String quizId) {
        String url = "http://localhost:8080/quizzes/" + quizId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .header("Authorization", "Bearer " + instructorToken)
                .build();

        Response response;


        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            return new LMSResponse(500, "Error executing request");
        }

        try {
            return new LMSResponse(response.code(), response.body().string());
        } catch (IOException e) {
            return new LMSResponse(500, "Error reading response body");
        }

    }


    public static LMSResponse deleteQuiz(String instructorToken, String quizId) {
        String url = "http://localhost:8080/quizzes/" + quizId;

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .header("Authorization", "Bearer " + instructorToken)
                .build();

        Response response;


        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            return new LMSResponse(500, "Error executing request");
        }

        try {
            return new LMSResponse(response.code(), response.body().string());
        } catch (IOException e) {
            return new LMSResponse(500, "Error reading response body");
        }
    }

    public static LMSResponse assignQuiz(String instructorToken, String quizId) {
        String url = "http://localhost:8080/quizzes/" + quizId + "/assign";


        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(new byte[0]))
                .header("Authorization", "Bearer " + instructorToken)
                .build();

        Response response;


        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            return new LMSResponse(500, "Error executing request");
        }

        try {
            return new LMSResponse(response.code(), response.body().string());
        } catch (IOException e) {
            return new LMSResponse(500, "Error reading response body");
        }

    }

    public static LMSResponse submitQuiz(String studentToken, String quizId, String lastQuizTrueAnswers) {
        String url = "http://localhost:8080/quizzes/" + quizId + "/submit";

        // Set up the request body with the JSON data
        RequestBody body = RequestBody.create(
                lastQuizTrueAnswers,
                okhttp3.MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer " + studentToken)
                .build();

        Response response;


        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            return new LMSResponse(500, "Error executing request");
        }

        try {
            return new LMSResponse(response.code(), response.body().string());
        } catch (IOException e) {
            return new LMSResponse(500, "Error reading response body");
        }


    }

    public static LMSResponse getAllQuizzesSubmissions(String instructorToken) {
        String url = "http://localhost:8080/quizzes/submissions";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .header("Authorization", "Bearer " + instructorToken)
                .build();

        Response response;


        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            return new LMSResponse(500, "Error executing request");
        }

        try {
            return new LMSResponse(response.code(), response.body().string());
        } catch (IOException e) {
            return new LMSResponse(500, "Error reading response body");
        }
    }

    public static LMSResponse getSubmissionsByQuiz(String instructorToken, String quizId) {
        String url = "http://localhost:8080/quizzes/submissions/" + quizId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .header("Authorization", "Bearer " + instructorToken)
                .build();

        Response response;


        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            return new LMSResponse(500, "Error executing request");
        }

        try {
            return new LMSResponse(response.code(), response.body().string());
        } catch (IOException e) {
            return new LMSResponse(500, "Error reading response body");
        }

    }


    public static LMSResponse getAllStudentProgress(String instructorToken) {
        String url = "http://localhost:8080/progress/students";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .header("Authorization", "Bearer " + instructorToken)
                .build();

        Response response;

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            return new LMSResponse(500, "Error executing request");
        }

        try {
            return new LMSResponse(response.code(), response.body().string());
        } catch (IOException e) {
            return new LMSResponse(500, "Error reading response body");
        }
    }

    public static LMSResponse getStudentProgressByStudentId(String instructorToken, String studentId) {
        String url = "http://localhost:8080/progress/students/" + studentId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .header("Authorization", "Bearer " + instructorToken)
                .build();

        Response response;

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            return new LMSResponse(500, "Error executing request");
        }

        try {
            return new LMSResponse(response.code(), response.body().string());
        } catch (IOException e) {
            return new LMSResponse(500, "Error reading response body");
        }
    }


    public static LMSResponse getStudentProgressByCourseId(String instructorToken, String studentId, String courseId) {
        String url = "http://localhost:8080/progress/students/" + studentId + "/" + courseId;


        Request request = new Request.Builder()
                .url(url)
                .get()
                .header("Authorization", "Bearer " + instructorToken)
                .build();

        Response response;

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            return new LMSResponse(500, "Error executing request");
        }

        try {
            return new LMSResponse(response.code(), response.body().string());
        } catch (IOException e) {
            return new LMSResponse(500, "Error reading response body");
        }
    }


    public static LMSResponse getCourseProgress(String instructorToken, String courseId) {
        String url = "http://localhost:8080/progress/courses/" + courseId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .header("Authorization", "Bearer " + instructorToken)
                .build();

        Response response;

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            return new LMSResponse(500, "Error executing request");
        }

        try {
            return new LMSResponse(response.code(), response.body().string());
        } catch (IOException e) {
            return new LMSResponse(500, "Error reading response body");
        }
    }
}
