package com.dach.sagiri.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.dach.sagiri.domain.dto.nager.HolidayNagerDto;
import com.dach.sagiri.domain.dto.nager.NagerCountry;

@Service
public class HolidayService {

    private static final Logger logger = LoggerFactory.getLogger(HolidayService.class);
    private static final String BASE_URL = "https://date.nager.at/api/v3/PublicHolidays";


    private final WebService webService;

    public HolidayService(WebService webService) {
        this.webService = webService;
    }

    public Optional<HolidayNagerDto> getTodayHolidays(NagerCountry country) {
        LocalDate today = LocalDate.now();
        List<HolidayNagerDto> holidayNagerDtos = getHolidays(country);

        return holidayNagerDtos.stream()
            .filter(dto -> today.equals(dto.date()))
            .findFirst();
    }

    public List<HolidayNagerDto> getHolidays(NagerCountry country) {
        int todayYear = LocalDate.now().getYear();
        String url = BASE_URL + "/" + todayYear + "/" + country.name();
        return webService.doRequestForList(url, HolidayNagerDto.class);
    }
}


