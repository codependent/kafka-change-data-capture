package com.codependent.cdc.transfer.stream

import com.codependent.cdc.account.Movement
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.slf4j.LoggerFactory


@Suppress("UNCHECKED_CAST")
class DeduplicationTransformer : Transformer<String?, Movement, KeyValue<String, Movement>> {

    private val logger = LoggerFactory.getLogger(javaClass)
    private lateinit var dedupStore: KeyValueStore<String, String>
    private lateinit var context: ProcessorContext

    override fun init(context: ProcessorContext) {
        this.context = context
        dedupStore = context.getStateStore(DEDUP_STORE) as KeyValueStore<String, String>
    }

    override fun transform(key: String?, value: Movement): KeyValue<String, Movement>? {
        return if (isDuplicate(value.transactionId)) {
            logger.warn("****** Detected duplicated transfer {}", value.transactionId)
            null
        } else {
            logger.info("****** Registering transfer {}", value.transactionId)
            dedupStore.put(value.transactionId, value.transactionId)
            KeyValue(value.transactionId, value)
        }
    }

    private fun isDuplicate(key: String) = dedupStore[key] != null

    override fun close() {
    }
}
