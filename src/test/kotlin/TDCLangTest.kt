
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TDCLangTest {


    @ParameterizedTest
    @MethodSource("logicalExpressionTest")
    fun `test executeLogicalExpression should return a boolean`(
        expression: String,
        expectedOutput: Boolean
    ) {

        val sessionData = mapOf(
            "variable1" to "2",
            "variable2" to "entry2Value"
        )
        val formNodeProcessor = ConversationalLanguage()
        val output = formNodeProcessor.executeLogicalExpression(expression, sessionData)

        assertEquals(expectedOutput, output)

    }

    @ParameterizedTest
    @MethodSource("whenExpression")
    fun `test executeWhenExpression should return a boolean`(
        whenExpression: String,
        expectedOutput: String
    ) {
        val eventDetails = mapOf(
            "variable1" to "2",
            "variable2" to "entry2Value"
        )
        val formNodeProcessor = ConversationalLanguage()
        val output = formNodeProcessor.executeWhenExpression(whenExpression, eventDetails)

        assertEquals(expectedOutput, output)
    }

    private fun logicalExpressionTest(): Stream<Arguments> = listOf(
        "variable1 > 2 AND variable2 == \"entry2Value\"" to false,
        "variable1 == 2" to true,
        "variable1 < 2 AND variable2 == \"entry2Value\"" to false,
        "variable1 > 2 AND variable2 == \"INVALID_ENTRY\"" to false,
        "variable1 > 2 OR variable2 == \"entry2Value\"" to true,
        "(variable1 > 2 OR variable2 == \"INVALID_ENTRY\") AND variable1 == 1" to false,
        "variable1 >= 2 OR (variable2 == \"INVALID_ENTRY\" AND variable1 == 1)" to true,
        "variable1 < 2 OR (variable2 == \"INVALID_ENTRY\" AND variable1 == 1)" to false,
        "variable1 == 2 AND (variable2 == \"entry2Value\" OR variable1 == 1)" to true,
        "variable1 >= 2 AND (variable2 == \"INVALID_ENTRY\" AND variable1 == 1)" to false,
        "variable1 >= 2 AND (variable2 == \"entry2Value\" AND variable1 == 2)" to true,
        "variable1 >= 2 AND ((variable2 == \"INVALID_ENTRY\" AND variable1 == 1) OR variable2 == \"entry2Value\")" to true,
        "(variable1 == \"2\" OR variable2 == \"entry2Value\") AND false" to false,
        "(variable1 == \"2\" OR variable2 == \"entry2Value\") AND true" to true,
        "(variable1 == \"2\" OR variable2 == \"entry2Value\") AND (true)" to true,
        "false AND true" to false
    ).map { (lang, out) ->
        Arguments.of(lang, out)
    }.stream()


    private fun whenExpression(): Stream<Arguments> = listOf(
        "WHEN { variable1 == 2 -> \"RESULT_1\"; variable1 < 2 -> \"RESULT_3\"; else ->  \"RESULT_4\"}" to "RESULT_1",
        "WHEN { variable1 < 2 -> \"RESULT_1\"; variable1 > 2 -> \"RESULT_2\";  variable1 == 2 -> \"RESULT_3\"; else ->  \"RESULT_4\"}" to "RESULT_3",
        "WHEN { variable1 < 2 -> \"RESULT_1\"; variable3 < 2 -> \"RESULT_2\"; variable3 < 2 -> \"RESULT_3\"; else ->  \"RESULT_4\"}" to "RESULT_4"
    ).map { (lang, out) ->
        Arguments.of(lang, out)
    }.stream()

}