package com.example.wcg_viewer.MemberStatsByProjectsDatabase;

public class MemberStatsByProjectDbSchema {
    public static final class MemberStatsByProjectTable{
        public static final String NAME = "memberStatsByProject";
        public static final class Columns{
            public static final String PROJECT_NAME = "projectName";
            public static final String PROJECT_SHORT_NAME = "projectShortName";
            public static final String RUNTIME = "runtime";
            public static final String POINTS = "points";
            public static final String RESULTS = "results";
            public static final String BADGE_URL = "url";
            public static final String BADGE_DESCRIPTION = "description";
        }
    }
}
