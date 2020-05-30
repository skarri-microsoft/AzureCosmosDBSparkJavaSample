package com.microsoft.azure;

import java.io.Serializable;

public class SampleCollModel2 implements Serializable {
    private String id;
    private String doc_id;
    private String payload;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id= id;
    }
    public String getDocId() {
        return doc_id;
    }

    public void setDocId(String doc_id) {
        this.doc_id= doc_id;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload= payload;
    }

    public SampleCollModel2 get(String json)
    {
        SampleCollModel2 model=new SampleCollModel2();
        model.id=JsonUtil.getTopLevelProp(json,"id");
        model.doc_id=JsonUtil.getTopLevelProp(json,"doc-id");
        model.payload=json;
        return model;
    }

    @Override
    public String toString() {
        return payload;
    }

}