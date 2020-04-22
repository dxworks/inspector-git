import groovy.lang.Binding
import groovy.lang.GroovyShell
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.StringWriter

class GroovyScriptTest {

    @Test
    fun `test simple scripts work`() {
        val shell = GroovyShell()

        assertEquals(5, shell.evaluate("2+3"))
    }

    @Test
    internal fun `test binding works`() {

        val binding = Binding()
        val stringWriter = StringWriter()
        binding.setVariable("log", stringWriter)
        val shell = GroovyShell(binding)

        shell.evaluate("log.println('Hello World!')")

        assertEquals("Hello World!", stringWriter.toString().trim())
    }

    @Test
    fun `test regex`() {
        val taskRegex = "(\\b|'|\")asd-\\d+(\\b|\"|')".toRegex()
        val toMatch = listOf("asd-23", "asd-23 nkjflds", "ksjalkdj asd-23", "asd- asd-23 asd-jdk", "dsafjaskd 'asd-23'", " nkjansd'asd-123' jdsank jnd asd-34")

        val flatMap = toMatch.flatMap { taskRegex.findAll(it).toList().map { it.value } }
        assertEquals(7, flatMap.size)
    }
}