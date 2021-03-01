package io.micronaut.starter.feature.github.workflows.aws

import io.micronaut.starter.BeanContextSpec
import io.micronaut.starter.application.ApplicationType
import io.micronaut.starter.feature.github.workflows.azure.AzureContainerInstanceGraalWorkflow
import io.micronaut.starter.feature.github.workflows.azure.AzureContainerInstanceJavaWorkflow
import io.micronaut.starter.fixture.CommandOutputFixture
import io.micronaut.starter.options.*
import io.micronaut.starter.util.VersionInfo
import spock.lang.Unroll

class AwsLambdaCliWorkflowSpec extends BeanContextSpec implements CommandOutputFixture {

    void 'test github java workflow readme'() {
        when:
        def output = generate([AwsLambdaCliJavaWorkflow.NAME])
        def readme = output['README.md']

        then:
        readme
        readme.contains("AWS Lambda Workflow (CLI)")
    }

    void 'test github graalvm workflow readme'() {
        when:
        def output = generate([AwsLambdaCliGraalWorkflow.NAME])
        def readme = output['README.md']

        then:
        readme
        readme.contains("AWS Lambda GraalVM Workflow (CLI)")
    }

    @Unroll
    void 'test java github workflow is created for #buildTool'(BuildTool buildTool, String cmd) {
        when:
        def output = generate(ApplicationType.DEFAULT,
                new Options(Language.JAVA, TestFramework.JUNIT, buildTool, JdkVersion.JDK_11),
                [AwsLambdaCliJavaWorkflow.NAME])
        def workflow = output[".github/workflows/aws-lambda-cli-java.yml"]

        then:
        workflow
        workflow.contains("FUNCTION_NAME: foo")
        workflow.contains("""
      - name: Configure AWS Credentials
        uses: aws-actions/configure-aws-credentials@v1
        with:
          aws-access-key-id: \${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: \${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: \${{ AWS_REGION }}
""")
        workflow.contains(cmd)

        where:
        buildTool | cmd
        BuildTool.MAVEN | "./mvnw package -Dpackaging=jar"
        BuildTool.GRADLE | "./gradlew shadowJar"
    }

    @Unroll
    void 'test graalvm github workflow is created for #buildTool'(BuildTool buildTool, String cmd) {
        when:
        def output = generate(ApplicationType.DEFAULT,
                new Options(Language.JAVA, TestFramework.JUNIT, buildTool, JdkVersion.JDK_11),
                [AwsLambdaCliGraalWorkflow.NAME])
        def workflow = output[".github/workflows/aws-lambda-cli-graalvm.yml"]

        then:
        workflow
        workflow.contains("FUNCTION_NAME: foo")
        workflow.contains("""
      - name: Configure AWS Credentials
        uses: aws-actions/configure-aws-credentials@v1
        with:
          aws-access-key-id: \${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: \${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: \${{ AWS_REGION }}
""")
        workflow.contains(cmd)

        where:
        buildTool | cmd
        BuildTool.MAVEN | "./mvnw package -Dpackaging=native-image"
        BuildTool.GRADLE | "./gradlew nativeImage"
    }


    void 'test maven contains only one runtime'(String workflow) {
        when:
        def output = generate(ApplicationType.DEFAULT,
                new Options(Language.JAVA, TestFramework.JUNIT, BuildTool.MAVEN, JdkVersion.JDK_11),
                [AwsLambdaCliGraalWorkflow.NAME])
        def pom = output["pom.xml"]

        then:
        pom
        pom.contains("<micronaut.runtime>lambda</micronaut.runtime>")
        !pom.contains("<micronaut.runtime>netty</micronaut.runtime>")

        where:
        workflow << [AwsLambdaCliJavaWorkflow.NAME, AwsLambdaCliGraalWorkflow.NAME]
    }
}
