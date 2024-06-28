#!groovy

def withDockerNetwork(Closure inner) {
	def networkId = "empty"
	try {
		networkId = env.JOB_NAME + "_" + UUID.randomUUID().toString()
		sh "echo ${networkId}"
		sh "docker network create --ipv6 ${networkId}"
		inner.call(networkId)
	} finally {
		sh "echo removing ${networkId}"
		sh "docker network rm ${networkId}"
	}
}

pipeline {
	agent any
	stages {
		stage('Checkout') {
			steps {
				checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'bemyndigelsesregister']], submoduleCfg: [], userRemoteConfigs: [[url: 'git@github.com:trifork/bemyndigelsesregister.git']]])
				script {
					GIT_COMMIT = sh(returnStdout: true, script: "cd bemyndigelsesregister && git log -n 1 --pretty=format:'%h'").trim()
					echo GIT_COMMIT
					GIT_BRANCH = 'origin/master'
				}
			}
		}

		stage('build') {
			steps {
				script {
					String jenkinsUserId = sh(returnStdout: true, script: 'id -u jenkins').trim()
					String dockerGroupId = sh(returnStdout: true, script: 'getent group docker | cut -d: -f3').trim()
					String containerUserMapping = "-u $jenkinsUserId:$dockerGroupId "
					withDockerNetwork { n ->
							docker.image("registry.fmk.netic.dk/fmk/fmkbuilder:17").inside(containerUserMapping + "--network ${n} --add-host ci.fmk.netic.dk:2a03:dc80:0:f12d::118 --add-host registry.fmk.netic.dk:2a03:dc80:0:f12d::118 --add-host f.aia.systemtest19.trust2408.com:2a03:dc80:0:f12d::120 --add-host f.aia.ica02.trust2408.com:2a03:dc80:0:f12d::120 --add-host registry.npmjs.org:2606:4700::6810:1923 --add-host nodejs.org:2606:4700:10::6814:162e --add-host registry.bower.io:2400:cb00:2048:1::6818:69ac --add-host github.com:64:ff9b::8c52:7603 --add-host github-production-release-asset-2e65be.s3.amazonaws.com:64:ff9b::34d8:ab93 -e _JAVA_OPTIONS='-Dfile.encoding=UTF-8 -Djava.net.preferIPv4Stack=false -Djava.net.preferIPv6Stack=true -Djava.net.preferIPv6Addresses=true' -e GIT_COMMIT=$GIT_COMMIT -e GIT_BRANCH=$GIT_BRANCH -v $HOME/.m2:/home/jenkins/.m2") {
										configFileProvider([configFile(fileId: 'unique_userid', targetLocation: 'uniqueUserid.py')]) {
											userid = sh script: "python3 uniqueUserid.py bemyndigelsesregister", returnStdout: true
											userid = userid.trim()
                                                                                }
                                                                                     configFileProvider([configFile(fileId: 'trifork-ci-fmk-settings', variable: 'MAVEN_SETTINGS')]) {
                                                                                        version = sh script: "cd bemyndigelsesregister && mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true
                                                                                        version = version.trim()
											sh "cd bemyndigelsesregister && mvn -s $MAVEN_SETTINGS --fail-at-end -Pdev,test clean deploy -DargLine='-Dspring.profiles.active=test -Dcatalina.base=${WORKSPACE} -Dftp.enabled=true'"
                                                                                     }
									             withCredentials([usernamePassword(credentialsId: 'ci_docker', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
											sh "echo $DOCKER_PASSWORD | docker login --username $DOCKER_USERNAME --password-stdin registry.fmk.netic.dk"
											bem_image = docker.build("registry.fmk.netic.dk/bemyndigelse/bemyndigelsesregister:$version", "--build-arg VERSION=$version --build-arg USERID=$userid ./bemyndigelsesregister")
											bem_image.push()
											bem_image.push('latest')
                                                                                     }
										}
									}
							}
					}
				}

		stage('Publish testng report') {
			steps {
				step([$class                   : 'Publisher',
					  reportFilenamePattern    : '**/reports/tests/test/testng-results.xml',
					  escapeTestDescp          : true,
					  escapeExceptionMsg       : true,
					  failureOnFailedTestConfig: true,
					  unstableSkips            : 100,
					  unstableFails            : 0,
					  failedSkips              : 100,
					  failedFails              : 100])
			}
		}

	}
}

