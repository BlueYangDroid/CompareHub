package com.hisense.fitlib.serialtask;

public class SerializeResult {

    public long runDuration;
    public int objectsParsed;

    public SerializeResult(long runDuration, int objectsSerialized) {
        this.runDuration = runDuration;
        this.objectsParsed = objectsSerialized;
    }

}
