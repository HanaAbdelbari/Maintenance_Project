package com.lms.persistence.repositories;

import com.lms.persistence.entities.Quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
class QuizRepository {

  private final List<Quiz> quizzes = new ArrayList<>();

  public Optional<Quiz> findById(String quizId) {
    return quizzes.stream().filter(q -> q.getId().equals(quizId)).findFirst();
  }

  public void save(Quiz quiz) {
    quizzes.add(quiz);
  }

  public List<Quiz> findAll() {
    return quizzes;
  }

  public void update(Quiz quiz) {
    quizzes.replaceAll(q -> q.getId().equals(quiz.getId()) ? quiz : q);
  }

}
