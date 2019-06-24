package com.codependent.cdc.transfer.stream

import movement_entity
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.slf4j.LoggerFactory


@Suppress("UNCHECKED_CAST")
class DeduplicationTransformer : Transformer<String, movement_entity, KeyValue<String, movement_entity>> {

    private val logger = LoggerFactory.getLogger(javaClass)
    private lateinit var dedupStore: KeyValueStore<String, String>
    private lateinit var context: ProcessorContext

    override fun init(context: ProcessorContext) {
        this.context = context
        dedupStore = context.getStateStore(DEDUP_STORE) as KeyValueStore<String, String>
    }

    override fun transform(key: String, value: movement_entity): KeyValue<String, movement_entity>? {
        return if (isDuplicate(key)) {
            logger.warn("****** Detected duplicated transfer {}", key)
            null
        } else {
            logger.warn("****** Registering transfer {}", key)
            dedupStore.put(key, key)
            KeyValue(key, value)
        }
    }

    private fun isDuplicate(key: String) = dedupStore[key] != null

    override fun close() {
    }
}
