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

        shell.evaluate("log.println('Hello World!')");

        assertEquals("Hello World!", stringWriter.toString().trim())
    }


}