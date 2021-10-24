package com.surfront.sieve;

import java.util.StringTokenizer;

public class CommentCommand<E> implements Command<E> {
    public static final String TYPE = "comment";
    public static final int SINGLELINE_COMMENT = 0;
    public static final int MULTILINE_COMMENT = 1;
    protected int style;
    protected String comment;

    public CommentCommand(int style) {
        this.style = style;
    }

    public String getType() {
        return "comment";
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void compile(SieveContext<E> context) {
    }

    public int execute(E data) {
        return CONTINUE;
    }

    public String toString() {
        return toString("");
    }

    protected String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        if (comment != null) {
            StringTokenizer st = new StringTokenizer(comment, "\r\n", true);
            if (st.hasMoreTokens()) {
                if (style == CommentCommand.SINGLELINE_COMMENT) {
                    sb.append('#');
                } else {
                    sb.append("/*");
                }
                sb.append(st.nextToken());
                while (st.hasMoreTokens()) {
                    sb.append(indent);
                    if (style == CommentCommand.SINGLELINE_COMMENT) {
                        sb.append('#');
                    }
                    sb.append(st.nextToken());
                }
                if (style != CommentCommand.SINGLELINE_COMMENT) {
                    sb.append("*/");
                }
            }
        }
        if (sb.length() == 0) {
            if (style == CommentCommand.SINGLELINE_COMMENT) {
                sb.append('#');
            } else {
                sb.append("/**/");
            }
        }
        return sb.toString();
    }

}
