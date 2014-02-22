import de.tototec.sbuild._

import de.tototec.sbuild.ant._
import de.tototec.sbuild.ant.tasks._

@version("0.7.1")
@classpath(
  "mvn:org.sbuild:org.sbuild.plugins.sbuildplugin:0.3.0",
  "mvn:org.apache.ant:ant:1.8.4",
  "mvn:org.sbuild:org.sbuild.plugins.mavendeploy:0.1.0"
)
class SBuild(implicit _project: Project) {

  val namespace = "org.sbuild.plugins.javaproject"
  val version = "0.0.9000"
  val url = "https://github.com/SBuild-org/sbuild-javaproject-plugin"
  val sourcesJar = s"target/${namespace}-${version}-sources.jar"
  val sourcesDir = "src/main/scala"

  val tmpBaseDir = Path("../..").getPath

  Target("phony:all") dependsOn "jar" ~ sourcesJar

  import org.sbuild.plugins.sbuildplugin._
  Plugin[SBuildPlugin] configure (_.copy(
    sbuildVersion = new SBuildVersion with SBuildVersion.Scala_2_10_3 with SBuildVersion.ScalaTest_2_0 {
      val basePath = Path("../../sbuild/sbuild-dist/target/sbuild-0.7.1.9000/lib")
      override protected def project = _project
      override val version = "0.7.0"
      override val sbuildClasspath =
        (basePath / "de.tototec.sbuild-0.7.1.9000.jar") ~
          (basePath / "de.tototec.sbuild.addons-0.7.1.9000.jar") ~
          (basePath / "de.tototec.sbuild.ant-0.7.1.9000.jar")
    },
    pluginClass = s"${namespace}.JavaProject",
    pluginVersion = version,
    deps = Seq(
      s"${tmpBaseDir}/clean/org.sbuild.plugins.clean/target/org.sbuild.plugins.clean-0.0.9000.jar",
      s"${tmpBaseDir}/javac/org.sbuild.plugins.javac/target/org.sbuild.plugins.javac-0.0.9000.jar",
      s"${tmpBaseDir}/jar/org.sbuild.plugins.jar/target/org.sbuild.plugins.jar-0.0.9000.jar"
    ),
    manifest = Map("SBuild-Version" -> "0.7.1.9000"),
    // reexport dependent plugins
    exportedPackages = Some(Seq(
      "org.sbuild.plugins.javaproject",
      //      "org.sbuild.plugins.clean",
      "org.sbuild.plugins.javac",
      "org.sbuild.plugins.jar"
    ))
  ))

  import org.sbuild.plugins.mavendeploy._
  Plugin[MavenDeploy] configure (_.copy(
    groupId = "org.sbuild",
    artifactId = namespace,
    version = version,
    artifactName = Some("SBuild Java Project Plugin"),
    description = Some("An SBuild Plugin that provides a typical Java Project toolchain."),
    repository = Repository.SonatypeOss,
    scm = Option(Scm(url = url, connection = url)),
    developers = Seq(Developer(id = "TobiasRoeser", name = "Tobias Roeser", email = "le.petit.fou@web.de")),
    gpg = true,
    licenses = Seq(License.Apache20),
    url = Some(url),
    files = Map(
      "jar" -> s"target/${namespace}-${version}.jar",
      "sources" -> s"target/${namespace}-${version}-sources.jar",
      "javadoc" -> "target/fake.jar"
    )
  ))

  Target(sourcesJar) dependsOn s"scan:${sourcesDir}" ~ "LICENSE.txt" exec { ctx: TargetContext =>
    AntZip(destFile = ctx.targetFile.get, fileSets = Seq(
      AntFileSet(dir = Path(sourcesDir)),
      AntFileSet(file = Path("LICENSE.txt"))
    ))
  }

  Target("target/fake.jar") dependsOn "LICENSE.txt" exec { ctx: TargetContext =>
    import de.tototec.sbuild.ant._
    tasks.AntJar(destFile = ctx.targetFile.get, fileSet = AntFileSet(file = "LICENSE.txt".files.head))
  }

}
