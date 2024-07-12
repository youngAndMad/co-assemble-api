package kz.danekerscode.coassembleapi.core.database.mongo

import kz.danekerscode.coassembleapi.core.annotation.SeesSoftlyDeletedRecords
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.repository.query.ConvertingParameterAccessor
import org.springframework.data.mongodb.repository.query.PartTreeMongoQuery
import org.springframework.data.projection.ProjectionFactory
import org.springframework.data.repository.core.NamedQueries
import org.springframework.data.repository.core.RepositoryMetadata
import org.springframework.data.repository.query.QueryLookupStrategy
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider
import org.springframework.data.repository.query.RepositoryQuery
import org.springframework.expression.spel.standard.SpelExpressionParser
import java.lang.reflect.Method

class SoftDeleteMongoQueryLookupStrategy(
    private val strategy: QueryLookupStrategy,
    private val mongoOperations: MongoOperations,
    private val evaluationContextProvider: QueryMethodEvaluationContextProvider,
) : QueryLookupStrategy {
    override fun resolveQuery(
        method: Method,
        metadata: RepositoryMetadata,
        factory: ProjectionFactory,
        namedQueries: NamedQueries,
    ): RepositoryQuery {
        println(method.name)
        val repositoryQuery: RepositoryQuery = strategy.resolveQuery(method, metadata, factory, namedQueries)

        if (method.getAnnotation(SeesSoftlyDeletedRecords::class.java) != null ||
            repositoryQuery !is PartTreeMongoQuery
        ) {
            return repositoryQuery
        }

        return SoftDeletePartTreeMongoQuery(repositoryQuery)
    }

    inner class SoftDeletePartTreeMongoQuery(partTreeQuery: PartTreeMongoQuery) : PartTreeMongoQuery(
        partTreeQuery.queryMethod,
        mongoOperations,
        SpelExpressionParser(),
        evaluationContextProvider,
    ) {
        override fun createQuery(accessor: ConvertingParameterAccessor): Query =
            super
                .createQuery(accessor)
                .apply { withNotDeleted(this) }

        override fun createCountQuery(accessor: ConvertingParameterAccessor): Query =
            super
                .createCountQuery(accessor)
                .apply { withNotDeleted(this) }

        private fun withNotDeleted(query: Query): Query = query.addCriteria(notDeleted())

        private fun notDeleted(): Criteria = Criteria.where("deletedDate").isNull
    }
}
