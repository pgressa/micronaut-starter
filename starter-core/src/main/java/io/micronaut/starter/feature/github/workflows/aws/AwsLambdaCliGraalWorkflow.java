/*
 * Copyright 2017-2020 original authors
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

import io.micronaut.core.annotation.NonNull;
import io.micronaut.starter.application.generator.GeneratorContext;
import io.micronaut.starter.feature.FeatureContext;
import io.micronaut.starter.feature.awslambdacustomruntime.AwsLambdaCustomRuntime;
import io.micronaut.starter.feature.function.awslambda.AwsLambda;
import io.micronaut.starter.feature.github.workflows.azure.AbstractAzureContainerInstanceWorkflow;
import io.micronaut.starter.feature.server.Netty;
import io.micronaut.starter.feature.server.ServerFeature;

import javax.inject.Singleton;

/**
 * Deploy GraalVM native image as AWS Lambda function.
 *
 * @author Pavol Gressa
 * @since 2.4
 */
@Singleton
public class AwsLambdaCliGraalWorkflow extends AbstractAwsLambdaWorkflow {

    public static final String NAME = "github-workflow-aws-lambda-cli-graalvm";

    private final AwsLambdaCustomRuntime awsLambdaCustomRuntime;

    public AwsLambdaCliGraalWorkflow(AwsLambda awsLambda, AwsLambdaCustomRuntime awsLambdaCustomRuntime) {
        super(true, awsLambda);
        this.awsLambdaCustomRuntime = awsLambdaCustomRuntime;
    }

    @NonNull
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getTitle() {
        return "AWS Lambda GraalVM Workflow (CLI)";
    }

    @Override
    public String getDescription() {
        return "Adds a GitHub Actions Workflow that deploys an micronaut application as GraalVM image to AWS Lambda using AWS cli.";
    }

    @Override
    public void processSelectedFeatures(FeatureContext featureContext) {
        super.processSelectedFeatures(featureContext);
        if (!featureContext.isPresent(AwsLambdaCustomRuntime.class)) {
            featureContext.addFeature(awsLambdaCustomRuntime);
        }
    }

    public String getWorkflowFileName(GeneratorContext generatorContext) {
        return "aws-lambda-cli-graalvm.yml";
    }
}
