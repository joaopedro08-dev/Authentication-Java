package com.auth.backend_java.config;

import com.auth.backend_java.exception.*;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(
            Throwable ex,
            DataFetchingEnvironment env) {

        if (ex instanceof ApiException apiEx) {
            return buildError(
                    mapErrorType(apiEx),
                    apiEx.getMessage(),
                    apiEx.getCode(),
                    env
            );
        }

        log.error("Unhandled GraphQL error", ex);

        return buildError(
                ErrorType.INTERNAL_ERROR,
                "Internal server error",
                "INTERNAL_500",
                env
        );
    }

    private ErrorType mapErrorType(ApiException ex) {
        if (ex instanceof UnauthorizedException) {
            return ErrorType.UNAUTHORIZED;
        }
        if (ex instanceof ForbiddenException) {
            return ErrorType.FORBIDDEN;
        }
        if (ex instanceof NotFoundException) {
            return ErrorType.NOT_FOUND;
        }
        return ErrorType.BAD_REQUEST;
    }

    private GraphQLError buildError(
            ErrorType type,
            String message,
            String code,
            DataFetchingEnvironment env) {

        return GraphqlErrorBuilder.newError()
                .errorType(type)
                .message(message)
                .path(env.getExecutionStepInfo().getPath())
                .extensions(Map.of("code", code))
                .build();
    }
}