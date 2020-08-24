import org.antlr.v4.runtime.tree.TerminalNode

class TDCLangGrammarImplementation(
    private val variables: Map<String, String>
) : TDCLangBaseVisitor<ValueContainer>() {

    override fun visitCompareEqualsNumber(ctx: TDCLangParser.CompareEqualsNumberContext): ValueContainer {
        val value = ctx.INT().text?.toInt() ?: 0
        val toCompare = getElementValue(ctx.ID())?.toIntOrNull()
        return executeNumberOperation(value, toCompare, ctx.EQ())
    }

    override fun visitCompareEqualsString(ctx: TDCLangParser.CompareEqualsStringContext): ValueContainer {
        val toCompare = getElementValue(ctx.ID())
        val value = ctx.String().textWithoutQuotes()
        return BooleanValueContainer(value == toCompare)
    }

    override fun visitCompareNumber(ctx: TDCLangParser.CompareNumberContext): ValueContainer {
        val value = ctx.INT().text?.toIntOrNull() ?: 0
        val toCompare = getElementValue(ctx.ID())?.toInt()
        return executeNumberOperation(value, toCompare, ctx.NumberComparators())
    }

    override fun visitExpression(ctx: TDCLangParser.ExpressionContext): ValueContainer {
        return when {
            ctx.FALSE() != null ->
                BooleanValueContainer(false)
            ctx.TRUE() != null ->
                BooleanValueContainer(true)
            else ->
                visit(ctx.logicalExpression())
        }
    }

    override fun visitLogicalAndComparison(ctx: TDCLangParser.LogicalAndComparisonContext):
            ValueContainer {
        return if (ctx.logicalAndComparison() != null) {
            when (val x1 = visit(ctx.logicalAndComparison())) {
                is BooleanValueContainer ->
                    if (!x1.value) x1 else visit(ctx.compareExpression())
                else -> BooleanValueContainer(false)
            }
        } else {
            visit(ctx.compareExpression())
        }
    }

    override fun visitLogicalORComparison(ctx: TDCLangParser.LogicalORComparisonContext): ValueContainer {
        return if (ctx.logicalORComparison() != null) {
            when (val x1 = visit(ctx.logicalORComparison())) {
                is BooleanValueContainer ->
                    if (x1.value) x1 else visit(ctx.logicalAndComparison())
                else -> BooleanValueContainer(false)
            }
        } else {
            visit(ctx.logicalAndComparison())
        }
    }

    override fun visitLogicalExpression(ctx: TDCLangParser.LogicalExpressionContext): ValueContainer {
        return visit(ctx.logicalORComparison())
    }

    override fun visitWhenLogicalCondition(ctx: TDCLangParser.WhenLogicalConditionContext): ValueContainer {

        return when (val currentValue = visit(ctx.logicalExpression())) {
            is BooleanValueContainer -> {
                val condition = currentValue.value
                if (condition) StringValueContainer(ctx.String().textWithoutQuotes()) else EmptyValueContainer
            }
            else -> EmptyValueContainer
        }
    }

    override fun visitWhenLogical(ctx: TDCLangParser.WhenLogicalContext): ValueContainer {
        return ctx.whenLogicalCondition().asSequence().map { visit(it) }.firstOrNull { it != EmptyValueContainer }
            ?: StringValueContainer(
                ctx.String().textWithoutQuotes()
            )
    }

    override fun visitToExpression(ctx: TDCLangParser.ToExpressionContext): ValueContainer {
        return visit(ctx.expression())
    }

    private fun executeNumberOperation(
        value: Int,
        toCompare: Int?,
        numberComparators: TerminalNode
    ): BooleanValueContainer {
        val result = if (toCompare != null) {
            when (numberComparators.text) {
                ">" -> toCompare > value
                ">=" -> toCompare >= value
                "<" -> toCompare < value
                "<=" -> toCompare <= value
                else -> toCompare == value
            }
        } else {
            false
        }
        return BooleanValueContainer(result)
    }

    override fun visitGeneralExpression(ctx: TDCLangParser.GeneralExpressionContext): ValueContainer {
        return when {
            ctx.logicalExpression() != null -> visit(ctx.logicalExpression())
            else -> visit(ctx.whenExpression())
        }
    }

    private fun TerminalNode.textWithoutQuotes(): String = this.text.replace("\"", "")
    private fun getElementValue(element: TerminalNode): String? =
        variables[element.text]

}