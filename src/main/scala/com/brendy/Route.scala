package com.brendy

import scalax.collection.Graph
import scalax.collection.edge.Implicits._
import scalax.collection.GraphTraversalImpl._
import scalax.collection.edge.WDiEdge

/**
  * Created by brberg on 11/2/16.
  */
class Route(path : Graph[City,WDiEdge]#Path) {

  var length = path.edges.map( _.weight).reduce(_ + _)

  val totalAwesomeness = path.nodes.map(_.value.awesomeness).reduce(_ + _)

  override def toString(): String = {
    val lengthInHours = length / (60*60)
    return path.nodes.map(_.value.cityState()).mkString(" -> ") + ". Takes: " + lengthInHours + " with awesomeness: " + totalAwesomeness
  }

}
