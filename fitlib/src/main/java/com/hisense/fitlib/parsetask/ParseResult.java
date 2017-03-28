package com.hisense.fitlib.parsetask;


@SuppressWarnings("CanBeFinal")
public class ParseResult {

    public long runDuration;
    public int countParsed;
    public Object response;

    public ParseResult() {
    }

    public ParseResult(long runDuration, int countParsed, Object response) {
        this.runDuration = runDuration;
        this.countParsed = countParsed;
        this.response = response;
    }

}
