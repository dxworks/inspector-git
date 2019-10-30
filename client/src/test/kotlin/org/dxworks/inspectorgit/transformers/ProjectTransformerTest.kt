package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.dto.ProjectDTO
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.utils.FileSystemUtils
import org.dxworks.inspectorgit.utils.JsonUtils
import org.junit.Test
import kotlin.test.assertEquals


internal class ProjectTransformerTest {
    @Test
    fun `test on local kafka repo`() {
        val project = ProjectTransformer(JsonUtils.jsonFromFile(FileSystemUtils.getDtoFileFor("kafka", "trunk"), ProjectDTO::class.java), "kafka").transform()
        assertEquals(-610134754, getProjectHash(project))
    }

    private fun getProjectHash(project: Project): Int {
        val filesHash = project.fileRegistry.all.map { it.hashCode() }.toIntArray().sum()
        val commitsHash = project.commitRegistry.all.map { it.id.hashCode() }.toIntArray().sum()
        val authorsHash = project.authorRegistry.all.map { it.id.hashCode() }.toIntArray().sum()
        return filesHash + authorsHash + commitsHash
    }
}