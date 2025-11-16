package com.dach.sagiri.domain.dto.nager;

import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public record HolidayNagerDto(
    @JsonProperty("date")
    LocalDate date,

    @JsonProperty("localName")
    String localName,

    @JsonProperty("name")
    String name,

    @JsonProperty("countryCode")
    String countryCode,

    @JsonProperty("fixed")
    Boolean fixed,

    @JsonProperty("global")
    Boolean global,

    @JsonProperty("counties")
    List<String> counties,

    @JsonProperty("launchYear")
    Integer launchYear,

    @JsonProperty("types")
    List<String> types
) {
}
