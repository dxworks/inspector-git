data class JiraIssuesRequestBodyDTO(
        var jql: String?,
        var startAt: Int,
        var maxResults: Int,
        var fields: List<String>?)