
lazy val template_master = (project in file("."))
  .aggregate(
    common,
    exercise_000_the_actor_system,
    exercise_001_the_actor_lifecycle,
    exercise_002_actor_supervision
 ).settings(CommonSettings.commonSettings: _*)

lazy val common = project.settings(CommonSettings.commonSettings: _*)

lazy val exercise_000_the_actor_system = project
  .settings(CommonSettings.commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")

lazy val exercise_001_the_actor_lifecycle = project
  .settings(CommonSettings.commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")

lazy val exercise_002_actor_supervision = project
  .settings(CommonSettings.commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")
       