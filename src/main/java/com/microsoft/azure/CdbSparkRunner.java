package com.microsoft.azure;


import com.microsoft.azure.cosmosdb.spark.SparkContextFunctions$;
import com.microsoft.azure.cosmosdb.spark.config.Config;
import com.microsoft.azure.cosmosdb.spark.config.Config$;
import com.microsoft.azure.cosmosdb.spark.rdd.CosmosDBRDD;
import com.microsoft.azure.documentdb.JsonSerializable;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import scala.Array;
import scala.Tuple2;

public class CdbSparkRunner {
    public static void Execute(
            String appName,
            String master)
    {
//       JavaSparkContext sc = new JavaSparkContext(
//               getReadSparkConfUsingChangeFeed(appName,master));

        JavaSparkContext sc = new JavaSparkContext(
                getReadSparkConfUsingQuery(appName,master));

        //sc.setLogLevel("Debug");

       LoadCosmosDBRdd(sc);
    }

    public static void LoadCosmosDBRdd(JavaSparkContext sc)
    {
        Config readConfig = Config$.MODULE$.apply(sc.getConf());

        CosmosDBRDD cosmosDBRDD= SparkContextFunctions$.MODULE$
                .apply(JavaSparkContext.toSparkContext(sc))
                .loadFromCosmosDB(readConfig);
        cosmosDBRDD.cache();

        JavaRDD<String> stringCollectionRDD=
                cosmosDBRDD
                        .toJavaRDD()
                        .map(JsonSerializable::toJson);

        JavaRDD<SampleColl1Model> sampleModel1RDD=
                cosmosDBRDD
                .toJavaRDD()
                .map(data ->{
                   return new SampleColl1Model().get(data.toJson());
                });


        sampleModel1RDD.take(1).forEach(System.out::println);

        SparkSession spark = SparkSession
                .builder()
                .getOrCreate();

        Dataset<Row> collDF =spark.createDataFrame(sampleModel1RDD, SampleColl1Model.class);
        collDF.createOrReplaceTempView("coll1");
        collDF.createOrReplaceTempView("coll2");
        Dataset<Row> coll1DF1 = spark.sql("SELECT Name,StoreId,Id,Payload FROM coll1");
        PrintSampleData(coll1DF1);

        Dataset<Row> coll1DF2 = spark.sql("SELECT Name,StoreId,Id,Payload FROM coll2");

        Dataset<Row> coll1DF3= coll1DF1.join(coll1DF2, "Id")
                .select(coll1DF1.col("Id").as("Coll1Id"), coll1DF2.col("Payload").as("Coll2PayLoad"));
        //String[] cols= coll1DF3.columns();
        coll1DF3.printSchema();
    }

    private static void PrintSampleData(Dataset<Row> collectionDS)
    {
        Encoder<String> stringEncoder = Encoders.STRING();
        Dataset<String> coll1DataDF = collectionDS.map(
                (MapFunction<Row, String>) row ->
                        "Store Id: " + row.getInt(1)+
                                " Name: "+ row.getString(0),
                stringEncoder);
        coll1DataDF.show();
    }

    private JavaPairRDD<String,Iterable<String>> GetJavaPairRDD(
            String idColumn,
            JavaSparkContext javaSparkContext)
    {
        Config readConfig = Config$.MODULE$.apply(javaSparkContext.getConf());

        CosmosDBRDD cosmosDBRDD= SparkContextFunctions$.MODULE$
                .apply(JavaSparkContext.toSparkContext(javaSparkContext))
                .loadFromCosmosDB(readConfig);
        cosmosDBRDD.cache();
                return cosmosDBRDD
                .toJavaRDD()
                .filter(d -> d.get(idColumn) != null)
                .mapToPair(d -> new Tuple2<>(
                        d.getString(idColumn),
                        d.toJson()))
                .groupByKey();
    }

    private static SparkConf getReadSparkConfUsingQuery(String appName,String master)
    {
        SparkConf conf = new SparkConf().setAppName(appName).setMaster(master);
        conf.set("spark.cosmosdb.endpoint","https://bugvalidation.documents.azure.com:443/");
        conf.set("spark.cosmosdb.masterkey","password");
        conf.set("spark.cosmosdb.database","test");
        conf.set("spark.cosmosdb.collection","testc");
        conf.set("spark.cosmosdb.schema_samplingratio","1.0");
        conf.set("spark.cosmosdb.query_custom", "select * from c where c.id=\"NWVkMjFkYTMxYTRmYjkwYjk4OGI1NzNl\"");
        return  conf;
    }

    // Reference: https://github.com/Azure/azure-cosmosdb-spark/wiki/Change-Feed-demos
    private static SparkConf getReadSparkConfUsingChangeFeed(String appName,String master)
    {
        Boolean rollingChangeFeed = false;
        Boolean startFromTheBeginning = true;
        Boolean useNextToken = true;
        String changeFeedQueryName=String.format(
                "change feed with rollingChangeFeed=%s, startFromtTheBeginning=%s, useNextToken=%s",
                rollingChangeFeed.toString(),startFromTheBeginning.toString(),useNextToken.toString());
        SparkConf conf = new SparkConf().setAppName(appName).setMaster(master);
        conf.set("spark.cosmosdb.endpoint","https://bugvalidation.documents.azure.com:443/");
        conf.set("spark.cosmosdb.masterkey","password");
        conf.set("spark.cosmosdb.database","test");
        conf.set("spark.cosmosdb.collection","testc");

        //Change feed
        conf.set("spark.cosmosdb.readchangefeed","true");
        conf.set("spark.cosmosdb.ChangeFeedQueryName",changeFeedQueryName);
        conf.set("spark.cosmosdb.changefeedstartfromthebeginning",startFromTheBeginning.toString());
        conf.set("spark.cosmosdb.changefeedusenexttoken",useNextToken.toString());
        conf.set("spark.cosmosdb.rollingchangefeed",rollingChangeFeed.toString());
        conf.set("spark.cosmosdb.changefeedcheckpointlocation","./changefeedcheckpoint2");

        conf.set("spark.cosmosdb.schema_samplingratio","1.0");

        return  conf;
    }
}
