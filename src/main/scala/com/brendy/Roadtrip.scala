/**
  * Created by brberg on 10/25/16.
  */
import com.brendy.City
import com.brendy.Routes
import com.google.maps.DistanceMatrixApi
import com.google.maps.GeoApiContext

import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._
import scalax.collection.edge.Implicits._
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.collection.mutable.ArrayBuffer
import scalax.collection.edge.WDiEdge


object Roadtrip {

  val reston = City("Reston","VA", 5)
  val seattle = City("Seattle", "WA", 5)


  def main(args: Array[String]): Unit = {

    val GOOGLE_API_KEY = System.getenv("GOOGLE_API_KEY")

    implicit val formats = DefaultFormats

    val stream = Roadtrip.getClass.getResourceAsStream("cities.json")
    val file = io.Source.fromInputStream(stream)
    val lines = try file.mkString finally stream.close()

    val cities = parse(lines).extract[List[City]].toArray

    cities.foreach(city => println(city.city))

    val context = new GeoApiContext().setApiKey(GOOGLE_API_KEY);

    val graph = calculateRoutes(context, 13,cities)

    val routes = new Routes(graph)

    println(graph.toString)

  }

  def calculateRoutes(context: GeoApiContext, maxDriveInHours: Int, cityList: Array[City]) : Graph[City,WDiEdge] = {
      val fullCityList = cityList ++ Array(reston,seattle)

      val graph = Graph[City,WDiEdge](reston)

      val routes = fullCityList.map( city => findRoutesFromCity(context, maxDriveInHours, city, fullCityList) ).reduce(_ ++ _)

      return graph ++ routes
  }

  def findRoutesFromCity(context: GeoApiContext, maxDriveInHours: Int, originCity: City, cityList: Array[City]) : List[WDiEdge[City]] = {

    val maxDriveInSeconds = maxDriveInHours * 60 * 60

    val distMatrix = DistanceMatrixApi.getDistanceMatrix(context,Array(originCity.cityState()), cityList.map(city => city.cityState())).await()

    val edgesToAdd = ArrayBuffer.empty[WDiEdge[City]];

    distMatrix.rows.foreach{ row =>
      row.elements.zipWithIndex.foreach{ case(element,i) =>
        println("From " + originCity.cityState() + " to " + cityList(i).cityState() + ": " + element.duration.inSeconds)
        if(element.duration.inSeconds < maxDriveInSeconds) {
          println("Adding: cityList(i) ")
          edgesToAdd += originCity ~> cityList(i) % element.duration.inSeconds
        }
      }
    }

    return edgesToAdd.toList
  }
}
