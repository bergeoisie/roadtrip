package com.brendy

import scala.collection.mutable.PriorityQueue
import scalax.collection.Graph

import scalax.collection.edge.WDiEdge

/**
  * Created by brberg on 11/2/16.
  */
class Routes(val graph : Graph[City,WDiEdge]) {

  val reston = graph.get(new City("Reston", "VA", 5))
  val seattle = graph.get(new City("Seattle", "WA", 5))

  var nodeSetNightOne = reston.diSuccessors

  println("Potential first night")

  nodeSetNightOne.foreach(city => println(city.cityState()))

  var nodeSetNightTwo = nodeSetNightOne.map(_.diSuccessors).reduce(_ union _) &~ nodeSetNightOne

  println("Potential second night")

  nodeSetNightTwo.foreach(city => println(city.cityState()))

  var nodeSetNightThree = nodeSetNightTwo.map(_.diSuccessors).reduce(_ union _ ) &~ nodeSetNightOne &~ nodeSetNightTwo

  println("Potential third night")

  nodeSetNightThree.foreach(city => println((city.cityState())))

  var nodeSetNightFour = nodeSetNightThree.map(_.diSuccessors).reduce(_ union _ ) &~ nodeSetNightOne &~ nodeSetNightTwo &~ nodeSetNightThree

  println("Potential fourth night")

  nodeSetNightFour.foreach(city => println(city.cityState()))

  var priorityQueue = scala.collection.mutable.PriorityQueue.empty[Route](
    Ordering.by((_: Route).length)
  )

  nodeSetNightOne.foreach { nodeOne =>
    nodeSetNightTwo.foreach { nodeTwo =>
      nodeSetNightThree.foreach { nodeThree =>
        nodeSetNightFour.foreach { nodeFour =>
          val pathBuilder = graph.newPathBuilder(graph.get(reston))
          if(pathBuilder add nodeOne) {
            if(pathBuilder add nodeTwo) {
              if(pathBuilder add nodeThree) {
                if (pathBuilder add nodeFour) {
                  if (pathBuilder add seattle) {
                    val path = pathBuilder.result()
                    val route = new Route(path)
                    println(route.toString())
                    priorityQueue.enqueue(route)
                  }
                }
              }
            }
          }
        }
      }
    }
  }



  def nextRoute() : Route = {
    return priorityQueue.dequeue()
  }
}
