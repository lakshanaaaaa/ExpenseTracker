package com.expenseTracker.model;


import java.time.LocalDateTime;

public class Expense {
    private int expId;
    private int catId;
    private String Notes;
    private double Amount;
    private LocalDateTime Date;

    public int getExpId() {
        return expId;
    }
    public void setExpId(int expId) {
        this.expId = expId;
    }
    public int getCatId() {
        return catId;
    }
    public void setCatId(int catId) {
        this.catId = catId;
    }
    public String getNotes() {
        return Notes;
    }
    public void setNotes(String Notes) {
        this.Notes = Notes;
    }
    public double getAmount() {
        return Amount;
    }
    public void setAmount(double Amount) {
        this.Amount = Amount;
    }
    public LocalDateTime getDate() {
        return Date;
    }
    public void setDate(LocalDateTime Date) {
        this.Date = Date;
    }

    public Expense() {
        this.Date = LocalDateTime.now();
    }
    public Expense(int catId,double Amount) {
        this();
        this.catId = catId;
        this.Amount = Amount;
    }
    public Expense(int expId, int catId, String Notes, double Amount, LocalDateTime Date) {
        this.expId = expId;
        this.catId = catId;
        this.Notes = Notes;
        this.Amount = Amount;
        this.Date = Date;
    }

}
