package jp.co.metateam.library.model;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DateCalendarDto {

    private String stockId;

    private Long availableBookNum;

    private Date expectedRentalOn;

}