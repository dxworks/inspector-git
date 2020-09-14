import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class CsvMapperTest {

    @Test
    fun `csv mapper works`() {
        val csvSchema = CsvSchema.builder()
                .addColumn("date")
                .addColumn("number", CsvSchema.ColumnType.NUMBER)
                .addColumn("id")
                .build().withHeader()
        val output = CsvMapper().registerModule(KotlinModule())
                .writer(csvSchema)
                .writeValueAsString(listOf(
                        mapOf(
                                "date" to ZonedDateTime.now().toString(),
                                "number" to 124,
                                "id" to "asd"
                        ),
                        mapOf(
                                "date" to ZonedDateTime.now().toString(),
                                "number" to 44543,
                                "id" to "ffff"
                        )

                ))
        print(output)
        assert(output.isNotBlank())
    }
}
