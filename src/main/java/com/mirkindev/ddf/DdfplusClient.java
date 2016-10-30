package com.mirkindev.ddf;

/**
 * Copyright (C) 2004 - 2015 by Barchart.com, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of Barchart.com, Inc.
 * Use is subject to license terms.
 */


import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddfplus.api.BookQuoteHandler;
import com.ddfplus.api.ClientConfig;
import com.ddfplus.api.ConnectionEvent;
import com.ddfplus.api.ConnectionEventHandler;
import com.ddfplus.api.FeedHandler;
import com.ddfplus.api.MarketEventHandler;
import com.ddfplus.api.QuoteHandler;
import com.ddfplus.api.TimestampHandler;
import com.ddfplus.db.BookQuote;
import com.ddfplus.db.MarketEvent;
import com.ddfplus.db.Quote;
import com.ddfplus.enums.ConnectionType;
import com.ddfplus.messages.DdfMessageBase;
import com.ddfplus.net.DdfClient;
import com.ddfplus.net.DdfClientImpl;
import com.ddfplus.net.SymbolProvider;
import com.ddfplus.net.SymbolProviderImpl;
import com.ddfplus.util.MessageStore;
import com.ddfplus.util.MessageStoreImpl;
import com.ddfplus.util.StoreFeedHandler;

/**
 * This is a DDF client program, using the DDF Plus API.
 *
 * The actual DDF calls are very simple. Essentially, you need to do the
 * following to receive streaming data:
 *
 * <ol>
 * <li>Instantiate DdfClient
 * <li>Register a ClientHandler callback with the DdfClient, this will start the
 * communication with the DDF Server.
 * <li>Process the DDF messages via the Handler.
 * </ol>
 *
 */
public class DdfplusClient implements ConnectionEventHandler, TimestampHandler {

    private static final Logger log = LoggerFactory.getLogger(DdfplusClient.class);

    private static final String CLIENT_PROPS_FILE = "client.properties";

    private final ClientConfig config;
    private SymbolProvider symbolProvider;
    private final DdfClient client;

    private Map<String, List<QuoteHandler>> quoteHandlers = new HashMap<String, List<QuoteHandler>>();

    // Quote Exchange Handlers, one per exchange code
    private Map<String, QuoteHandler> quoteExchangeHandlers = new HashMap<String, QuoteHandler>();

    private Map<String, List<BookQuoteHandler>> depthHandlers = new HashMap<String, List<BookQuoteHandler>>();

    // log modes
    private boolean logTS;
    private boolean logQuote;
    private boolean logMarketEvent;
    private boolean logDdf;
    private boolean logBook;
    private boolean logQuoteExchange;

    private FeedHandlerImpl feedHandler;
    private MarketEventHandlerImpl marketEventHandler;

    private MessageStore messageStore;

    public static DdfplusClient createClient() throws Exception {

        System.out.println("Creating DdfplusClient" );

        ClientConfig config = new ClientConfig();

        String propFile = CLIENT_PROPS_FILE;

		/*
		 * Check for properties in the current directory, if arguments not used.
		 *
		 */
        Properties p = null;
        File f = new File(propFile);
        if (f.exists() && config.getUserName() == null && config.getPassword() == null
                && (config.getSymbols() == null || config.getExchangeCodes() == null)) {
            System.out.println("\nReading DDF Client properties file: " + f);
            p = new Properties();
            p.load(new FileReader(f));
            if (p.getProperty("username") != null && !p.getProperty("username").isEmpty()) {
                config.setUserName(p.getProperty("username"));
            }
            if (p.getProperty("password") != null && !p.getProperty("password").isEmpty()) {
                config.setPassword(p.getProperty("password"));
            }
            if (p.getProperty("symbols") != null && !p.getProperty("symbols").isEmpty()) {
                config.setSymbols(p.getProperty("symbols"));
            }
            if (p.getProperty("exchangeCodes") != null && !p.getProperty("exchangeCodes").isEmpty()) {
                config.setExchangeCodes(p.getProperty("exchangeCodes"));
            }
            if (p.getProperty("connectionType") != null && !p.getProperty("connectionType").isEmpty()) {
                String v = p.getProperty("connectionType").toUpperCase();
                ConnectionType type = ConnectionType.valueOf(v);
                config.setConnectionType(type);
            }
            if (p.getProperty("logMode") != null && !p.getProperty("logMode").isEmpty()) {
                config.setLogMode(p.getProperty("logMode"));
            }
            if (p.getProperty("server.primary") != null && !p.getProperty("server.primary").isEmpty()) {
                config.setPrimaryServer(p.getProperty("server.primary"));
            }
            if (p.getProperty("server.secondary") != null && !p.getProperty("server.secondary").isEmpty()) {
                config.setSecondaryServer(p.getProperty("server.secondary"));
            }
            if (p.getProperty("server.port") != null && !p.getProperty("server.port").isEmpty()) {
                config.setServerPort(Integer.parseInt(p.getProperty("server.port")));
            }
            if (p.getProperty("depthSubscriptions") != null && !p.getProperty("depthSubscriptions").isEmpty()) {
                config.setDepthSubscription(p.getProperty("depthSubscriptions").equals("true") ? true : false);
            }
            if (p.getProperty("snapshotUser") != null && !p.getProperty("snapshotUser").isEmpty()) {
                config.setSnapshotUser(p.getProperty("snapshotUser"));
            }
            if (p.getProperty("snapshotPassword") != null && !p.getProperty("snapshotPassword").isEmpty()) {
                config.setSnapshotPassword(p.getProperty("snapshotPassword"));
            }
            if (p.getProperty("storeMessages") != null && !p.getProperty("storeMessages").isEmpty()) {
                config.setStoreMessages(p.getProperty("storeMessages").equals("true") ? true : false);
            }
            if (p.getProperty("definitionRefreshIntervalSec") != null
                    && !p.getProperty("definitionRefreshIntervalSec").isEmpty()) {
                Long interval = new Long(p.getProperty("definitionRefreshIntervalSec"));
                config.setDefinitionRefreshIntervalSec(interval);
            }
            if (p.getProperty("unknownSymbolIntervalSec") != null
                    && !p.getProperty("unknownSymbolIntervalSec").isEmpty()) {
                Long interval = new Long(p.getProperty("unknownSymbolIntervalSec"));
                config.setUnknownSymbolInterval(interval);
            }
        }

        // Validity Checks
        if (config.getUserName() == null || config.getPassword() == null) {
            System.err.println("user and password are required.");
            printHelp();
            System.exit(0);
        }

        if (config.getSymbols() == null && config.getExchangeCodes() == null) {
            System.err.println("Either -sym or -e must be specified.");
            printHelp();
            System.exit(0);
        }

        if (config.getSymbols() != null && config.getExchangeCodes() != null) {
            System.err.println("-sym and -e cannot both be set.");
            printHelp();
            System.exit(0);
        }

		/*
		 * Log any un-handled exceptions
		 */
        Thread.setDefaultUncaughtExceptionHandler(new ApplicationUncaughtExceptionHandler());

        System.out.println("Starting DDF Client with " + config);

        DdfplusClient client = new DdfplusClient(config);

        ShutdownHook shutdownThread = new ShutdownHook(client);
        Runtime.getRuntime().addShutdownHook(shutdownThread);

        //client.start();
        return client;

    }

    /**
     * Prints out the basic help for this program, and how to use this program.
     */
    public static void printHelp() {

        final StringBuilder text = new StringBuilder(1024);

        text.append("" + "Loads and runs the sample ddfplus client application.\n")
                .append("Usage: java " + DdfplusClient.class.getCanonicalName())
                .append(" -u user -p password -sym symbols|-e exchangeCodes [-t TCP|HTTP|HTTPSTREAM|WSS][-s server] [-d] [-su user] [-sp password] [-l a,ts,d,me,q,qe,b] [-st] [-f <prop file namne>]");
        text.append("\n-u user             - User Name");
        text.append("\n-p password         - Password");
        text.append("\n-sym symbols        - Symbols, comma separated. Required if -e not used.");
        text.append("\n-e exchangeCodes    - Subscribe for quotes for all symbols at the given exchanges");
        text.append("\n-t connection type  - Connection Type, TCP,HTTP,HTTPSTREAM,WSS, defaults to TCP");
        text.append("\n-s server           - DDF Server, otherwise it defaults to the server assigned to the user.");
        text.append("\n-d                  - Activate depth subscriptions");
        text.append("\n-su user            - Snapshot User Name");
        text.append("\n-sp password        - Snapshot Password");
        text.append(
                "\n-l a,ts,d,me,q,qe,b - Logs messages: a=all,ts=timetamp,d=ddf messages,me=market events,q=quotes,qe=all exchange quotes,b=book/depth");
        text.append("\n-sm                 - Stores DDF messages to a binary file in the current directory.");
        text.append("\n-f <prop filename>  - Use property file instead of the default: " + CLIENT_PROPS_FILE);
        System.out.println(text);

    }

    private DdfplusClient(ClientConfig config) throws Exception {

        this.config = config;
        parseLogModes(config.getLogMode());

        symbolProvider = new SymbolProviderImpl();
        if (config.getSymbols() != null) {
            symbolProvider.setSymbols(config.getSymbols());
        } else {
            symbolProvider.setSymbols(config.getExchangeCodes());
        }

		/*
		 * Symbol provider is not required for the TCP or Web Socket transport,
		 * it is required for the following ConnectionType transports:
		 *
		 * UDP, HTTP, HTTPSTREAM
		 *
		 * @see ConnectionType
		 *
		 */
        if (config.getConnectionType() == ConnectionType.TCP || config.getConnectionType() == ConnectionType.WS
                || config.getConnectionType() == ConnectionType.WSS) {
            client = new DdfClientImpl(config);
        } else {
            client = new DdfClientImpl(config, symbolProvider);
        }

		/*
		 * Will activate snapshot refreshes for pull by exchange mode (-e
		 * <exchangeCodes> is given)
		 */
        if (config.getExchangeCodes() != null && config.getSnapshotUser() != null
                && config.getSnapshotPassword() != null) {
            client.setSnapshotLogin(config.getSnapshotUser(), config.getSnapshotPassword());
        }

        // Initialize
        client.init();

        // Admin Handlers
        client.addConnectionEventHandler(this);
        client.addTimestampHandler(this);

        // Add Raw Message Handler
        feedHandler = new FeedHandlerImpl();
        client.addFeedHandler(feedHandler);
        // Add DDF store handler
        if (config.isStoreMessages()) {
            messageStore = new MessageStoreImpl();
            String fn = buildStorageFileName(config);
            messageStore.open(fn, false);
            client.addFeedHandler(new StoreFeedHandler(messageStore));
        }

        // Add Market Event Handler
        marketEventHandler = new MarketEventHandlerImpl();
        client.addMarketEventHandler(marketEventHandler);

    }

    public void start() throws Exception {
        // Will log in to DDF Server and start processing messages.
        client.connect();

    }

    public void shutdown() {
        client.disconnect();
        if (messageStore != null) {
            messageStore.close();
        }
    }

    /**
     * Called by DdfClient whenever a connection type event happens. Useful for
     * monitoring if the connection drops, etc.
     *
     * @param event
     *            Connection Event
     */
    @Override
    public void onEvent(ConnectionEvent event) {
        switch (event.getType()) {
            case CONNECTED:
                log.info(event.toString());
                break;
            case DISCONNECTED:
                log.error(event.toString());
                break;
            case CONNECTION_FAILED:
                log.error(event.toString());
                break;
            case USER_LOCKOUT:
                log.error(event.toString());
                break;
            case LOGIN_SUCCESS:
                log.info(event.toString());
                if (!event.isReconnect()) {
                    startSubscriptions();
                }
                break;
            case LOGIN_FAILED:
                log.error(event.toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void onTimestamp(Date ts) {
        if (logTS) {
            log.info("TS: < " + java.text.DateFormat.getDateTimeInstance().format(ts));
        }
    }

    private String buildStorageFileName(ClientConfig config) {
        String fn = "ddf_";
        if (config.getSymbols() != null) {
            fn += config.getSymbols().replace(',', '_');
        }
        if (config.getExchangeCodes() != null) {
            fn += config.getExchangeCodes().replace(',', '_');
        }
        String dt = new DateTime().toString("YYYYMMdd");
        fn += "_" + dt + ".dat";
        return fn;
    }

    private void parseLogModes(String logMode) {
        if (logMode != null) {
            String[] modes = logMode.split(",");
            for (String m : modes) {
                if (m.equals("ts")) {
                    logTS = true;
                } else if (m.equals("q")) {
                    logQuote = true;
                } else if (m.equals("me")) {
                    logMarketEvent = true;
                } else if (m.equals("d")) {
                    logDdf = true;
                } else if (m.equals("b")) {
                    logBook = true;
                } else if (m.equals("qe")) {
                    logQuoteExchange = true;
                } else if (m.equals("a")) {
                    logTS = logQuote = logMarketEvent = logDdf = logBook = logQuoteExchange = true;
                }
            }
        }

    }

    /*
     * An example of how to remove subscriptions.
     */
    private void stopSubscriptions() {

        // remove subscriptions from the Client

        // Depth
        Set<String> mdSymbols = depthHandlers.keySet();
        for (String symbol : mdSymbols) {
            List<BookQuoteHandler> l = depthHandlers.get(symbol);
            for (BookQuoteHandler h : l) {
                client.removeBookQuoteHandler(symbol, h);
            }
        }
        depthHandlers.clear();

        // Quote
        Set<String> quoteSymbols = quoteHandlers.keySet();
        for (String symbol : quoteSymbols) {
            List<QuoteHandler> l = quoteHandlers.get(symbol);
            for (QuoteHandler h : l) {
                client.removeQuoteHandler(symbol, h);
            }
        }
        quoteHandlers.clear();

        // Quote Exchange
        Set<String> exchangeCodes = quoteExchangeHandlers.keySet();
        for (String exchangeCode : exchangeCodes) {
            client.removeQuoteExchangeHandler(exchangeCode);
        }
        quoteExchangeHandlers.clear();

    }

    private void startSubscriptions() {

        if (config.getExchangeCodes() != null) {
			/*
			 * There can only be one quote handler per exchange when subscribing
			 * for all quotes on the exchange. Note: You must be provisioned for
			 * this.
			 */
            String[] codes = config.getExchangeCodes().split(",");
            for (String exchangeCode : codes) {
                log.info("Adding exchange handler for exchange: " + exchangeCode);
                ClientQuoteExchangeHandler h = new ClientQuoteExchangeHandler(exchangeCode);
                quoteExchangeHandlers.put(exchangeCode, h);
                client.addQuoteExchangeHandler(exchangeCode, h);
            }
        }

        if (config.getSymbols() == null) {
            return;
        }

        String[] symbols = config.getSymbols().split(",");
        for (String symbol : symbols) {
            // Market Quote/BBO
            QuoteHandler handler = new ClientQuoteHandler();
            List<QuoteHandler> l = quoteHandlers.get(symbol);
            if (l == null) {
                l = new CopyOnWriteArrayList<QuoteHandler>();
                quoteHandlers.put(symbol, l);
            }
            l.add(handler);
            // This will request quotes if client does not have a subscription
            // to the symbol
            client.addQuoteHandler(symbol, handler);
        }

        // Depth
        if (config.isDepthSubscription()) {
            for (String symbol : symbols) {
                ClientBookQuoteHandler depthHandler = new ClientBookQuoteHandler();
                List<BookQuoteHandler> l = depthHandlers.get(symbol);
                if (l == null) {
                    l = new CopyOnWriteArrayList<BookQuoteHandler>();
                    depthHandlers.put(symbol, l);
                }
                l.add(depthHandler);
                // Will request a depth subscription if client is not already
                // subscribed.
                client.addBookQuoteHandler(symbol, depthHandler);
            }
        }

    }

    private class FeedHandlerImpl implements FeedHandler {

        @Override
        public void onMessage(DdfMessageBase msg) {
            if (logDdf) {
                log.info("DDF: <" + msg);
            }
        }

    }

    private class MarketEventHandlerImpl implements MarketEventHandler {

        @Override
        public void onEvent(MarketEvent event) {
            if (logMarketEvent) {
                log.info("MARKET EVENT: <" + event);
            }
        }

    }

    private class ClientQuoteHandler implements QuoteHandler {

        @Override
        public void onQuote(Quote quote) {
            if (logQuote) {
                log.info("QUOTE: " + quote.toXMLNode().toXMLString());
            }
        }

    }

    private class ClientQuoteExchangeHandler implements QuoteHandler {

        private String exchange;

        public ClientQuoteExchangeHandler(String exchange) {
            this.exchange = exchange;
        }

        @Override
        public void onQuote(Quote quote) {
            if (logQuoteExchange) {
                log.info("EXCH QUOTE(" + exchange + "): " + quote.toXMLNode().toXMLString());
            }
        }

    }

    /*
     * Market Depth
     */
    private class ClientBookQuoteHandler implements BookQuoteHandler {

        @Override
        public void onBookQuote(BookQuote bookQuote) {
            if (logBook) {
                log.info("BOOK: " + bookQuote.toXMLNode().toXMLString());
            }
        }
    }

    private static class ShutdownHook extends Thread {

        private DdfplusClient app;

        public ShutdownHook(DdfplusClient app) {
            this.app = app;
        }

        @Override
        public void run() {
            log.warn("Executing shutdown hook, client application is exiting.");
            app.shutdown();
        }

    }

    private static class ApplicationUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.err.println("Uncaught exception for tid: " + t + " at: " + new DateTime() + " error: " + e);
            e.printStackTrace();
        }

    }

}

