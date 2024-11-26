package no.fintlabs.gyldighetsperiode;

import no.fintlabs.gyldighetsPeriode.GyldighetsperiodeService;
import no.fint.model.felles.kompleksedatatyper.Periode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GyldighetsperiodeServiceTest {
    private GyldighetsperiodeService gyldighetsperiodeService;
    private Date currentTime;

    private static final int daysBeforeStart= 10;

    @BeforeEach
    public void initEachTest(){
        gyldighetsperiodeService = new GyldighetsperiodeService();
        currentTime = Date.from(LocalDate.of(2020,10,31)
                .atStartOfDay(ZoneId.of("Z")).toInstant());
    }


    @Test
    public void shouldReturnTrueGyldighetsperiodeNotNullCurrentTimeBetweenStartAndSluttDate(){
        Date startDate = Date.from((LocalDate.of(2020,8,1)
                .atStartOfDay(ZoneId.of("Z")).toInstant()));
        Date sluttDate = Date.from(LocalDate.of(2020,12,31)
                .atStartOfDay(ZoneId.of("Z")).toInstant());
        Periode gyldighetsPeriode = new Periode();
        gyldighetsPeriode.setSlutt(sluttDate);
        gyldighetsPeriode.setStart(startDate);

        boolean valid = gyldighetsperiodeService.isValid(gyldighetsPeriode,currentTime);

        assertTrue(valid);

    }

    @Test
    public void shouldThrowNullPeriodeExeptionGyldihetsperiodeIsNull(){

        assertThrows(GyldighetsperiodeService.NullPeriodeExecption.class, () -> gyldighetsperiodeService.isValid(null
                ,currentTime));

    }


    @Test
    public void shouldReturnFalseGyldighetsperiodeNotNullCurrentTimeAfterEndDate(){
        Date startDate = Date.from((LocalDate.of(2020,8,1)
                .atStartOfDay(ZoneId.of("Z")).toInstant()));
        Date sluttDate = Date.from(LocalDate.of(2020,9,28)
                .atStartOfDay(ZoneId.of("Z")).toInstant());
        Periode gyldighetsPeriode = new Periode();
        gyldighetsPeriode.setSlutt(sluttDate);
        gyldighetsPeriode.setStart(startDate);

        boolean valid = gyldighetsperiodeService.isValid(gyldighetsPeriode,currentTime);

        assertFalse(valid);
    }


    @Test
    public void shouldReturTrueGyldighetsperiodeNotNullEndDateIsNull(){
        Date startDate = Date.from((LocalDate.of(2020,8,1)
                .atStartOfDay(ZoneId.of("Z")).toInstant()));
        Date sluttDate = null;
        Periode gyldighetsPeriode = new Periode();
        gyldighetsPeriode.setSlutt(sluttDate);
        gyldighetsPeriode.setStart(startDate);

        boolean valid = gyldighetsperiodeService.isValid(gyldighetsPeriode,currentTime);

        assertTrue(valid);
    }

}
