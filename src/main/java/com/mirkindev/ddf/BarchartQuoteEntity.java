package com.mirkindev.ddf;

import com.ddfplus.db.Quote;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by dimam on 31/10/2016.
 */
@Entity
@Table(name = "barchart_quote", schema = "commo_index" )
public class BarchartQuoteEntity {
    private int id;
    private String symbol;
    private String name;
    private String exchange;
    private String basecode;
    private Float pointvalue;
    private Integer tickincrement;
    private String ddfexchange;
    private Timestamp lastupdate;
    private Float bid;
    private Float ask;
    private Integer bidsize;
    private Integer asksize;

    public BarchartQuoteEntity()
    {

    }

    public BarchartQuoteEntity(Quote q) {
        symbol = q.getSymbolInfo().getSymbol();
        name = q.getSymbolInfo().getName();
        exchange = q.getSymbolInfo().getExchange();
        basecode = Character.toString(q.getSymbolInfo().getBaseCode());
        pointvalue = q.getSymbolInfo().getPointValue();
        tickincrement = q.getSymbolInfo().getTickIncrement();
        ddfexchange = q.getDDFExchange();
        lastupdate = new Timestamp(q.getLastUpdated());
        bid = q.getBid();
        ask = q.getAsk();
        bidsize = q.getBidSize();
        asksize = q.getAskSize();
    }

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "symbol")
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "exchange")
    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    @Basic
    @Column(name = "basecode")
    public String getBasecode() {
        return basecode;
    }

    public void setBasecode(String basecode) {
        this.basecode = basecode;
    }

    @Basic
    @Column(name = "pointvalue")
    public Float getPointvalue() {
        return pointvalue;
    }

    public void setPointvalue(Float pointvalue) {
        this.pointvalue = pointvalue;
    }

    @Basic
    @Column(name = "tickincrement")
    public Integer getTickincrement() {
        return tickincrement;
    }

    public void setTickincrement(Integer tickincrement) {
        this.tickincrement = tickincrement;
    }

    @Basic
    @Column(name = "ddfexchange")
    public String getDdfexchange() {
        return ddfexchange;
    }

    public void setDdfexchange(String ddfexchange) {
        this.ddfexchange = ddfexchange;
    }

    @Basic
    @Column(name = "lastupdate")
    public Timestamp getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(Timestamp lastupdate) {
        this.lastupdate = lastupdate;
    }

    @Basic
    @Column(name = "bid")
    public Float getBid() {
        return bid;
    }

    public void setBid(Float bid) {
        this.bid = bid;
    }

    @Basic
    @Column(name = "ask")
    public Float getAsk() {
        return ask;
    }

    public void setAsk(Float ask) {
        this.ask = ask;
    }

    @Basic
    @Column(name = "bidsize")
    public Integer getBidsize() {
        return bidsize;
    }

    public void setBidsize(Integer bidsize) {
        this.bidsize = bidsize;
    }

    @Basic
    @Column(name = "asksize")
    public Integer getAsksize() {
        return asksize;
    }

    public void setAsksize(Integer asksize) {
        this.asksize = asksize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BarchartQuoteEntity that = (BarchartQuoteEntity) o;

        if (id != that.id) return false;
        if (symbol != null ? !symbol.equals(that.symbol) : that.symbol != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (exchange != null ? !exchange.equals(that.exchange) : that.exchange != null) return false;
        if (basecode != null ? !basecode.equals(that.basecode) : that.basecode != null) return false;
        if (pointvalue != null ? !pointvalue.equals(that.pointvalue) : that.pointvalue != null) return false;
        if (tickincrement != null ? !tickincrement.equals(that.tickincrement) : that.tickincrement != null)
            return false;
        if (ddfexchange != null ? !ddfexchange.equals(that.ddfexchange) : that.ddfexchange != null) return false;
        if (lastupdate != null ? !lastupdate.equals(that.lastupdate) : that.lastupdate != null) return false;
        if (bid != null ? !bid.equals(that.bid) : that.bid != null) return false;
        if (ask != null ? !ask.equals(that.ask) : that.ask != null) return false;
        if (bidsize != null ? !bidsize.equals(that.bidsize) : that.bidsize != null) return false;
        if (asksize != null ? !asksize.equals(that.asksize) : that.asksize != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (exchange != null ? exchange.hashCode() : 0);
        result = 31 * result + (basecode != null ? basecode.hashCode() : 0);
        result = 31 * result + (pointvalue != null ? pointvalue.hashCode() : 0);
        result = 31 * result + (tickincrement != null ? tickincrement.hashCode() : 0);
        result = 31 * result + (ddfexchange != null ? ddfexchange.hashCode() : 0);
        result = 31 * result + (lastupdate != null ? lastupdate.hashCode() : 0);
        result = 31 * result + (bid != null ? bid.hashCode() : 0);
        result = 31 * result + (ask != null ? ask.hashCode() : 0);
        result = 31 * result + (bidsize != null ? bidsize.hashCode() : 0);
        result = 31 * result + (asksize != null ? asksize.hashCode() : 0);
        return result;
    }
}
