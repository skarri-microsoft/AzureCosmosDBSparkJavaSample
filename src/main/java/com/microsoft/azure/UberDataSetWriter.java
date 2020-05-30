package com.microsoft.azure;

import java.util.List;

public class UberDataSetWriter {

    public void Save(List<String> docs)
    {
        // Either MOngo spark
        // or Mongo driver updateMany with upsert true
        // https://docs.mongodb.com/manual/reference/method/db.collection.updateMany/
        // Mongospark.save()

    }
}
