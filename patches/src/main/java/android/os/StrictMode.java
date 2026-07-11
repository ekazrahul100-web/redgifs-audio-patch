package android.os;

public class StrictMode {
    public static class ThreadPolicy {
        public static class Builder {
            public Builder permitAll() { return this; }
            public ThreadPolicy build() { return new ThreadPolicy(); }
        }
    }
    public static void setThreadPolicy(ThreadPolicy policy) {}
}
