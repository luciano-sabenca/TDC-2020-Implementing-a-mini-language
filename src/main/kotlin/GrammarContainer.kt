
sealed class ValueContainer
data class BooleanValueContainer(val value: Boolean) : ValueContainer()
data class StringValueContainer(val value: String) : ValueContainer()
object EmptyValueContainer : ValueContainer()