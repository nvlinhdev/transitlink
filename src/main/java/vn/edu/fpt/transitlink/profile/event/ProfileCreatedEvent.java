package vn.edu.fpt.transitlink.profile.event;

import org.springframework.stereotype.Component;

import java.util.UUID;

public record ProfileCreatedEvent(UUID accountId) {}

