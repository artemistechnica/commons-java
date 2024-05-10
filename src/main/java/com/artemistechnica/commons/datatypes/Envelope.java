package com.artemistechnica.commons.datatypes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.net.URI;
import java.util.List;

@Builder
@JsonAutoDetect
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Envelope<A> {

    public final Code         code;
    public final List<A>      data;
    public final List<Error>  errors;

    private Envelope(Code code, List<A> data, List<Error> errors) {
        this.code   = code;
        this.data   = data;
        this.errors = errors;
    }

    @SafeVarargs
    public static <A> Envelope<A> mkSuccess(A... data) {
        Code code = Code.mk(URI.create("sampleapp://localhost/success#200"), 200, "SUCCESS");
        return new Envelope<>(code, List.of(data), null);
    }

    public static <A> Envelope<A> mkFailure(String error) {
        Code code = Code.mk(URI.create("sampleapp://localhost/error#500"), 500, "ERROR");
        return new Envelope<>(code, null, List.of(Error.mk(error)));
    }

    @Builder
    @JsonAutoDetect
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Error {
        public final String error;

        private Error(String error) {
            this.error = error;
        }

        public static Error mk(String error) {
            return new Error(error);
        }
    }

    @Builder
    @JsonAutoDetect
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Code {
        public final URI uri;
        public final Integer id;
        public final String message;

        private Code(URI uri, Integer id, String message) {
            this.uri        = uri;
            this.id         = id;
            this.message    = message;
        }

        public static Code mk(URI uri, Integer id, String message) {
            return new Code(uri, id, message);
        }
    }
}
