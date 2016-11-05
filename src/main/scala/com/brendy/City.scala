package com.brendy

/**
  * Created by brberg on 11/2/16.
  */
case class City(city: String, state: String, awesomeness: Int) {
  def cityState() : String = {
    return city + ", " + state
  }
}
