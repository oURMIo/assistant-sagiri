package com.dach.sagiri.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.dach.sagiri.domain.dto.nager.HolidayNagerDto;
import com.dach.sagiri.domain.dto.nager.NagerCountry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HolidayServiceTest {

    private WebService webService;
    private HolidayService holidayService;

    @BeforeEach
    void setUp() {
        webService = mock(WebService.class);
        holidayService = new HolidayService(webService);
    }

    @Test
    void getTodayHolidays_returns_holiday_when_present() {
        LocalDate todayLocal = LocalDate.now();
        String expectedUrl = "https://date.nager.at/api/v3/PublicHolidays/" + todayLocal.getYear() + "/RU";

        HolidayNagerDto dto = mock(HolidayNagerDto.class);
        when(dto.date()).thenReturn(todayLocal);

        when(webService.doRequestForList(eq(expectedUrl), eq(HolidayNagerDto.class)))
            .thenReturn(List.of(dto));

        Optional<HolidayNagerDto> result = holidayService.getTodayHolidays(NagerCountry.RU);

        assertTrue(result.isPresent(), "Expected holiday for today to be present");
        assertEquals(dto, result.get(), "Returned DTO should be the one from WebService");
        verify(webService).doRequestForList(eq(expectedUrl), eq(HolidayNagerDto.class));
    }
}
