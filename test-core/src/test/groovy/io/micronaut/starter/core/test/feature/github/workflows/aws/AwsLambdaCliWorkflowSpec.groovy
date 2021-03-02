package io.micronaut.starter.core.test.feature.github.workflows.aws

import groovy.json.JsonSlurper
import io.micronaut.starter.feature.github.workflows.Secret
import io.micronaut.starter.feature.github.workflows.aws.AbstractAwsLambdaWorkflow
import io.micronaut.starter.feature.github.workflows.aws.AwsLambdaCliGraalWorkflow
import io.micronaut.starter.feature.github.workflows.aws.AwsLambdaCliJavaWorkflow
import io.micronaut.starter.options.BuildTool
import io.micronaut.starter.options.Language
import io.micronaut.starter.test.github.WorkflowSpec
import io.micronaut.starter.util.NameUtils
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.lambda.LambdaClient
import software.amazon.awssdk.services.lambda.model.GetFunctionRequest
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse
import software.amazon.awssdk.services.lambda.model.InvokeRequest
import software.amazon.awssdk.services.lambda.model.InvokeResponse
import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Unroll

import java.util.stream.Collectors

/**
 * This integration spec contains tests for {@link AbstractAwsLambdaWorkflow} for both build tools.
 * To run this spec env variable required to be in env which are afterwards automatically created as GitHub Secrets,
 * see {@link io.micronaut.starter.core.test.feature.github.workflows.aws.AwsLambdaCliWorkflowSpec#envVariables()}
 */
@Requires({
    envVariables().stream().allMatch { envVar -> System.getenv().containsKey(envVar) } &&  \
    jvm.isJava11()
})

class AwsLambdaCliWorkflowSpec extends WorkflowSpec {

    static List<String> envVariables() {
        return Arrays.asList(
                AbstractAwsLambdaWorkflow.AWS_ACCESS_KEY_ID,
                AbstractAwsLambdaWorkflow.AWS_SECRET_ACCESS_KEY,
                AbstractAwsLambdaWorkflow.AWS_ROLE_ARN,
                GH_TOKEN
        )
    }

    @Shared
    private List<Secret> secrets

    @Shared
    private jsonSlurper = new JsonSlurper()

    @Override
    String getTempDirectoryPrefix() {
        return "AwsLambdaCliWorkflowSpec"
    }


    void setupSpec() {
        secrets = envVariables().stream()
                .map { envVar -> secretFromEnvVariable(envVar) }
                .collect(Collectors.toList())
    }

    @Unroll
    void "test aws lambda cli #buildTool graalvm workflow "(BuildTool buildTool) {
        given:
        def project = NameUtils.parse("com.example.aws-lambda-cli-${buildTool.name.toLowerCase()}-graalvm-test")

        when:
        generateProject(project, Language.JAVA, buildTool, [AwsLambdaCliGraalWorkflow.NAME])
        pushToGitHub(project, secrets)
        workflowPassed(project, 15 * 60)

        then:
        noExceptionThrown()
        !invokeFunction(project.getName()).functionError()

        cleanup:
        //cleanupGitHubRepository(project)

        where:
        buildTool << [BuildTool.MAVEN, BuildTool.GRADLE]
    }

    @Unroll
    void "test aws lambda cli #buildTool java workflow "(BuildTool buildTool) {
        given:
        def project = NameUtils.parse("com.example.aws-lambda-cli-${buildTool.name.toLowerCase()}-java-test")

        when:
        generateProject(project, Language.JAVA, buildTool, [AwsLambdaCliJavaWorkflow.NAME])
        pushToGitHub(project, secrets)
        workflowPassed(project, 5 * 60)

        then:
        noExceptionThrown()
        with(invokeFunction(project.getName())){
            statusCode() == 200
            ((String)jsonSlurper.parseText(payload().asUtf8String())["body"]).contains("isbn")
        }

        cleanup:
        //cleanupGitHubRepository(project)

        where:
        buildTool << [BuildTool.GRADLE, BuildTool.MAVEN]
    }

    InvokeResponse invokeFunction(String functionName){
        Region region = Region.of(AbstractAwsLambdaWorkflow.AWS_DEFAULT_REGION)
        LambdaClient client = LambdaClient.builder().region(region).credentialsProvider().build()
        return client.invoke(InvokeRequest.builder()
                .functionName(functionName)
                .payload(SdkBytes.fromUtf8String(PAYLOAD)).build())
    }

    private static final String PAYLOAD = """
{
  "body": "{\\"name\\":\\"vajco\\"}",
  "resource": "/",
  "path": "/",
  "httpMethod": "POST",
  "isBase64Encoded": false,
  "queryStringParameters": {},
  "multiValueQueryStringParameters": {},
  "pathParameters": {},
  "stageVariables": {},
  "headers": {
    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
    "Accept-Encoding": "gzip, deflate, sdch",
    "Accept-Language": "en-US,en;q=0.8",
    "Cache-Control": "max-age=0",
    "CloudFront-Forwarded-Proto": "https",
    "CloudFront-Is-Desktop-Viewer": "true",
    "CloudFront-Is-Mobile-Viewer": "false",
    "CloudFront-Is-SmartTV-Viewer": "false",
    "CloudFront-Is-Tablet-Viewer": "false",
    "CloudFront-Viewer-Country": "US",
    "Host": "1234567890.execute-api.eu-central-1.amazonaws.com",
    "Upgrade-Insecure-Requests": "1",
    "User-Agent": "Custom User Agent String",
    "Via": "1.1 08f323deadbeefa7af34d5feb414ce27.cloudfront.net (CloudFront)",
    "X-Amz-Cf-Id": "cDehVQoZnx43VYQb9j2-nvCh-9z396Uhbp027Y2JvkCPNLmGJHqlaA==",
    "X-Forwarded-For": "127.0.0.1, 127.0.0.2",
    "X-Forwarded-Port": "443",
    "X-Forwarded-Proto": "https"
  }
}
"""

//
//    @Unroll
//    void "test azure container instance #buildTool workflow"(BuildTool buildTool) {
//        given:
//        def project = NameUtils.parse("com.example.azure-container-instance-${buildTool.name.toLowerCase()}-test")
//
//        when:
//        generateProject(project, Language.JAVA, buildTool, [AzureContainerInstanceJavaWorkflow.NAME])
//        pushToGitHub(project, secrets)
//        workflowPassed(project, 15 * 60)
//
//        then:
//        noExceptionThrown()
//        invokeRestApi(project) == "Example Response"
//
//        cleanup:
//        cleanupGitHubRepository(project)
//
//        where:
//        buildTool << [BuildTool.MAVEN, BuildTool.GRADLE]
//    }
}
