package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BicItemProcessor implements ItemProcessor<Bic, Bic> {
    private static final Date currentDate = new Date();
    private static final Logger log = LoggerFactory.getLogger(BicItemProcessor.class);

    @Override
    public Bic process(final Bic bic) throws Exception {

        final String currentBic = bic.getCompanyBic();
        final Date rstrList = bic.getRstrList();
        final Date accRstrList = bic.getAccRstrList();

        if (rstrList == null && accRstrList == null) {
            return new Bic();
        }

        if (rstrList.compareTo(currentDate) <= 0 || accRstrList.compareTo(currentDate) <= 0) {
            return new Bic();
        } else {
            final Bic transformedBic = new Bic(currentBic);
            log.info("Converting (" + bic + ") into (" + transformedBic + ")");
            return transformedBic;
        }
    }

}
