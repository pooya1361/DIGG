package com.digg.service;

import com.digg.entity.User;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

// Generates users
@ApplicationScoped
public class DataInitializer {

    private final Random random = new Random();

    private final List<String> firstNames = Arrays.asList(
            "Alice", "Bob", "Carol", "David", "Emma", "Frank", "Grace", "Henry",
            "Ivy", "Jack", "Kate", "Liam", "Mia", "Noah", "Olivia", "Paul",
            "Quinn", "Ruby", "Sam", "Tina", "Uma", "Victor", "Wendy", "Xavier",
            "Yara", "Zoe", "Aaron", "Beth", "Chris", "Diana", "Eric", "Fiona",
            "George", "Hannah", "Ian", "Julia", "Kevin", "Luna", "Mark", "Nina",
            "Oscar", "Penny", "Quincy", "Rose", "Steve", "Tara", "Ulrich", "Vera",
            "Will", "Xara", "Yale", "Zara"
    );

    private final List<String> lastNames = Arrays.asList(
            "Anderson", "Brown", "Clark", "Davis", "Evans", "Fisher", "Garcia", "Harris",
            "Johnson", "King", "Lee", "Martinez", "Nelson", "O'Connor", "Parker", "Quinn",
            "Robinson", "Smith", "Taylor", "Underwood", "Vance", "Wilson", "Young", "Zhang",
            "Adams", "Baker", "Cooper", "Duncan", "Edwards", "Foster", "Green", "Hall",
            "Irving", "Jones", "Kelly", "Lopez", "Moore", "Nash", "Owen", "Phillips",
            "Reed", "Sullivan", "Thompson", "Vaughn", "White", "Cross", "Bell", "Stone"
    );

    private final List<String> streets = Arrays.asList(
            "Main St", "Oak Ave", "Pine Rd", "Cedar Ln", "Elm Dr", "Maple Way",
            "First St", "Second Ave", "Park Blvd", "River Rd", "Hill St", "Valley Dr",
            "Sunset Ave", "Spring St", "Garden Ln", "Forest Dr", "Lake Ave", "Beach Rd"
    );

    private final List<String> cities = Arrays.asList(
            "Springfield", "Riverside", "Franklin", "Georgetown", "Arlington", "Fairview",
            "Clinton", "Madison", "Washington", "Jackson", "Lincoln", "Jefferson",
            "Hamilton", "Monroe", "Adams", "Wilson", "Taylor", "Brown"
    );

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        // Check if users already exist to avoid duplicates on restart
        if (User.count() > 0) {
            System.out.println("Users already exist, skipping data initialization");
            return;
        }

        System.out.println("Generating 50 sample users...");

        for (int i = 1; i <= 100; i++) {
            String firstName = getRandomElement(firstNames);
            String lastName = getRandomElement(lastNames);
            String name = firstName + " " + lastName;

            String address = generateAddress();
            String email = generateEmail(firstName, lastName, i);
            String telephone = generateTelephone();

            User user = new User(name, address, email, telephone);
            user.persist();
        }

        System.out.println("Successfully created 50 sample users!");
    }

    private String generateAddress() {
        int streetNumber = 100 + random.nextInt(9900); // 100-9999
        String street = getRandomElement(streets);
        String city = getRandomElement(cities);
        String state = generateState();
        String zipCode = String.format("%05d", 10000 + random.nextInt(90000));

        return streetNumber + " " + street + ", " + city + ", " + state + " " + zipCode;
    }

    private String generateEmail(String firstName, String lastName, int index) {
        List<String> domains = Arrays.asList("@gmail.com", "@yahoo.com", "@outlook.com", "@example.com");
        String domain = getRandomElement(domains);

        // Mix of email formats to make it realistic
        switch (random.nextInt(3)) {
            case 0: return firstName.toLowerCase() + "." + lastName.toLowerCase() + domain;
            case 1: return firstName.toLowerCase() + lastName.toLowerCase() + index + domain;
            default: return firstName.toLowerCase().charAt(0) + lastName.toLowerCase() + domain;
        }
    }

    private String generateTelephone() {
        // Generate US phone number format: (XXX) XXX-XXXX
        int areaCode = 200 + random.nextInt(700); // 200-899
        int exchange = 200 + random.nextInt(700); // 200-899
        int number = random.nextInt(10000);       // 0000-9999

        return String.format("(%03d) %03d-%04d", areaCode, exchange, number);
    }

    private String generateState() {
        List<String> states = Arrays.asList(
                "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
                "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
                "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
                "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
                "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
        );
        return getRandomElement(states);
    }

    private <T> T getRandomElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}