package com.hisense.fitlib.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Name {

    @JsonField
    public String first;

    @JsonField
    public String last;
}
