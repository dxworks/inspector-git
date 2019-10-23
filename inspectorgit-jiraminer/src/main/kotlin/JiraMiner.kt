import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.util.Assert.notNull
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.HashMap

private const val API_BASE = "/rest/api/2/"

private const val JIRA_ISSUES = "Jira Issues"
private val FIELDS = listOf("issuetype", "created", "updated", "status", "parent", "components", "summary", "description")
private const val JIRA_URL_FIELD = "url"
private const val JIRA_PROJECT_FIELD = "project"
private const val JIRA_AUTHENTICATION_FIELD = "authentication"
private const val JIRA_PASSWORD_FIELD = "password"
private const val JIRA_USERNAME_FIELD = "username"


@Service
class JiraMiner {
    companion object {
        private val LOG = LoggerFactory.getLogger(JiraMiner::class.java)
    }

    private val restTemplate = RestTemplate()

    @Synchronized
    private fun validateJiraProject(jiraProject: String, jiraURL: URI?, headersHttpEntity: HttpEntity<HttpHeaders>): Boolean {
        val response = restTemplate.exchange(jiraURL.toString() + API_BASE + "projectvalidate/key?key=" + jiraProject,
                HttpMethod.GET,
                headersHttpEntity, ProjectValidateDTO::class.java)
        val body = response.body
        if (body != null) {
            val errors = body.errors
            if (errors != null) {
                return errors.projectKey!!.matches("Project '[^']*' uses this project key\\.".toRegex())
            }
        }
        return false
    }

    fun getIssuesForConfiguration(configurationMap: Map<String, String>): JiraLogDTO {
        val jiraProjectConfigurationDTO = extractConfigurationFields(configurationMap)
        val jiraURL = jiraProjectConfigurationDTO.jiraURL
        val jiraUsername = jiraProjectConfigurationDTO.jiraUsername
        val jiraPassword = jiraProjectConfigurationDTO.jiraPassword

        var jiraServerURI: URI? = null
        try {
            jiraServerURI = URI(jiraURL!!)
        } catch (e: URISyntaxException) {
            LOG.error("Can not create URL $jiraURL", e)
        }

        val httpHeaders = getHttpHeaders(jiraUsername ?: "", jiraPassword ?: "")

        val jiraProjects = getJiraProjects(jiraProjectConfigurationDTO.jiraProject ?: "", jiraServerURI, httpHeaders)
        val allIssues = ArrayList<Issue>()

        val jiraJqlLink = jiraServerURI.toString() + API_BASE + "search"
        val jiraJqlUrl = URI.create(jiraJqlLink)

        var startAt = 0
        val maxResults = 500
        var total: Int
        for (projectName in jiraProjects) {
            do {
                val searchResult = searchIssues(jiraJqlUrl, "project=\"$projectName\"", maxResults, startAt, httpHeaders)

                searchResult?.issues?.let { allIssues.addAll(it) }

                startAt += maxResults
                total = searchResult?.total!!
            } while (startAt < total)
        }

        val jiraIssueDTOS = createJiraIssueDTOSFromIssues(allIssues)
        return JiraLogDTO(jiraIssueDTOS)
    }

    private fun getHttpHeaders(jiraUsername: String, jiraPassword: String): HttpHeaders {
        val httpHeaders = HttpHeaders()
        httpHeaders.set("Authorization",
                "Basic " + Base64.getEncoder().encodeToString("$jiraUsername:$jiraPassword".toByteArray()))
        httpHeaders.set("Accept", "application/json")

        return httpHeaders
    }

    @Synchronized
    private fun searchIssues(jiraUrl: URI, jql: String, maxResults: Int, startAt: Int, httpHeaders: HttpHeaders): SearchResult? {
        return restTemplate.exchange(jiraUrl, HttpMethod.POST,
                HttpEntity(JiraIssuesRequestBodyDTO(jql, startAt, maxResults, FIELDS), httpHeaders),
                SearchResult::class.java).body
    }

    private fun getJiraProjects(jiraProjects: String, jiraURL: URI?,
                                httpHeaders: HttpHeaders): List<String> {
        val projects = jiraProjects.split(",").dropLastWhile { it.isEmpty() }
        val headersHttpEntity: HttpEntity<HttpHeaders> = HttpEntity(httpHeaders)
        return projects.map { it.trim() }.filter { validateJiraProject(it, jiraURL, headersHttpEntity) }
    }

    private fun extractConfigurationFields(configurationMap: Map<String, String>): JiraProjectDetailsDTO {
        val jiraUrl = configurationMap[JIRA_URL_FIELD]
        val jiraProject = configurationMap[JIRA_PROJECT_FIELD]

        notNull(jiraUrl, "Jira URL can not be null")
        notNull(jiraProject, "Jira Project can not be null")

        val jiraAuthentication = java.lang.Boolean.parseBoolean(configurationMap[JIRA_AUTHENTICATION_FIELD])

        var jiraUsername: String? = null
        var jiraPassword: String? = null

        if (jiraAuthentication) {
            jiraUsername = configurationMap[JIRA_USERNAME_FIELD]
            jiraPassword = configurationMap[JIRA_PASSWORD_FIELD]
        }

        return JiraProjectDetailsDTO(jiraUrl, jiraUsername, jiraPassword, jiraProject)
    }

    private fun createJiraIssueDTOSFromIssues(allIssues: Collection<Issue>): List<JiraIssueDTO> {
        return allIssues.map { issue ->
            val fields = issue.fields!!
            JiraIssueDTO(key = issue.key,
                    issueType = fields.issuetype?.name,
                    status = fields.status?.name,
                    startDate = fields.created,
                    endDate = fields.updated,
                    parentKey = getParentKey(issue),
                    summary = fields.summary,
                    description = fields.description,
                    components = getComponentNames(fields)
            )
        }
    }

    private fun getComponentNames(fields: IssueFields): List<String> {
        return fields.components!!.mapNotNull { it.name }
    }

    private fun getParentKey(issue: Issue): String? {
        val parent = issue.fields!!.parent
        return parent?.key
    }
}


fun main() {
    val jiraMiner = JiraMiner()
    val configMap = HashMap<String, String>()
    configMap[JIRA_URL_FIELD] = "https://jira.dialogdata.de"
    configMap[JIRA_PROJECT_FIELD] = "DXPF"
    configMap[JIRA_AUTHENTICATION_FIELD] = "true"
    configMap[JIRA_USERNAME_FIELD] = "DNA"
    configMap[JIRA_PASSWORD_FIELD] = "!DialogData123"
    val issuesForConfiguration = jiraMiner.getIssuesForConfiguration(configMap)

    println(issuesForConfiguration)
}
