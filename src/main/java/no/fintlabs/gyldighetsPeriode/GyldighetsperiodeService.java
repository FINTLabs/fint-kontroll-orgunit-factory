package no.fintlabs.gyldighetsPeriode;

import no.fint.model.felles.kompleksedatatyper.Periode;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GyldighetsperiodeService {
    public boolean isValid(Periode gyldighetsPeriode, Date currentTime){
        if (gyldighetsPeriode == null){
            throw new NullPeriodeExecption();
        }

        return currentTime.after(gyldighetsPeriode.getStart())
                && isEndValid(gyldighetsPeriode.getSlutt(),currentTime);

    }

    private boolean isEndValid(Date endDate, Date currentTime){
        return endDate == null || currentTime.before(endDate);
    }


    public static class NullPeriodeExecption extends RuntimeException{}


}
