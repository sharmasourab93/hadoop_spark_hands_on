package com.examples.spark.core;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

/**
 * This is a simple example of Spark of a counter, well explained and verbose
 * about Spark and it components. The word count example read a file from the an
 * input file and count all the words in the file. The present version is based
 * in java 8 lambda expressions
 *
 */
public class SparkWordCount {

	public static void main(String[] args) throws Exception {

		String inputPath = args[0];
		String outputPath = args[1];

		/**
		 * The first thing a Spark program must do is to create a
		 * JavaSparkContext object, which tells Spark how to access a cluster.
		 *
		 * 1- The AppName will be shown in the cluster UI: Mesos, Spark, ot YARN
		 * 2- The master is the name of the machine, we use local if the user
		 * run the program in a local machine 3- A property of the number of
		 * cores to be use by the software
		 */

		SparkConf conf = new SparkConf().setAppName("word-counter").setMaster("local").set("spark.cores.max", "4");
		JavaSparkContext sc = new JavaSparkContext(conf);

		/**
		 * Text file RDDs can be created using SparkContext’s textFile method.
		 * This method takes an URI for the file either a local path on the
		 * machine, or a hdfs://, s3n://, etc URI) and reads it as a collection
		 * of lines. Every line in the file is an element in the JavaRDD list.
		 *
		 * Important **Note**: This line defines a base RDD from an external
		 * file. This dataset is not loaded in memory or otherwise acted on:
		 * lines is merely a pointer to the file.
		 *
		 * Here is an example invocation:
		 */

		JavaRDD<String> rdd = sc.textFile(inputPath);

		/**
		 * This function allow to compute the number of occurrences for a
		 * particular word, the first instruction flatMap allows to create the
		 * key of the map by splitting each line of the JavaRDD. Map to pair
		 * maps each token with constant count indicator. ReduceByKey sums up
		 * count for each token.
		 *
		 */

		JavaPairRDD<String, Integer> counts = rdd
												.flatMap(x -> Arrays.asList(x.split(" ")).iterator())
												.mapToPair(x -> new Tuple2<>(x, 1))
												.reduceByKey((x, y) -> x + y);

		long time = System.currentTimeMillis();
		long countEntries = counts.count();
		System.out.println(countEntries + ": " + String.valueOf(System.currentTimeMillis() - time));

		/**
		 * The RDDs can be save to a text file by using the saveAsTextFile
		 * function which export the RDD information to a text representation,
		 * either a local path on the machine, or a hdfs://, s3n://, etc URI)
		 */
		counts.saveAsTextFile(outputPath);
		sc.close();

	}

}
