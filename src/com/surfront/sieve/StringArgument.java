package com.surfront.sieve;

public class StringArgument implements Argument {
    protected String[] value;

    public StringArgument(String value) {
        this(new String[]{value});
    }

    public StringArgument(String... value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        String[] values = (String[]) getValue();
        if (values.length == 1) {
            sb.append('"');
            quote(sb, values[0]);
            sb.append('"');
        } else {
            sb.append('[');
            sb.append('"');
            quote(sb, values[0]);
            for (int i = 1; i < values.length; i++) {
                sb.append("\", \"");
                quote(sb, values[i]);
            }
            sb.append('"');
            sb.append(']');
        }
        return sb.toString();
    }

    public static String unquote(String str) {
        StringBuilder sb = new StringBuilder();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            if (ch == '\\') {
                char c1 = str.charAt(++i);
                switch (c1) {
                    case '\\':
                        sb.append('\\');
                        break;
                    case '"':
                        sb.append('"');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'u':
                        sb.append((char) Integer.parseInt(str.substring(i + 1, i + 5), 16));
                        i += 4;
                        break;
                    default:
                        sb.append(c1);
                        break;
                }
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static String quote(String str) {
        StringBuilder sb = new StringBuilder();
        quote(sb, str);
        return sb.toString();
    }

    private static void quote(StringBuilder sb, String str) {
        if (str != null) {
            int length = str.length();
            for (int i = 0; i < length; i++) {
                char ch = str.charAt(i);
                switch (ch) {
                    case '\\':
                        sb.append('\\');
                        sb.append('\\');
                        break;
                    case '"':
                        sb.append('\\');
                        sb.append('"');
                        break;
                    case '\t':
                        sb.append('\\');
                        sb.append('t');
                        break;
                    case '\n':
                        sb.append('\\');
                        sb.append('n');
                        break;
                    case '\r':
                        sb.append('\\');
                        sb.append('r');
                        break;
                    case '\f':
                        sb.append('\\');
                        sb.append('f');
                        break;
                    default:
                        sb.append(ch);
                        break;
                }
            }
        }
    }
}
