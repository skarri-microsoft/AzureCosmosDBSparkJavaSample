package com.microsoft.azure;

import com.jayway.jsonpath.JsonPath;

import java.io.Serializable;

public class SampleColl1Model implements Serializable {
    private String id;
    private String name;
    private int storeId;
    private String payload;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id= id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name= name;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId= storeId;
    }
    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload= payload;
    }

    public SampleColl1Model get(String json)
    {
        SampleColl1Model model=new SampleColl1Model();
        model.id=JsonUtil.getTopLevelProp(json,"id");
        model.storeId=JsonUtil.getTopLevelProp(json,"storeid");
        model.name=JsonUtil.getTopLevelProp(json,"name");
        model.payload=json;
        return model;
    }


    @Override
    public String toString() {
        return payload;
    }

}
