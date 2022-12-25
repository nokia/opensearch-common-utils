package org.opensearch.commons.alerting.model

import org.opensearch.common.CheckedFunction
import org.opensearch.common.ParseField
import org.opensearch.common.io.stream.StreamInput
import org.opensearch.common.io.stream.StreamOutput
import org.opensearch.common.xcontent.NamedXContentRegistry
import org.opensearch.common.xcontent.ToXContent
import org.opensearch.common.xcontent.XContentBuilder
import org.opensearch.common.xcontent.XContentParser
import org.opensearch.common.xcontent.XContentParserUtils
import java.io.IOException

data class CompositeInput(
    val sequence: Sequence
) : Input {
    @Throws(IOException::class)
    constructor(sin: StreamInput) : this(
        Sequence(sin)
    )

    fun asTemplateArg(): Map<String, Any?> {
        return mapOf(
            SEQUENCE_FIELD to sequence
        )
    }

    @Throws(IOException::class)
    override fun writeTo(out: StreamOutput) {
        sequence.writeTo(out)
    }

    override fun toXContent(builder: XContentBuilder, params: ToXContent.Params): XContentBuilder {
        builder.startObject()
            .startObject(COMPOSITE_INPUT_FIELD)
            .field(SEQUENCE_FIELD, sequence)
            .endObject()
            .endObject()
        return builder
    }

    override fun name(): String {
        return COMPOSITE_INPUT_FIELD
    }

    companion object {
        const val COMPOSITE_INPUT_FIELD = "composite_input"
        const val SEQUENCE_FIELD = "sequence"

        val XCONTENT_REGISTRY = NamedXContentRegistry.Entry(
            Input::class.java,
            ParseField(COMPOSITE_INPUT_FIELD), CheckedFunction { CompositeInput.parse(it) }
        )

        @JvmStatic
        @Throws(IOException::class)
        fun parse(xcp: XContentParser): CompositeInput {
            var sequence = Sequence(emptyList())
            XContentParserUtils.ensureExpectedToken(XContentParser.Token.START_OBJECT, xcp.currentToken(), xcp)
            while (xcp.nextToken() != XContentParser.Token.END_OBJECT) {
                val fieldName = xcp.currentName()
                xcp.nextToken()

                when (fieldName) {
                    SEQUENCE_FIELD -> {
                        sequence = Sequence.parse(xcp)
                    }
                }
            }

            return CompositeInput(sequence)
        }

        @JvmStatic
        @Throws(IOException::class)
        fun readFrom(sin: StreamInput): CompositeInput {
            return CompositeInput(sin)
        }
    }
}
