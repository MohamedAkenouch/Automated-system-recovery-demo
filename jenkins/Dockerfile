FROM jenkins/jenkins:2.426.1-lts-jdk17

USER root

# Install necessary tools
RUN apt-get update && apt-get install -y sudo

# Optional: Set up passwordless sudo for the jenkins user
RUN echo 'jenkins ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

# Install Docker client cli
RUN apt-get update && \
    apt-get -y install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg2 \
    software-properties-common && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add - && \
    apt-key fingerprint 0EBFCD88 && \
    add-apt-repository \
    "deb [arch=amd64] https://download.docker.com/linux/debian \
    $(lsb_release -cs) \
    stable" && \
    apt-get update && \
    apt-get -y install docker-ce-cli

# Install Docker Compose dependencies
RUN apt-get update && \
    apt-get install -y \
    curl \
    python3-pip

# Install Docker Compose
RUN curl -L "https://github.com/docker/compose/releases/download/$(curl -s https://api.github.com/repos/docker/compose/releases/latest | grep 'tag_name' | cut -d\" -f4)/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose \
    && chmod +x /usr/local/bin/docker-compose

# create docker group
RUN groupadd -g 1001 docker

# add jenkins user to docker group
RUN usermod -aG docker jenkins

USER jenkins