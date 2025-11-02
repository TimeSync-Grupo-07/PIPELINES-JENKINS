import jenkins.model.*
import org.jenkinsci.plugins.workflow.job.*
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition

println "🔧 Iniciando criação automática de pipelines a partir da pasta /pipelines..."

def jenkins = Jenkins.instance
def pipelinesDir = new File("/var/jenkins_home/pipelines")

if (!pipelinesDir.exists() || !pipelinesDir.isDirectory()) {
    println "⚠️  Diretório /pipelines não encontrado ou inválido. Nenhum job será criado."
    return
}

pipelinesDir.eachFileMatch(~/.*\.jenkinsfile$/) { file ->
    def jobName = file.name.replaceAll(/\.jenkinsfile$/, "")
    def job = jenkins.getItem(jobName)

    if (job == null) {
        println "🧱 Criando job: ${jobName}"

        def pipelineScript = file.text
        def newJob = new WorkflowJob(jenkins, jobName)
        newJob.definition = new CpsFlowDefinition(pipelineScript, true)

        jenkins.add(newJob, jobName)
        newJob.save()

        println "✅ Job '${jobName}' criado com sucesso."
    } else {
        println "ℹ️ Job '${jobName}' já existe. Pulando criação."
    }
}

jenkins.save()
println "🏁 Criação automática de pipelines finalizada."