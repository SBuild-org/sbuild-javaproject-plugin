package org.sbuild.plugins.javaproject

import de.tototec.sbuild._
import java.io.File

case class JavaProject(
  //  targetDir: File,
  compileDeps: TargetRefs,
  testDeps: TargetRefs,
  sourceDirs: Seq[File],
  testSourceDirs: Seq[File],
  resourceDirs: Seq[File],
  testResourceDirs: Seq[File],
  testFramework: Option[String])
