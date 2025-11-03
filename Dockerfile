FROM jenkins/jenkins:lts

USER root

# Instalar Docker CLI + AWS CLI + ferramentas básicas
RUN apt-get update && apt-get install -y \
    docker.io \
    awscli \
    zip \
    git \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Criar grupo docker com GID dinâmico (será sobrescrito pelo host)
RUN groupadd -g docker 116 || true
RUN usermod -aG docker jenkins

# Plugins
COPY jenkins_home/plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

# Jenkins Configuration as Code (JCasC)
ENV CASC_JENKINS_CONFIG=/var/jenkins_home/casc.yaml

# Scripts de inicialização
COPY jenkins_home/init.groovy.d/ /usr/share/jenkins/ref/init.groovy.d/

# Pipelines
COPY jenkins_home/pipelines/ /var/jenkins_home/pipelines/

USER jenkins
