package app.com.work.shimonaj.helpdx.remote;

import java.net.MalformedURLException;
import java.net.URL;

public class Config {
    public static final URL TICKET_URL;
    public static final URL COMMON_URL;
    public static final String  BASE="http://xavient.co.in/HelpDeskService";
    public static final String  TICKET_STATUS="TicketStatus";
    public static final String TicketId = "Ticket_ID";
    public static final String  USER_KEY="UserInfo";
    public static final String  HELPDX_NAME="HelpDeskName";
    public static final String  COMPANY_NAME="CompanyName";
    public static  boolean  mTwoPane=false;
    static {
        URL ticketUrl = null;
        URL commonUrl = null;
        try {
            ticketUrl = new URL(BASE+"/HelpDx/Tickets.svc/" );
            commonUrl = new URL(BASE+"/HelpDx/Common.svc/" );
        } catch (MalformedURLException ignored) {
            // TODO: throw a real error
        }

        TICKET_URL = ticketUrl;
        COMMON_URL = commonUrl;
    }
}
