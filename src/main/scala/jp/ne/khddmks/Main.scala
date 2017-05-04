package jp.ne.khddmks

import java.io.{BufferedReader, InputStreamReader}

import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.typesafe.config.ConfigFactory

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
      val conf = ConfigFactory.load(ConfigFactory.parseReader(
        new BufferedReader(new InputStreamReader(s3.getObject(bucket, key).getObjectContent, "UTF-8"))
      ))
      conf
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
    } catch {
      case NonFatal(e) => println(s"エラーが発生しました。設定ファイルが正しいか確認してください。: ${e}")
    }
  }
}
