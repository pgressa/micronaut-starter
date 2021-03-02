/*
 * Copyright 2017-2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.starter.feature.github.workflows.aws;

import io.micronaut.starter.application.generator.GeneratorContext;
import io.micronaut.starter.feature.FeatureContext;
import io.micronaut.starter.feature.FeaturePhase;
import io.micronaut.starter.feature.awslambdacustomruntime.AwsLambdaCustomRuntime;
import io.micronaut.starter.feature.function.awslambda.AwsLambda;
import io.micronaut.starter.feature.github.workflows.GitHubWorkflowFeature;
import io.micronaut.starter.feature.github.workflows.Secret;
import io.micronaut.starter.feature.github.workflows.aws.template.awsLambdaCliWorkflow;
import io.micronaut.starter.feature.github.workflows.aws.template.awsLambdaCliWorkflowReadme;
import io.micronaut.starter.feature.server.ServerFeature;
import io.micronaut.starter.template.RockerTemplate;
import io.micronaut.starter.template.RockerWritable;

import java.util.Arrays;
import java.util.List;

/**
 * Deploy GraalVM native image to Azure Container Instance.
 *
 * @author Pavol Gressa
 * @since 2.4
 */
public abstract class AbstractAwsLambdaWorkflow extends GitHubWorkflowFeature {

    public static final String NAME = "github-workflow-aws-lambda-cli-java";
    public static final String AWS_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID";
    public static final String AWS_SECRET_ACCESS_KEY = "AWS_SECRET_ACCESS_KEY";
    public static final String AWS_ROLE_ARN = "AWS_ROLE_ARN";

    public static final String AWS_DEFAULT_REGION = "eu-central-1";
    public static final String AWS_DEFAULT_FUNCTION_TIMEOUT = "30";
    public static final String AWS_DEFAULT_FUNCTION_MEMORY = "512";

    private final boolean isGraalVM;
    private final AwsLambda awsLambda;

    public AbstractAwsLambdaWorkflow(boolean isGraalVM, AwsLambda awsLambda) {
        this.isGraalVM = isGraalVM;
        this.awsLambda = awsLambda;
    }

    @Override
    public List<Secret> getSecrets() {
        return Arrays.asList(
                new Secret(AWS_ACCESS_KEY_ID, "AWS Access Key Id."),
                new Secret(AWS_SECRET_ACCESS_KEY, "AWS Secret Access Key."),
                new Secret(AWS_ROLE_ARN, "AWS Role ARN under which the Lambda function runs.")
        );
    }

    @Override
    public void processSelectedFeatures(FeatureContext featureContext) {
        if (!featureContext.isPresent(AwsLambda.class)) {
            featureContext.addFeature(awsLambda);
        }

        // Remove default server feature
        if(featureContext.isPresent(ServerFeature.class)){
            featureContext.exclude( x -> x instanceof ServerFeature);
        }
    }

    public void apply(GeneratorContext generatorContext) {
        super.apply(generatorContext);

        String workflowFilePath = ".github/workflows/" + getWorkflowFileName(generatorContext);

        generatorContext.addTemplate("javaWorkflow",
                new RockerTemplate(workflowFilePath,
                        awsLambdaCliWorkflow.template(generatorContext.getProject(),  generatorContext.getBuildTool(),
                                generatorContext.getJdkVersion(), isGraalVM)
                )
        );

        generatorContext.addHelpTemplate(new RockerWritable(
                awsLambdaCliWorkflowReadme.template(this, generatorContext.getProject(),
                        workflowFilePath)));
    }
}
