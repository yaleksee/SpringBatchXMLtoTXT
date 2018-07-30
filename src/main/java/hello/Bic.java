package hello;

import javax.xml.bind.annotation.XmlRootElement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@XmlRootElement

public class Bic {

    private String companyBic;
    private Date rstrList;
    private Date accRstrList;
    private Date date;
    private DateFormat format = new SimpleDateFormat("yyyy MM dd");

    public Bic() {
    }

    public Bic(String companyBic) {
        this.companyBic = companyBic;
    }

    public void setCompanyBic(String CompanyBic) {
        this.companyBic = companyBic;
    }

    public String getCompanyBic() {
        return companyBic;
    }


    public Date getRstrList() {
        return rstrList;
    }

    public void setRstrList(String rstrList) {
        try {
            date = format.parse(rstrList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.rstrList = date;
    }

    public Date getAccRstrList() {
        return accRstrList;
    }

    public void setAccRstrList(String accRstrList) {
        try {
            date = format.parse(accRstrList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.rstrList = date;
    }
}
