import de.tototec.sbuild._

@version("0.7.1.9000")
@classpath("../../org.sbuild.plugins.javaproject/target/org.sbuild.plugins.javaproject-0.0.9000.jar")
class SBuild(implicit _project: Project) {

  val compileCp = "mvn:org.slf4j:slf4j-api:1.7.6"

  import org.sbuild.plugins._
  Plugin[javaproject.JavaProject] configure (_.copy(
    projectInfo = "test1",
    compileDeps = compileCp
  ))

}
