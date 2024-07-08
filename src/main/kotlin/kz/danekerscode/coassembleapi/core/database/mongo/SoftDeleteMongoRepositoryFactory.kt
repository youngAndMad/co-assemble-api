package kz.danekerscode.coassembleapi.core.database.mongo

import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory
import org.springframework.data.repository.query.QueryLookupStrategy
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider
import java.util.*

class SoftDeleteMongoRepositoryFactory(
    private val mongoOperations: MongoOperations
) : MongoRepositoryFactory(mongoOperations) {

    override fun getQueryLookupStrategy(
        key: QueryLookupStrategy.Key?,
        evaluationContextProvider: QueryMethodEvaluationContextProvider
    ): Optional<QueryLookupStrategy> {
        val queryLookupStrategy = super.getQueryLookupStrategy(
            key,
            evaluationContextProvider
        )
        return Optional.of(createSoftDeleteQueryLookupStrategy(queryLookupStrategy.get(), evaluationContextProvider))
    }

    private fun createSoftDeleteQueryLookupStrategy(
        strategy: QueryLookupStrategy,
        evaluationContextProvider: QueryMethodEvaluationContextProvider
    ): SoftDeleteMongoQueryLookupStrategy =
        SoftDeleteMongoQueryLookupStrategy(strategy, mongoOperations, evaluationContextProvider)

}