# Jenkins Pipeline para Deploy de Lambdas AWS

Este repositório contém uma configuração completa para subir um ambiente Jenkins totalmente automatizado usando Docker, Jenkins Configuration as Code (JCasC) e scripts em Groovy.
O objetivo é permitir criar pipelines automaticamente a partir de arquivos ```.jenkinsfile``` que realizam deploy de código em funções Lambda na AWS.

## Estrutura do repositório

```text
.
├── docker-compose.yml
├── Dockerfile
├── jenkins_home/
│   ├── casc.yml
│   ├── plugins.txt
│   ├── init.groovy.d/
│   │   ├── basic_security.groovy
│   │   └── create-jobs.groovy
│   └── pipelines/
│       ├── deploy_lambda_backup.jenkinsfile
│       ├── deploy_lambda_insert_db.jenkinsfile
│       ├── deploy_lambda_process_raw.jenkinsfile
│       ├── deploy_lambda_process_step_2_raw.jenkinsfile
│       └── deploy_lambda_process_trusted.jenkinsfile
```

## O que esse ambiente faz automaticamente

1. Instala o Jenkins + plugins necessários (BlueOcean, Docker Workflow, Git, JCasC etc.)
2. Configura o Jenkins automaticamente via ```casc.yml```
3. Define usuário e permissões via Groovy
4. Carrega automaticamente pipelines a partir de arquivos ```.jenkinsfile```
5. Inclui Docker CLI e AWS CLI dentro do contêiner
6. Lê as credenciais AWS do host via volume (~/.aws)

## Como Usar

### Pré-requisitos

- Docker e Docker Compose instalados
- Credenciais da AWS configuradas no host:

    ```bash
    ~/.aws/credentials
    ~/.aws/config
    ```
Esses arquivos são montados no container com acesso somente leitura.

### Subindo o Jenkins

Na raiz do repositório:
```docker
docker-compose up -d --build
```

O Jenkins será iniciado e ficará acessível em:
```bash
http://localhost:8080
```

### Credenciais padrão

O usuário é definido pelos scripts Groovy:

- Usuário: ```admin```
- Senha: ```admin```

Recomenda-se alterar isso ao finalizar os testes.

## Como funcionam as pipelines

Todas as pipelines devem ser colocadas em:

```bash
jenkins_home/pipelines/
```

Cada arquivo deve ter extensão:

```bash
nome_da_pipeline.jenkinsfile
```

Na primeira inicialização o Jenkins:

1. Lê todos os .jenkinsfile
2. Cria um job com o nome do arquivo
3. Aplica o conteúdo do pipeline

Se você adicionar novos pipelines depois do container já estar rodando, reinicie-o:

```bash
docker restart jenkins
```

### Deploy para AWS Lambda

As pipelines existentes já assumem que:

- sua conta AWS está acessível pelo volume ~/.aws
- você usa AWS CLI dentro do Jenkins para deploy
- as funções Lambda já existem ou são criadas via pipeline.