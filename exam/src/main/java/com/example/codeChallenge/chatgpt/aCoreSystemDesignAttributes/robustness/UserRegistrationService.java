package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.robustness;

import codeChallenge.chatgpt.eCommon.ExternalEmailValidationClient;
import codeChallenge.chatgpt.eCommon.User;
import codeChallenge.chatgpt.eCommon.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserRegistrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRegistrationService.class);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();
    private final String pattern = "^\\w*?[a-zA-Z]\\w+@[a-z\\d\\-]+(\\.[a-z\\d\\-]+)*\\.[a-z]+\\z";
    private ExternalEmailValidationClient client;
    private UserRepository userRepository;
    private final Semaphore limit = new Semaphore(100);

    public void register(User user) {
        if (!checkFormat(user.getEmail())) {
            throw new RuntimeException("Email has invalid format;");
        }


        EXECUTOR_SERVICE.submit(() -> {
            try {
                limit.acquireUninterruptibly();
                validateWithRetry(user);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } finally {
                limit.release();
            }
        });
    }

    private boolean checkFormat(String email) {
        try {
            Pattern regexPattern = Pattern.compile(pattern);
            Matcher regmatcher = regexPattern.matcher(email);

            return regmatcher.matches();
        } catch (Exception e) {
            throw new RuntimeException("Anexception occured in method preValidation for email : " + email, e);
        }
    }

    private void validateWithRetry(User user) throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            try {
                boolean isValid = client.validateEmail(user.getEmail());

                if (!isValid) {
                    throw new RuntimeException("Invalid email");
                }

                userRepository.save(user);
                break;
            } catch (Exception e) {
                LOGGER.error("Error in method validateWithRetry for email {}. message: {}",
                        user.getEmail(), e.getMessage());
                long time = (long) (Math.pow(2, i) * 1000L);
                Thread.sleep(time);
            }
        }
    }
}
