package android.jun.birdwatch.database;

public class BirdDbSchema{
    public static final class BirdTable {
        public static final String NAME = "birds";
            public static final class Cols{
                public static final String UUID = "uuid";
                public static final String NAME = "name";
                public static final String DATE = "date";
                public static final String DESCRIPTION = "description";

            }
    }
}
