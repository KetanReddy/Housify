package app.housify;

public class Path {

    public static class Web {
        public static final String INDEX = "/index";
        public static final String LOGIN = "/login";
        public static final String AGENT = "/agent";
        public static final String SEARCH = "/search";
    }

    public static class Template {
        public static final String INDEX = "/velocity/index/index.vm";
        public static final String LOGIN = "/velocity/login/login.vm";
        public static final String AGENT_LIST = "/velocity/agent/list.vm";
        public static final String AGENT_SINGLE = "/velocity/agent/single.vm";
        public static final String SEARCH = "/velocity/search/search.vm";
        public static final String SEARCH_RESULTS = "/velocity/search/results.vm";
    }

}
