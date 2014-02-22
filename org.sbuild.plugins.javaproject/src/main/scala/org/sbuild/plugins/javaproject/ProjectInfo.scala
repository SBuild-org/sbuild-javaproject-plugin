package org.sbuild.plugins.javaproject

object ProjectInfo {
  def apply(name: String): ProjectInfo = ProjectInfo(name, None)
  def apply(name: String, version: String): ProjectInfo = ProjectInfo(name, Some(version))

  implicit def fromString(string: String): ProjectInfo = {
    string.split(":", 2) match {
      case Array(name) => ProjectInfo(name, None)
      case Array(name, version) => ProjectInfo(name, Some(version))
    }
  }
}

case class ProjectInfo(name: String, version: Option[String])
