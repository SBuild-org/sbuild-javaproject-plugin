package org.sbuild.plugins.javaproject

import de.tototec.sbuild._

class JavaProjectPlugin(implicit project: Project) extends Plugin[JavaProject] with PluginWithDependencies {

  override def create(name: String): JavaProject = {
    val sources = Seq(Path("src/main/java"))
    val testSources = Seq(Path("src/test/java"))
    val resources = Seq(Path("src/main/resources"))
    val testResources = Seq(Path("src/test/resources"))
    JavaProject(
      projectInfo = ProjectInfo("main"),
      compileDeps = TargetRefs(),
      testDeps = TargetRefs(),
      sourceDirs = sources,
      testSourceDirs = testSources,
      resourceDirs = resources,
      testResourceDirs = testResources,
      testFramework = Some("testng"),
      preCompileAction = TargetRefs(),
      preTestCompileAction = TargetRefs(),
      source = None,
      target = None,
      manifest = Map()
    )
  }

  override def dependsOn: Seq[Class[_]] = Seq(
    classOf[org.sbuild.plugins.javac.Javac],
    classOf[org.sbuild.plugins.jar.Jar],
    classOf[org.sbuild.plugins.clean.Clean]
  )

  override def applyToProject(instances: Seq[(String, JavaProject)]): Unit = instances.foreach {
    case (name, javaProject) =>

      import org.sbuild.plugins.javac._
      import org.sbuild.plugins.jar._
      import org.sbuild.plugins.clean._

      val targetDir = Path("target")
      val compileTargetDir = targetDir / "classes"
      val testCompileTargetDir = targetDir / "test-classes"

      val compile = Plugin[Javac].configure(_.copy(
        classpath = javaProject.compileDeps,
        targetDir = compileTargetDir,
        srcDirs = javaProject.sourceDirs,
        encoding = "UTF-8",
        deprecation = Some(true),
        debugInfo = Some("vars"),
        source = javaProject.source,
        target = javaProject.target,
        dependsOn = javaProject.preCompileAction
      )).get

      val jarNameBase = javaProject.projectInfo.name +
        (javaProject.projectInfo.version match {
          case None => ""
          case Some(v) => s"-${v}"
        })

      val jar = Plugin[Jar].configure(_.copy(
        deps = compile.compileTargetName,
        jarFile = targetDir / s"${jarNameBase}.jar",
        fileSets =
          Seq(JarFileSet.Dir(compileTargetDir)) ++
            javaProject.resourceDirs.map(dir => JarFileSet.Dir(dir)),
        manifest = javaProject.manifest,
        phonyTarget = Some("jar")
      )).get

      val testCompile = Plugin[Javac]("test").configure(_.copy(
        classpath = javaProject.testDeps ~ jar.jarFile,
        targetDir = testCompileTargetDir,
        srcDirs = javaProject.testSourceDirs,
        encoding = "UTF-8",
        deprecation = Some(true),
        debugInfo = Some("vars"),
        source = javaProject.source,
        target = javaProject.target,
        dependsOn = javaProject.preTestCompileAction
      )).get

      val testJar = Plugin[Jar].configure(_.copy(
        jarFile = targetDir / s"${jarNameBase}-tests.jar",
        deps = testCompile.compileTargetName,
        phonyTarget = Some("jar-tests")
      )).get

      Plugin[Clean].configure(clean =>
        clean.copy(
          dirs = clean.dirs ++ Seq(targetDir),
          dependsOn =
            clean.dependsOn ~
              compile.cleanTargetName.toSeq.map(TargetRef(_)) ~
              testCompile.cleanTargetName.toSeq.map(TargetRef(_))
        )
      ).get

  }

}