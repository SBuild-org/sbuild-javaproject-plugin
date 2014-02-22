package org.sbuild.plugins.javaproject

object ProjectInfo {
  def apply(name: String): ProjectInfo = ProjectInfo(Some(name), None)
  def apply(name: String, version: String): ProjectInfo = ProjectInfo(Some(name), Some(version))

  implicit def fromString(string: String): ProjectInfo = {
    string.split(":", 2) match {
      case Array(name) => apply(string)
      case Array(name, version) => apply(string, version)
    }
  }
}

case class ProjectInfo(name: Option[String], version: Option[String])
