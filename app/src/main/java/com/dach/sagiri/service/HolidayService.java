package com.dach.sagiri.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.dach.sagiri.domain.dto.nager.HolidayNagerDto;
import com.dach.sagiri.domain.dto.nager.NagerCountry;

@Service
public class HolidayService {

    private static final Logger logger = LoggerFactory.getLogger(HolidayService.class);
    private static final String NAGER_API_PUBLIC_HOLIDAYS_URL = "https://date.nager.at/api/v3/PublicHolidays";
    private static final String KAKOYSEGODNYAPRAZDNIK_URL = "https://kakoysegodnyaprazdnik.ru/";

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
        String url = NAGER_API_PUBLIC_HOLIDAYS_URL + "/" + todayYear + "/" + country.name();
        return webService.doRequestForList(url, HolidayNagerDto.class);
    }

    public List<String> getTodayHolidaysFromKakoySegodnyaPrazdnik() {
        Optional<String> htmlOpt = webService.doSafeRequest(KAKOYSEGODNYAPRAZDNIK_URL);
        if (htmlOpt.isEmpty() || htmlOpt.get().isBlank()) {
            return List.of();
        }

        String html = htmlOpt.get();
        try {
            Document doc = Jsoup.parse(html, KAKOYSEGODNYAPRAZDNIK_URL);
            Elements spans = doc.select("span[itemprop=text]");

            return spans.stream()
                .map(element -> element.text().trim())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to parse holidays from {}", KAKOYSEGODNYAPRAZDNIK_URL, e);
            return List.of();
        }
    }
}
