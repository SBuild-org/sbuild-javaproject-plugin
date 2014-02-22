package org.sbuild.plugins.javaproject

import de.tototec.sbuild._

class JavaProjectPlugin(implicit project: Project) extends Plugin[JavaProject] with PluginWithDependencies {

  override def create(name: String): JavaProject = {
    val sources = Seq(Path("src/main/java"))
    val testSources = Seq(Path("src/test/java"))
    val resources = Seq(Path("src/main/resources"))
    val testResources = Seq(Path("src/test/resources"))
    JavaProject(
      compileDeps = TargetRefs(),
      testDeps = TargetRefs(),
      sourceDirs = sources,
      testSourceDirs = testSources,
      resourceDirs = resources,
      testResourceDirs = testResources,
      testFramework = Some("testng")
    )
  }

  override def dependsOn: Seq[Class[_]] = Seq(
    classOf[org.sbuild.plugins.javac.Javac],
    classOf[org.sbuild.plugins.jar.Jar]
  )

  override def applyToProject(instances: Seq[(String, JavaProject)]): Unit = instances.foreach {
    case (name, javaProject) =>

      import org.sbuild.plugins.javac._
      val javac = Plugin[Javac].get

      javac.cleanTargetName.map { cleanTargetName =>
        Target("phony:clean") dependsOn cleanTargetName
      }

      import org.sbuild.plugins.jar._
      val jar = Plugin[Jar].configure(_.copy(
        deps = javac.compileTargetName
      )).get

  }

}