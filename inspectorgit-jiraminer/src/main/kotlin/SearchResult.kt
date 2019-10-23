data class SearchResult(
        var startIndex: Int,
        var maxResults: Int,
        var total: Int,
        var issues: List<Issue>?)