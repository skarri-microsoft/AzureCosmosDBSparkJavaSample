package com.microsoft.azure;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;

import java.util.Arrays;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        String masterDefault="local";
        if(args.length>0)
        {
            int index = -1;
            for (int i=0;i<args.length;i++) {
                if (args[i].contains("master")) {
                    index = i;
                    break;
                }
            }
           if(index>=0)
           {
               String master=args[index].split("=")[1];
               masterDefault=master;
           }
        }
        CdbSparkRunner.Execute("test",masterDefault);
    }

    public static void sparkSample()
    {
        String logFile = "C:\\hadoop-2.7.1\\README.md"; // Should be some file on your system
        SparkSession spark = SparkSession.builder().master("local").appName("Simple Application").getOrCreate();
        Dataset<String> logData = spark.read().textFile(logFile).cache();
        long numAs = logData.filter((org.apache.spark.api.java.function.FilterFunction<String>)s -> s.contains("a")).count();
        long numBs = logData.filter((org.apache.spark.api.java.function.FilterFunction<String>)s -> s.contains("b")).count();

        System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);

        spark.stop();
    }

}
