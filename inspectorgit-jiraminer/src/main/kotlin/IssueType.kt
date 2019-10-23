import com.fasterxml.jackson.annotation.JsonProperty


data class IssueType(
        var name: String?,
        var description: String?,
        @JsonProperty("subtask")
        var isSubTask: Boolean)