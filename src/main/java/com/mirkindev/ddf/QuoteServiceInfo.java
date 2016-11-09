package com.mirkindev.ddf;

import com.ddfplus.api.ConnectionEventType;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by dimam on 04/11/2016.
 */
public class QuoteServiceInfo {

    private ConnectionEventType connectionStatus = ConnectionEventType.DISCONNECTED;
    private HashMap<String,BarchartQuoteEntity> lastQuotes = new  HashMap<String,BarchartQuoteEntity>();
    private boolean isStarted = false;


    public  QuoteServiceInfo(BarchartQuoteService service){
        connectionStatus = service.getConnectionStatus();
        lastQuotes = service.getLastQuotes();
        isStarted = service.isStarted();
    }

    public ConnectionEventType getConnectionStatus() {
        return connectionStatus;
    }


    public Collection<BarchartQuoteEntity> getLastQuotes() {
        return lastQuotes.values();
    }

    public boolean isStarted() {
        return isStarted;
    }
}
