/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"));
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

configurations {
    css {
        // define a configuration that, when resolved, will look in
        // the producer for a publication that exposes CSS resources
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE,(project.objects.named(Usage, "css-resources")))
        }
        canBeResolved = true
        canBeConsumed = false
    }
}

dependencies {
    compile libraries.groovy.coordinates

    compile(project(":resources"))
    compile(project(":core"))
    compile(project(":toolingApi"))
    compile(project(":reporting"))
    compile(project(":plugins"))
    compile(project(":ear"))
    compile(libraries.guava.coordinates)
    compile(libraries.slf4j_api.coordinates)

    testCompile(testLibraries.jsoup)

    integTestRuntime(project(":toolingApiBuilders"))

    css(project(":docs"))
}

testFixtures {
    from(":core")
}

processResources {
    into "org/gradle/api/plugins/buildcomparison/render/internal/html", {
        from configurations.css
        include "base.css"
    }
}
