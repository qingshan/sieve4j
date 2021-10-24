package com.surfront.sieve.transformer;

public class PipelineTransformer implements Transformer {
    public static final String TYPE = "pipeline";
    private final Transformer[] transformers;

    public PipelineTransformer(Transformer[] transformers) {
        this.transformers = transformers;
    }

    public String getType() {
        return TYPE;
    }

    public Transformer[] getTransformers() {
        return transformers;
    }

    public String transform(String value) {
        for (Transformer transformer : transformers) {
            value = transformer.transform(value);
        }
        return value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Transformer transformer : transformers) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("\"");
            sb.append(transformer.toString());
            sb.append("\"");
        }
        sb.append("]");
        return sb.toString();
    }
}
