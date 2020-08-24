import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree


class ConversationalLanguage(private val parserExceptionListener: ParserExceptionListener = ParserExceptionListener()) {

    fun executeWhenExpression(
        whenExpression: String,
        variables: Map<String, String>
    ): String? {
        return when (val result = executeGenericExpression(whenExpression, variables)) {
            is BooleanValueContainer -> null
            is StringValueContainer -> result.value
            EmptyValueContainer -> null
        }
    }

    fun executeLogicalExpression(
        logicalExpression: String,
        variables: Map<String, String>
    ): Boolean? {
        return when (val result = executeGenericExpression(logicalExpression, variables)) {
            is BooleanValueContainer -> result.value
            is StringValueContainer -> null
            EmptyValueContainer -> null
        }
    }

    fun executeGenericExpression(
        expression: String,
        variables: Map<String, String>
    ): ValueContainer {
        val parser = getParser(expression)
        val langImplementation = TDCLangGrammarImplementation(variables)
        return langImplementation.visit(parser)
    }

    private fun getParser(expression: String): ParseTree {
        val lexer = TDCLangLexer(CharStreams.fromString(expression))
        val tokens = CommonTokenStream(lexer)
        val parser = TDCLangParser(tokens)
        parser.removeErrorListeners()
        parser.addErrorListener(parserExceptionListener)
        return parser.generalExpression()
    }
}