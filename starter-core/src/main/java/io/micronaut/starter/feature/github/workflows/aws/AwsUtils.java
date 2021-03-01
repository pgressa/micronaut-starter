/*
 * Copyright 2017-2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.starter.feature.github.workflows.aws;

import io.micronaut.starter.application.Project;
import io.micronaut.starter.options.BuildTool;
import io.micronaut.starter.options.JdkVersion;

import javax.validation.constraints.NotNull;

/**
 * Utils function used in templates.
 *
 * @author Pavol Gressa
 * @since 2.4
 */
public class AwsUtils {
    public static String runtimeVersion(@NotNull JdkVersion jdkVersion) {
        if (jdkVersion.majorVersion() == 8) {
            return "java8";
        } else {
            return "java11";
        }
    }

    public static String runnablePath(@NotNull Project project, @NotNull BuildTool buildTool, boolean isGraalVM){
        if(isGraalVM){
            if(buildTool == BuildTool.MAVEN){
                return BuildTool.MAVEN.getJarDirectory() + "/" + project.getName();
            }else{
                return "build/native-image/application";
            }
        }else{
            return buildTool.getJarDirectory() + "/" + project.getName() + "-all.jar";
        }

    }
}
