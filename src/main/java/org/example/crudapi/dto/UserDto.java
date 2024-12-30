package org.example.crudapi.dto;

import java.time.OffsetDateTime;

public record UserDto(Long id,
                      String uuid,
                      OffsetDateTime createdOn,
                      OffsetDateTime updatedOn,
                      String username) {
}
