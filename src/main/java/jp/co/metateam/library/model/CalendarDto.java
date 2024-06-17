package jp.co.metateam.library.model;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * calendarDTO
 */
@Getter
@Setter
public class CalendarDto {

    private String title;

    private Long stockCount;

    private List<DateCalendarDto> CalendarDtoListPerDay;

}