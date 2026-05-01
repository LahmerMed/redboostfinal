/*package team.project.redboost.services;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import jdk.jfr.Event;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
public class CalendarService {

    private static final String APPLICATION_NAME = "Redboost";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private final NetHttpTransport httpTransport;

    public CalendarService() throws GeneralSecurityException, IOException {
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    }

    /**
     * Get Google Calendar API credentials
     */
    /*private Credential getCredentials() throws IOException {
        InputStream in = new ClassPathResource("credentials.json").getInputStream();
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, Collections.singletonList("https://www.googleapis.com/auth/calendar.events"))
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Create a Google Calendar event and send invitations
     */
    /*public String createEvent(String summary, String description, String location, LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> attendees) throws IOException {
        com.google.api.services.calendar.Calendar calendarService = new com.google.api.services.calendar.Calendar.Builder(
                httpTransport, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Convert LocalDateTime to Google Calendar DateTime
        DateTime start = new DateTime(startDateTime.toString() + "Z");
        DateTime end = new DateTime(endDateTime.toString() + "Z");

        // Create the event
        Event event = new Event()
                .setSummary(summary)
                .setDescription(description)
                .setLocation(location)
                .setStart(new EventDateTime().setDateTime(start))
                .setEnd(new EventDateTime().setDateTime(end));

        // Add attendees
        if (attendees != null && !attendees.isEmpty()) {
            List<EventAttendee> eventAttendees = attendees.stream()
                    .map(email -> new EventAttendee().setEmail(email))
                    .collect(Collectors.toList());
            event.setAttendees(eventAttendees);
        }

        // Insert the event and send invitations
        event = calendarService.events().insert("primary", event)
                .setSendUpdates("all") // Send invitations to all attendees
                .execute();

        return event.getId(); // Return the event ID for reference
    }
}*/