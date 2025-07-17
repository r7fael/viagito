package com.project.viagito.viagito.config;

import com.project.viagito.viagito.model.Local;
import com.project.viagito.viagito.model.Review;
import com.project.viagito.viagito.model.User;
import com.project.viagito.viagito.repository.LocalRepository;
import com.project.viagito.viagito.repository.ReviewRepository;
import com.project.viagito.viagito.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class MockData implements CommandLineRunner {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final LocalRepository localRepository;

    public MockData(ReviewRepository reviewRepository, UserRepository userRepository, LocalRepository localRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.localRepository = localRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (reviewRepository.count() > 0) {
            return;
        }

        List<User> users = userRepository.findByEmailContaining("mock");
        List<Local> locales = localRepository.findAll();

        if (users.isEmpty() ||  locales.isEmpty()) {
            return;
        }

        Random random = new Random();
        List<String> mockComments = Arrays.asList("Lugar incrível, com certeza voltarei!",
                "A comida estava deliciosa e o atendimento foi rápido.",
                "Não gostei muito, o ambiente era barulhento.",
                "Perfeito para um passeio em família.",
                "Um pouco caro, mas a experiência vale a pena.",
                "Recomendo! A vista é espetacular.",
                "Atendimento nota 10, muito atenciosos.",
                "Ótimo custo-benefício. Recomendo a todos.",
                "Ambiente agradável e aconchegante.");

        for (Local local : locales) {
            int numberOfReviews = random.nextInt(4) + 2;

            for (int i = 0; i < numberOfReviews; i++) {
                Review review = new Review();
                User randomUser = users.get(random.nextInt(users.size()));
                review.setUser(randomUser);
                review.setLocal(local);
                review.setComment(mockComments.get(random.nextInt(mockComments.size())));
                double randomRating = 3.0 +  random.nextDouble() * 2;
                review.setRating(Math.round(randomRating * 10.0) / 10.0);
                reviewRepository.save(review);
            }

            List<Review> reviewsOfCurrentLocal = reviewRepository.findByLocalId(local.getId());
            double average = reviewsOfCurrentLocal.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);

            local.setAverageRating(Math.round(average * 10.0) / 10.0);
            localRepository.save(local);
        }
        System.out.println("Criação de reviews e cálculo de médias concluídos!");
    }
}