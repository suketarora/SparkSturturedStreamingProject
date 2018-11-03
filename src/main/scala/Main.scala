
import org.apache.spark._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession

object Main extends App {

  override def main(arg: Array[String]): Unit = {

    val spark = SparkSession
                .builder
                .appName("Spark Streaming Project")
                .master("local[*]")
                .getOrCreate()
                
    import spark.implicits._
    
    // Create DataFrame representing the stream of input lines from connection to localhost:9999
    val lines = spark.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 9999)
      .load()
    
    // Split the lines into words
    val words = lines.as[String].flatMap(_.split(" "))
    
    // Generate running word count
    val wordCounts = words.groupBy("value").count()
    
    // Start running the query that prints the running counts to the console
    val query = wordCounts.writeStream
      .outputMode("complete")
      .format("console")
      .start()
    
    query.awaitTermination()
        
    


  }

}