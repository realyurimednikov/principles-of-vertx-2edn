package net.yurimednikov.vertxbook.cashx.verticles;

import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.client.predicate.ResponsePredicateResult;

class HeaderResponsePredicate implements ResponsePredicate {

    private String header;
    private String value;

    HeaderResponsePredicate(String header, Object value){
        this.header = header;
        this.value = value.toString();
    }

    @Override
    public ResponsePredicateResult apply(HttpResponse<Void> response) {
        Boolean isValid = response.getHeader(header) != null && response.getHeader(header).contentEquals(value);
        return isValid ? ResponsePredicateResult.success() : ResponsePredicateResult.failure("Header value does not match");
    }
}
