/*
 * Copyright 2012 David M. Carr, Commerce Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import grails.plugin.mongeez.ReflectionsChangeSetFileProvider
import org.mongeez.MongeezRunner

class MongeezGrailsPlugin {
    def version = "0.2.1"
    def grailsVersion = "2.0.1 > *"
    def dependsOn = [:]
    def loadAfter = ['mongodb']
    def pluginExcludes = [
        "grails-app/i18n/messages.properties",
        'grails-app/migrations/**',
        "grails-app/views/index.gsp",
        "src/docs/**"
    ]

    def title = "Mongeez Plugin"
    def author = "David M. Carr"
    def authorEmail = "david@carrclan.us"
    def description = 'A plugin that integrates the Mongeez change management system for MongoDB into Grails.'

    def documentation = "http://davidmc24.bitbucket.org/grails-mongeez"
    def license = "APACHE"
    def issueManagement = [ url: "https://bitbucket.org/davidmc24/grails-mongeez/issues?status=new&status=open" ]
    def scm = [ url: "https://bitbucket.org/davidmc24/grails-mongeez" ]

    def doWithSpring = {
        def mongoBeanName = (
            application.config?.grails?.mongeez?.mongoBean ?:
                manager?.hasGrailsPlugin('mongodb') ? 'mongoBean' : 'mongo'
        )
        def databaseName = (
            application.config?.grails?.mongeez?.databaseName ?:
                application.config?.grails?.mongo?.databaseName ?:
                    application.config?.mongo?.databaseName ?:
                        application.metadata.getApplicationName()
        )
        def updateOnStart = application.config?.grails?.mongeez?.updateOnStart ?: false
        changeSetFileProvider(ReflectionsChangeSetFileProvider)
        'grails.mongeez.MongeezController'(grails.mongeez.MongeezController) { bean ->
            bean.scope = 'prototype'
            bean.autowire = 'byName'
            mongo = ref(mongoBeanName)
        }
        mongeez(MongeezRunner) {
            executeEnabled = updateOnStart
            mongo = ref(mongoBeanName)
            dbName = databaseName
            changeSetFileProvider = ref('changeSetFileProvider')
        }
    }
}
