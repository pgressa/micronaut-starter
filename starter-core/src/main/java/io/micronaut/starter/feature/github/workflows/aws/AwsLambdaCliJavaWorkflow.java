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

import io.micronaut.core.annotation.NonNull;
import io.micronaut.starter.application.generator.GeneratorContext;
import io.micronaut.starter.feature.FeatureContext;
import io.micronaut.starter.feature.awslambdacustomruntime.AwsLambdaCustomRuntime;
import io.micronaut.starter.feature.function.awslambda.AwsLambda;

import javax.inject.Singleton;

/**
 * Deploy Micronaut application as AWS Lambda function using the AWS CLI.
 *
 * @author Pavol Gressa
 * @since 2.4
 */
@Singleton
public class AwsLambdaCliJavaWorkflow extends AbstractAwsLambdaWorkflow {

    public static final String NAME = "github-workflow-aws-lambda-cli-java";

    public AwsLambdaCliJavaWorkflow(AwsLambda awsLambda) {
        super(false, awsLambda);
    }

    @NonNull
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getTitle() {
        return "AWS Lambda Workflow (CLI)";
    }

    @Override
    public String getDescription() {
        return "Adds a GitHub Actions Workflow that deploys an micronaut application to AWS Lambda using AWS cli.";
    }

    public String getWorkflowFileName(GeneratorContext generatorContext) {
        return "aws-lambda-cli-java.yml";
    }
}
