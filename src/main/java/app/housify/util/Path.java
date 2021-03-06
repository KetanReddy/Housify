package app.housify.util;

public class Path {

    public static class Web {
        public static final String INDEX = "/";
        public static final String AGENT = "/agent";
        public static final String SEARCH = "/search";
        public static final String MANAGE = "/manage";
    }

    public static class Template {
        public static final String AGENT_LIST = "/velocity/agent/list.vm";
        public static final String AGENT_SINGLE = "/velocity/agent/single.vm";
        public static final String SEARCH = "/velocity/search/search.vm";
        public static final String LISTINGS = "/velocity/listings.vm";
        public static final String MANAGE = "/velocity/manage/manage.vm";
        public static final String AGENT_METRICS = "/velocity/manage/metrics.vm";
    }
}
