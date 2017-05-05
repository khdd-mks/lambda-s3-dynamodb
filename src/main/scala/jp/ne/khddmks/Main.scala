package jp.ne.khddmks

import java.io.{BufferedReader, InputStreamReader}

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.{DynamoDB, Item}
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.typesafe.config.{ConfigFactory, ConfigRenderOptions}

import scala.util.control.NonFatal

/**
  * Created by khdd-mks on 2017/05/02.
  */
class Main {
  def handler(s3Event: S3Event, context: Context): Unit = {
    def createS3 = {
      val s3 = AmazonS3ClientBuilder.standard()
        .withRegion(Regions.AP_NORTHEAST_1)
        .build()
      s3
    }
    def readFromS3(s3: AmazonS3, bucket: String, key: String) = {
//      // システムプロパティを含むConfig
//      val conf = ConfigFactory.load(ConfigFactory.parseReader(
//        new BufferedReader(new InputStreamReader(s3.getObject(bucket, key).getObjectContent, "UTF-8"))
//      ))
      // システムプロパティを含まないConfig (プロパティ内で${java.home}のようにシステムプロパティを参照することはできない)
      val conf = ConfigFactory.parseReader(
        new BufferedReader(new InputStreamReader(s3.getObject(bucket, key).getObjectContent, "UTF-8"))
      ).resolve
      conf
    }
    def createDynamoDB = {
      val dbClient = AmazonDynamoDBClientBuilder.standard
        .withRegion(Regions.AP_NORTHEAST_1)
        .build
      val db = new DynamoDB(dbClient)
      db
    }
    def writeToDynamoDB(db: DynamoDB, json: String) = {
      val table = db.getTable("sample001")
      val item = new Item().withPrimaryKey("key", "config")
        .withJSON("values", json)
      table.putItem(item)
    }

    try {
      import scala.collection.JavaConverters._
      val (bucketInfo, objectInfo) = {
        val temp = s3Event.getRecords.asScala.head.getS3
        (temp.getBucket, temp.getObject)
      }
      val (bucket, key) = (bucketInfo.getName, objectInfo.getKey)
      println(s"bucket : ${bucket}, key : ${key}")
      val conf = readFromS3(createS3, bucket, key)
      println(conf)
      val json = conf.root.render(ConfigRenderOptions.concise)
      writeToDynamoDB(createDynamoDB, json)
    } catch {
      case NonFatal(e) => println(s"エラーが発生しました。設定ファイルが正しいか確認してください。: ${e}")
    }
  }
}
